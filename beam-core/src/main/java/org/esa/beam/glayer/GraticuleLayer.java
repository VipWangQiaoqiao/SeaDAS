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
package org.esa.beam.glayer;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerTypeRegistry;
import com.bc.ceres.grender.Rendering;
import com.bc.ceres.grender.Viewport;
import org.esa.beam.framework.datamodel.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * @author Marco Zuehlke
 * @version $Revision$ $Date$
 * @since BEAM 4.2
 */
public class GraticuleLayer extends Layer {

    private static final GraticuleLayerType LAYER_TYPE = LayerTypeRegistry.getLayerType(GraticuleLayerType.class);

    private RasterDataNode raster;

    private ProductNodeHandler productNodeHandler;
    private Graticule graticule;

    int FONT_SIZING_AUTOMATION_KEY = 0;
    double FONT_SIZE_AUTO_PERCENT = 0.025;
    int FONT_SIZE_MINIMUM = 1;


    // DANNY new fields to add to preferences


    private boolean dashedLine = true;
    private double dashedLinePhase = 9.0;
    private boolean showLatitudeLabelsEastside = true;
    private boolean showLatitudeLabelsWestside = true;
    private boolean showLongitudeLabelsNorthside = true;
    private boolean showLongitudeLabelsSouthside = true;
    private boolean showBorder = true;
    private boolean showTickMarks = true;
    private boolean tickMarksInside = true;
    private boolean showGridLines = true;
    private int minorStep = 4;
    private double rotationThetaNorthLabels = 60;   // in degrees
    private double rotationThetaWestLabels = 60;   // in degrees
    int textOutwardsOffset = 0;
    int textSidewardsOffset = 0;
    boolean textOutside = false;
    int gridLineWidth = GraticuleLayerType.DEFAULT_LINE_WIDTH;
    int borderWidth = GraticuleLayerType.DEFAULT_BORDER_WIDTH;

    double borderTransparency = 0.0;
    double tickmarkLength = 1;

    public GraticuleLayer(RasterDataNode raster) {
        this(LAYER_TYPE, raster, initConfiguration(LAYER_TYPE.createLayerConfig(null), raster));
    }

    public GraticuleLayer(GraticuleLayerType type, RasterDataNode raster, PropertySet configuration) {
        super(type, configuration);
        setName("Graticule Layer");
        this.raster = raster;

        productNodeHandler = new ProductNodeHandler();
        raster.getProduct().addProductNodeListener(productNodeHandler);

        // DANNY master transparency
        //      setTransparency(0.5);
        setTransparency(0.0);

    }

    private static PropertySet initConfiguration(PropertySet configurationTemplate, RasterDataNode raster) {
        configurationTemplate.setValue(GraticuleLayerType.PROPERTY_NAME_RASTER, raster);
        return configurationTemplate;
    }

    private Product getProduct() {
        return getRaster().getProduct();
    }

    RasterDataNode getRaster() {
        return raster;
    }

    @Override
    public void renderLayer(Rendering rendering) {

        getUserValues();

        if (graticule == null) {
            graticule = Graticule.create(raster,
                    getResAuto(),
                    getResPixels(),
                    (float) getResLat(),
                    (float) getResLon());
        }
        if (graticule != null) {


            final Graphics2D g2d = rendering.getGraphics();
            final Viewport vp = rendering.getViewport();
            final AffineTransform transformSave = g2d.getTransform();
            try {
                final AffineTransform transform = new AffineTransform();
                transform.concatenate(transformSave);
                transform.concatenate(vp.getModelToViewTransform());
                transform.concatenate(raster.getSourceImage().getModel().getImageToModelTransform(0));
                g2d.setTransform(transform);


                final GeneralPath[] linePaths = graticule.getLinePaths();
                if (linePaths != null && showGridLines) {
                    drawLinePaths(g2d, linePaths);
                }

                if (showBorder) {
                    drawBorderPath(g2d, raster);
                }

                final Graticule.TextGlyph[] textGlyphsNorth = graticule.getTextGlyphsNorth();
                if (textGlyphsNorth != null) {
                    if (showLongitudeLabelsNorthside) {
                        if (showTickMarks) {
                            drawTickMarks(g2d, graticule.getTickPointsNorth(), Graticule.TextLocation.NORTH);
                        }

                        drawTextLabels(g2d, textGlyphsNorth, Graticule.TextLocation.NORTH);
                    }
                }

                final Graticule.TextGlyph[] textGlyphsSouth = graticule.getTextGlyphsSouth();
                if (textGlyphsSouth != null) {
                    if (showLongitudeLabelsSouthside) {
                        if (showTickMarks) {
                            drawTickMarks(g2d, graticule.getTickPointsSouth(), Graticule.TextLocation.SOUTH);
                        }
                        drawTextLabels(g2d, textGlyphsSouth, Graticule.TextLocation.SOUTH);
                    }
                }

                if (showLatitudeLabelsWestside) {
                    final Graticule.TextGlyph[] textGlyphsWest = graticule.getTextGlyphsWest();

                    if (textGlyphsWest != null) {
                        if (showTickMarks) {
                            drawTickMarks(g2d, graticule.getTickPointsWest(), Graticule.TextLocation.WEST);
                        }
                        drawTextLabels(g2d, textGlyphsWest, Graticule.TextLocation.WEST);
                    }
                }

                final Graticule.TextGlyph[] textGlyphsEast = graticule.getTextGlyphsEast();
                if (textGlyphsEast != null) {

                    if (showLatitudeLabelsEastside) {
                        if (showTickMarks) {
                            drawTickMarks(g2d, graticule.getTickPointsEast(), Graticule.TextLocation.EAST);
                        }
                        drawTextLabels(g2d, textGlyphsEast, Graticule.TextLocation.EAST);
                    }
                }


                if (getTextCornerLeftLatEnabled()) {
                    if (showTickMarks) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.WEST);
                    }
                    drawLeftSideLatCornerLabels(g2d);
                }

                if (getTextCornerRightLatEnabled()) {
                    if (showTickMarks) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.EAST);
                    }
                    drawRightSideLatCornerLabels(g2d);
                }

                if (getTextCornerTopLonEnabled()) {
                    if (showTickMarks) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.NORTH);
                    }
                    drawNorthSideLonCornerLabels(g2d);
                }

                if (getTextCornerBottomLonEnabled()) {
                    if (showTickMarks) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.SOUTH);
                    }
                    drawSouthSideLonCornerLabels(g2d);
                }


            } finally {
                g2d.setTransform(transformSave);
            }
        }
    }


    private void drawLeftSideLatCornerLabels(Graphics2D g2d) {

        final ArrayList<Graticule.TextGlyph> textGlyphArrayList = new ArrayList<>();

        Graticule.TextGlyph textGlyph = graticule.getTextGlyphsLatCorners()[Graticule.TOP_LEFT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        textGlyph = graticule.getTextGlyphsLatCorners()[Graticule.BOTTOM_LEFT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        final Graticule.TextGlyph[] textGlyphs = textGlyphArrayList.toArray(new Graticule.TextGlyph[textGlyphArrayList.size()]);
        if (textGlyphs != null) {
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.WEST);
        }
    }

    private void drawRightSideLatCornerLabels(Graphics2D g2d) {

        final ArrayList<Graticule.TextGlyph> textGlyphArrayList = new ArrayList<>();

        Graticule.TextGlyph textGlyph = graticule.getTextGlyphsLatCorners()[Graticule.TOP_RIGHT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        textGlyph = graticule.getTextGlyphsLatCorners()[Graticule.BOTTOM_RIGHT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        final Graticule.TextGlyph[] textGlyphs = textGlyphArrayList.toArray(new Graticule.TextGlyph[textGlyphArrayList.size()]);
        if (textGlyphs != null) {
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.EAST);
        }
    }

    private void drawNorthSideLonCornerLabels(Graphics2D g2d) {

        final ArrayList<Graticule.TextGlyph> textGlyphArrayList = new ArrayList<>();

        Graticule.TextGlyph textGlyph = graticule.getTextGlyphsLonCorners()[Graticule.TOP_LEFT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        textGlyph = graticule.getTextGlyphsLonCorners()[Graticule.TOP_RIGHT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        final Graticule.TextGlyph[] textGlyphs = textGlyphArrayList.toArray(new Graticule.TextGlyph[textGlyphArrayList.size()]);
        if (textGlyphs != null) {
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.NORTH);
        }
    }

    private void drawSouthSideLonCornerLabels(Graphics2D g2d) {

        final ArrayList<Graticule.TextGlyph> textGlyphArrayList = new ArrayList<>();

        Graticule.TextGlyph textGlyph = graticule.getTextGlyphsLonCorners()[Graticule.BOTTOM_LEFT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        textGlyph = graticule.getTextGlyphsLonCorners()[Graticule.BOTTOM_RIGHT_CORNER_INDEX];
        if (textGlyph != null) {
            textGlyphArrayList.add(textGlyph);
        }

        final Graticule.TextGlyph[] textGlyphs = textGlyphArrayList.toArray(new Graticule.TextGlyph[textGlyphArrayList.size()]);
        if (textGlyphs != null) {
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.SOUTH);
        }
    }


    private void getUserValues() {

        dashedLine = isLineDashed();

        showLatitudeLabelsEastside = isTextEnabledEast();
        showLatitudeLabelsWestside = isTextEnabledWest();
        showLongitudeLabelsNorthside = isTextEnabledNorth();
        showLongitudeLabelsSouthside = isTextEnabledSouth();
        showBorder = isBorderEnabled();
        showGridLines = isLineEnabled();
        //    minorStep = 4;
        //       rotationThetaNorthLabels = 90 - getTextRotationNorth();   // in degrees
        rotationThetaNorthLabels = getTextRotationNorth();   // in degrees


        //   rotationThetaSouthLabels = 90 - getTextRotationSouth();   // in degrees
        //     rotationThetaWestLabels = 90 - getTextRotationWest();   // in degrees
        rotationThetaWestLabels = getTextRotationWest();   // in degrees


        //  rotationThetaEastLabels = 90 - getTextRotationEast();   // in degrees
        textOutwardsOffset = getTextOffsetOutward();
        textSidewardsOffset = getTextOffsetSideward();


        double min = Math.min(raster.getRasterHeight(), raster.getRasterWidth());

        textOutside = !isTextInside();


        gridLineWidth = getLineWidth();
        if (gridLineWidth < 1) {
            gridLineWidth = (int) Math.ceil(0.001 * min);
            if (gridLineWidth < 1) {
                gridLineWidth = 1;
            }
        }

        borderWidth = getBorderWidth();
        if (borderWidth < 1) {
            borderWidth = (int) Math.ceil(0.002 * min);
            if (borderWidth < 1) {
                borderWidth = 1;
            }
        }

        dashedLinePhase = getLineDashedPhase();
        if (dashedLinePhase < 1) {
            dashedLinePhase = Math.round(0.01 * min);
            if (dashedLinePhase < 1) {
                dashedLinePhase = 1;
            }
        }

        tickmarkLength = Math.round(0.01 * min);
        showTickMarks = isShowTickMarks();
        tickMarksInside = isTickMarkInside();

    }

    private void drawLinePaths(Graphics2D g2d, final GeneralPath[] linePaths) {
        Composite oldComposite = null;
        if (getLineTransparency() > 0.0) {
            oldComposite = g2d.getComposite();
            g2d.setComposite(getAlphaComposite(getLineTransparency()));
        }
        g2d.setPaint(getLineColor());


        Stroke drawingStroke;


        if (dashedLine) {
            drawingStroke = new BasicStroke((float) gridLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{(float) dashedLinePhase}, 0);
        } else {

            drawingStroke = new BasicStroke((float) gridLineWidth);
        }

        g2d.setStroke(drawingStroke);
        for (GeneralPath linePath : linePaths) {
            g2d.draw(linePath);
        }
        if (oldComposite != null) {
            g2d.setComposite(oldComposite);
        }


    }

    private void drawCornerTickMarks(Graphics2D g2d, RasterDataNode raster, Graticule.TextLocation textLocation) {

        PixelPos pixelPos1 = null;
        PixelPos pixelPos2 = null;

        switch (textLocation) {
            case NORTH:
                pixelPos1 = new PixelPos(0, 0);
                pixelPos2 = new PixelPos(raster.getRasterWidth(), 0);
                break;
            case SOUTH:
                pixelPos1 = new PixelPos(0, raster.getRasterHeight());
                pixelPos2 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight());
                break;
            case WEST:
                pixelPos1 = new PixelPos(0, 0);
                pixelPos2 = new PixelPos(0, raster.getRasterHeight());
                break;
            case EAST:
                pixelPos1 = new PixelPos(raster.getRasterWidth(), 0);
                pixelPos2 = new PixelPos(raster.getRasterWidth(), raster.getRasterHeight());
                break;
        }

        if (pixelPos1 != null && pixelPos2 != null) {
            PixelPos pixelPos[];
            pixelPos = new PixelPos[2];
            pixelPos[0] = pixelPos1;
            pixelPos[1] = pixelPos2;

            drawTickMarks(g2d, pixelPos, textLocation);
        }
    }


    private void drawBorderPath(Graphics2D g2d, RasterDataNode raster) {

        Composite oldComposite = null;
        if (getLineTransparency() > 0.0) {
            oldComposite = g2d.getComposite();
            g2d.setComposite(getAlphaComposite(borderTransparency));
        }
        g2d.setPaint(getBorderColor());


        Stroke drawingStroke = new BasicStroke((float) borderWidth);


        g2d.setStroke(drawingStroke);


        GeneralPath westBorderPath = new GeneralPath();
        westBorderPath.moveTo(0, 0);
        westBorderPath.lineTo(0, raster.getRasterHeight());
        westBorderPath.closePath();
        g2d.draw(westBorderPath);

        GeneralPath northBorderPath = new GeneralPath();
        northBorderPath.moveTo(0, raster.getRasterHeight());
        northBorderPath.lineTo(raster.getRasterWidth(), raster.getRasterHeight());
        northBorderPath.closePath();
        g2d.draw(northBorderPath);

        GeneralPath eastBorderPath = new GeneralPath();
        eastBorderPath.moveTo(raster.getRasterWidth(), raster.getRasterHeight());
        eastBorderPath.lineTo(raster.getRasterWidth(), 0);
        eastBorderPath.closePath();
        g2d.draw(eastBorderPath);

        GeneralPath southBorderPath = new GeneralPath();
        southBorderPath.moveTo(raster.getRasterWidth(), 0);
        southBorderPath.lineTo(0, 0);
        southBorderPath.closePath();
        g2d.draw(southBorderPath);


        if (oldComposite != null) {
            g2d.setComposite(oldComposite);
        }
    }


    private void drawTextLabels(Graphics2D g2d, final Graticule.TextGlyph[] textGlyphs, Graticule.TextLocation textLocation) {

        int fontStyle = Font.PLAIN;
        if (isTextFontItalic()) {
            fontStyle = Font.ITALIC;
        }

        Font font = new Font("SansSerif", fontStyle, getTextFontSize());
        g2d.setFont(font);

        if (getTextBgTransparency() < 1.0 && getTextBgTransparency() >= 0 && !textOutside) {
            drawRectangle(g2d, textGlyphs, textLocation);
        }

        Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
        double letterWidth = singleLetter.getWidth();
        float halfLetterWidth = (float) (letterWidth / 2.0);


        g2d.setPaint(getTextFgColor());
        for (Graticule.TextGlyph glyph : textGlyphs) {
            g2d.translate(glyph.getX(), glyph.getY());
            g2d.rotate(glyph.getAngle());

            Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
            float width = (float) labelBounds.getWidth();
            float height = (float) labelBounds.getHeight();

            float shiftSideways = height / 3;

            float halfLabelWidth = width / 2;


            AffineTransform orig = g2d.getTransform();


            if (textOutside) {
                if (textLocation == Graticule.TextLocation.NORTH) {
                    double theta = (rotationThetaNorthLabels / 180) * Math.PI;

                    float xOffset = 0;
                    float yOffset = 0;
                    double verticalShift = letterWidth / 2;

                    if (isShowTickMarks() && !tickMarksInside) {
                        verticalShift += tickmarkLength;
                    }

                    if (rotationThetaNorthLabels > 85) {
                        xOffset = -halfLabelWidth;
                    }

                    if (rotationThetaNorthLabels < 5) {
                        yOffset = height / 3;
                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = -1 * (float) (verticalShift * Math.sin(theta));

                    g2d.rotate(-1 * Math.PI + theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }

                if (textLocation == Graticule.TextLocation.SOUTH) {
                    double theta = (rotationThetaNorthLabels / 180) * Math.PI;

                    float xOffset = -width;
                    float yOffset = 2 * height / 3;
                    double verticalShift = -letterWidth / 2;

                    if (isShowTickMarks() && !tickMarksInside) {
                        verticalShift -= tickmarkLength;
                    }

                    if (rotationThetaNorthLabels > 85) {
                        xOffset = xOffset + halfLabelWidth;
                    }

                    if (rotationThetaNorthLabels < 5) {
                        yOffset = yOffset - height / 3;
                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = -1 * (float) (verticalShift * Math.sin(theta));

                    g2d.rotate(theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }

                if (textLocation == Graticule.TextLocation.EAST) {
                    double theta = (rotationThetaWestLabels / 180) * Math.PI;

                    float xOffset = 0;
                    float yOffset = 2 * height / 3;
                    double verticalShift = letterWidth / 2;

                    if (isShowTickMarks() && !tickMarksInside) {
                        verticalShift += tickmarkLength;
                    }

                    if (rotationThetaWestLabels > 85) {
                        xOffset = -halfLabelWidth;
                    }

                    if (rotationThetaWestLabels < 5) {
                        yOffset = height / 3;
                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = (float) (verticalShift * Math.sin(theta));


                    g2d.rotate(-1 * Math.PI - theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }


                if (textLocation == Graticule.TextLocation.WEST) {
                    double theta = (rotationThetaWestLabels / 180) * Math.PI;

                    float xOffset = -width;
                    float yOffset = 0;
                    double verticalShift = -letterWidth / 2;

                    if (isShowTickMarks() && !tickMarksInside) {
                        verticalShift -= tickmarkLength;
                    }

                    if (rotationThetaWestLabels > 85) {
                        xOffset = xOffset + halfLabelWidth;
                    }

                    if (rotationThetaWestLabels < 5) {
                        yOffset = yOffset + height / 3;
                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = (float) (verticalShift * Math.sin(theta));


                    g2d.rotate(-theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }
            } else {
                if (textLocation == Graticule.TextLocation.WEST ||
                        textLocation == Graticule.TextLocation.SOUTH) {
                    g2d.drawString(glyph.getText(), halfLetterWidth - textOutwardsOffset, shiftSideways + textSidewardsOffset);
                } else {
                    g2d.rotate(-Math.PI);
                    g2d.drawString(glyph.getText(), -width - halfLetterWidth + textOutwardsOffset, shiftSideways + textSidewardsOffset);
                }
            }

            g2d.setTransform(orig);

            g2d.rotate(-glyph.getAngle());
            g2d.translate(-glyph.getX(), -glyph.getY());
        }
    }

    private void drawRectangle(Graphics2D g2d, final Graticule.TextGlyph[] textGlyphs, Graticule.TextLocation textLocation) {

        Font font = new Font("SansSerif", Font.ITALIC, getTextFontSize());
        g2d.setFont(font);

        if (getTextBgTransparency() < 1.0 && getTextBgTransparency() >= 0.0) {
            Composite oldComposite = null;
            if (getTextBgTransparency() > 0.0) {
                oldComposite = g2d.getComposite();
                g2d.setComposite(getAlphaComposite(getTextBgTransparency()));
            }

            g2d.setPaint(getTextBgColor());
            g2d.setStroke(new BasicStroke(0));

            for (Graticule.TextGlyph glyph : textGlyphs) {
                g2d.translate(glyph.getX(), glyph.getY());
                g2d.rotate(glyph.getAngle());

                Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
                float width = (float) labelBounds.getWidth();
                float height = (float) labelBounds.getHeight();

                float shiftSideways = height / 3;

                Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
                double letterWidth = singleLetter.getWidth();
                float halfLetterWidth = (float) (letterWidth / 2.0);

                if (textLocation == Graticule.TextLocation.NORTH ||
                        textLocation == Graticule.TextLocation.WEST ||
                        textLocation == Graticule.TextLocation.SOUTH) {

                    labelBounds.setRect(labelBounds.getX() + halfLetterWidth - textOutwardsOffset - 1,
                            labelBounds.getY() + shiftSideways + textSidewardsOffset - 1,
                            labelBounds.getWidth(),
                            labelBounds.getHeight());


                } else {
                    AffineTransform orig = g2d.getTransform();
                    g2d.rotate(-Math.PI);

                    labelBounds.setRect(labelBounds.getX() + halfLetterWidth - textOutwardsOffset - 1,
                            labelBounds.getY() + shiftSideways + textSidewardsOffset - 1,
                            labelBounds.getWidth(),
                            labelBounds.getHeight());

                    g2d.setTransform(orig);
                }

                g2d.fill(labelBounds);

                g2d.rotate(-glyph.getAngle());
                g2d.translate(-glyph.getX(), -glyph.getY());
            }


            if (oldComposite != null) {
                g2d.setComposite(oldComposite);
            }
        }
    }


    private void drawTickMarks(Graphics2D g2d, final PixelPos[] pixelPoses, Graticule.TextLocation textLocation) {

        Composite oldComposite = g2d.getComposite();

        Stroke drawingStroke = new BasicStroke((float) gridLineWidth);
        g2d.setStroke(drawingStroke);

        for (PixelPos pixelPos : pixelPoses) {

            GeneralPath path = new GeneralPath();
            path.moveTo(pixelPos.getX(), pixelPos.getY());


            switch (textLocation) {
                case NORTH:
                    if (tickMarksInside) {
                        path.lineTo(pixelPos.getX(), pixelPos.getY() + tickmarkLength);
                    } else {
                        path.lineTo(pixelPos.getX(), pixelPos.getY() - tickmarkLength);
                    }
                    break;
                case SOUTH:
                    if (tickMarksInside) {
                        path.lineTo(pixelPos.getX(), pixelPos.getY() - tickmarkLength);
                    } else {
                        path.lineTo(pixelPos.getX(), pixelPos.getY() + tickmarkLength);
                    }
                    break;
                case WEST:
                    if (tickMarksInside) {
                        path.lineTo(pixelPos.getX() + tickmarkLength, pixelPos.getY());
                    } else {
                        path.lineTo(pixelPos.getX() - tickmarkLength, pixelPos.getY());
                    }
                    break;
                case EAST:
                    if (tickMarksInside) {
                        path.lineTo(pixelPos.getX() - tickmarkLength, pixelPos.getY());
                    } else {
                        path.lineTo(pixelPos.getX() + tickmarkLength, pixelPos.getY());
                    }
                    break;

            }

            path.closePath();
            g2d.draw(path);

        }

        if (oldComposite != null) {
            g2d.setComposite(oldComposite);
        }
    }


    private AlphaComposite getAlphaComposite(double itemTransparancy) {
        double combinedAlpha = (1.0 - getTransparency()) * (1.0 - itemTransparancy);
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) combinedAlpha);
    }

    @Override
    public void disposeLayer() {
        final Product product = getProduct();
        if (product != null) {
            product.removeProductNodeListener(productNodeHandler);
            graticule = null;
            raster = null;
        }
    }

    @Override
    protected void fireLayerPropertyChanged(PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        if (propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_AUTO) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_LAT) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_LON) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_PIXELS)) {
            graticule = null;
        }
        if (getConfiguration().getProperty(propertyName) != null) {
            getConfiguration().setValue(propertyName, event.getNewValue());
        }
        super.fireLayerPropertyChanged(event);
    }

    private boolean getResAuto() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_RES_AUTO,
                GraticuleLayerType.DEFAULT_RES_AUTO);
    }

    private double getResLon() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_RES_LON,
                GraticuleLayerType.DEFAULT_RES_LON);
    }

    private double getResLat() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_RES_LAT,
                GraticuleLayerType.DEFAULT_RES_LAT);
    }

    private int getResPixels() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_RES_PIXELS,
                GraticuleLayerType.DEFAULT_RES_PIXELS);
    }

    private boolean isTextEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_ENABLED);
    }

    private Color getLineColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR,
                GraticuleLayerType.DEFAULT_LINE_COLOR);
    }

    private double getLineTransparency() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY,
                GraticuleLayerType.DEFAULT_LINE_TRANSPARENCY);
    }

    private int getLineWidth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH,
                GraticuleLayerType.DEFAULT_LINE_WIDTH);
    }

//    private Font getTextFont() {
//        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT,
//                GraticuleLayerType.DEFAULT_TEXT_FONT);
//    }


    private Color getTextFgColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR,
                GraticuleLayerType.DEFAULT_TEXT_FG_COLOR);
    }

    private Color getTextBgColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR,
                GraticuleLayerType.DEFAULT_TEXT_BG_COLOR);
    }

    private double getTextBgTransparency() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY,
                GraticuleLayerType.DEFAULT_TEXT_BG_TRANSPARENCY);
    }



    private Integer getTextFontSize() {

        int fontSize = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE,
                GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);

        double min = Math.min(raster.getRasterHeight(), raster.getRasterWidth());

        if (fontSize == FONT_SIZING_AUTOMATION_KEY) {
            fontSize = (int) Math.round(FONT_SIZE_AUTO_PERCENT * min);
            if (fontSize < FONT_SIZE_MINIMUM) {
                fontSize = FONT_SIZE_MINIMUM;
            }
        } else {
            double fontPercent = fontSize/ 100.0;

            fontSize = (int) Math.round(fontPercent * min);
        }

        return fontSize;
    }



    private Boolean isTextFontItalic() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_ITALIC,
                GraticuleLayerType.DEFAULT_TEXT_FONT_ITALIC);
    }


    private Integer getTextOffsetOutward() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_OFFSET_OUTWARD,
                GraticuleLayerType.DEFAULT_TEXT_OFFSET_OUTWARD);
    }

    private Integer getTextOffsetSideward() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_OFFSET_SIDEWARD,
                GraticuleLayerType.DEFAULT_TEXT_OFFSET_SIDEWARD);
    }

    private boolean isTextInside() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE,
                GraticuleLayerType.DEFAULT_TEXT_INSIDE);
    }

    private int getTextRotationNorth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH,
                GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH);
    }


    private int getTextRotationWest() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST,
                GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST);
    }


    private boolean isTextEnabledNorth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH,
                GraticuleLayerType.DEFAULT_TEXT_ENABLED_NORTH);
    }

    private boolean isTextEnabledSouth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH,
                GraticuleLayerType.DEFAULT_TEXT_ENABLED_SOUTH);
    }

    private boolean isTextEnabledWest() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST,
                GraticuleLayerType.DEFAULT_TEXT_ENABLED_WEST);
    }

    private boolean isTextEnabledEast() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST,
                GraticuleLayerType.DEFAULT_TEXT_ENABLED_EAST);
    }

    private boolean isLineEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED,
                GraticuleLayerType.DEFAULT_LINE_ENABLED);
    }

    private boolean isLineDashed() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED,
                GraticuleLayerType.DEFAULT_LINE_DASHED);
    }

    private double getLineDashedPhase() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE,
                GraticuleLayerType.DEFAULT_LINE_DASHED_PHASE);
    }

    private boolean isBorderEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED,
                GraticuleLayerType.DEFAULT_BORDER_ENABLED);
    }

    private int getBorderWidth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH,
                GraticuleLayerType.DEFAULT_BORDER_WIDTH);
    }

    private Color getBorderColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR,
                GraticuleLayerType.DEFAULT_BORDER_COLOR);
    }


    private boolean getTextCornerTopLonEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_TOP_LON_ENABLED);
    }

    private boolean getTextCornerLeftLatEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED);
    }


    private boolean getTextCornerRightLatEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED);
    }


    private boolean getTextCornerBottomLonEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED);
    }


    private boolean isShowTickMarks() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED,
                GraticuleLayerType.DEFAULT_TICKMARK_ENABLED);
    }

    private boolean isTickMarkInside() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE,
                GraticuleLayerType.DEFAULT_TICKMARK_INSIDE);
    }

    private class ProductNodeHandler extends ProductNodeListenerAdapter {

        /**
         * Overwrite this method if you want to be notified when a node changed.
         *
         * @param event the product node which the listener to be notified
         */
        @Override
        public void nodeChanged(ProductNodeEvent event) {
            if (event.getSourceNode() == getProduct() && Product.PROPERTY_NAME_GEOCODING.equals(
                    event.getPropertyName())) {
                // Force recreation
                graticule = null;
                fireLayerDataChanged(getModelBounds());
            }
        }
    }

}
