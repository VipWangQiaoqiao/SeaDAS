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

package org.esa.beam.watermask.operator;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.watermask.util.ImageDescriptor;
import org.esa.beam.watermask.util.ImageDescriptorBuilder;

import javax.media.jai.OpImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Classifies a pixel given by its geo-coordinate as water pixel.
 */
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class WatermaskClassifier {

    public static final int WATER_VALUE = 1;
    public static final int INVALID_VALUE = 127;
    public static final int LAND_VALUE = 0;
    public static final int RESOLUTION_50m = 50;
    public static final int RESOLUTION_150m = 150;
    public static final int RESOLUTION_1km = 1000;
    public static final int RESOLUTION_10km = 10000;


    public enum Mode {
        MODIS("MODIS"),
        SRTM_GC("SRTM_GC"),
        GSHHS("GSHHS");

        public String SRTM_GC_DESCRIPTION = "SRTM (Shuttle Radar Topography Mission) and GC(GlobCover World Map)";
        public String GSHHS_DESCRIPTION = "GSHHS (Global Self-consistent, Hierarchical, High-resolution Shoreline Database)";
        public String MODIS_DESCRIPTION = "";

        private final String name;

        private Mode(String name) {
            this.name = name;
        }


        public String getDescription() {
            switch (this) {
                case MODIS: return  MODIS_DESCRIPTION;
                case SRTM_GC: return SRTM_GC_DESCRIPTION;
                case GSHHS: return GSHHS_DESCRIPTION;
            }

            return null;
        }

        public String toString() {
            return name;
        }
    }


//    public static final int MODE_MODIS = 1;
//    public static final int MODE_GC = MODE_MODIS << 1;
//    public static final int MODE_GSHHS = MODE_MODIS << 2;

    static final int GC_TILE_WIDTH = 576;
    static final int GC_TILE_HEIGHT = 491;
    static final int GC_IMAGE_WIDTH = 129600;
    static final int GC_IMAGE_HEIGHT = 10800;

    static final int GSHHS_1_TILE_WIDTH = 500;
    static final int GSHHS_1_TILE_HEIGHT = 250;
    static final int GSHHS_1_IMAGE_WIDTH = 36000;
    static final int GSHHS_1_IMAGE_HEIGHT = 18000;

    static final int GSHHS_10_TILE_WIDTH = 250;
    static final int GSHHS_10_TILE_HEIGHT = 125;
    static final int GSHHS_10_IMAGE_WIDTH = 3600;
    static final int GSHHS_10_IMAGE_HEIGHT = 1800;

    static final int MODIS_IMAGE_WIDTH = 155520;
    static final int MODIS_IMAGE_HEIGHT = 12960;
    static final int MODIS_TILE_WIDTH = 640;
    static final int MODIS_TILE_HEIGHT = 540;

//    private SRTMOpImage centerImage;
//    private final PNGSourceImage gshhsImage;
//    private final PNGSourceImage aboveSixtyNorthImage;
//    private final PNGSourceImage belowSixtySouthImage;

    private SRTMOpImage centerImage;
    private PNGSourceImage gshhsImage;
    private PNGSourceImage aboveSixtyNorthImage;
    private PNGSourceImage belowSixtySouthImage;
    private Mode mode;
    private int resolution;

    /**
     * Creates a new classifier instance on the given resolution.
     * The classifier uses a tiled image in background to determine the if a
     * given geo-position is over land or over water.
     * Tiles do not exist if the whole region of the tile would only cover land or water.
     * Where a tile does not exist a so called fill algorithm can be performed.
     * In this case the next existing tile is searched and the nearest classification value
     * for the given geo-position is returned.
     * If the fill algorithm is not performed a value indicating invalid is returned.
     *
     * @param resolution The resolution specifying the source data which is to be queried. The units are in meters.
     *                   Needs to be <code>RESOLUTION_50m</code>, <code>RESOLUTION_150m</code>,
     *                   <code>RESOLUTION_1km</code> or <code>RESOLUTION_10km</code>
     * @param mode       The mode the classifier shall run in. Must be one of <code>MODE_MODIS</code>,
     *                   <code>Mode.SRTM_GC</code> or <code>Mode.GSHHS</code>.
     *                   <p/>
     *                   If <code>Mode.MODIS</code> is chosen, the watermask is based on MODIS above 60° north,
     *                   on SRTM-shapefiles between 60° north and 60° south, and on MODIS below 60°
     *                   south.
     *                   <p/>
     *                   If <code>Mode.SRTM_GC</code> is chosen, the watermask is based on GlobCover above 60° north,
     *                   on SRTM-shapefiles between 60° north and 60° south, and on MODIS below 60° south.
     *                   <p/>
     *                   If <code>Mode.GSHHS</code> is chosen, the watermask is based on the Global Self-consistent,
     *                   Hierarchical, High-resolution Shoreline Database
     * @throws java.io.IOException If some IO-error occurs creating the sources.
     */
    public WatermaskClassifier(int resolution, Mode mode) throws IOException {
        if (resolution != RESOLUTION_50m && resolution != RESOLUTION_150m && resolution != RESOLUTION_1km && resolution != RESOLUTION_10km) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Resolution needs to be {0}, {1}, {2} or {3}.", RESOLUTION_50m, RESOLUTION_150m, RESOLUTION_1km, RESOLUTION_10km));
        }

        if (mode != Mode.SRTM_GC && mode != Mode.GSHHS && mode != Mode.MODIS) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Mode needs to be {0}, {1} or {2}.", Mode.SRTM_GC, Mode.GSHHS, Mode.MODIS));
        }

        this.mode = mode;
        this.resolution = resolution;

        final File auxdataDir = installAuxdata();

//        ImageDescriptor gshhsDescriptor = getNorthDescriptor(auxdataDir);
//        ImageDescriptor northDescriptor = getNorthDescriptor(auxdataDir);
//        ImageDescriptor southDescriptor = getSouthDescriptor(auxdataDir);
//        gshhsImage = createImage(gshhsDescriptor);
//
//        centerImage = null;
//        if (mode == Mode.SRTM_GC) {
//            centerImage = createSrtmImage(auxdataDir);
//        }
//        aboveSixtyNorthImage = createImage(northDescriptor);
//        belowSixtySouthImage = createImage(southDescriptor);


        if (mode == Mode.GSHHS) {
            ImageDescriptor gshhsDescriptor = getNorthDescriptor(auxdataDir);
            gshhsImage = createImage(gshhsDescriptor);
        } else if (mode == Mode.SRTM_GC) {
            centerImage = createSrtmImage(auxdataDir);

            ImageDescriptor northDescriptor = getNorthDescriptor(auxdataDir);
            aboveSixtyNorthImage = createImage(northDescriptor);

            ImageDescriptor southDescriptor = getSouthDescriptor(auxdataDir);
            belowSixtySouthImage = createImage(southDescriptor);
        }


    }

//    public WatermaskClassifier(int resolution) throws IOException {
//        this(resolution, MODE_GSHHS);
//    }

    private ImageDescriptor getSouthDescriptor(File auxdataDir) {
        return new ImageDescriptorBuilder()
                .width(MODIS_IMAGE_WIDTH)
                .height(MODIS_IMAGE_HEIGHT)
                .tileWidth(MODIS_TILE_WIDTH)
                .tileHeight(MODIS_TILE_HEIGHT)
                .auxdataDir(auxdataDir)
                .zipFileName("MODIS_south_water_mask.zip")
                .build();
    }

    private ImageDescriptor getNorthDescriptor(File auxdataDir) {
        ImageDescriptor northDescriptor;
        switch (mode) {
            case MODIS:
                northDescriptor = new ImageDescriptorBuilder()
                        .width(MODIS_IMAGE_WIDTH)
                        .height(MODIS_IMAGE_HEIGHT)
                        .tileWidth(MODIS_TILE_WIDTH)
                        .tileHeight(MODIS_TILE_HEIGHT)
                        .auxdataDir(auxdataDir)
                        .zipFileName("MODIS_north_water_mask.zip")
                        .build();
                break;
            case SRTM_GC:
                northDescriptor = new ImageDescriptorBuilder()
                        .width(GC_IMAGE_WIDTH)
                        .height(GC_IMAGE_HEIGHT)
                        .tileWidth(GC_TILE_WIDTH)
                        .tileHeight(GC_TILE_HEIGHT)
                        .auxdataDir(auxdataDir)
                        .zipFileName("GC_water_mask.zip")
                        .build();
                break;
            case GSHHS:
                String zipname = String.format("GSHHS_water_mask_%skm.zip", String.valueOf(resolution / 1000));
                if (resolution == 1000) {
                    northDescriptor = new ImageDescriptorBuilder()
                            .width(GSHHS_1_IMAGE_WIDTH)
                            .height(GSHHS_1_IMAGE_HEIGHT)
                            .tileWidth(GSHHS_1_TILE_WIDTH)
                            .tileHeight(GSHHS_1_TILE_HEIGHT)
                            .auxdataDir(auxdataDir)
                            .zipFileName(zipname)
                            .build();
                } else {
                    northDescriptor = new ImageDescriptorBuilder()
                            .width(GSHHS_10_IMAGE_WIDTH)
                            .height(GSHHS_10_IMAGE_HEIGHT)
                            .tileWidth(GSHHS_10_TILE_WIDTH)
                            .tileHeight(GSHHS_10_TILE_HEIGHT)
                            .auxdataDir(auxdataDir)
                            .zipFileName(zipname)
                            .build();
                }
                break;
            default:
                String msg = String.format("Unknown mode '%d'. Known modes are {%d, %d, %d}", mode, Mode.MODIS, Mode.SRTM_GC, Mode.GSHHS);
                throw new IllegalArgumentException(msg);
        }
        return northDescriptor;
    }

    private SRTMOpImage createSrtmImage(File auxdataDir) throws IOException {
        int tileSize = WatermaskUtils.computeSideLength(resolution);

        int width = tileSize * 360;
        int height = tileSize * 180;
        final Properties properties = new Properties();
        properties.setProperty("width", String.valueOf(width));
        properties.setProperty("height", String.valueOf(height));
        properties.setProperty("tileWidth", String.valueOf(tileSize));
        properties.setProperty("tileHeight", String.valueOf(tileSize));
        final URL imageProperties = getClass().getResource("image.properties");
        properties.load(imageProperties.openStream());

        File zipFile = new File(auxdataDir, resolution + "m.zip");
        return SRTMOpImage.create(properties, zipFile);
    }

    private PNGSourceImage createImage(ImageDescriptor descriptor) throws IOException {
        int width = descriptor.getImageWidth();
        int tileWidth = descriptor.getTileWidth();
        int height = descriptor.getImageHeight();
        int tileHeight = descriptor.getTileHeight();
        final Properties properties = new Properties();
        properties.setProperty("width", String.valueOf(width));
        properties.setProperty("height", String.valueOf(height));
        properties.setProperty("tileWidth", String.valueOf(tileWidth));
        properties.setProperty("tileHeight", String.valueOf(tileHeight));
        final URL imageProperties = getClass().getResource("image.properties");
        properties.load(imageProperties.openStream());

        final File auxdataDir = descriptor.getAuxdataDir();
        final String zipFileName = descriptor.getZipFileName();
        File zipFile = new File(auxdataDir, zipFileName);
        return PNGSourceImage.create(properties, zipFile, mode, resolution);
    }

    private File installAuxdata() throws IOException {
        String auxdataSrcPath = "auxdata/images";
        final String relativeDestPath = ".beam/" + "beam-watermask-operator" + "/" + auxdataSrcPath;
        File auxdataTargetDir = new File(SystemUtils.getUserHomeDir(), relativeDestPath);
        URL sourceUrl = ResourceInstaller.getSourceUrl(this.getClass());

        ResourceInstaller resourceInstaller = new ResourceInstaller(sourceUrl, auxdataSrcPath, auxdataTargetDir);
        resourceInstaller.install(".*", ProgressMonitor.NULL);

        return auxdataTargetDir;
    }

    /**
     * Returns the sample value at the given geo-position, regardless of the source resolution.
     *
     * @param lat The latitude value.
     * @param lon The longitude value.
     * @return 0 if the given position is over land, 1 if it is over water, 2 if no definite statement can be made
     *         about the position.
     */
    public int getWaterMaskSample(float lat, float lon) {
        double tempLon = lon + 180.0;
        if (tempLon >= 360) {
            tempLon %= 360;
        }

        float normLat = Math.abs(lat - 90.0f);

        if (tempLon < 0.0 || tempLon > 360.0 || normLat < 0.0 || normLat > 180.0) {
            return INVALID_VALUE;
        }
        if (mode == Mode.GSHHS) {
            return getSample(normLat, tempLon, 180.0, 360.0, 0.0, gshhsImage);
        } else {
            if (normLat < 150.0f && normLat > 30.0f) {
                return getSample(normLat, tempLon, 180.0, 360.0, 0.0, centerImage);
            } else if (normLat <= 30.0f) {
                return getSample(normLat, tempLon, 30.0, 360.0, 0.0, aboveSixtyNorthImage);
            } else if (normLat >= 150.0f) {
//                return WATER_VALUE;
                return getSample(normLat, tempLon, 30.0, 360.0, 0.0, belowSixtySouthImage);
            }
        }
        throw new IllegalStateException("Cannot come here");
    }

    private int getSample(double lat, double lon, double latDiff, double lonDiff, double offset, OpImage image) {
        final double pixelSizeX = lonDiff / image.getWidth();
        final double pixelSizeY = latDiff / image.getHeight();
        final int x = (int) Math.round(lon / pixelSizeX);
        final int y = (int) (Math.round((lat - offset) / pixelSizeY));
        final Raster tile = image.getTile(image.XToTileX(x), image.YToTileY(y));
        if (tile == null) {
            return INVALID_VALUE;
        }
        return tile.getSample(x, y, 0);
    }

    /**
     * Returns the fraction of water for the given region, considering a subsampling factor.
     *
     * @param geoCoding          The geo coding of the product the watermask fraction shall be computed for.
     * @param pixelPos           The pixel position the watermask fraction shall be computed for.
     * @param subsamplingFactorX The factor between the high resolution water mask and the - lower resolution -
     *                           source image in x direction. Only values in [1..M] are sensible,
     *                           with M = (source image resolution in m/pixel) / (50 m/pixel)
     * @param subsamplingFactorY The factor between the high resolution water mask and the - lower resolution -
     *                           source image in y direction. Only values in [1..M] are sensible,
     *                           with M = (source image resolution in m/pixel) / (50 m/pixel)
     * @return The fraction of water in the given geographic rectangle, in the range [0..100].
     */
    public byte getWaterMaskFraction(GeoCoding geoCoding, PixelPos pixelPos, int subsamplingFactorX, int subsamplingFactorY) {
        float valueSum = 0;
        double xStep = 1.0 / subsamplingFactorX;
        double yStep = 1.0 / subsamplingFactorY;
        final GeoPos geoPos = new GeoPos();
        final PixelPos currentPos = new PixelPos();
        int invalidCount = 0;
        for (int sx = 0; sx < subsamplingFactorX; sx++) {
            currentPos.x = (float) (pixelPos.x + sx * xStep);
            for (int sy = 0; sy < subsamplingFactorY; sy++) {
                currentPos.y = (float) (pixelPos.y + sy * yStep);
                geoCoding.getGeoPos(currentPos, geoPos);
                int waterMaskSample = getWaterMaskSample(geoPos);
                if (waterMaskSample != WatermaskClassifier.INVALID_VALUE) {
                    valueSum += waterMaskSample;
                } else {
                    invalidCount++;
                }
            }
        }

        return computeAverage(subsamplingFactorX, subsamplingFactorY, valueSum, invalidCount);
    }

    private byte computeAverage(int subsamplingFactorX, int subsamplingFactorY, float valueSum, int invalidCount) {
        final boolean allValuesInvalid = invalidCount == subsamplingFactorX * subsamplingFactorY;
        if (allValuesInvalid) {
            return WatermaskClassifier.INVALID_VALUE;
        } else {
            return (byte) (100 * valueSum / (subsamplingFactorX * subsamplingFactorY));
        }
    }

    private int getWaterMaskSample(GeoPos geoPos) {
        final int waterMaskSample;
        if (geoPos.isValid()) {
            waterMaskSample = getWaterMaskSample(geoPos.lat, geoPos.lon);
        } else {
            waterMaskSample = WatermaskClassifier.INVALID_VALUE;
        }
        return waterMaskSample;
    }

    /**
     * Classifies the given geo-position as water or land.
     *
     * @param lat The latitude value.
     * @param lon The longitude value.
     * @return true, if the geo-position is over water, false otherwise.
     * @throws java.io.IOException If some IO-error occurs reading the source file.
     */
    public boolean isWater(float lat, float lon) throws IOException {
        final int waterMaskSample = getWaterMaskSample(lat, lon);
        return waterMaskSample == WATER_VALUE;
    }

}
