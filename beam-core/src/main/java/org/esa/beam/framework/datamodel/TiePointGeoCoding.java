/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
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
package org.esa.beam.framework.datamodel;

import org.esa.beam.framework.dataio.ProductSubsetDef;
import org.esa.beam.framework.dataop.maptransf.Datum;
import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.math.FXYSum;
import org.esa.beam.util.math.MathUtils;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * A geo-coding based on two tie-point grids. One grid stores the latitude tie-points, the other stores the longitude
 * tie-points.
 */
public class TiePointGeoCoding extends AbstractGeoCoding {

    private static final double ABS_ERROR_LIMIT = 0.5; // pixels
    private static final int MAX_NUM_POINTS_PER_TILE = 1000;

    private final TiePointGrid latGrid;
    private final TiePointGrid lonGrid;
    private final Approximation[] approximations;
    private final Datum datum;

    private boolean normalized;
    private float normalizedLonMin;
    private float normalizedLonMax;
    private float latMin;
    private float latMax;
    private float overlapStart;
    private float overlapEnd;

    /**
     * Constructs geo-coding based on two given tie-point grids providing coordinates on the WGS-84 datum.
     *
     * @param latGrid the latitude grid
     * @param lonGrid the longitude grid
     */
    public TiePointGeoCoding(TiePointGrid latGrid, TiePointGrid lonGrid) {
        this(latGrid, lonGrid, Datum.WGS_84);
    }

    /**
     * Constructs geo-coding based on two given tie-point grids.
     *
     * @param latGrid the latitude grid
     * @param lonGrid the longitude grid
     * @param datum   the geodetic datum
     */
    public TiePointGeoCoding(TiePointGrid latGrid, TiePointGrid lonGrid, Datum datum) {
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
        this.latGrid = latGrid;
        this.lonGrid = lonGrid;
        this.datum = datum;
        final TiePointGrid normalizedLonGrid = initNormalizedLonGrid();
        initLatLonMinMax(normalizedLonGrid);
        approximations = initApproximations(normalizedLonGrid);
    }

    /**
     * Gets the datum, the reference point or surface against which {@link GeoPos} measurements are made.
     *
     * @return the datum
     */
    @Override
    public Datum getDatum() {
        return datum;
    }

    /**
     * Gets the flag indicating that the geographic boundary of the tie-points in this geo-coding
     * intersects the 180 degree meridian.
     *
     * @return true if so
     */
    @Override
    public boolean isCrossingMeridianAt180() {
        return normalized;
    }

    /**
     * Gets the number of approximations used for the transformation map (lat,lon) --> image (x,y).
     *
     * @return the number of approximations, zero if no approximations could be computed
     */
    public int getNumApproximations() {
        return approximations != null ? approximations.length : 0;
    }

    /**
     * Gets the approximations for the given index.
     *
     * @param index the index, must be between 0 and {@link #getNumApproximations()} - 1
     *
     * @return the approximation, never null
     */
    public Approximation getApproximation(int index) {
        return approximations[index];
    }

    /**
     * Checks whether this geo-coding can determine the geodetic position from a pixel position.
     *
     * @return <code>true</code>, if so
     */
    @Override
    public boolean canGetGeoPos() {
        return true;
    }

    /**
     * Checks whether this geo-coding can determine the pixel position from a geodetic position.
     *
     * @return <code>true</code>, if so
     */
    @Override
    public boolean canGetPixelPos() {
        return approximations != null;
    }

    /**
     * @return the latitude grid, never <code>null</code>.
     */
    public TiePointGrid getLatGrid() {
        return latGrid;
    }

    /**
     * @return the longitude grid, never <code>null</code>.
     */
    public TiePointGrid getLonGrid() {
        return lonGrid;
    }

    /**
     * Returns the latitude and longitude value for a given pixel co-ordinate.
     *
     * @param pixelPos the pixel's co-ordinates given as x,y
     * @param geoPos   an instance of <code>GeoPos</code> to be used as retun value. If this parameter is
     *                 <code>null</code>, the method creates a new instance which it then returns.
     *
     * @return the geographical position as lat/lon.
     */
    @Override
    public GeoPos getGeoPos(final PixelPos pixelPos, GeoPos geoPos) {
        if (geoPos == null) {
            geoPos = new GeoPos();
        }
        if (pixelPos.x < 0 || pixelPos.x > latGrid.getSceneRasterWidth()
            || pixelPos.y < 0 || pixelPos.y > latGrid.getSceneRasterHeight()) {
            geoPos.setInvalid();
        } else {
            geoPos.lat = latGrid.getPixelFloat(pixelPos.x, pixelPos.y);
            geoPos.lon = lonGrid.getPixelFloat(pixelPos.x, pixelPos.y);
        }
        return geoPos;
    }

    /**
     * Returns the pixel co-ordinates as x/y for a given geographical position given as lat/lon.
     *
     * @param geoPos   the geographical position as lat/lon.
     * @param pixelPos an instance of <code>Point</code> to be used as retun value. If this parameter is
     *                 <code>null</code>, the method creates a new instance which it then returns.
     *
     * @return the pixel co-ordinates as x/y
     */
    @Override
    public PixelPos getPixelPos(GeoPos geoPos, PixelPos pixelPos) {
        if (approximations != null) {
            float lat = normalizeLat(geoPos.lat);
            float lon = normalizeLon(geoPos.lon);
            // ensure that pixel is out of image (= no source position)
            if (pixelPos == null) {
                pixelPos = new PixelPos();
            }

            if (isValidGeoPos(lat, lon)) {
                Approximation approximation = getBestApproximation(approximations, lat, lon);
                // retry with pixel in overlap range, re-normalise
                // solves the problem with overlapping normalized and unnormalized orbit areas (AATSR)
                if (lon >= overlapStart && lon <= overlapEnd) {
                    final float squareDistance;
                    if (approximation != null) {
                        squareDistance = approximation.getSquareDistance(lat, lon);
                    } else {
                        squareDistance = Float.MAX_VALUE;
                    }
                    float tempLon = lon + 360;
                    final Approximation renormalizedApproximation = findRenormalizedApproximation(lat, tempLon,
                                                                                                  squareDistance);
                    if (renormalizedApproximation != null) {
                        approximation = renormalizedApproximation;
                        lon = tempLon;
                    }
                }
                if (approximation != null) {
                    lat = (float) rescaleLatitude(lat);
                    lon = (float) rescaleLongitude(lon, approximation.getCenterLon());
                    pixelPos.x = (float) approximation.getFX().computeZ(lat, lon);
                    pixelPos.y = (float) approximation.getFY().computeZ(lat, lon);
                } else {
                    pixelPos.setInvalid();
                }
            } else {
                pixelPos.setInvalid();
            }
        }
        return pixelPos;
    }

    private boolean isValidGeoPos(final float lat, final float lon) {
        return !Float.isNaN(lat) && !Float.isNaN(lon);
    }

    /**
     * Gets the normalized latitude value.
     * The method returns <code>Float.NaN</code> if the given latitude value is out of bounds.
     *
     * @param lat the raw latitude value in the range -90 to +90 degrees
     *
     * @return the normalized latitude value, <code>Float.NaN</code> else
     */
    public static float normalizeLat(float lat) {
        if (lat < -90 || lat > 90) {
            return Float.NaN;
        }
        return lat;
    }

    /**
     * Gets the normalized longitude value.
     * The method returns <code>Float.NaN</code> if the given longitude value is out of bounds
     * or if it's normalized value is not in the value range of this geo-coding's normalized longitude grid..
     *
     * @param lon the raw longitude value in the range -180 to +180 degrees
     *
     * @return the normalized longitude value, <code>Float.NaN</code> else
     */
    public final float normalizeLon(float lon) {
        if (lon < -180 || lon > 180) {
            return Float.NaN;
        }
        float normalizedLon = lon;
        if (normalizedLon < normalizedLonMin) {
            normalizedLon += 360;
        }
        if (normalizedLon < normalizedLonMin || normalizedLon > normalizedLonMax) {
            return Float.NaN;
        }
        return normalizedLon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TiePointGeoCoding that = (TiePointGeoCoding) o;

        if (!latGrid.equals(that.latGrid)) {
            return false;
        }
        if (!lonGrid.equals(that.lonGrid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = latGrid.hashCode();
        result = 31 * result + lonGrid.hashCode();
        return result;
    }

    @Override
    public void dispose() {
    }

    /////////////////////////////////////////////////////////////////////////
    // Private stuff

    private TiePointGrid initNormalizedLonGrid() {
        final int w = lonGrid.getRasterWidth();
        final int h = lonGrid.getRasterHeight();

        float p1;
        float p2;
        float lonDelta;
        boolean westNormalized = false;
        boolean eastNormalized = false;

        final float[] longitudes = lonGrid.getTiePoints();
        final int numValues = longitudes.length;
        final float[] normalizedLongitudes = new float[numValues];
        System.arraycopy(longitudes, 0, normalizedLongitudes, 0, numValues);
        float lonDeltaMax = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) { // Normalise line-wise, by detecting longituidal discontinuities. lonDelta is the difference between a base point and the current point
                final int index = x + y * w;
                if (x == 0 && y == 0) { // first point in grid: base point is un-normalised
                    p1 = normalizedLongitudes[index];
                } else if (x == 0) { // first point in line: base point is the (possibly) normalised lon. of first point of last line
                    p1 = normalizedLongitudes[x + (y - 1) * w];
                } else { // other points in line: base point is the (possibly) normalised lon. of last point in line
                    p1 = normalizedLongitudes[index - 1];
                }
                p2 = normalizedLongitudes[index]; // the current, un-normalised point
                lonDelta = p2 - p1;  // difference = current point minus base point

                if (lonDelta > 180.0f) {
                    p2 -= 360.0f;  // place new point in the west (with a lon. < -180)
                    westNormalized = true; // mark what we've done
                    normalizedLongitudes[index] = p2;
                } else if (lonDelta < -180.0f) {
                    p2 += 360.0f;  // place new point in the east (with a lon. > +180)
                    eastNormalized = true;  // mark what we've done
                    normalizedLongitudes[index] = p2;
                } else {
                    lonDeltaMax = Math.max(lonDeltaMax, Math.abs(lonDelta));
                }
            }
        }

        // West-normalisation can result in longitudes down to -540 degrees
        if (westNormalized) {
            // This ensures that the all longitude points are >= -180 degree
            for (int i = 0; i < numValues; i++) {
                normalizedLongitudes[i] += 360;
            }
        }

        normalized = westNormalized || eastNormalized;

        final TiePointGrid normalizedLonGrid;
        if (normalized) {
            normalizedLonGrid = new TiePointGrid(lonGrid.getName(),
                                                 lonGrid.getRasterWidth(),
                                                 lonGrid.getRasterHeight(),
                                                 lonGrid.getOffsetX(),
                                                 lonGrid.getOffsetY(),
                                                 lonGrid.getSubSamplingX(),
                                                 lonGrid.getSubSamplingY(),
                                                 normalizedLongitudes,
                                                 lonGrid.getDiscontinuity());
        } else {
            normalizedLonGrid = lonGrid;
        }

        Debug.trace("TiePointGeoCoding.westNormalized = " + westNormalized);
        Debug.trace("TiePointGeoCoding.eastNormalized = " + eastNormalized);
        Debug.trace("TiePointGeoCoding.normalized = " + normalized);
        Debug.trace("TiePointGeoCoding.lonDeltaMax = " + lonDeltaMax);

        return normalizedLonGrid;
    }

    private void initLatLonMinMax(TiePointGrid normalizedLonGrid) {
        final float[] latPoints = getLatGrid().getTiePoints();
        final float[] lonPoints = normalizedLonGrid.getTiePoints();
        normalizedLonMin = +Float.MAX_VALUE;
        normalizedLonMax = -Float.MAX_VALUE;
        latMin = +Float.MAX_VALUE;
        latMax = -Float.MAX_VALUE;
        for (int i = 0; i < lonPoints.length; i++) {
            normalizedLonMin = Math.min(normalizedLonMin, lonPoints[i]);
            normalizedLonMax = Math.max(normalizedLonMax, lonPoints[i]);
            latMin = Math.min(latMin, latPoints[i]);
            latMax = Math.max(latMax, latPoints[i]);
        }

        overlapStart = normalizedLonMin;
        if (overlapStart < -180) {
            overlapStart += 360;
        }
        overlapEnd = normalizedLonMax;
        if (overlapEnd > 180) {
            overlapEnd -= 360;
        }

        Debug.trace("TiePointGeoCoding.normalizedLonMin = " + normalizedLonMin);
        Debug.trace("TiePointGeoCoding.normalizedLonMax = " + normalizedLonMax);
        Debug.trace("TiePointGeoCoding.latMin = " + latMin);
        Debug.trace("TiePointGeoCoding.latMax = " + latMax);
        Debug.trace("TiePointGeoCoding.overlapRange = " + overlapStart + " - " + overlapEnd);

    }

    private Approximation[] initApproximations(TiePointGrid normalizedLonGrid) {
        final int numPoints = latGrid.getRasterData().getNumElems();
        final int w = latGrid.getRasterWidth();
        final int h = latGrid.getRasterHeight();

        // Compute number of required approximation tiles
        //
        int numTiles; // 10 degree sizing
        if (h > 2) {
            final float lonSpan = normalizedLonMax - normalizedLonMin;
            final float latSpan = latMax - latMin;
            final float angleSpan = Math.max(lonSpan, latSpan);
            numTiles = Math.round(angleSpan / 10.0f);
            if (numTiles < 1) {
                numTiles = 1;
            }
        } else {
            numTiles = 30;
        }
        while (numTiles > 1) {
            // 10 points are at least required for a quadric polynomial
            if (numPoints / numTiles >= 10) {
                break;
            }
            numTiles--;
        }

        final Dimension tileDim = MathUtils.fitDimension(numTiles, w, h);
        int numTilesI = tileDim.width;
        int numTilesJ = tileDim.height;
        numTiles = numTilesI * numTilesJ;

        Debug.trace("TiePointGeoCoding.numTiles =  " + numTiles);
        Debug.trace("TiePointGeoCoding.numTilesI = " + numTilesI);
        Debug.trace("TiePointGeoCoding.numTilesJ = " + numTilesJ);

        // Compute actual approximations for all tiles
        //
        final Approximation[] approximations = new Approximation[numTiles];
        final Rectangle[] rectangles = MathUtils.subdivideRectangle(w, h, numTilesI, numTilesJ, 1);
        for (int i = 0; i < rectangles.length; i++) {
            final Approximation approximation = createApproximation(normalizedLonGrid, rectangles[i]);
            if (approximation == null) {
                return null;
            }
            approximations[i] = approximation;
        }
        return approximations;
    }

    private static FXYSum getBestPolynomial(double[][] data, int[] indices) {
        // These are the potential polynomials which we will check
        final FXYSum[] potentialPolynomials = new FXYSum[]{
                new FXYSum.Linear(),
                new FXYSum.BiLinear(),
                new FXYSum.Quadric(),
                new FXYSum.BiQuadric(),
                new FXYSum.Cubic(),
                new FXYSum.BiCubic(),
                new FXYSum(FXYSum.FXY_4TH, 4),
                new FXYSum(FXYSum.FXY_BI_4TH, 4 + 4)
        };

        // Find the polynomial which best fitts the warp points
        //
        double rmseMin = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < potentialPolynomials.length; i++) {
            FXYSum potentialPolynomial = potentialPolynomials[i];
            final int order = potentialPolynomial.getOrder();
            final int numPointsRequired;
            if (order >= 0) {
                numPointsRequired = (order + 2) * (order + 1) / 2;
            } else {
                numPointsRequired = 2 * potentialPolynomial.getNumTerms();
            }
            if (data.length >= numPointsRequired) {
                try {
                    potentialPolynomial.approximate(data, indices);
                    double rmse = potentialPolynomial.getRootMeanSquareError();
                    double maxError = potentialPolynomial.getMaxError();
                    if (rmse < rmseMin) {
                        index = i;
                        rmseMin = rmse;
                    }
                    if (maxError < ABS_ERROR_LIMIT) { // this accuracy is sufficient
                        index = i;
                        break;
                    }
                } catch (ArithmeticException e) {
                    Debug.trace("Polynomial cannot be constructed due to a numerically singular or degenerate matrix:");
                    Debug.trace(e);
                }
            }
        }
        return index >= 0 ? potentialPolynomials[index] : null;
    }


    private double[][] createWarpPoints(TiePointGrid lonGrid, Rectangle subsetRect) {
        final TiePointGrid latGrid = getLatGrid();
        final int w = latGrid.getRasterWidth();
        final int sw = subsetRect.width;
        final int sh = subsetRect.height;
        final int i1 = subsetRect.x;
        final int i2 = i1 + sw - 1;
        final int j1 = subsetRect.y;
        final int j2 = j1 + sh - 1;

        Debug.trace("Selecting warp points for X/Y approximations");
        Debug.trace("  subset rectangle (in tie point coordinates): " + subsetRect);
        Debug.trace("  index i: " + i1 + " to " + i2);
        Debug.trace("  index j: " + j1 + " to " + j2);

        // Determine stepI and stepJ so that maximum number of warp points is not exceeded,
        // numU * numV shall be less than _MAX_NUM_POINTS_PER_TILE.
        //
        int numU = sw;
        int numV = sh;
        int stepI = 1;
        int stepJ = 1;

        // Adjust number of hor/ver (numU,numV) tie-points to be considered
        // so that a maximum of circa numPointsMax points is not exceeded
        boolean adjustStepI = true;
        while (numU * numV > MAX_NUM_POINTS_PER_TILE) {
            if (adjustStepI) {
                stepI++;
                numU = sw / stepI;
            } else {
                stepJ++;
                numV = sh / stepJ;
            }
            adjustStepI = !adjustStepI;
        }
        numU = Math.max(1, numU);
        numV = Math.max(1, numV);

        // Make sure we include the right border tie-points
        // if sw/stepI not divisible without remainder
        if (sw % stepI != 0) {
            numU++;
        }
        // Make sure we include the bottom border tie-points
        // if sh/stepJ not divisible without remainder
        if (sh % stepJ != 0) {
            numV++;
        }

        // Collect numU * numV warp points
        //
        final int m = numU * numV;
        final double[][] data = new double[m][4];
        float lat, lon, x, y;
        int i, j, k = 0;
        for (int v = 0; v < numV; v++) {
            j = j1 + v * stepJ;
            // Adjust bottom border
            if (j > j2) {
                j = j2;
            }
            for (int u = 0; u < numU; u++) {
                i = i1 + u * stepI;
                // Adjust right border
                if (i > i2) {
                    i = i2;
                }
                lat = latGrid.getRasterData().getElemFloatAt(j * w + i);
                lon = lonGrid.getRasterData().getElemFloatAt(j * w + i);
                x = latGrid.getOffsetX() + i * latGrid.getSubSamplingX();
                y = latGrid.getOffsetY() + j * latGrid.getSubSamplingY();
                data[k][0] = lat;
                data[k][1] = lon;
                data[k][2] = x;
                data[k][3] = y;
                k++;
            }
        }

        Debug.assertTrue(k == m);
        Debug.trace("TiePointGeoCoding: numU=" + numU + ", stepI=" + stepI);
        Debug.trace("TiePointGeoCoding: numV=" + numV + ", stepJ=" + stepJ);

        return data;
    }

    private Approximation createApproximation(TiePointGrid normalizedLonGrid, Rectangle subsetRect) {
        final double[][] data = createWarpPoints(normalizedLonGrid, subsetRect);

        float sumLat = 0.0f;
        float sumLon = 0.0f;
        for (final double[] point : data) {
            sumLat += point[0];
            sumLon += point[1];
        }
        float centerLon = sumLon / data.length;
        float centerLat = sumLat / data.length;
        final float maxSquareDistance = getMaxSquareDistance(data, centerLat, centerLon);

        for (int i = 0; i < data.length; i++) {
            data[i][0] = rescaleLatitude(data[i][0]);
            data[i][1] = rescaleLongitude(data[i][1], centerLon);
        }

        final int[] xIndices = new int[]{0, 1, 2};
        final int[] yIndices = new int[]{0, 1, 3};

        final FXYSum fX = getBestPolynomial(data, xIndices);
        final FXYSum fY = getBestPolynomial(data, yIndices);
        if (fX == null || fY == null) {
            return null;
        }

        final double rmseX = fX.getRootMeanSquareError();
        final double rmseY = fY.getRootMeanSquareError();

        final double maxErrorX = fX.getMaxError();
        final double maxErrorY = fY.getMaxError();

        Debug.trace(
                "TiePointGeoCoding: RMSE X      = " + rmseX + ", " + (rmseX < ABS_ERROR_LIMIT ? "OK" : "too large"));
        Debug.trace(
                "TiePointGeoCoding: RMSE Y      = " + rmseY + ", " + (rmseY < ABS_ERROR_LIMIT ? "OK" : "too large"));
        Debug.trace(
                "TiePointGeoCoding: Max.error X = " + maxErrorX + ", " + (maxErrorX < ABS_ERROR_LIMIT ? "OK" : "too large"));
        Debug.trace(
                "TiePointGeoCoding: Max.error Y = " + maxErrorY + ", " + (maxErrorY < ABS_ERROR_LIMIT ? "OK" : "too large"));

        return new Approximation(fX, fY, centerLat, centerLon, maxSquareDistance * 1.1f);
    }

    private static float getMaxSquareDistance(final double[][] data, float centerLat, float centerLon) {
        float maxSquareDistance = 0.0f;
        for (final double[] point : data) {
            final float dLat = (float) point[0] - centerLat;
            final float dLon = (float) point[1] - centerLon;
            final float squareDistance = dLat * dLat + dLon * dLon;
            if (squareDistance > maxSquareDistance) {
                maxSquareDistance = squareDistance;
            }
        }
        return maxSquareDistance;
    }

    private static Approximation getBestApproximation(final Approximation[] approximations, float lat, float lon) {
        Approximation approximation = null;
        if (approximations.length == 1) {
            Approximation a = approximations[0];
            final float squareDistance = a.getSquareDistance(lat, lon);
            if (squareDistance < a.getMinSquareDistance()) {
                approximation = a;
            }
        } else {
            float minSquareDistance = Float.MAX_VALUE;
            for (final Approximation a : approximations) {
                final float squareDistance = a.getSquareDistance(lat, lon);
                if (squareDistance < minSquareDistance && squareDistance < a.getMinSquareDistance()) {
                    minSquareDistance = squareDistance;
                    approximation = a;
                }
            }
        }

        return approximation;
    }

    private Approximation findRenormalizedApproximation(final float lat, final float renormalizedLon,
                                                        final float distance) {
        Approximation renormalizedApproximation = getBestApproximation(approximations, lat, renormalizedLon);
        if (renormalizedApproximation != null) {
            float renormalizedDistance = renormalizedApproximation.getSquareDistance(lat, renormalizedLon);
            if (renormalizedDistance < distance) {
                return renormalizedApproximation;
            }
        }
        return null;
    }

    float getNormalizedLonMin() {
        return normalizedLonMin;
    }


    private static double rescaleLongitude(double lon, double centerLon) {
        return (lon - centerLon) / 90.0;
    }

    private static double rescaleLatitude(double lat) {
        return lat / 90.0;
    }

    /**
     * Transfers the geo-coding of the {@link Scene srcScene} to the {@link Scene destScene} with respect to the given
     * {@link org.esa.beam.framework.dataio.ProductSubsetDef subsetDef}.
     *
     * @param srcScene  the source scene
     * @param destScene the destination scene
     * @param subsetDef the definition of the subset, may be <code>null</code
     *                  >
     *
     * @return true, if the geo-coding could be transferred.
     */
    @Override
    public boolean transferGeoCoding(final Scene srcScene, final Scene destScene, final ProductSubsetDef subsetDef) {
        final String latGridName = getLatGrid().getName();
        final String lonGridName = getLonGrid().getName();
        final Product destProduct = destScene.getProduct();
        TiePointGrid latGrid = destProduct.getTiePointGrid(latGridName);
        if (latGrid == null) {
            latGrid = TiePointGrid.createSubset(getLatGrid(), subsetDef);
            destProduct.addTiePointGrid(latGrid);
        }
        TiePointGrid lonGrid = destProduct.getTiePointGrid(lonGridName);
        if (lonGrid == null) {
            lonGrid = TiePointGrid.createSubset(getLonGrid(), subsetDef);
            destProduct.addTiePointGrid(lonGrid);
        }
        if (latGrid != null && lonGrid != null) {
            destScene.setGeoCoding(new TiePointGeoCoding(latGrid, lonGrid, getDatum()));
            return true;
        } else {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Inner Classes

    public static final class Approximation {

        private final FXYSum _fX;
        private final FXYSum _fY;
        private final float _centerLat;
        private final float _centerLon;
        private final float _minSquareDistance;

        public Approximation(FXYSum fX, FXYSum fY, float centerLat, float centerLon, float minSquareDistance) {
            _fX = fX;
            _fY = fY;
            _centerLat = centerLat;
            _centerLon = centerLon;
            _minSquareDistance = minSquareDistance;
        }

        public final FXYSum getFX() {
            return _fX;
        }

        public final FXYSum getFY() {
            return _fY;
        }

        public float getCenterLat() {
            return _centerLat;
        }

        public float getCenterLon() {
            return _centerLon;
        }

        public float getMinSquareDistance() {
            return _minSquareDistance;
        }

        /**
         * Computes the square distance to the given geographical coordinate.
         *
         * @param lat the latitude value
         * @param lon the longitude value
         *
         * @return the square distance
         */
        public final float getSquareDistance(float lat, float lon) {
            final float dx = lon - _centerLon;
            final float dy = lat - _centerLat;
            return dx * dx + dy * dy;
        }
    }
}
