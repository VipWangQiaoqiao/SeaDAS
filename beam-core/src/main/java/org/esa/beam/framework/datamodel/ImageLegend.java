
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

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.math.MathUtils;

// @todo 2 nf/** - if orientation is vertical, sample values should increase from bottom to top
// @todo 1 nf/** - make PALETTE_HEIGHT a fixed value, fill space into gaps instead
// @todo 2 nf/** - draw header text vertically for vertical orientations
// @todo 3 nf/** - also draw legend into product scene view
//                 make "color legend properties" dialog a preferences page


/**
 * The <code>ImageLegend</code> class is used to generate an image legend from a <code>{@link
 * ImageInfo}</code> instance.
 *
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 */
public class ImageLegend {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int GAP = 20;   // TITLE_TO_PALETTE_GAP
    //DANNY
    private static final int _LABEL_GAP = 10;      // LABEL_TO_PALETTE GAP
    private static final int _LABEL_TO_LABEL_GAP = 40;

    // private static final int _LABEL_GAP = 12;
    private static final int _SLIDER_WIDTH = 10;
    private static final int _SLIDER_HEIGHT = 14;

    private static final int _MIN_PALETTE_WIDTH = 256;
    private static final int _MIN_PALETTE_HEIGHT = 24;
    private static final int _MIN_LEGEND_WIDTH = 320;
    private static final int _MIN_LEGEND_HEIGHT = 48;

    // DANNY
    private static final Font _DEFAULT_FONT = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
    private static final Font _DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font _DEFAULT_TITLE_UNITS_FONT = new Font("SansSerif", Font.ITALIC, 18);
    //   private static final Font _DEFAULT_FONT = new Font("Arial", Font.BOLD, 14);

    // Independent attributes (Properties)
    private final ImageInfo imageInfo;
    private final RasterDataNode raster;
    private boolean usingHeader;
    private String headerText;
    private String headerUnitsText;
    private int orientation;
    private Font font;
    private int numberOfTicks;
    private Color foregroundColor;
    private Color backgroundColor;
    private boolean backgroundTransparencyEnabled;
    private float backgroundTransparency;
    private boolean antialiasing;
    private boolean evenDistribution;
    private int decimalPlaces;
    private String fullCustomAddThesePoints;

    // Dependent, internal attributes
    private Rectangle paletteRect;
    private Dimension legendSize;
    private Shape sliderShape;
    private String[] labels;
    private int[] labelWidths;
    private int palettePos1;
    private int palettePos2;
    private ArrayList<ColorBarInfo> colorBarInfos = new ArrayList<ColorBarInfo>();

    public ImageLegend(ImageInfo imageInfo, RasterDataNode raster) {
        this.imageInfo = imageInfo;
        this.raster = raster;
        usingHeader = true;
        headerText = "";

        orientation = HORIZONTAL;
        font = _DEFAULT_FONT;
        antialiasing = false;
        backgroundColor = Color.white;
        foregroundColor = Color.black;
        backgroundTransparency = 0.0f;
        evenDistribution = true;
        decimalPlaces = 1;
        setFullCustomAddThesePoints("");

    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public RasterDataNode getRaster() {
        return raster;
    }


    public boolean isUsingHeader() {
        return usingHeader;
    }

    public void setUsingHeader(boolean usingHeader) {
        this.usingHeader = usingHeader;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getHeaderUnitsText() {
        return headerUnitsText;
    }

    public void setHeaderUnitsText(String headerUnitsText) {
        this.headerUnitsText = headerUnitsText;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }


    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public boolean isEvenDistribution() {
        return evenDistribution;
    }

    public void setEvenDistribution(boolean evenDistribution) {
        this.evenDistribution = evenDistribution;
    }

    public int getNumberOfTicks() {
        return numberOfTicks;
    }

    public void setNumberOfTicks(int numberOfTicks) {
        this.numberOfTicks = numberOfTicks;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public boolean isAntialiasing() {
        return antialiasing;
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
    }

    public boolean isBackgroundTransparencyEnabled() {
        return backgroundTransparencyEnabled;
    }

    public void setBackgroundTransparencyEnabled(boolean backgroundTransparencyEnabled) {
        this.backgroundTransparencyEnabled = backgroundTransparencyEnabled;
    }

    public float getBackgroundTransparency() {
        return backgroundTransparency;
    }

    public void setBackgroundTransparency(float backgroundTransparency) {
        this.backgroundTransparency = backgroundTransparency;
    }

    public boolean isAlphaUsed() {
        return backgroundTransparencyEnabled && backgroundTransparency > 0.0f && backgroundTransparency <= 1.0f;
    }

    public int getBackgroundAlpha() {
        return isAlphaUsed() ? Math.round(255f * (1f - backgroundTransparency)) : 255;
    }

    public BufferedImage createImage() {
        createColorBarInfos();
        initDrawing();
        final BufferedImage bi = createBufferedImage(legendSize.width, legendSize.height);
        final Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        if (antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        if (font != null) {
            g2d.setFont(font);
        }
        draw(g2d);
        return bi;
    }

    private void createColorBarInfos() {

        double value, weight;
        colorBarInfos.clear();

        if (isEvenDistribution()) {
            if (getNumberOfTicks() >= 2) {
                double normalizedDelta = (1.0 / (getNumberOfTicks() - 1.0));

                for (int i = 0; i < getNumberOfTicks(); i++) {
                    weight = i * normalizedDelta;

                    if (imageInfo.isLogScaled()) {
                        value = getLogarithmicValue(getLinearValue(weight));
                    } else {
                        value = getLinearValue(weight);
                    }

                    if (isValidWeight(weight)) {
                        ColorBarInfo colorBarInfo = new ColorBarInfo(value, weight, getDecimalPlaces());
                        colorBarInfos.add(colorBarInfo);
                    }
                }
            }
        } else {
            final int numPointsInCpdFile = getNumGradationCurvePoints();
            int stepSize = 1;
            //    int stepSize = numPointsInCpdFile / getNumberOfTicks();

            for (int i = 0; i < numPointsInCpdFile; i = i + stepSize) {

                ColorPaletteDef.Point slider = getGradationCurvePointAt(i);
                value = slider.getSample();

                if (imageInfo.isLogScaled()) {
                    weight = getLinearWeightFromLogValue(slider.getSample());
                } else {
                    weight = getLinearWeightFromLinearValue(slider.getSample());
                }
                if (isValidWeight(weight)) {
                    ColorBarInfo colorBarInfo = new ColorBarInfo(value, weight, getDecimalPlaces());
                    colorBarInfos.add(colorBarInfo);
                }
            }
        }


        String addThese = getFullCustomAddThesePoints();

        if (addThese != null) {
        if (addThese.length() > 0) {
            String[] formattedValues = addThese.split(",");

            for (String formattedValue : formattedValues) {
                value = Double.valueOf(formattedValue);

                if (imageInfo.isLogScaled()) {
                    weight = getLinearWeightFromLogValue(value);
                } else {
                    weight = getLinearWeightFromLinearValue(value);
                }

                if (isValidWeight(weight)) {
                    ColorBarInfo colorBarInfo = new ColorBarInfo(value, weight, formattedValue);
                    colorBarInfos.add(colorBarInfo);
                }
            }
        }
        }
    }

    private boolean isValidWeight(double weight) {
        return (weight >= 0 && weight <= 1) ? true : false;
    }

    private void initDrawing() {
        final FontMetrics fontMetrics = createFontMetrics();
        final int n = getNumGradationCurvePoints();
        labels = new String[n];
        labelWidths = new int[n];
        int textHeight = fontMetrics.getHeight();
        final double minValue = imageInfo.getColorPaletteDef().getMinDisplaySample();
        final double maxValue = imageInfo.getColorPaletteDef().getMaxDisplaySample();
        double roundFactor = MathUtils.computeRoundFactor(minValue, maxValue, 2);
        for (int i = 0; i < n; i++) {
            ColorPaletteDef.Point slider = getGradationCurvePointAt(i);
            labels[i] = String.valueOf(MathUtils.round(slider.getSample(), roundFactor));
            labelWidths[i] = fontMetrics.stringWidth(labels[i]);
        }

        int headerTextVSpace = 0;
        int headerTextWidth = 0;
        if (hasHeaderText()) {
            headerTextVSpace = textHeight + GAP;
            headerTextWidth = fontMetrics.stringWidth(headerText);
        }

        int legendWidth = 0;
        int legendHeight = 0;
        int maxLabelWidth = 0;


        //DANNY
        int MAXPOINTS = 8;
        int stepSize = n / MAXPOINTS;

        for (int i = 0; i < n; i = i + stepSize) {
            legendWidth += _LABEL_TO_LABEL_GAP + labelWidths[i];
            legendHeight += 2 * textHeight;
            maxLabelWidth = Math.max(labelWidths[i], maxLabelWidth);
        }


//        for (int i = 0; i < n; i++) {
//            legendWidth += _LABEL_GAP + labelWidths[i];
//            legendHeight += 2 * textHeight;
//            maxLabelWidth = Math.max(labelWidths[i], maxLabelWidth);
//        }

        if (orientation == HORIZONTAL) {
            legendWidth = Math.max(legendWidth, _MIN_PALETTE_HEIGHT);
            legendWidth = GAP + Math.max(legendWidth, headerTextWidth) + GAP;
            legendHeight = GAP + headerTextVSpace + _MIN_PALETTE_HEIGHT + _LABEL_GAP + textHeight + GAP;
            legendWidth = Math.max(_MIN_LEGEND_WIDTH, adjust(legendWidth, 16));
            legendHeight = Math.max(_MIN_LEGEND_HEIGHT, adjust(legendHeight, 16));
        } else {
            legendWidth = _MIN_PALETTE_HEIGHT + _LABEL_GAP + maxLabelWidth;
            legendWidth = GAP + Math.max(legendWidth, headerTextWidth) + GAP;
            legendHeight = GAP + headerTextVSpace + Math.max(legendHeight, _MIN_PALETTE_WIDTH) + _LABEL_GAP + textHeight + GAP;
            legendWidth = Math.max(_MIN_LEGEND_HEIGHT, adjust(legendWidth, 16));
            legendHeight = Math.max(_MIN_LEGEND_WIDTH, adjust(legendHeight, 16));
        }

        legendSize = new Dimension(legendWidth, legendHeight);


        final int headerTextSpace = headerText != null ? textHeight + GAP : 0;
        final int labelTextSpace = _LABEL_GAP + textHeight;
        if (orientation == HORIZONTAL) {
            paletteRect = new Rectangle(GAP,
                    GAP + headerTextSpace,
                    legendSize.width - (GAP + GAP),
                    legendSize.height - (GAP + headerTextSpace + labelTextSpace + GAP));
            int paletteGap = Math.max(labelWidths[0], labelWidths[n - 1]) / 2;
            palettePos1 = paletteRect.x + paletteGap;
            palettePos2 = paletteRect.x + paletteRect.width - paletteGap;
        } else {
            paletteRect = new Rectangle(GAP,
                    GAP + headerTextSpace,
                    legendSize.width - (GAP + labelTextSpace + maxLabelWidth + GAP),
                    legendSize.height - (GAP + headerTextSpace + GAP));
            int paletteGap = Math.max(textHeight, _SLIDER_WIDTH) / 2;
            palettePos1 = paletteRect.y + paletteGap;
            palettePos2 = paletteRect.y + paletteRect.height - paletteGap;
        }
        sliderShape = createSliderShape();
    }

    private boolean hasHeaderText() {
        return usingHeader && StringUtils.isNotNullAndNotEmpty(headerText);
    }

    private void draw(Graphics2D g2d) {
        fillBackground(g2d);
        drawHeaderText(g2d);
        drawPalette(g2d);
        drawLabels(g2d);
    }

    private void fillBackground(Graphics2D g2d) {
        Color c = backgroundColor;
        if (isAlphaUsed()) {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), getBackgroundAlpha());
        }
        g2d.setColor(c);
        g2d.fillRect(0, 0, legendSize.width + 1, legendSize.height + 1);
    }

    private void drawHeaderText(Graphics2D g2d) {
        if (hasHeaderText()) {
            final FontMetrics fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(foregroundColor);
            int x0 = GAP;
            int y0 = GAP + fontMetrics.getMaxAscent();

            // DANNY

            String headerStart = new String("  " + headerText + "    ");
            int headerStartWidth = fontMetrics.stringWidth(headerStart);

            g2d.setFont(_DEFAULT_TITLE_FONT);
            g2d.drawString(headerStart, x0, y0);

            g2d.setFont(_DEFAULT_TITLE_UNITS_FONT);
            g2d.drawString(getHeaderUnitsText(), x0 + headerStartWidth, y0);
            //      g2d.drawString("  "+headerText+ "   "+ headerUnitsText, x0, y0);
            //   g2d.drawString("  "+headerText+ " ", x0, y0);

            //     g2d.drawString(headerUnitsText, x0, y0);
            g2d.setFont(_DEFAULT_FONT);
        }
    }

    private void drawPalette(Graphics2D g2d) {
   //     final Color[] palette = imageInfo.getColorPaletteDef().createColorPalette(getRaster());
// todo Danny, when merging with SeaDAS 7.1  use this line below (the commented one) not the old one above
               final Color[] palette = ImageManager.createColorPalette(getRaster().getImageInfo());


        final int x1 = paletteRect.x;
        final int x2 = paletteRect.x + paletteRect.width;
        final int y1 = paletteRect.y;
        final int y2 = paletteRect.y + paletteRect.height;
        final int i1;
        final int i2;
        if (orientation == HORIZONTAL) {
            i1 = x1;
            i2 = x2;
        } else {
            i1 = y1;
            i2 = y2;
        }
        g2d.setStroke(new BasicStroke(1));
        for (int i = i1; i < i2; i++) {
            int divisor = palettePos2 - palettePos1;
            int palIndex;
            if (divisor == 0) {
                palIndex = i < palettePos1 ? 0 : palette.length - 1;
            } else {
                palIndex = (palette.length * (i - palettePos1)) / divisor;
            }
            if (palIndex < 0) {
                palIndex = 0;
            }
            if (palIndex > palette.length - 1) {
                palIndex = palette.length - 1;
            }
            g2d.setColor(palette[palIndex]);
            if (orientation == HORIZONTAL) {
                g2d.drawLine(i, y1, i, y2);
            } else {
                g2d.drawLine(x1, i, x2, i);
            }
        }
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(foregroundColor);
        g2d.draw(paletteRect);
    }


    private void drawLabels(Graphics2D g2d) {

        // customPoints(g2d);

        int numpoints = getNumberOfTicks();
        int tickWidth = 2;

//        if (isEvenDistribution()) {
        drawLabelsUseEvenDistribution(g2d, numpoints, tickWidth);
//        } else {
//            drawLabelsUseExactCpdPoints(g2d, numpoints, tickWidth);
//        }


    }


    private void drawLabelsUseExactCpdPoints(Graphics2D g2d, int numPointsDesired, int tickWidth) {

        g2d.setStroke(new BasicStroke(tickWidth));


        final FontMetrics fontMetrics = g2d.getFontMetrics();
        final int numPointsInCpdFile = getNumGradationCurvePoints();
        g2d.setStroke(new BasicStroke(1));
        Color c1 = (foregroundColor != null ? foregroundColor : Color.black).brighter();
        Color c2 = (backgroundColor != null ? backgroundColor : Color.white).darker();

        //DANNY

        if (numPointsDesired == 3) {
            numPointsDesired = getNumGradationCurvePoints();
        }

        int stepSize = numPointsInCpdFile / numPointsDesired;

        for (int i = 0; i < numPointsInCpdFile; i = i + stepSize) {

            ColorPaletteDef.Point slider = getGradationCurvePointAt(i);
            double value = slider.getSample();
            double normalizedSample = normalizeSample(slider.getSample());

            double weight;
            if (imageInfo.isLogScaled()) {
                weight = getLinearWeightFromLogValue(slider.getSample());
            } else {
                weight = getLinearWeightFromLinearValue(slider.getSample());
            }

            //    double sliderPos = normalizedSample * (palettePos2 - palettePos1);
            double sliderPos = weight * (palettePos2 - palettePos1);


            double tx;
            double ty;
            if (orientation == HORIZONTAL) {
                tx = palettePos1 + sliderPos;
                ty = paletteRect.y + paletteRect.height;
            } else {
                tx = paletteRect.x + paletteRect.width;
                ty = palettePos1 + sliderPos;
            }
            g2d.translate(tx, ty);

            // DANNY
            //   g2d.setPaint(slider.getColor());
            Color sliderColor = new Color(0, 0, 0);
            g2d.setPaint(sliderColor);

            g2d.fill(sliderShape);

            //     int gray = (slider.getColor().getRed() + slider.getColor().getGreen() + slider.getColor().getBlue()) / 3;
            //    g2d.setColor(gray < 128 ? c2 : c1);
            g2d.setColor(sliderColor);
            g2d.draw(sliderShape);


            float x0;
            float y0;
            if (orientation == HORIZONTAL) {
                x0 = -0.5f * labelWidths[i];
                y0 = _LABEL_GAP + fontMetrics.getMaxAscent();
            } else {
                x0 = _LABEL_GAP;
                y0 = fontMetrics.getMaxAscent();
            }
            g2d.setPaint(foregroundColor);
            g2d.drawString(labels[i], x0, y0);

            g2d.translate(-tx, -ty);
        }

    }

    private void drawLabelsUseEvenDistribution(Graphics2D g2d, int numPointsDesired, int tickMarkWidth) {

        Color origColor = g2d.getColor();
        Stroke origStroke = g2d.getStroke();
        Color origPaint = (Color) g2d.getPaint();

        Color tickMarkColor = new Color(0, 0, 0);

        Stroke tickMarkStroke = new BasicStroke(tickMarkWidth);
        g2d.setStroke(tickMarkStroke);

        double translateX, translateY;
        for (ColorBarInfo colorBarInfo : colorBarInfos) {
            String formattedValue = colorBarInfo.getFormattedValue();
            double weight = colorBarInfo.getLocationWeight();

            double tickMarkRelativePosition = weight * (palettePos2 - palettePos1);
            if (orientation == HORIZONTAL) {
                translateX = palettePos1 + tickMarkRelativePosition;
                translateY = paletteRect.y + paletteRect.height;
            } else {
                translateX = paletteRect.x + paletteRect.width;
                translateY = palettePos1 + tickMarkRelativePosition;
            }
            g2d.translate(translateX, translateY);

            g2d.setPaint(foregroundColor);
            g2d.draw(sliderShape);

            final FontMetrics fontMetrics = g2d.getFontMetrics();
            int labelWidth = fontMetrics.stringWidth(formattedValue);
            int labelHeight = fontMetrics.getHeight();

            float x0, y0;
            if (orientation == HORIZONTAL) {
                x0 = -0.5f * labelWidth;
                y0 = _LABEL_GAP + fontMetrics.getMaxAscent();
            } else {
                x0 = _LABEL_GAP;
                y0 = -0.5f * labelHeight + fontMetrics.getMaxAscent();
            }

            g2d.setColor(tickMarkColor);
            g2d.drawString(formattedValue, x0, y0);

            g2d.translate(-translateX, -translateY);
        }

        g2d.setColor(origColor);
        g2d.setStroke(origStroke);
        g2d.setPaint(origPaint);
    }


    private void drawLabelsUseEvenDistributionOld(Graphics2D g2d, int numPointsDesired, int tickMarkWidth) {

        Color tickMarkColor = new Color(0, 0, 0);

        Color origColor = g2d.getColor();
        Stroke origStroke = g2d.getStroke();

        Stroke tickMarkStroke = new BasicStroke(tickMarkWidth);
        g2d.setStroke(tickMarkStroke);

        double tx;
        double ty;
        double value;

        double normalizedDelta = (1.0 / (numPointsDesired - 1.0));
        for (int i = 0; i < numPointsDesired; i++) {
            double weight = i * normalizedDelta;
            double tickMarkPos = weight * (palettePos2 - palettePos1);
            if (orientation == HORIZONTAL) {
                tx = palettePos1 + tickMarkPos;
                ty = paletteRect.y + paletteRect.height;
            } else {
                tx = paletteRect.x + paletteRect.width;
                ty = palettePos1 + tickMarkPos;
            }

            g2d.translate(tx, ty);
//            g2d.setPaint(sliderColor);
//            g2d.fill(sliderShape);
            g2d.setColor(tickMarkColor);
            g2d.draw(sliderShape);

            if (imageInfo.isLogScaled()) {
                value = getLogarithmicValue(getLinearValue(weight));
            } else {
                value = getLinearValue(weight);
            }


            StringBuilder decimalFormatStringBuilder = new StringBuilder("0");
            for (int j = 0; j < getDecimalPlaces(); j++) {
                if (j == 0) {
                    decimalFormatStringBuilder.append(".");
                }
                decimalFormatStringBuilder.append("0");

            }
            String decimalFormatLabel = new DecimalFormat(decimalFormatStringBuilder.toString()).format(value);

            final FontMetrics fontMetrics = g2d.getFontMetrics();
            int labelWidth = fontMetrics.stringWidth(decimalFormatLabel);


            float x0;
            float y0;
            if (orientation == HORIZONTAL) {
                x0 = -0.5f * labelWidth;
                y0 = _LABEL_GAP + fontMetrics.getMaxAscent();
            } else {
                x0 = _LABEL_GAP;
                y0 = fontMetrics.getMaxAscent();
            }
            g2d.setPaint(foregroundColor);

            g2d.drawString(decimalFormatLabel.toString(), x0, y0);

            g2d.translate(-tx, -ty);
        }

        g2d.setColor(origColor);
        g2d.setStroke(origStroke);
    }


    private double getLinearValueFromLogValue(double logValue) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());


        double weight = 0.0;      // todo

        return weight;

    }


    private double getLinearWeightFromLinearValue(double linearValue) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());

        double linearWeight = linearValue / (max - min);

        return linearWeight;
    }


    private double getLinearValue(double linearWeight) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());

        double deltaNormalized = (max - min);

        double linearValue = min + linearWeight * (deltaNormalized);

        return linearValue;
    }


    private double getLinearWeightFromLogValue(double logValue) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());

        double b = Math.log(max / min) / (max - min);
        double a = min / (Math.exp(b * min));

        double linearWeight = Math.log(logValue / a) / b;
        linearWeight = linearWeight / (max - min);

        return linearWeight;
    }


    private double getLogarithmicValue(double linearValue) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());

        double b = Math.log(max / min) / (max - min);
        double a = min / (Math.exp(b * min));
        double logValue = a * Math.exp(b * linearValue);

        return logValue;
    }


    //      double scale = (Math.log(max) - Math.log(min) / (max - min));

    //  double value =  Math.exp(Math.log(min) + (scale * (getLinearValue(weight) - min)));
    //    value = min + (((Math.log(weight)-Math.log(min))/scale)+min);


    private double normalizeSample(double sample) {
        final double minDisplaySample = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double maxDisplaySample = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());
        sample = getRaster().scaleInverse(sample);
        double delta = maxDisplaySample - minDisplaySample;
        if (delta == 0 || Double.isNaN(delta)) {
            delta = 1;
        }
        return (sample - minDisplaySample) / delta;
    }

    private Shape createSliderShape() {
        GeneralPath path = new GeneralPath();
        if (orientation == HORIZONTAL) {
            path.moveTo(0.0F, 0.7F * _SLIDER_HEIGHT);
            path.lineTo(0.0F, 0.0F);
        } else {
            path.moveTo(0.0F, 0.0F);
            path.lineTo(0.7F * _SLIDER_HEIGHT, 0.0F);
        }
        path.closePath();
        return path;
    }

    private int getNumGradationCurvePoints() {
        return getImageInfo().getColorPaletteDef().getNumPoints();
    }

    private ColorPaletteDef.Point getGradationCurvePointAt(int index) {
        return getImageInfo().getColorPaletteDef().getPointAt(index);
    }

    private static int adjust(int size, final int blockSize) {
        return blockSize * (size / blockSize) + (size % blockSize == 0 ? 0 : blockSize);
    }

    private FontMetrics createFontMetrics() {
        BufferedImage bi = createBufferedImage(32, 32);
        final Graphics2D g2d = bi.createGraphics();
        if (font != null) {
            g2d.setFont(font);
        }
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        g2d.dispose();
        return fontMetrics;
    }

    private BufferedImage createBufferedImage(final int width, final int height) {
        return new BufferedImage(width, height,
                isAlphaUsed() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    }


    public String getFullCustomAddThesePoints() {
        return fullCustomAddThesePoints;
    }

    public void setFullCustomAddThesePoints(String fullCustomAddThesePoints) {
        this.fullCustomAddThesePoints = fullCustomAddThesePoints;
    }
}