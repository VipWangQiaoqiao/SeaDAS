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

import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.ProductUtils;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A geometric representation of a geographical grid measured in longitudes and latitudes.
 */
public class Graticule {

    private final GeneralPath[] _linePaths;
    private final TextGlyph[] _textGlyphsNorth;
    private final TextGlyph[] _textGlyphsSouth;
    private final TextGlyph[] _textGlyphsWest;
    private final TextGlyph[] _textGlyphsEast;
    private final TextGlyph[] _textGlyphsLatCorners;
    private final TextGlyph[] _textGlyphsLonCorners;

    public enum TextLocation {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public enum GridLineType {
        LAT,
        LON
    }

    public static int TOP_LEFT_CORNER_INDEX = 0;
    public static int TOP_RIGHT_CORNER_INDEX = 1;
    public static int BOTTOM_RIGHT_CORNER_INDEX = 2;
    public static int BOTTOM_LEFT_CORNER_INDEX = 3;


    private Graticule(GeneralPath[] paths,
                      TextGlyph[] textGlyphsNorth,
                      TextGlyph[] textGlyphsSouth,
                      TextGlyph[] textGlyphsWest,
                      TextGlyph[] textGlyphsEast,
                      TextGlyph[] textGlyphsLatCorners,
                      TextGlyph[] textGlyphsLonCorners
    ) {
        _linePaths = paths;
        _textGlyphsNorth = textGlyphsNorth;
        _textGlyphsSouth = textGlyphsSouth;
        _textGlyphsWest = textGlyphsWest;
        _textGlyphsEast = textGlyphsEast;
        _textGlyphsLatCorners = textGlyphsLatCorners;
        _textGlyphsLonCorners = textGlyphsLonCorners;
    }

    public GeneralPath[] getLinePaths() {
        return _linePaths;
    }


    public TextGlyph[] getTextGlyphsNorth() {
        return _textGlyphsNorth;
    }

    public TextGlyph[] getTextGlyphsSouth() {
        return _textGlyphsSouth;
    }

    public TextGlyph[] getTextGlyphsWest() {
        return _textGlyphsWest;
    }

    public TextGlyph[] getTextGlyphsEast() {
        return _textGlyphsEast;
    }

    public TextGlyph[] getTextGlyphsLatCorners() {
        return _textGlyphsLatCorners;
    }

    public TextGlyph[] getTextGlyphsLonCorners() {
        return _textGlyphsLonCorners;
    }



    /**
     * Creates a graticule for the given product.
     *
     * @param product              the product
     * @param autoDeterminingSteps if true, <code>gridCellSize</code> is used to compute <code>latMajorStep</code>, <code>lonMajorStep</code> for the given product
     * @param gridCellSize         the grid cell size in pixels, ignored if <code>autoDeterminingSteps</code> if false
     * @param latMajorStep         the grid cell size in meridional direction, ignored if <code>autoDeterminingSteps</code> if true
     * @param lonMajorStep         the grid cell size in parallel direction, ignored if <code>autoDeterminingSteps</code> if true
     * @return the graticule or null, if it could not be created
     */
//    public static Graticule create(Product product,
//                                   boolean autoDeterminingSteps,
//                                   int gridCellSize,
//                                   float latMajorStep,
//                                   float lonMajorStep) {
//        Guardian.assertNotNull("product", product);
//        final GeoCoding geoCoding = product.getGeoCoding();
//        if (geoCoding == null || product.getSceneRasterWidth() < 16 || product.getSceneRasterHeight() < 16) {
//            return null;
//        }
//
//        if (autoDeterminingSteps) {
//            final PixelPos pixelPos1 = new PixelPos(0.5f * product.getSceneRasterWidth(), 0.5f * product.getSceneRasterHeight());
//            final PixelPos pixelPos2 = new PixelPos(pixelPos1.x + 1f, pixelPos1.y + 1f);
//            final GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
//            final GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
//            double deltaLat = Math.abs(geoPos2.lat - geoPos1.lat);
//            double deltaLon = Math.abs(geoPos2.lon - geoPos1.lon);
//            if (deltaLon > 180) {
//                deltaLon += 360;
//            }
//// todo Danny adding new code for the raster version of this below but only in part here
//            // is this code being used?
//            //
//
//            int height = product.getSceneRasterHeight();
//            int width = product.getSceneRasterWidth();
//            int min = width;
//
//            if (height < min) {
//                min = height;
//            }
//
//            double ratio = min / 4.0;
//            gridCellSize = (int) Math.floor(ratio);
//            Debug.trace("Graticule.create: deltaLat=" + deltaLat + ", deltaLon=" + deltaLon);
//            latMajorStep = (float) compose(normalize(gridCellSize * 0.5 * (deltaLon + deltaLat), null));
//            lonMajorStep = latMajorStep;
//        }
//        Debug.trace("Graticule.create: latMajorStep=" + latMajorStep + ", lonMajorStep=" + lonMajorStep);
//
//        float latMinorStep = latMajorStep / 4.0f;
//        float lonMinorStep = lonMajorStep / 4.0f;
//
//        int geoBoundaryStep = getGeoBoundaryStep(geoCoding);
//        Debug.trace("Graticule.create: geoBoundaryStep=" + geoBoundaryStep);
//        final GeoPos[] geoBoundary = ProductUtils.createGeoBoundary(product, null, geoBoundaryStep);
//        ProductUtils.normalizeGeoPolygon(geoBoundary);
//
//// nf Debugging, don't delete!
////        GeneralPath generalPath = createPixelBoundaryPath(geoCoding, geoBoundary);
////        if (generalPath != null) {
////            return new Graticule(new GeneralPath[]{generalPath}, null);
////        }
//
//        double xMin = +1.0e10;
//        double yMin = +1.0e10;
//        double xMax = -1.0e10;
//        double yMax = -1.0e10;
//        for (GeoPos geoPos : geoBoundary) {
//            xMin = Math.min(xMin, geoPos.lon);
//            yMin = Math.min(yMin, geoPos.lat);
//            xMax = Math.max(xMax, geoPos.lon);
//            yMax = Math.max(yMax, geoPos.lat);
//        }
//
//
//        final List<List<Coord>> parallelList = computeParallelList(product.getGeoCoding(), geoBoundary, latMajorStep, lonMinorStep, yMin, yMax);
//        final List<List<Coord>> meridianList = computeMeridianList(product.getGeoCoding(), geoBoundary, lonMajorStep, latMinorStep, xMin, xMax);
//        final GeneralPath[] paths = createPaths(parallelList, meridianList);
//
//
//        final TextGlyph[] textGlyphsNorth = createTextGlyphs(parallelList, meridianList, TextLocation.NORTH, null, false, false);
//        final TextGlyph[] textGlyphsSouth = createTextGlyphs(parallelList, meridianList, TextLocation.SOUTH, null, false, false);
//        final TextGlyph[] textGlyphsWest = createTextGlyphs(parallelList, meridianList, TextLocation.WEST, null, false, false);
//        final TextGlyph[] textGlyphsEast = createTextGlyphs(parallelList, meridianList, TextLocation.EAST, null, false, false);
//
//
//        return new Graticule(paths, textGlyphsNorth, textGlyphsSouth, textGlyphsWest, textGlyphsEast, textGlyphsLatCorners, textGlyphsLonCorners);
//
//    }

    /**
     * Creates a graticule for the given product.
     *
     * @param raster               the product
     * @param autoDeterminingSteps if true, <code>gridCellSize</code> is used to compute <code>latMajorStep</code>, <code>lonMajorStep</code> for the given product
     * @param minDivisions         the grid cell size in pixels, ignored if <code>autoDeterminingSteps</code> if false
     * @param latMajorStep         the grid cell size in meridional direction, ignored if <code>autoDeterminingSteps</code> if true
     * @param lonMajorStep         the grid cell size in parallel direction, ignored if <code>autoDeterminingSteps</code> if true
     * @return the graticule or null, if it could not be created
     */
    public static Graticule create(RasterDataNode raster,
                                   boolean autoDeterminingSteps,
                                   int minDivisions,
                                   float latMajorStep,
                                   float lonMajorStep,
                                   boolean includeWestLonBorderText,
                                   boolean includeEastLonBorderText,
                                   boolean includeNorthLatBorderText,
                                   boolean includeSouthLatBorderText) {

        int gridCellSize = 0;
        if (minDivisions <= 1) {
            minDivisions = 2;
        }

        Guardian.assertNotNull("product", raster);
        final GeoCoding geoCoding = raster.getGeoCoding();
        if (geoCoding == null || raster.getSceneRasterWidth() < 16 || raster.getSceneRasterHeight() < 16) {
            return null;
        }

      //  if (autoDeterminingSteps || (latMajorStep == 0 && lonMajorStep == 0)) {
        if (latMajorStep == 0 && lonMajorStep == 0) {
            final PixelPos pixelPos1 = new PixelPos(0.5f * raster.getSceneRasterWidth(), 0.5f * raster.getSceneRasterHeight());
            final PixelPos pixelPos2 = new PixelPos(pixelPos1.x + 1f, pixelPos1.y + 1f);
            final GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
            final GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
            double deltaLat = Math.abs(geoPos2.lat - geoPos1.lat);
            double deltaLon = Math.abs(geoPos2.lon - geoPos1.lon);
            if (deltaLon > 180) {
                deltaLon += 360;
            }

            int height = raster.getRasterHeight();
            int width = raster.getRasterWidth();
            int min = width;
            double minDegrees = deltaLon * width;

            if (height < min) {
                min = height;
                minDegrees = deltaLat * height;
            }

            double ratio = min / (minDivisions - 1);
            gridCellSize = (int) Math.floor(ratio);
            Debug.trace("Graticule.create: deltaLat=" + deltaLat + ", deltaLon=" + deltaLon);
            // this is what BEAM had
            // it has some cool behaviour but is a bit rigid when adjusted desireed gridline count
            //     latMajorStep = (float) compose(normalize(gridCellSize * 0.5 * (deltaLon + deltaLat), null));

            double tmpLatMajorStep = gridCellSize * 0.5 * (deltaLon + deltaLat);

            // if each division is greater than 5 degrees then round to nearest 5 degrees
            if (tmpLatMajorStep > 5) {
                tmpLatMajorStep = 5 * Math.round((tmpLatMajorStep / 5));
            } else {
                tmpLatMajorStep = Math.round(tmpLatMajorStep);
            }

            latMajorStep = (float) tmpLatMajorStep;
            lonMajorStep = latMajorStep;
        }

        if (latMajorStep == 0) {
            final PixelPos pixelPos1 = new PixelPos(0.5f * raster.getSceneRasterWidth(), 0.5f * raster.getSceneRasterHeight());
            final PixelPos pixelPos2 = new PixelPos(pixelPos1.x + 1f, pixelPos1.y + 1f);
            final GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
            final GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
            double deltaLat = Math.abs(geoPos2.lat - geoPos1.lat);

            int height = raster.getRasterHeight();
            int width = raster.getRasterWidth();

            double ratio = height / (minDivisions - 1);
            gridCellSize = (int) Math.floor(ratio);


            double tmpLatMajorStep = gridCellSize *  deltaLat;

            // if each division is greater than 5 degrees then round to nearest 5 degrees
            if (tmpLatMajorStep > 5) {
                tmpLatMajorStep = 5 * Math.round((tmpLatMajorStep / 5));
            } else {
                tmpLatMajorStep = Math.round(tmpLatMajorStep);
            }

            latMajorStep = (float) tmpLatMajorStep;
        }


        if (lonMajorStep == 0) {
            final PixelPos pixelPos1 = new PixelPos(0.5f * raster.getSceneRasterWidth(), 0.5f * raster.getSceneRasterHeight());
            final PixelPos pixelPos2 = new PixelPos(pixelPos1.x + 1f, pixelPos1.y + 1f);
            final GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
            final GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
            double deltaLon = Math.abs(geoPos2.lon - geoPos1.lon);
            if (deltaLon > 180) {
                deltaLon += 360;
            }

            int width = raster.getRasterWidth();

            double ratio = width / (minDivisions - 1);
            gridCellSize = (int) Math.floor(ratio);


            double tmpLonMajorStep = gridCellSize *  deltaLon;

            // if each division is greater than 5 degrees then round to nearest 5 degrees
            if (tmpLonMajorStep > 5) {
                tmpLonMajorStep = 5 * Math.round((tmpLonMajorStep / 5));
            } else {
                tmpLonMajorStep = Math.round(tmpLonMajorStep);
            }

            lonMajorStep = (float) tmpLonMajorStep;
        }




        if (latMajorStep == 0) {
            latMajorStep = (float) 10;
        }

        if (lonMajorStep == 0) {
            lonMajorStep = (float) 10;
        }
        Debug.trace("Graticule.create: latMajorStep=" + latMajorStep + ", lonMajorStep=" + lonMajorStep);

        float latMinorStep = latMajorStep / 4.0f;
        float lonMinorStep = lonMajorStep / 4.0f;

        int geoBoundaryStep = getGeoBoundaryStep(geoCoding);
        Debug.trace("Graticule.create: geoBoundaryStep=" + geoBoundaryStep);
        final GeoPos[] geoBoundary = ProductUtils.createGeoBoundary(raster, null, geoBoundaryStep);
        ProductUtils.normalizeGeoPolygon(geoBoundary);

// nf Debugging, don't delete!
//        GeneralPath generalPath = createPixelBoundaryPath(geoCoding, geoBoundary);
//        if (generalPath != null) {
//            return new Graticule(new GeneralPath[]{generalPath}, null);
//        }

        double xMin = +1.0e10;
        double yMin = +1.0e10;
        double xMax = -1.0e10;
        double yMax = -1.0e10;
        for (GeoPos geoPos : geoBoundary) {
            xMin = Math.min(xMin, geoPos.lon);
            yMin = Math.min(yMin, geoPos.lat);
            xMax = Math.max(xMax, geoPos.lon);
            yMax = Math.max(yMax, geoPos.lat);
        }


        final List<List<Coord>> parallelList = computeParallelList(raster.getGeoCoding(), geoBoundary, latMajorStep, lonMinorStep, yMin, yMax);

        final List<List<Coord>> meridianList = computeMeridianList(raster.getGeoCoding(), geoBoundary, lonMajorStep, latMinorStep, xMin, xMax);
        final GeneralPath[] paths = createPaths(parallelList, meridianList);


        final TextGlyph[] textGlyphsNorth = createTextGlyphs(parallelList, meridianList, TextLocation.NORTH, raster, includeWestLonBorderText, includeEastLonBorderText);
        final TextGlyph[] textGlyphsSouth = createTextGlyphs(parallelList, meridianList, TextLocation.SOUTH, raster, includeWestLonBorderText, includeEastLonBorderText);

        final TextGlyph[] textGlyphsWest = createTextGlyphs(parallelList, meridianList, TextLocation.WEST, raster, includeNorthLatBorderText, includeSouthLatBorderText);
        final TextGlyph[] textGlyphsEast = createTextGlyphs(parallelList, meridianList, TextLocation.EAST, raster, includeNorthLatBorderText, includeSouthLatBorderText);

        final TextGlyph[] textGlyphsLatCorners = createLatCornerTextGlyphs(raster);
        final TextGlyph[] textGlyphsLonCorners = createLonCornerTextGlyphs(raster);

        return new Graticule(paths, textGlyphsNorth, textGlyphsSouth, textGlyphsWest, textGlyphsEast, textGlyphsLatCorners, textGlyphsLonCorners);
    }

    private static int getGeoBoundaryStep(final GeoCoding geoCoding) {
        int step = 16;
        if (geoCoding instanceof TiePointGeoCoding) {
            final TiePointGeoCoding tiePointGeoCoding = (TiePointGeoCoding) geoCoding;
            step = Math.round(Math.min(tiePointGeoCoding.getLonGrid().getSubSamplingX(), tiePointGeoCoding.getLonGrid().getSubSamplingY()));
        }
        return step;
    }

    private static List<List<Coord>> computeParallelList(final GeoCoding geoCoding,
                                                         final GeoPos[] geoBoundary,
                                                         final double latMajorStep,
                                                         final double lonMinorStep,
                                                         final double yMin,
                                                         final double yMax) {
//        final GeoCoding geoCoding = product.getGeoCoding();
        List<List<Coord>> parallelList = new ArrayList<List<Coord>>();
        ArrayList<GeoPos> intersectionList = new ArrayList<GeoPos>();
        GeoPos geoPos, int1, int2;
        PixelPos pixelPos;
        float lat, lon;
        double my = latMajorStep * Math.floor(yMin / latMajorStep);
        for (; my <= yMax; my += latMajorStep) {
            intersectionList.clear();
            computeParallelIntersections(geoBoundary, my, intersectionList);
            if (intersectionList.size() > 0 && intersectionList.size() % 2 == 0) {
                final GeoPos[] intersections = intersectionList.toArray(new GeoPos[intersectionList.size()]);
                Arrays.sort(intersections, new GeoPosLonComparator());
                List<Coord> parallel = new ArrayList<Coord>();
                // loop forward order
                for (int i = 0; i < intersections.length; i += 2) {
                    int1 = intersections[i];
                    int2 = intersections[i + 1];
                    lat = int1.lat;
                    lon = int1.lon;
                    for (int k = 0; k <= 1; ) {
                        geoPos = new GeoPos(lat, limitLon(lon));
                        pixelPos = geoCoding.getPixelPos(geoPos, null);
                        // DANNY added this to avoid adding in null pixels
                        if (!Double.isNaN(pixelPos.getX()) && !Double.isNaN(pixelPos.getY())) {
                            parallel.add(new Coord(geoPos, pixelPos));
                        }
                        lon += lonMinorStep;
                        if (lon >= int2.lon) {
                            lon = int2.lon;
                            k++;
                        }
                    }
                }
                parallelList.add(parallel);
            }
        }
        return parallelList;
    }


    private static List<List<Coord>> computeMeridianList(final GeoCoding geoCoding,
                                                         final GeoPos[] geoBoundary,
                                                         final double lonMajorStep,
                                                         final double latMinorStep,
                                                         final double xMin,
                                                         final double xMax) {
//        final GeoCoding geoCoding = product.getGeoCoding();
        List<List<Coord>> meridianList = new ArrayList<List<Coord>>();
        List<GeoPos> intersectionList = new ArrayList<GeoPos>();
        GeoPos geoPos, int1, int2;
        PixelPos pixelPos;
        float lat, lon;
        double mx = lonMajorStep * Math.floor(xMin / lonMajorStep);
        for (; mx <= xMax; mx += lonMajorStep) {
            intersectionList.clear();
            computeMeridianIntersections(geoBoundary, mx, intersectionList);
            if (intersectionList.size() > 0 && intersectionList.size() % 2 == 0) {
                final GeoPos[] intersections = intersectionList.toArray(new GeoPos[intersectionList.size()]);
                Arrays.sort(intersections, new GeoPosLatComparator());
                List<Coord> meridian = new ArrayList<Coord>();
                // loop reverse order
                for (int i = intersections.length - 2; i >= 0; i -= 2) {
                    int1 = intersections[i + 1];
                    int2 = intersections[i];
                    lat = int1.lat;
                    lon = int1.lon;
                    for (int k = 0; k <= 1; ) {
                        geoPos = new GeoPos(lat, limitLon(lon));
                        pixelPos = geoCoding.getPixelPos(geoPos, null);

                        // DANNY added this to avoid adding in null pixels
                        if (!Double.isNaN(pixelPos.getX()) && !Double.isNaN(pixelPos.getY())) {
                            meridian.add(new Coord(geoPos, pixelPos));
                        }
                        lat -= latMinorStep;
                        if (lat <= int2.lat) {
                            lat = int2.lat;
                            k++;
                        }
                    }
                }
                meridianList.add(meridian);
            }
        }
        return meridianList;
    }

    private static void computeParallelIntersections(final GeoPos[] geoBoundary,
                                                     final double my,
                                                     final List<GeoPos> intersectionList) {
        double p0x = 0, p0y = 0;
        double p1x, p1y;
        double pa;
        double mx;
        for (int i = 0; i < geoBoundary.length; i++) {
            GeoPos geoPos = geoBoundary[i];
            p1x = geoPos.lon;
            p1y = geoPos.lat;
            if (i > 0) {
                if (((my >= p0y && my <= p1y) || (my >= p1y && my <= p0y)) &&
                        (p1y - p0y != 0.0)) {
                    pa = (my - p0y) / (p1y - p0y);
                    if (pa >= 0.0 && pa < 1.0) {
                        mx = p0x + pa * (p1x - p0x);
                        intersectionList.add(new GeoPos((float) my, (float) mx));
                    }
                }
            }
            p0x = p1x;
            p0y = p1y;
        }
    }

    private static void computeMeridianIntersections(final GeoPos[] geoBoundary,
                                                     final double mx,
                                                     final List<GeoPos> intersectionList) {
        double p0x = 0, p0y = 0;
        double p1x, p1y;
        double pa;
        double my;
        for (int i = 0; i < geoBoundary.length; i++) {
            GeoPos geoPos = geoBoundary[i];
            p1x = geoPos.lon;
            p1y = geoPos.lat;
            if (i > 0) {
                if (((mx >= p0x && mx <= p1x) || (mx >= p1x && mx <= p0x)) &&
                        (p1x - p0x != 0.0)) {
                    pa = (mx - p0x) / (p1x - p0x);
                    if (pa >= 0.0 && pa < 1.0) {
                        my = p0y + pa * (p1y - p0y);
                        intersectionList.add(new GeoPos((float) my, (float) mx));
                    }
                }
            }
            p0x = p1x;
            p0y = p1y;
        }
    }

    private static GeneralPath[] createPaths(List<List<Coord>> parallelList, List<List<Coord>> meridianList) {
        final ArrayList<GeneralPath> generalPathList = new ArrayList<GeneralPath>();
        addToPath(parallelList, generalPathList);
        addToPath(meridianList, generalPathList);
        return generalPathList.toArray(new GeneralPath[generalPathList.size()]);
    }


    private static void addToPath(List<List<Coord>> lineList, List<GeneralPath> generalPathList) {
        for (final List<Coord> coordList : lineList) {
            if (coordList.size() >= 2) {
                final GeneralPath generalPath = new GeneralPath();
                boolean restart = true;
                for (Coord coord : coordList) {
                    PixelPos pixelPos = coord.pixelPos;
                    if (pixelPos.isValid()) {
                        if (restart) {
                            generalPath.moveTo(pixelPos.x, pixelPos.y);
                        } else {
                            generalPath.lineTo(pixelPos.x, pixelPos.y);
                        }
                        restart = false;
                    } else {
                        restart = true;
                    }
                }
                generalPathList.add(generalPath);
            }
        }
    }

    private static TextGlyph[] createTextGlyphs(List<List<Coord>> parallelList, List<List<Coord>> meridianList) {
        final List<TextGlyph> textGlyphList = new ArrayList<TextGlyph>();
        createWesternLatitudeTextGlyphs(parallelList, textGlyphList);
        createNorthernLongitudeTextGlyphs(meridianList, textGlyphList);
        return textGlyphList.toArray(new TextGlyph[textGlyphList.size()]);
    }


    public static TextGlyph getBorderGlyphNorthWestCornerLat(RasterDataNode raster) {

        GeoPos geoPos = null;

        PixelPos pixelPos = new PixelPos(0, 0);
        raster.getGeoCoding().getGeoPos(pixelPos, geoPos);
        Coord coord1 = new Coord(geoPos, pixelPos);

        pixelPos = new PixelPos(1, 0);
        raster.getGeoCoding().getGeoPos(pixelPos, geoPos);
        Coord coord2 = new Coord(geoPos, pixelPos);


        TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLatString(), coord1, coord2);

        return textGlyph;

    }


    private static TextGlyph[] createLonCornerTextGlyphs(RasterDataNode raster) {
        final TextGlyph[] textGlyphs;
        textGlyphs = new TextGlyph[4];

        GeoCoding geoCoding = raster.getGeoCoding();

        PixelPos pixelPos1;
        PixelPos pixelPos2;

        if (geoCoding != null && raster.getSceneRasterHeight() >= 2 && raster.getSceneRasterWidth() >= 2) {
            pixelPos1 = new PixelPos(0, 0);
            pixelPos2 = new PixelPos(0, 1);
            textGlyphs[TOP_LEFT_CORNER_INDEX] = getLonCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);

            pixelPos1 = new PixelPos(raster.getRasterWidth(), 0);
            pixelPos2 = new PixelPos(raster.getRasterWidth(), 1);
            textGlyphs[TOP_RIGHT_CORNER_INDEX] = getLonCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);

            pixelPos1 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight());
            pixelPos2 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight() - 1);
            textGlyphs[BOTTOM_RIGHT_CORNER_INDEX] = getLonCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);

            pixelPos1 = new PixelPos(0, raster.getRasterHeight());
            pixelPos2 = new PixelPos(0, raster.getRasterHeight() - 1);
            textGlyphs[BOTTOM_LEFT_CORNER_INDEX] = getLonCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);
        }

        return textGlyphs;
    }


    private static TextGlyph[] createLatCornerTextGlyphs(RasterDataNode raster) {
        final TextGlyph[] textGlyphs;
        textGlyphs = new TextGlyph[4];

        GeoCoding geoCoding = raster.getGeoCoding();

        PixelPos pixelPos1;
        PixelPos pixelPos2;

        if (geoCoding != null && raster.getSceneRasterHeight() >= 2 && raster.getSceneRasterWidth() >= 2) {
            pixelPos1 = new PixelPos(0, 0);
            pixelPos2 = new PixelPos(1, 0);
            textGlyphs[TOP_LEFT_CORNER_INDEX] = getLatCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);

            pixelPos1 = new PixelPos(raster.getRasterWidth(), 0);
            pixelPos2 = new PixelPos(raster.getRasterWidth() - 1, 0);
            textGlyphs[TOP_RIGHT_CORNER_INDEX] = getLatCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);

            pixelPos1 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight());
            pixelPos2 = new PixelPos(raster.getRasterWidth() - 1, raster.getRasterHeight());
            textGlyphs[BOTTOM_RIGHT_CORNER_INDEX] = getLatCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);

            pixelPos1 = new PixelPos(0, raster.getRasterHeight());
            pixelPos2 = new PixelPos(1, raster.getRasterHeight());
            textGlyphs[BOTTOM_LEFT_CORNER_INDEX] = getLatCornerTextGlyph(geoCoding, pixelPos1, pixelPos2);
        }

        return textGlyphs;
    }


    private static TextGlyph[] createTextGlyphs(List<List<Coord>> latitudeGridLinePoints, List<List<Coord>> longitudeGridLinePoints,
                                                TextLocation textLocation, RasterDataNode raster, boolean border1, boolean border2) {
        final List<TextGlyph> textGlyphs = new ArrayList<TextGlyph>();

        switch (textLocation) {
            case NORTH:
                createNorthernLongitudeTextGlyphs(longitudeGridLinePoints, textGlyphs);
                //           addBorderNorthernLongitudeTextGlyphs(raster, textGlyphs, border1, border2);
                break;
            case SOUTH:
                createSouthernLongitudeTextGlyphs(longitudeGridLinePoints, textGlyphs);
                //           addBorderSouthernLongitudeTextGlyphs(raster, textGlyphs, border1, border2);
                break;
            case WEST:
                createWesternLatitudeTextGlyphs(latitudeGridLinePoints, textGlyphs);
                //           addBorderWesternLatitudeTextGlyphs(raster, textGlyphs, border1, border2);
                break;
            case EAST:
                createEasternLatitudeTextGlyphs(latitudeGridLinePoints, textGlyphs);
                //           addBorderEasternLatitudeTextGlyphs(raster, textGlyphs, border1, border2);
                break;
        }

        return textGlyphs.toArray(new TextGlyph[textGlyphs.size()]);
    }


    private static void createWesternLatitudeTextGlyphs(List<List<Coord>> latitudeGridLinePoints,
                                                        List<TextGlyph> textGlyphs) {

        // Assumes that the line was drawn from west to east
        // coord1 set to first point in order to anchor the text to the edge of the line
        for (final List<Coord> latitudeGridLinePoint : latitudeGridLinePoints) {

            if (latitudeGridLinePoint.size() >= 2) {

                int first = 0;
                int second = 1;

                Coord coord1 = latitudeGridLinePoint.get(first);
                Coord coord2 = latitudeGridLinePoint.get(second);

                if (isCoordPairValid(coord1, coord2)) {
                    TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLatString(), coord1, coord2);
                    textGlyphs.add(textGlyph);
                }
            }
        }
    }


    private static void createEasternLatitudeTextGlyphs(List<List<Coord>> latitudeGridLinePoints,
                                                        List<TextGlyph> textGlyphs) {

        // Assumes that the line was drawn from west to east
        // coord1 set to last point in order to anchor the text to the edge of the line
        // text will point backwards due to this so it will subsequently need to be rotated
        for (final List<Coord> latitudeGridLinePoint : latitudeGridLinePoints) {
            if (latitudeGridLinePoint.size() >= 2) {

                int last = latitudeGridLinePoint.size() - 1;
                int nextToLast = last - 1;

                Coord coord1 = latitudeGridLinePoint.get(last);
                Coord coord2 = latitudeGridLinePoint.get(nextToLast);

                if (isCoordPairValid(coord1, coord2)) {
                    TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLatString(), coord1, coord2);
                    textGlyphs.add(textGlyph);
                }
            }
        }
    }


    private static void createNorthernLongitudeTextGlyphs(List<List<Coord>> longitudeGridLinePoints,
                                                          List<TextGlyph> textGlyphs) {

        // Assumes that the line was drawn from north to south
        // coord1 set to first point in order to anchor the text to the edge of the line
        for (List<Coord> longitudeGridLinePoint : longitudeGridLinePoints) {

            if (longitudeGridLinePoint.size() >= 2) {
                int first = 0;
                int second = 1;

                Coord coord1 = longitudeGridLinePoint.get(first);
                Coord coord2 = longitudeGridLinePoint.get(second);


                if (isCoordPairValid(coord1, coord2)) {
                    TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLonString(), coord1, coord2);
                    textGlyphs.add(textGlyph);
                }
            }
        }


    }


//    private static void addBorderNorthernLongitudeTextGlyphs(RasterDataNode raster,
//                                                             List<TextGlyph> textGlyphs, boolean border1, boolean border2) {
//        if (border1) {
//            PixelPos pixelPos1 = new PixelPos(0, 0);
//            PixelPos pixelPos2 = new PixelPos(0, 1);
//            addBorderLonTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//
//        if (border2) {
//            PixelPos pixelPos1 = new PixelPos(raster.getRasterWidth(), 0);
//            PixelPos pixelPos2 = new PixelPos(raster.getRasterWidth(), 1);
//            addBorderLonTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//    }
//
//    private static void addBorderSouthernLongitudeTextGlyphs(RasterDataNode raster,
//                                                             List<TextGlyph> textGlyphs, boolean border1, boolean border2) {
//        if (border1) {
//            PixelPos pixelPos1 = new PixelPos(0, raster.getRasterHeight());
//            PixelPos pixelPos2 = new PixelPos(0, raster.getRasterHeight() - 1);
//            addBorderLonTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//
//        if (border2) {
//            PixelPos pixelPos1 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight());
//            PixelPos pixelPos2 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight() - 1);
//            addBorderLonTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//    }

//    private static void addBorderWesternLatitudeTextGlyphs(RasterDataNode raster,
//                                                           List<TextGlyph> textGlyphs, boolean border1, boolean border2) {
//        if (border1) {
//            PixelPos pixelPos1 = new PixelPos(0, 0);
//            PixelPos pixelPos2 = new PixelPos(1, 0);
//            addBorderLatTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//
//        if (border2) {
//            PixelPos pixelPos1 = new PixelPos(0, raster.getRasterHeight());
//            PixelPos pixelPos2 = new PixelPos(1, raster.getRasterHeight());
//            addBorderLatTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//    }
//
//
//    private static void addBorderEasternLatitudeTextGlyphs(RasterDataNode raster,
//                                                           List<TextGlyph> textGlyphs, boolean border1, boolean border2) {
//        if (border1) {
//            PixelPos pixelPos1 = new PixelPos(raster.getRasterWidth(), 0);
//            PixelPos pixelPos2 = new PixelPos(raster.getRasterWidth() - 1, 0);
//            addBorderLatTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//
//        if (border2) {
//            PixelPos pixelPos1 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight());
//            PixelPos pixelPos2 = new PixelPos(raster.getRasterWidth() - 1, raster.getRasterHeight());
//            addBorderLatTextGlyphs(raster, textGlyphs, pixelPos1, pixelPos2);
//        }
//    }

//    private static void addBorderLatTextGlyphs(RasterDataNode raster, List<TextGlyph> textGlyphs, PixelPos pixelPos1, PixelPos pixelPos2) {
//
//        GeoCoding geoCoding = raster.getGeoCoding();
//
//        if (geoCoding != null && raster.getSceneRasterHeight() >= 2 && raster.getSceneRasterWidth() >= 2) {
//
//            GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
//            Coord coord1 = new Coord(geoPos1, pixelPos1);
//
//            GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
//            Coord coord2 = new Coord(geoPos2, pixelPos2);
//
//            if (isCoordPairValid(coord1, coord2)) {
//                TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLatString(), coord1, coord2);
//                textGlyphs.add(textGlyph);
//            }
//        }
//    }

    private static TextGlyph getLonCornerTextGlyph(GeoCoding geoCoding, PixelPos pixelPos1, PixelPos pixelPos2) {

        if (geoCoding != null) {
            GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
            Coord coord1 = new Coord(geoPos1, pixelPos1);

            GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
            Coord coord2 = new Coord(geoPos2, pixelPos2);

            if (isCoordPairValid(coord1, coord2)) {
                TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLonString(), coord1, coord2);
                return textGlyph;
            }
        }

        return null;
    }

    private static TextGlyph getLatCornerTextGlyph(GeoCoding geoCoding, PixelPos pixelPos1, PixelPos pixelPos2) {

        if (geoCoding != null) {
            GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
            Coord coord1 = new Coord(geoPos1, pixelPos1);

            GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
            Coord coord2 = new Coord(geoPos2, pixelPos2);

            if (isCoordPairValid(coord1, coord2)) {
                TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLatString(), coord1, coord2);
                return textGlyph;
            }
        }

        return null;
    }


//    private static void addBorderLonTextGlyphs(RasterDataNode raster, List<TextGlyph> textGlyphs, PixelPos pixelPos1, PixelPos pixelPos2) {
//
//        GeoCoding geoCoding = raster.getGeoCoding();
//
//        if (geoCoding != null && raster.getSceneRasterHeight() >= 2 && raster.getSceneRasterWidth() >= 2) {
//
//            GeoPos geoPos1 = geoCoding.getGeoPos(pixelPos1, null);
//            Coord coord1 = new Coord(geoPos1, pixelPos1);
//
//            GeoPos geoPos2 = geoCoding.getGeoPos(pixelPos2, null);
//            Coord coord2 = new Coord(geoPos2, pixelPos2);
//
//            if (isCoordPairValid(coord1, coord2)) {
//                TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLonString(), coord1, coord2);
//                textGlyphs.add(textGlyph);
//            }
//        }
//    }

    private static void createSouthernLongitudeTextGlyphs(List<List<Coord>> longitudeGridLinePoints,
                                                          List<TextGlyph> textGlyphs) {

        // Assumes that the line was drawn from north to south
        // coord1 set to last point in order to anchor the text to the edge of the line
        // text will point upwards due to this so it may be subsequently rotated if desired
        for (List<Coord> longitudeGridLinePoint : longitudeGridLinePoints) {

            if (longitudeGridLinePoint.size() >= 2) {
                int last = longitudeGridLinePoint.size() - 1;
                int nextToLast = last - 1;

                Coord coord1 = longitudeGridLinePoint.get(last);
                Coord coord2 = longitudeGridLinePoint.get(nextToLast);

                if (isCoordPairValid(coord1, coord2)) {
                    TextGlyph textGlyph = createTextGlyph(coord1.geoPos.getLonString(), coord1, coord2);
                    textGlyphs.add(textGlyph);
                }
            }
        }
    }


    private static boolean isCoordPairValid(Coord coord1, Coord coord2) {
        return coord1.pixelPos.isValid() && coord2.pixelPos.isValid();
    }

    private static TextGlyph createLatTextGlyph(Coord coord1, Coord coord2) {
        return createTextGlyph(coord1.geoPos.getLatString(), coord1, coord2);
    }

    private static TextGlyph createLonTextGlyph(Coord coord1, Coord coord2) {
        return createTextGlyph(coord1.geoPos.getLonString(), coord1, coord2);
    }

    private static TextGlyph createTextGlyph(String text, Coord coord1, Coord coord2) {
        final float angle = (float) Math.atan2(coord2.pixelPos.y - coord1.pixelPos.y,
                coord2.pixelPos.x - coord1.pixelPos.x);
        return new TextGlyph(text, coord1.pixelPos.x, coord1.pixelPos.y, angle);
    }

    private static float limitLon(float lon) {
        while (lon < -180f) {
            lon += 360f;
        }
        while (lon > 180f) {
            lon -= 360f;
        }
        return lon;
    }

    private static double[] normalize(double x, double[] result) {
        final double exponent = (x == 0.0) ? 0.0 : Math.ceil(Math.log(Math.abs(x)) / Math.log(10.0));
        final double mantissa = (x == 0.0) ? 0.0 : x / Math.pow(10.0, exponent);
        if (result == null) {
            result = new double[2];
        }
        result[0] = mantissa;
        result[1] = exponent;
        return result;
    }

    private static double compose(final double[] components) {
        final double mantissa = components[0];
        final double exponent = components[1];
        final double mantissaRounded;
        if (mantissa < 0.15) {
            mantissaRounded = 0.1;
        } else if (mantissa < 0.225) {
            mantissaRounded = 0.2;
        } else if (mantissa < 0.375) {
            mantissaRounded = 0.25;
        } else if (mantissa < 0.75) {
            mantissaRounded = 0.5;
        } else {
            mantissaRounded = 1.0;
        }
        return mantissaRounded * Math.pow(10.0, exponent);
    }

    /**
     * Not used, but useful for debugging: DON'T delete this method!
     *
     * @param geoCoding   The geo-coding
     * @param geoBoundary The geo-boundary
     * @return the geo-boundary
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private static GeneralPath createPixelBoundaryPath(final GeoCoding geoCoding, final GeoPos[] geoBoundary) {
        final GeneralPath generalPath = new GeneralPath();
        boolean restart = true;
        for (final GeoPos geoPos : geoBoundary) {
            geoPos.lon = limitLon(geoPos.lon);
            final PixelPos pixelPos = geoCoding.getPixelPos(geoPos, null);
            if (pixelPos.isValid()) {
                if (restart) {
                    generalPath.moveTo(pixelPos.x, pixelPos.y);
                } else {
                    generalPath.lineTo(pixelPos.x, pixelPos.y);
                }
                restart = false;
            } else {
                restart = true;
            }
        }
        return generalPath;
    }

    public static class TextGlyph {

        private final String text;
        private final float x;
        private final float y;
        private final float angle;

        public TextGlyph(String text, float x, float y, float angle) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.angle = angle;
        }

        public String getText() {
            return text;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getAngle() {
            return angle;
        }
    }

    private static class Coord {
        GeoPos geoPos;
        PixelPos pixelPos;

        public Coord(GeoPos geoPos, PixelPos pixelPos) {
            this.geoPos = geoPos;
            this.pixelPos = pixelPos;
        }
    }

    private static class GeoPosLatComparator implements Comparator<GeoPos> {
        @Override
        public int compare(GeoPos geoPos1, GeoPos geoPos2) {
            final float delta = geoPos1.lat - geoPos2.lat;
            if (delta < 0f) {
                return -1;
            } else if (delta > 0f) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private static class GeoPosLonComparator implements Comparator<GeoPos> {
        @Override
        public int compare(GeoPos geoPos1, GeoPos geoPos2) {
            final float delta = geoPos1.lon - geoPos2.lon;
            if (delta < 0f) {
                return -1;
            } else if (delta > 0f) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
