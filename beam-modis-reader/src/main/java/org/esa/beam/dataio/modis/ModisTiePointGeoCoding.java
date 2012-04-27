/*
 * Copyright (C) 2011 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.esa.beam.dataio.modis;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.dataio.ProductSubsetDef;
import org.esa.beam.framework.datamodel.AbstractGeoCoding;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.ProductNode;
import org.esa.beam.framework.datamodel.Scene;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.dataop.maptransf.Datum;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.math.IndexValidator;
import org.esa.beam.util.math.Range;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ModisTiePointGeoCoding</code> class is a special geo-coding for
 * MODIS Level-1B and Level-2 swath products.
 * <p/>
 * <p>It enables BEAM to transform the MODIS swaths to uniformly gridded
 * image that is geographically referenced according to user-specified
 * projection and resampling parameters.
 * Correction for oversampling between scans as a function of increasing
 * (off-nadir) scan angle is performed (correction for bow-tie effect).
 */
public class ModisTiePointGeoCoding extends AbstractGeoCoding {

    private final Datum _datum;
    private TiePointGrid _latGrid;
    private TiePointGrid _lonGrid;
    private List<GeoCoding> _gcList;
    private boolean _cross180;
    private List<PolyLine> _centerLineList;
    private int _lastCenterLineIndex;
    private int _smallestValidIndex;
    private int _biggestValidIndex;
    private int _gcStripeSceneHeight;

    /**
     * Constructs geo-coding based on two given tie-point grids.
     *
     * @param latGrid the latitude grid, must not be <code>null</code>
     * @param lonGrid the longitude grid, must not be <code>null</code>
     */
    public ModisTiePointGeoCoding(TiePointGrid latGrid, TiePointGrid lonGrid) {
        this(latGrid, lonGrid, Datum.WGS_84); // todo  - check datum, is it really WGS84 for MODIS?
    }

    /**
     * Constructs geo-coding based on two given tie-point grids.
     *
     * @param latGrid the latitude grid, must not be <code>null</code>
     * @param lonGrid the longitude grid, must not be <code>null</code>
     * @param datum   the datum (f.e. WGS84)
     */
    public ModisTiePointGeoCoding(TiePointGrid latGrid, TiePointGrid lonGrid, final Datum datum) {
        Guardian.assertNotNull("latGrid", latGrid);
        Guardian.assertNotNull("lonGrid", lonGrid);
        Guardian.assertNotNull("datum", datum);
        if (latGrid.getRasterWidth() != lonGrid.getRasterWidth() ||
            latGrid.getRasterHeight() != lonGrid.getRasterHeight() ||
            latGrid.getOffsetX() != lonGrid.getOffsetX() ||
            latGrid.getOffsetY() != lonGrid.getOffsetY() ||
            latGrid.getSubSamplingX() != lonGrid.getSubSamplingX() ||
            latGrid.getSubSamplingY() != lonGrid.getSubSamplingY()) {
            throw new IllegalArgumentException("latGrid is not compatible with lonGrid");
        }
        _latGrid = latGrid;
        _lonGrid = lonGrid;
        _datum = datum;
        _lastCenterLineIndex = 0;
        init();
    }

    /**
     * Checks whether or not this geo-coding can determine the pixel position from a geodetic position.
     *
     * @return <code>true</code>, if so
     */
    public boolean canGetPixelPos() {
        return true;
    }

    /**
     * Checks whether or not this geo-coding can determine the geodetic position from a pixel position.
     *
     * @return <code>true</code>, if so
     */
    public boolean canGetGeoPos() {
        return true;
    }

    /**
     * Returns the pixel co-ordinates as x/y for a given geographical position given as lat/lon.
     *
     * @param geoPos   the geographical position as lat/lon in the coodinate system determined by {@link #getDatum()}
     * @param pixelPos an instance of <code>Point</code> to be used as retun value. If this parameter is
     *                 <code>null</code>, the method creates a new instance which it then returns.
     *
     * @return the pixel co-ordinates as x/y
     */
    public PixelPos getPixelPos(GeoPos geoPos, PixelPos pixelPos) {
        if (pixelPos == null) {
            pixelPos = new PixelPos();
        }
        pixelPos.x = -1;
        pixelPos.y = -1;

        // tb 2009-01-21 - this shortcut check is numerically incorrect. MERCI fails to calculate intersection
        // lines - althoiugh the operation should be consistent.
        // - calculate geo-boundary of product
        // - intersect with search region
        // - re-transform intersection polygon points to x/y space
        // - NONE of the boundary points transforms to a consistent x/y position - all result in (-1,-1)
//        if (_generalArea == null) {
//            initGeneralArea();
//        }

//        if (!_generalArea.contains(geoPos.lon, geoPos.lat)) {
//            return pixelPos;
//        }

        final int index = getGeoCodingIndexfor(geoPos);
        _lastCenterLineIndex = index;
        final GeoCoding gc = _gcList.get(index);
        if (gc != null) {
            gc.getPixelPos(geoPos, pixelPos);
        }

        if (pixelPos.x == -1 || pixelPos.y == -1) {
            return pixelPos;
        }
        pixelPos.y += (_lastCenterLineIndex * _gcStripeSceneHeight);
        return pixelPos;
    }

    /**
     * Returns the latitude and longitude value for a given pixel co-ordinate.
     *
     * @param pixelPos the pixel's co-ordinates given as x,y
     * @param geoPos   an instance of <code>GeoPos</code> to be used as retun value. If this parameter is
     *                 <code>null</code>, the method creates a new instance which it then returns.
     *
     * @return the geographical position as lat/lon in the coodinate system determined by {@link #getDatum()}
     */
    public GeoPos getGeoPos(PixelPos pixelPos, GeoPos geoPos) {
        final int index = computeIndex(pixelPos);
        final GeoCoding gc = _gcList.get(index);
        if (gc != null) {
            return gc.getGeoPos(new PixelPos(pixelPos.x, pixelPos.y - _gcStripeSceneHeight * index), geoPos);
        } else {
            if (geoPos == null) {
                geoPos = new GeoPos();
            }
            geoPos.setInvalid();
            return geoPos;
        }
    }

    /**
     * Gets the datum, the reference point or surface against which {@link org.esa.beam.framework.datamodel.GeoPos} measurements are made.
     *
     * @return the datum
     */
    public Datum getDatum() {
        return _datum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModisTiePointGeoCoding that = (ModisTiePointGeoCoding) o;
        if (_latGrid == null || that._latGrid == null) {
            return false;
        }
        if (!_latGrid.equals(that._latGrid)) {
            return false;
        }
        if (_lonGrid == null || that._lonGrid == null) {
            return false;
        }
        if (!_lonGrid.equals(that._lonGrid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _latGrid != null ? _latGrid.hashCode() : 0;
        result = 31 * result + (_lonGrid != null ? _lonGrid.hashCode() : 0);
        return result;
    }

    /**
     * Releases all of the resources used by this object instance and all of its owned children. Its primary use is to
     * allow the garbage collector to perform a vanilla job.
     * <p/>
     * <p>This method should be called only if it is for sure that this object instance will never be used again. The
     * results of referencing an instance of this class after a call to <code>dispose()</code> are undefined.
     */
    @Override
    public void dispose() {
        for (GeoCoding gc : _gcList) {
            if (gc != null) {
                gc.dispose();
            }
        }
        _gcList.clear();
        _latGrid = null;
        _lonGrid = null;
    }

    /**
     * Checks whether or not the longitudes of this geo-coding cross the +/- 180 degree meridian.
     *
     * @return <code>true</code>, if so
     */
    public boolean isCrossingMeridianAt180() {
        return _cross180;
    }

    private void init() {
        _cross180 = false;
        _gcList = new ArrayList<GeoCoding>();
        _centerLineList = new ArrayList<PolyLine>();
        final float osX = _lonGrid.getOffsetX();
        final float osY = _lonGrid.getOffsetY() + 0.5f;
        final float ssX = _lonGrid.getSubSamplingX();
        final float ssY = _lonGrid.getSubSamplingY();

        final float[] latFloats = (float[]) _latGrid.getDataElems();
        final float[] lonFloats = (float[]) _lonGrid.getDataElems();

        final int stripeW = _lonGrid.getRasterWidth();
        final int gcStripeSceneWidth = _lonGrid.getSceneRasterWidth();
        final int h = _lonGrid.getRasterHeight();
        final int sceneHeight = _lonGrid.getSceneRasterHeight();
        final int stripeH;
        if (sceneHeight / h == 2) {
            stripeH = 10;
            _gcStripeSceneHeight = 20;
        } else {
            stripeH = 2;
            _gcStripeSceneHeight = 10;
        }

        final int gcRawWidth = stripeW * stripeH;
        for (int y = 0; y < h; y += stripeH) {
            final float[] lats = new float[gcRawWidth];
            final float[] lons = new float[gcRawWidth];
            System.arraycopy(lonFloats, y * stripeW, lons, 0, stripeW * stripeH);
            System.arraycopy(latFloats, y * stripeW, lats, 0, stripeW * stripeH);

            final Range range = Range.computeRangeFloat(lats, IndexValidator.TRUE, null, ProgressMonitor.NULL);
            if (range.getMin() < -90) {
                _gcList.add(null);
                _centerLineList.add(null);
            } else {
                final ModisTiePointGrid latTPG = new ModisTiePointGrid("lat" + y, stripeW, stripeH, osX, osY, ssX, ssY, lats);
                final ModisTiePointGrid lonTPG = new ModisTiePointGrid("lon" + y, stripeW, stripeH, osX, osY, ssX, ssY, lons, true);

                final TiePointGeoCoding geoCoding = new TiePointGeoCoding(latTPG, lonTPG, _datum);
                _cross180 = _cross180 || geoCoding.isCrossingMeridianAt180();
                _gcList.add(geoCoding);
                _centerLineList.add(createCenterPolyLine(geoCoding, gcStripeSceneWidth, _gcStripeSceneHeight));
            }
        }
        initSmallestAndLargestValidGeocodingIndices();
    }

    private void initSmallestAndLargestValidGeocodingIndices() {
        for (int i = 0; i < _gcList.size(); i++) {
            if (_gcList.get(i) != null) {
                _smallestValidIndex = i;
                break;
            }
        }
        for (int i = _gcList.size() - 1; i > 0; i--) {
            if (_gcList.get(i) != null) {
                _biggestValidIndex = i;
                break;
            }
        }
    }

    // tb 2009-01-21 - this intersection is numerically incorrect - see comments above
//    private void initGeneralArea() {
//        final GeneralPath[] geoBoundaryPaths = ProductUtils.createGeoBoundaryPaths(_latGrid.getProduct(), null, 20);
//        _generalArea = new Area();
//        for (int i = 0; i < geoBoundaryPaths.length; i++) {
//            final GeneralPath geoBoundaryPath = geoBoundaryPaths[i];
//            _generalArea.add(new Area(geoBoundaryPath));
//        }
//    }

    private static PolyLine createCenterPolyLine(TiePointGeoCoding geoCoding, final int sceneWidth,
                                                 final int sceneHeight) {

        final double numberOfSegments = 100.0;
        final double stepX = sceneWidth / numberOfSegments;

        final PixelPos pixelPos = new PixelPos();
        final GeoPos geoPos = new GeoPos();
        final PolyLine polyLine = new PolyLine();

        pixelPos.y = sceneHeight / 2f;

        for (pixelPos.x = 0; pixelPos.x < sceneWidth + 0.5; pixelPos.x += stepX) {
            geoCoding.getGeoPos(pixelPos, geoPos);
            if (pixelPos.x == 0) {
                polyLine.moveTo(geoPos.lon, geoPos.lat);
            } else {
                polyLine.lineTo(geoPos.lon, geoPos.lat);
            }
        }

        return polyLine;
    }

    private int computeIndex(PixelPos pixelPos) {
        final int y = (int) pixelPos.getY();
        final int index = y / _gcStripeSceneHeight;
        if (index < _smallestValidIndex) {
            return _smallestValidIndex;
        } else if (index > _biggestValidIndex) {
            return _biggestValidIndex;
        } else {
            return index;
        }
    }

    private int getGeoCodingIndexfor(final GeoPos geoPos) {
        int index = _lastCenterLineIndex;
        index = getNextCenterLineIndex(index, 1);
        final PolyLine centerLine1 = _centerLineList.get(index);
        double v = centerLine1.getDistance(geoPos.lon, geoPos.lat);
        int vIndex = index;

        int direction = -1;
        if (index == _smallestValidIndex) {
            direction = +1;
        }
        while (true) {
            index += direction;
            index = getNextCenterLineIndex(index, direction);
            final PolyLine centerLine2 = _centerLineList.get(index);
            final double v2 = centerLine2.getDistance(geoPos.lon, geoPos.lat);
            if (v2 < v) {
                if (index == _smallestValidIndex || index == _biggestValidIndex) {
                    return index;
                }
                v = v2;
                vIndex = index;
            } else if (direction == -1) {
                index++;
                direction = +1;
                if (index == _biggestValidIndex) {
                    return index;
                }
            } else if (direction == +1) {
                return vIndex;
            }
        }
    }

    private int getNextCenterLineIndex(int index, final int direction) {
        while (_centerLineList.get(index) == null) {
            index += direction;
            if (index < _smallestValidIndex) {
                index = _biggestValidIndex;
            } else if (index > _biggestValidIndex) {
                index = _smallestValidIndex;
            }
        }
        return index;
    }

    /**
     * Transfers the geo-coding of the {@link org.esa.beam.framework.datamodel.Scene srcScene} to the {@link org.esa.beam.framework.datamodel.Scene destScene} with respect to the given
     * {@link org.esa.beam.framework.dataio.ProductSubsetDef subsetDef}.
     *
     * @param srcScene  the source scene
     * @param destScene the destination scene
     * @param subsetDef the definition of the subset, may be <code>null</code>
     *
     * @return true, if the geo-coding could be transferred.
     */
    @Override
    public boolean transferGeoCoding(final Scene srcScene, final Scene destScene, final ProductSubsetDef subsetDef) {
        final String latGridName = _latGrid.getName();
        final String lonGridName = _lonGrid.getName();
        final TiePointGrid latGrid = destScene.getProduct().getTiePointGrid(latGridName);
        final TiePointGrid lonGrid = destScene.getProduct().getTiePointGrid(lonGridName);
        if (latGrid != null && lonGrid != null) {
            destScene.setGeoCoding(new ModisTiePointGeoCoding(latGrid, lonGrid, getDatum()));
            return true;
        }
        return false;
    }

    private static class PolyLine {

        private float _x1;
        private float _y1;
        private boolean _started;
        private ArrayList<Line2D.Float> _lines;

        public PolyLine() {
            _started = false;
        }

        public void lineTo(final float x, final float y) {
            _lines.add(new Line2D.Float(_x1, _y1, x, y));
            setXY1(x, y);
        }

        public void moveTo(final float x, final float y) {
            if (_started) {
                throw new IllegalStateException("Polyline alredy started");
            }
            setXY1(x, y);
            _lines = new ArrayList<Line2D.Float>();
            _started = true;
        }

        private void setXY1(final float x, final float y) {
            _x1 = x;
            _y1 = y;
        }

        public double getDistance(final float x, final float y) {
            double smallestDistPoints = Double.MAX_VALUE;
            double pointsDist = smallestDistPoints;
            if (_lines != null && _lines.size() > 0) {
                for (final Line2D.Float line : _lines) {
                    final double distPoints = line.ptSegDistSq(x, y);
                    if (distPoints < smallestDistPoints) {
                        smallestDistPoints = distPoints;
                    }
                }

                pointsDist = Math.sqrt(smallestDistPoints);
            }

            return pointsDist;
        }
    }

    private class ModisTiePointGrid extends TiePointGrid {

        public ModisTiePointGrid(String name, int gridWidth, int gridHeight, float offsetX, float offsetY, float subSamplingX, float subSamplingY, float[] tiePoints) {
            super(name, gridWidth, gridHeight, offsetX, offsetY, subSamplingX, subSamplingY, tiePoints);
        }

        public ModisTiePointGrid(String name, int gridWidth, int gridHeight, float offsetX, float offsetY, float subSamplingX, float subSamplingY, float[] tiePoints, int discontinuity) {
            super(name, gridWidth, gridHeight, offsetX, offsetY, subSamplingX, subSamplingY, tiePoints, discontinuity);
        }

        public ModisTiePointGrid(String name, int gridWidth, int gridHeight, float offsetX, float offsetY, float subSamplingX, float subSamplingY, float[] tiePoints, boolean containsAngles) {
            super(name, gridWidth, gridHeight, offsetX, offsetY, subSamplingX, subSamplingY, tiePoints, containsAngles);
        }

        @Override
        public ProductNode getOwner() {
            return _lonGrid.getOwner();
        }
    }
}
