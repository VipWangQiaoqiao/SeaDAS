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

import com.bc.ceres.core.Assert;
import org.esa.beam.jai.ImageManager;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;

/**
 * This class contains information about how a product's raster data node is displayed as an image.
 *
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 */
public class ImageInfo implements Cloneable {

    public static final Color NO_COLOR = new Color(0, 0, 0, 0);

    /**
     * @deprecated since BEAM 4.2, use enum {@link HistogramMatching}
     */
    @Deprecated
    public static final String HISTOGRAM_MATCHING_OFF = "off";
    @Deprecated
    public static final String HISTOGRAM_MATCHING_EQUALIZE = "equalize";
    @Deprecated
    public static final String HISTOGRAM_MATCHING_NORMALIZE = "normalize";

    private static final double FORCED_CHANGE_FACTOR = 0.0001;

    private ColorPaletteSourcesInfo colorPaletteSourcesInfo = new ColorPaletteSourcesInfo();



    /**
     * Enumerates the possible histogram matching modes.
     */
    public enum HistogramMatching {
        None,
        Equalize,
        Normalize
    }

    private ColorPaletteDef colorPaletteDef;
    // todo - save in DIMAP   (nf/mp - 26.06.2008)
    private RGBChannelDef rgbChannelDef;
    private Color noDataColor;
    private HistogramMatching histogramMatching;
    // todo - save in DIMAP   (nf - 13.03.2012)
    private boolean logScaled;

    /**
     * Constructs a new image information instance.
     *
     * @param colorPaletteDef the color palette definition
     */
    public ImageInfo(ColorPaletteDef colorPaletteDef) {
        Assert.notNull(colorPaletteDef, "colorPaletteDef");
        this.colorPaletteDef = colorPaletteDef;
        this.rgbChannelDef = null;
        this.noDataColor = NO_COLOR;
        this.histogramMatching = HistogramMatching.None;
    }

    /**
     * Constructs a new RGB image information instance.
     *
     * @param rgbChannelDef the RGB channel definition
     */
    public ImageInfo(RGBChannelDef rgbChannelDef) {
        Assert.notNull(rgbChannelDef, "rgbChannelDef");
        this.colorPaletteDef = null;
        this.rgbChannelDef = rgbChannelDef;
        this.noDataColor = NO_COLOR;
        this.histogramMatching = HistogramMatching.None;
    }


    //    This class stores original selection criteria such as cpdFileName and colorScheme
    public ColorPaletteSourcesInfo getColorPaletteSourcesInfo() {
        return colorPaletteSourcesInfo;
    }


    /**
     * Gets the color palette definition as used for images created from single bands.
     *
     * @return The color palette definition. Can be {@code null}.
     * In this case {@link #getRgbChannelDef()} is non-null.
     */
    public ColorPaletteDef getColorPaletteDef() {
        return colorPaletteDef;
    }

    /**
     * Gets the RGB(A) channel definition as used for images created from 3 tp 4 bands.
     *
     * @return The RGB(A) channel definition.
     * Can be {@code null}. In this case {@link #getColorPaletteDef()} is non-null.
     */
    public RGBChannelDef getRgbChannelDef() {
        return rgbChannelDef;
    }

    public Color getNoDataColor() {
        return noDataColor;
    }

    public void setNoDataColor(Color noDataColor) {
        Assert.notNull(noDataColor, "noDataColor");
        this.noDataColor = noDataColor;
    }

    public HistogramMatching getHistogramMatching() {
        return histogramMatching;
    }

    public void setHistogramMatching(HistogramMatching histogramMatching) {
        Assert.notNull(histogramMatching, "histogramMatching");
        this.histogramMatching = histogramMatching;
    }

    public boolean isLogScaled() {
        return logScaled;
    }

    public void setLogScaled(boolean logScaled) {
        this.logScaled = logScaled;
    }

    public Color[] getColors() {
        return colorPaletteDef != null ? colorPaletteDef.getColors() : new Color[0];
    }

    /**
     * Gets the number of color components the image shall have using an instance of this {@code ImageInfo}.
     *
     * @return {@code 3} for RGB images, {@code 4} for RGB images with an alpha channel (transparency)
     */
    public int getColorComponentCount() {
        if (noDataColor.getAlpha() < 255) {
            return 4;
        }
        if (colorPaletteDef != null) {
            final Color[] colors = colorPaletteDef.getColors();
            for (Color color : colors) {
                if (color.getAlpha() < 255) {
                    return 4;
                }
            }
        }
        if (rgbChannelDef != null) {
            if (rgbChannelDef.isAlphaUsed()) {
                return 4;
            }
        }
        return 3;
    }

    public IndexColorModel createIndexColorModel(Scaling scaling) {
        if (colorPaletteDef == null) {
            return null;
        }
        Color[] palette = ImageManager.createColorPalette(this);
//        Color[] palette = colorPaletteDef.createColorPalette(scaling);
        final int numColors = palette.length;
        final byte[] red = new byte[numColors];
        final byte[] green = new byte[numColors];
        final byte[] blue = new byte[numColors];
        for (int i = 0; i < palette.length; i++) {
            Color color = palette[i];
            red[i] = (byte) color.getRed();
            green[i] = (byte) color.getGreen();
            blue[i] = (byte) color.getBlue();
        }
        return new IndexColorModel(numColors <= 256 ? 8 : 16, numColors, red, green, blue);
    }

    public ComponentColorModel createComponentColorModel() {
        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ComponentColorModel cm;
        if (getColorComponentCount() == 4) {
            cm = new ComponentColorModel(cs,
                    true, // hasAlpha,
                    false, //isAlphaPremultiplied,
                    Transparency.TRANSLUCENT, //  transparency,
                    DataBuffer.TYPE_BYTE); //transferType
        } else {
            cm = new ComponentColorModel(cs,
                    false, // hasAlpha,
                    false, //isAlphaPremultiplied,
                    Transparency.OPAQUE, //  transparency,
                    DataBuffer.TYPE_BYTE); //transferType

        }
        return cm;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a copy of this object
     */
    @Override
    public final ImageInfo clone() {
        try {
            ImageInfo imageInfo = (ImageInfo) super.clone();
            if (colorPaletteDef != null) {
                imageInfo.colorPaletteDef = (ColorPaletteDef) colorPaletteDef.clone();
            }
            if (rgbChannelDef != null) {
                imageInfo.rgbChannelDef = (RGBChannelDef) rgbChannelDef.clone();
            }
            return imageInfo;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and returns a "deep" copy of this object. The method simply returns the value of
     * {@link #clone()}.
     *
     * @return a copy of this object
     */
    public ImageInfo createDeepCopy() {
        return clone();
    }

    /**
     * Releases all of the resources used by this object instance and all of its owned children. Its primary use is to
     * allow the garbage collector to perform a vanilla job.
     * <p/>
     * <p>This method should be called only if it is for sure that this object instance will never be used again. The
     * results of referencing an instance of this class after a call to <code>dispose()</code> are undefined.
     * <p/>
     * <p>Overrides of this method should always call <code>super.dispose();</code> after disposing this instance.
     */
    public void dispose() {
        if (colorPaletteDef != null) {
            colorPaletteDef.dispose();
        }
        colorPaletteDef = null;
        rgbChannelDef = null;
    }

    /**
     * Sets the colours of the colour palette of this image info.
     *
     * @param colors the new colours
     */
    public void setColors(Color[] colors) {
        ColorPaletteDef cpd = getColorPaletteDef();
        int numPoints = cpd.getNumPoints();
        for (int i = 0; i < numPoints; i++) {
            ColorPaletteDef.Point point = cpd.getPointAt(i);
            point.setColor(colors[i % colors.length]);
        }
    }

    /**
     * Transfers the colour palette into this image info.
     *
     * @param colorPaletteDef another colour palette
     * @param minSample       the minium allowed sample value in the new colour palette
     * @param maxSample       the maximum allowed sample value in the new colour palette
     * @param autoDistribute  if true, points are distributed between minSample/maxSample.
     */
    public void setColorPaletteDef(ColorPaletteDef colorPaletteDef,
                                   double minSample,
                                   double maxSample, boolean autoDistribute) {
        transferPoints(colorPaletteDef, minSample, maxSample, autoDistribute, getColorPaletteDef());
    }

    private static void transferPoints(ColorPaletteDef sourceCPD,
                                       double minTargetValue,
                                       double maxTargetValue,
                                       boolean autoDistribute,
                                       ColorPaletteDef targetCPD) {

        if (autoDistribute || sourceCPD.isAutoDistribute()) {
            alignNumPoints(sourceCPD, targetCPD);
            double minSourceValue = sourceCPD.getMinDisplaySample();
            double maxSourceValue = sourceCPD.getMaxDisplaySample();
            double delta1 = (maxTargetValue > minTargetValue) ? maxTargetValue - minTargetValue : 1.0;
            double delta2 = (maxSourceValue > minSourceValue) ? maxSourceValue - minSourceValue : 1.0;
            double b = delta1 / delta2;
            double a = minTargetValue - minSourceValue * b;

//            if (1 == 1) {
//                for (int i = 0; i < sourceCPD.getNumPoints(); i++) {
//                    if (maxTargetValue != minTargetValue) {
//                        double weight = (sourceCPD.getPointAt(i).getSample() - minTargetValue) / (minTargetValue - maxTargetValue);
//                        double logValue = getLogarithmicValueUsingLinearWeight(weight, minTargetValue, maxTargetValue);
//                        targetCPD.getPointAt(i).setSample(logValue);
//                        targetCPD.getPointAt(i).setColor(sourceCPD.getPointAt(i).getColor());
//                        targetCPD.getPointAt(i).setLabel(sourceCPD.getPointAt(i).getLabel());
//                    }
//                }
//            } else {
            for (int i = 0; i < sourceCPD.getNumPoints(); i++) {
                double currTargetValue;
                if (i == 0) {
                    currTargetValue = minTargetValue;
                } else if (i == sourceCPD.getNumPoints() -1) {
                    currTargetValue = maxTargetValue;
                } else {
                    currTargetValue = a + b * sourceCPD.getPointAt(i).getSample();
                }

                targetCPD.getPointAt(i).setSample(currTargetValue);
                targetCPD.getPointAt(i).setColor(sourceCPD.getPointAt(i).getColor());
                targetCPD.getPointAt(i).setLabel(sourceCPD.getPointAt(i).getLabel());
//                }
            }
        } else {
            targetCPD.setPoints(sourceCPD.getPoints().clone());
        }
    }


    public void setColorPaletteDef(ColorPaletteDef colorPaletteDef,
                                   double minSample,
                                   double maxSample, boolean autoDistribute, boolean isSourceLogScaled, boolean isTargetLogScaled) {
        transferPoints(colorPaletteDef, minSample, maxSample, autoDistribute, getColorPaletteDef(), isSourceLogScaled, isTargetLogScaled);
    }


    private static void transferPoints(ColorPaletteDef sourceCPD,
                                       double minTargetValue,
                                       double maxTargetValue,
                                       boolean autoDistribute,
                                       ColorPaletteDef targetCPD,
                                       boolean isSourceLogScaled,
                                       boolean isTargetLogScaled) {

        if (autoDistribute || sourceCPD.isAutoDistribute()) {
            alignNumPoints(sourceCPD, targetCPD);
            double minSourceValue = sourceCPD.getMinDisplaySample();
            double maxSourceValue = sourceCPD.getMaxDisplaySample();

            // The target CPD log status needs to be set here to be effective
            targetCPD.setLogScaled(isTargetLogScaled);

            for (int i = 0; i < sourceCPD.getNumPoints(); i++) {

                if (minTargetValue != maxTargetValue && minSourceValue != maxSourceValue) {

                    double linearWeight;
                    if (isSourceLogScaled) {
                        double currentSourceLogValue = sourceCPD.getPointAt(i).getSample();
                        linearWeight = getLinearWeightFromLogValue(currentSourceLogValue, minSourceValue, maxSourceValue);

                    } else {
                        double currentSourceValue = sourceCPD.getPointAt(i).getSample();
                        linearWeight = (currentSourceValue - minSourceValue) / (maxSourceValue - minSourceValue);
                    }

                    double currentLinearTargetValue = getLinearValue(linearWeight, minTargetValue, maxTargetValue);

                    if (isTargetLogScaled) {
                        double currentLogTargetValue = getLogarithmicValue(currentLinearTargetValue, minTargetValue, maxTargetValue);
                        targetCPD.getPointAt(i).setSample(currentLogTargetValue);
                    } else {
                        targetCPD.getPointAt(i).setSample(currentLinearTargetValue);
                    }


                } else {
                    // cant do much here so just set all to min value and let user fix either palette or bad entry
                    targetCPD.getPointAt(i).setSample(minTargetValue);
                }

                Color currentSourceColor = sourceCPD.getPointAt(i).getColor();
                targetCPD.getPointAt(i).setColor(currentSourceColor);
                targetCPD.getPointAt(i).setLabel(sourceCPD.getPointAt(i).getLabel());
            }

        } else {
            targetCPD.setPoints(sourceCPD.getPoints().clone());
            targetCPD.setLogScaled(isTargetLogScaled);
        }

    }


    private static double getLogarithmicValue(double linearValue, double min, double max) {

        // Prevent extrapolation which could occur due to machine roundoffs in the calculations
        if (linearValue == min) {
            return min;
        }
        if (linearValue == max) {
            return max;
        }

        double b = Math.log(max / min) / (max - min);
        double a = min / (Math.exp(b * min));
        double logValue = a * Math.exp(b * linearValue);

        // Prevent UNEXPECTED interpolation/extrapolation which could occur due to machine roundoffs in the calculations
        if (linearValue > min && logValue < min) {
            return min;
        }
        if (linearValue < max && logValue > max) {
            return max;
        }
        if (linearValue < min && logValue >= min) {
            return min - (max-min)*FORCED_CHANGE_FACTOR;
        }
        if (linearValue > max && logValue <= max) {
            return max + (max-min)*FORCED_CHANGE_FACTOR;
        }

        return logValue;
    }

    private static double getLinearValue(double linearWeight, double min, double max) {

        // Prevent extrapolation which could occur due to machine roundoffs in the calculations
        if (linearWeight == 0) {
            return min;
        }
        if (linearWeight == 1) {
            return max;
        }

        double deltaNormalized = (max - min);
        double linearValue = min + linearWeight * (deltaNormalized);

        // Prevent UNEXPECTED interpolation/extrapolation which could occur due to machine roundoffs in the calculations
        if (linearWeight > 0 && linearValue < min) {
            return min;
        }
        if (linearWeight < 1 && linearValue > max) {
            return max;
        }
        if (linearWeight < 0 && linearValue >= min) {
            return min - (max-min)*FORCED_CHANGE_FACTOR;
        }
        if (linearWeight > 1 && linearValue <= max) {
            return max + (max-min)*FORCED_CHANGE_FACTOR;
        }

        return linearValue;
    }


    private static double getLinearWeightFromLogValue(double logValue, double min, double max) {

        // Prevent extrapolation which could occur due to machine roundoffs in the calculations
        if (logValue == min) {
            return 0;
        }
        if (logValue == max) {
            return 1;
        }

        double b = Math.log(max / min) / (max - min);
        double a = min / (Math.exp(b * min));

//        double linearWeight = Math.log(logValue / a) / b;
//        linearWeight = (linearWeight - min) / (max - min);
        double linearWeight = ((Math.log(logValue / a) / b) - min) / (max - min);

        // Prevent UNEXPECTED interpolation/extrapolation which could occur due to machine roundoffs in the calculations
        if (logValue > min && linearWeight < 0) {
            return 0;
        }
        if (logValue < max && linearWeight > 1) {
            return 1;
        }
        if (logValue < min && linearWeight >= 0) {
            return 0 - FORCED_CHANGE_FACTOR;
        }
        if (logValue > max && linearWeight <= 1) {
            return 1 + FORCED_CHANGE_FACTOR;
        }

        return linearWeight;
    }


    private static void alignNumPoints(ColorPaletteDef sourceCPD, ColorPaletteDef targetCPD) {
        int deltaNumPoints = targetCPD.getNumPoints() - sourceCPD.getNumPoints();
        if (deltaNumPoints < 0) {
            for (; deltaNumPoints != 0; deltaNumPoints++) {
                targetCPD.insertPointAfter(0, new ColorPaletteDef.Point());
            }
        } else if (deltaNumPoints > 0) {
            for (; deltaNumPoints != 0; deltaNumPoints--) {
                targetCPD.removePointAt(1);
            }
        }
    }

    /**
     * Converts a string to a histogram matching.
     *
     * @param mode the histogram matching string
     * @return the histogram matching. {@link ImageInfo.HistogramMatching#None} if {@code maode} is not "Equalize" or "Normalize".
     */
    public static ImageInfo.HistogramMatching getHistogramMatching(String mode) {
        ImageInfo.HistogramMatching histogramMatchingEnum = ImageInfo.HistogramMatching.None;
        if ("Equalize".equalsIgnoreCase(mode)) {
            histogramMatchingEnum = ImageInfo.HistogramMatching.Equalize;
        } else if ("Normalize".equalsIgnoreCase(mode)) {
            histogramMatchingEnum = ImageInfo.HistogramMatching.Normalize;
        }
        return histogramMatchingEnum;
    }

}
