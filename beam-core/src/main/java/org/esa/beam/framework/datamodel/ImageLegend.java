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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.StringUtils;

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


    public static final String DISTRIB_EVEN_STR = "Use Even Distribution";
    public static final String DISTRIB_EXACT_STR = "Use Palette Distribution";
    public static final String DISTRIB_MANUAL_STR = "Use Manually Entered Points";


    private static final int BORDER_GAP = 20;   // TITLE_TO_PALETTE_GAP
    //DANNY
    private static final int LABEL_GAP = 10;      // LABEL_TO_COLORBAR BORDER_GAP
    private static final int HEADER_GAP = 10;      // HEADER_TO_COLORBAR BORDER_GAP
    private static final int _LABEL_TO_LABEL_GAP = 40;


    // private static final int LABEL_GAP = 12;
    private static final int _SLIDER_WIDTH = 10;
    private static final int _SLIDER_HEIGHT = 14;

    private static final int MIN_HORIZONTAL_COLORBAR_WIDTH = 550;
    private static final int MIN_HORIZONTAL_COLORBAR_HEIGHT = 24;

    private static final int MIN_VERTICAL_COLORBAR_WIDTH = 24;
    private static final int MIN_VERTICAL_COLORBAR_HEIGHT = 550;

    private static final int _MIN_LEGEND_WIDTH = 600;
    private static final int _MIN_LEGEND_HEIGHT = 48;

    // DANNY
    private static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
    private static final Font _DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font _DEFAULT_TITLE_UNITS_FONT = new Font("SansSerif", Font.ITALIC, 14);
    //   private static final Font DEFAULT_LABEL_FONT = new Font("Arial", Font.BOLD, 14);

    // Independent attributes (Properties)
    private final ImageInfo imageInfo;
    private final RasterDataNode raster;
    private boolean usingHeader;
    private String headerText;
    private String headerUnitsText;
    private int orientation;
    private String distributionType;
    private Font font;
    private int numberOfTicks;
    private Color foregroundColor;
    private Color backgroundColor;
    private boolean backgroundTransparencyEnabled;
    private float backgroundTransparency;
    private boolean antiAliasing;
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
    private int tickWidth;

    public ImageLegend(ImageInfo imageInfo, RasterDataNode raster) {
        this.imageInfo = imageInfo;
        this.raster = raster;
        usingHeader = true;
        headerText = "";

        orientation = HORIZONTAL;
        font = DEFAULT_LABEL_FONT;
        backgroundColor = Color.white;
        foregroundColor = Color.black;
        backgroundTransparency = 0.0f;
        antiAliasing = true;
        decimalPlaces = 1;
        setFullCustomAddThesePoints("");
        tickWidth = 1;

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

    public String getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(String distributionType) {
        this.distributionType = distributionType;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }


    public Font getLabelFont() {
        return font;
    }

    public void setLabelFont(Font font) {
        this.font = font;
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
        return antiAliasing;
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antiAliasing = antialiasing;
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
        if (isAntialiasing()) {
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

        if (DISTRIB_EVEN_STR.equals(getDistributionType())) {
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
        } else if (DISTRIB_EXACT_STR.equals(getDistributionType())) {
            final int numPointsInCpdFile = getNumGradationCurvePoints();
            int stepSize = 1;
            //    int stepSize = numPointsInCpdFile / getNumberOfTicks();

            for (int i = 0; i < numPointsInCpdFile; i = i + stepSize) {

                ColorPaletteDef.Point slider = getGradationCurvePointAt(i);
                value = getRaster().scaleInverse(slider.getSample());

                if (imageInfo.isLogScaled()) {
                    weight = getLinearWeightFromLogValue(value);
                } else {
                    weight = getLinearWeightFromLinearValue(value);
                }
                if (isValidWeight(weight)) {
                    ColorBarInfo colorBarInfo = new ColorBarInfo(value, weight, getDecimalPlaces());
                    colorBarInfos.add(colorBarInfo);
                }
            }
        } else if (DISTRIB_MANUAL_STR.equals(getDistributionType())) {
            String addThese = getFullCustomAddThesePoints();

            if (addThese != null && addThese.length() > 0) {
                String[] formattedValues = addThese.split(",");

                for (String formattedValue : formattedValues) {
                    value = Double.valueOf(formattedValue);
                    value = getRaster().scaleInverse(value);

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
        // weight could be slightly above  1 or below 0 due to rounding issue so use a small buffer
        // to make sure the end points are accepted
        double buffer = .01;

        return (weight >= (0 - buffer) && weight <= (1 + buffer)) ? true : false;
    }


    private void initDrawing() {

        final BufferedImage bufferedImage = createBufferedImage(100, 100);
        final Graphics2D g2dTmp = bufferedImage.createGraphics();

        g2dTmp.setFont(getLabelFont());

        Dimension headerRequiredDimension = getHeaderTextRequiredDimension(g2dTmp);

        if (orientation == HORIZONTAL) {

            double discreteBooster = 0;

            Dimension labelsRequiredDimension = getHorizontalLabelsRequiredDimension(g2dTmp);


            double requiredWidth = Math.max(labelsRequiredDimension.getWidth(),
                    headerRequiredDimension.getWidth());

            requiredWidth = Math.max(requiredWidth, MIN_HORIZONTAL_COLORBAR_WIDTH);
            requiredWidth = BORDER_GAP + requiredWidth + BORDER_GAP;

            final int n = getNumGradationCurvePoints();
            // todo isDiscrete goes here

            if (n > 1 && imageInfo.getColorPaletteDef().isDiscrete()) {
                discreteBooster = labelsRequiredDimension.getWidth() / (n - 1);
                requiredWidth += discreteBooster;
            }

            int requiredHeaderHeight = (int) Math.ceil(headerRequiredDimension.getHeight());
            int requiredLabelsHeight = (int) Math.ceil(labelsRequiredDimension.getHeight());


            int requiredHeight = BORDER_GAP
                    + requiredHeaderHeight
                    + HEADER_GAP
                    + MIN_HORIZONTAL_COLORBAR_HEIGHT
                    + LABEL_GAP
                    + requiredLabelsHeight
                    + BORDER_GAP;


            legendSize = new Dimension((int) requiredWidth, requiredHeight);

            double firstLabelWidth = getSingleLabelRequiredDimension(g2dTmp, 0).getWidth();
            int firstLabelOverhangWidth = (int) Math.ceil(firstLabelWidth / 2.0);

            double lastLabelWidth = getSingleLabelRequiredDimension(g2dTmp, colorBarInfos.size() - 1).getWidth();
            int lastLabelOverhangWidth = (int) Math.ceil(lastLabelWidth / 2.0);


            paletteRect = new Rectangle(BORDER_GAP + firstLabelOverhangWidth,
                    BORDER_GAP + requiredHeaderHeight + HEADER_GAP,
                    legendSize.width - BORDER_GAP - BORDER_GAP - firstLabelOverhangWidth - lastLabelOverhangWidth,
                    MIN_HORIZONTAL_COLORBAR_HEIGHT);

            //DANNY
            int paletteGap = 0;
            palettePos1 = paletteRect.x + paletteGap;
            palettePos2 = paletteRect.x + paletteRect.width - (int) discreteBooster;

            // todo a piece of the old beam code, see what adjust does
            //   Math.max(_MIN_LEGEND_WIDTH, adjust(legendWidth, 16));

        } else {

            Dimension labelsRequiredDimension = getVerticalLabelsRequiredDimension(g2dTmp);

            double requiredWidth = MIN_VERTICAL_COLORBAR_WIDTH + LABEL_GAP + labelsRequiredDimension.getWidth();

            requiredWidth = Math.max(requiredWidth, headerRequiredDimension.getWidth());
            requiredWidth = BORDER_GAP + requiredWidth + BORDER_GAP;

            int requiredHeaderHeight = (int) Math.ceil(headerRequiredDimension.getHeight());
            int requiredLabelsHeight = (int) Math.ceil(labelsRequiredDimension.getHeight());


            int requiredHeight = BORDER_GAP
                    + requiredHeaderHeight
                    + HEADER_GAP
                    + requiredLabelsHeight
                    + BORDER_GAP;


            legendSize = new Dimension((int) requiredWidth, requiredHeight);


            double firstLabelHeight = getSingleLabelRequiredDimension(g2dTmp, 0).getHeight();
            int labelOverhangHeight = (int) Math.ceil(firstLabelHeight / 2.0);


            paletteRect = new Rectangle(BORDER_GAP,
                    requiredHeaderHeight + HEADER_GAP + labelOverhangHeight,
                    MIN_VERTICAL_COLORBAR_WIDTH,
                    legendSize.height - BORDER_GAP - BORDER_GAP - labelOverhangHeight - labelOverhangHeight);


            int paletteGap = 0;
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


    private Dimension getHeaderTextRequiredDimension(Graphics2D g2d) {

        double width = 0;
        double height = 0;


        int UNITS_GAP_FACTOR = 3;

        if (hasHeaderText()) {

            Font originalFont = g2d.getFont();

            g2d.setFont(_DEFAULT_TITLE_FONT);
            Rectangle2D headerTextRectangle = g2d.getFontMetrics().getStringBounds(headerText, g2d);
            width += headerTextRectangle.getWidth();

            Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("A", g2d);
            width += (UNITS_GAP_FACTOR * singleLetter.getWidth());

            g2d.setFont(_DEFAULT_TITLE_UNITS_FONT);
            Rectangle2D unitsTextRectangle = g2d.getFontMetrics().getStringBounds(getHeaderUnitsText(), g2d);
            width += unitsTextRectangle.getWidth();

            height = singleLetter.getHeight();

            g2d.setFont(originalFont);
        }

        return new Dimension((int) Math.ceil(width), (int) Math.ceil(height));
    }


    private Dimension getHorizontalLabelsRequiredDimension(Graphics2D g2d) {

        double width = 0;
        double height = 0;

        if (colorBarInfos.size() > 0) {

            int INTER_LABEL_GAP_FACTOR = 4;

            Font originalFont = g2d.getFont();
            g2d.setFont(getLabelFont());

            double totalLabelsNoGapsWidth = 0;

            for (ColorBarInfo colorBarInfo : colorBarInfos) {
                Rectangle2D labelRectangle = g2d.getFontMetrics().getStringBounds(colorBarInfo.getFormattedValue(), g2d);
                totalLabelsNoGapsWidth += labelRectangle.getWidth();
            }

            Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("A", g2d);
            double interLabelGap = (INTER_LABEL_GAP_FACTOR * singleLetter.getWidth());

            double totalLabelGapsWidth = (colorBarInfos.size() - 1) * interLabelGap;

            width = totalLabelsNoGapsWidth + totalLabelGapsWidth;

            height = singleLetter.getHeight();

            g2d.setFont(originalFont);
        }

        return new Dimension((int) Math.ceil(width), (int) Math.ceil(height));
    }


    private Dimension getVerticalLabelsRequiredDimension(Graphics2D g2d) {

        double width = 0;
        double height = 0;

        if (colorBarInfos.size() > 0) {

            int INTER_LABEL_GAP_FACTOR = 4;

            Font originalFont = g2d.getFont();
            g2d.setFont(getLabelFont());

            double totalLabelsNoGapHeight = 0;

            for (ColorBarInfo colorBarInfo : colorBarInfos) {
                Rectangle2D labelRectangle = g2d.getFontMetrics().getStringBounds(colorBarInfo.getFormattedValue(), g2d);
                totalLabelsNoGapHeight += labelRectangle.getHeight();
                width = Math.max(width, labelRectangle.getWidth());
            }

            Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("A", g2d);
            double interLabelGap = (INTER_LABEL_GAP_FACTOR * singleLetter.getHeight());

            double totalLabelGapsHeight = (colorBarInfos.size() - 1) * interLabelGap;

            height = totalLabelsNoGapHeight + totalLabelGapsHeight;

            g2d.setFont(originalFont);
        }

        return new Dimension((int) Math.ceil(width), (int) Math.ceil(height));
    }


    private Dimension getSingleLabelRequiredDimension(Graphics2D g2d, int colorBarInfoIndex) {

        double width = 0;
        double height = 0;

        if (colorBarInfos.size() > 0 && colorBarInfos.size() > colorBarInfoIndex) {
            Font originalFont = g2d.getFont();
            g2d.setFont(getLabelFont());

            ColorBarInfo colorBarInfo = colorBarInfos.get(colorBarInfoIndex);
            Rectangle2D labelRectangle = g2d.getFontMetrics().getStringBounds(colorBarInfo.getFormattedValue(), g2d);
            width = labelRectangle.getWidth();
            height = labelRectangle.getHeight();


            g2d.setFont(originalFont);
        }

        return new Dimension((int) Math.ceil(width), (int) Math.ceil(height));
    }


    private void drawHeaderText(Graphics2D g2d) {
        if (hasHeaderText()) {
            Font origFont = g2d.getFont();

            final FontMetrics fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(foregroundColor);
            int x0 = BORDER_GAP;
            int y0 = BORDER_GAP; // + fontMetrics.getMaxAscent();

            g2d.setFont(_DEFAULT_TITLE_FONT);
            Rectangle2D headerTextRectangle = g2d.getFontMetrics().getStringBounds(headerText, g2d);
            g2d.drawString(headerText, x0, y0);

            Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("A", g2d);
            int gap = (int) (2 * singleLetter.getWidth());

            g2d.setFont(_DEFAULT_TITLE_UNITS_FONT);
            g2d.drawString(getHeaderUnitsText(), (int) (x0 + headerTextRectangle.getWidth() + gap), y0);

            g2d.setFont(origFont);
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

        Color origColor = g2d.getColor();
        Stroke origStroke = g2d.getStroke();
        Color origPaint = (Color) g2d.getPaint();

        Color tickMarkColor = new Color(0, 0, 0);

        Stroke tickMarkStroke = new BasicStroke(tickWidth);
        g2d.setStroke(tickMarkStroke);

        double translateX, translateY;
        for (ColorBarInfo colorBarInfo : colorBarInfos) {
            String formattedValue = colorBarInfo.getFormattedValue();
            double weight = colorBarInfo.getLocationWeight();

            double tickMarkRelativePosition = weight * (palettePos2 - palettePos1);
            if (orientation == HORIZONTAL) {
                translateX = palettePos1 + tickMarkRelativePosition;
                translateY = paletteRect.y + paletteRect.height;

                // make sure end tickmarks are placed within palette
                if (translateX <= palettePos1) {
                    translateX = palettePos1;
                }

                if (translateX >= palettePos2) {
                    translateX = palettePos2;
                }

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
                y0 = LABEL_GAP + fontMetrics.getMaxAscent();
            } else {
                x0 = LABEL_GAP;
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


    private double getLinearWeightFromLinearValue(double linearValue) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());

        double linearWeight = (linearValue - min) / (max - min);

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


    private double getLogarithmicValue(double linearWeight) {
        final double min = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMinDisplaySample());
        final double max = getRaster().scaleInverse(getImageInfo().getColorPaletteDef().getMaxDisplaySample());

        double b = Math.log(max / min) / (max - min);
        double a = min / (Math.exp(b * min));
        double logValue = a * Math.exp(b * linearWeight);

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