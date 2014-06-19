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


    // DANNY new fields to add to preferences

    boolean includeWestLonBorderText = true;
    boolean includeEastLonBorderText = true;
    boolean includeNorthLatBorderText = true;
    boolean includeSouthLatBorderText = true;


    private boolean dashedLine = true;
    private double dashedLinePhase = 9.0;
    //    private boolean rotateLongitudeLabelsSouthside = false;
//    private boolean rotateLongitudeLabelsNorthside = false;
//    private boolean rotateLongitudeLabelsWestside = false;
//    private boolean rotateLongitudeLabelsEastside = false;
    private boolean showLatitudeLabelsEastside = true;
    private boolean showLatitudeLabelsWestside = true;
    private boolean showLongitudeLabelsNorthside = true;
    private boolean showLongitudeLabelsSouthside = true;
    private boolean showBorder = true;
    private boolean showTickMarks = false;
    private boolean showGridLines = true;
    private int minorStep = 4;
    private double rotationThetaNorthLabels = 60;   // in degrees
    //   private double rotationThetaSouthLabels = 60;   // in degrees
    private double rotationThetaWestLabels = 60;   // in degrees
    //   private double rotationThetaEastLabels = 60;   // in degrees
    int textOutwardsOffset = 0;
    int textSidewardsOffset = 0;
    int fontSize = 50;
    boolean autoFontSize = true;
    boolean textOutside = false;
    int gridLineWidth = GraticuleLayerType.DEFAULT_LINE_WIDTH;
    int borderWidth = GraticuleLayerType.DEFAULT_BORDER_WIDTH;

    double borderTransparency = 0.0;

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

        if (!textOutside) {
            includeWestLonBorderText = false;
            includeEastLonBorderText = false;
            includeNorthLatBorderText = false;
            includeSouthLatBorderText = false;
        }

        if (graticule == null) {
            graticule = Graticule.create(raster,
                    getResAuto(),
                    getResPixels(),
                    (float) getResLat(),
                    (float) getResLon(), includeWestLonBorderText, includeEastLonBorderText, includeNorthLatBorderText, includeSouthLatBorderText);
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
                    if (isTextEnabled() && showLongitudeLabelsNorthside) {

                        drawTextLabels(g2d, textGlyphsNorth, Graticule.TextLocation.NORTH);
                    }

                    if (showTickMarks) {
                        drawTickMarks(g2d, textGlyphsNorth, Graticule.TextLocation.NORTH);
                    }
                }

                final Graticule.TextGlyph[] textGlyphsSouth = graticule.getTextGlyphsSouth();
                if (textGlyphsSouth != null) {
                    if (isTextEnabled() && showLongitudeLabelsSouthside) {
                        drawTextLabels(g2d, textGlyphsSouth, Graticule.TextLocation.SOUTH);
                    }

                    if (showTickMarks) {
                        drawTickMarks(g2d, textGlyphsSouth, Graticule.TextLocation.SOUTH);
                    }
                }

                final Graticule.TextGlyph[] textGlyphsWest = graticule.getTextGlyphsWest();
                if (textGlyphsWest != null) {
                    if (isTextEnabled() && showLatitudeLabelsWestside) {
                        drawTextLabels(g2d, textGlyphsWest, Graticule.TextLocation.WEST);
                    }

                    if (showTickMarks) {
                        drawTickMarks(g2d, textGlyphsWest, Graticule.TextLocation.WEST);
                    }
                }

                final Graticule.TextGlyph[] textGlyphsEast = graticule.getTextGlyphsEast();
                if (textGlyphsEast != null) {
                    if (isTextEnabled() && showLatitudeLabelsEastside) {
                        drawTextLabels(g2d, textGlyphsEast, Graticule.TextLocation.EAST);
                    }

                    if (showTickMarks) {
                        drawTickMarks(g2d, textGlyphsEast, Graticule.TextLocation.EAST);
                    }
                }


            } finally {
                g2d.setTransform(transformSave);
            }
        }
    }

    private void getUserValues() {
        int height = raster.getRasterHeight();
        int width = raster.getRasterWidth();
        int min = width;

        if (height < min) {
            min = height;
        }

        dashedLine = isLineDashed();

        showLatitudeLabelsEastside = isTextEnabledEast();
        showLatitudeLabelsWestside = isTextEnabledWest();
        showLongitudeLabelsNorthside = isTextEnabledNorth();
        showLongitudeLabelsSouthside = isTextEnabledSouth();
        showBorder = isBorderEnabled();
        showGridLines = isLineEnabled();
        //    minorStep = 4;
        rotationThetaNorthLabels = 90 - getTextRotationNorth();   // in degrees
        //   rotationThetaSouthLabels = 90 - getTextRotationSouth();   // in degrees
        rotationThetaWestLabels = 90 - getTextRotationWest();   // in degrees
        //  rotationThetaEastLabels = 90 - getTextRotationEast();   // in degrees
        textOutwardsOffset = getTextOffsetOutward();
        textSidewardsOffset = getTextOffsetSideward();

        fontSize = getTextFontSize();
        if (fontSize < 1) {
            // make font 2.5% of total image
            fontSize = (int) Math.floor(0.025 * min);
            if (fontSize < 1) {
                fontSize = 1;
            }
        }

        textOutside = isTextOutside();


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

        includeWestLonBorderText = getTextBorderLonWestEnabled();
        includeEastLonBorderText = getTextBorderLonEastEnabled();
        includeNorthLatBorderText = getTextBorderLatNorthEnabled();
        includeSouthLatBorderText = getTextBorderLatSouthEnabled();

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


    private void drawBorderPath(Graphics2D g2d, RasterDataNode raster) {


        //   Graticule.TextGlyph textGlyph =  graticule.getBorderGlyphNorthWestCornerLat(raster);

//        PixelPos pixelPos = new PixelPos(0,0);
//       GeoCoding geoCoding = raster.getGeoCoding();
//        GeoPos geoPos = new GeoPos();
//      GeoPos test =   geoCoding.getGeoPos(pixelPos, null);
//    String lon =    test.getLonString();
//    String lat =    test.getLatString();
//
////
////        Graticule.TextGlyph
//        new Graticule.TextGlyph(text, coord1.pixelPos.x, coord1.pixelPos.y);


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
        final float tx = 3;
        final float ty = -3;

        // Danny added this

        int fontStyle = Font.PLAIN;

        if (isTextFontItalic()) {
            fontStyle = Font.ITALIC;
        }

        Font font = new Font("SansSerif", fontStyle, fontSize);
        g2d.setFont(font);

        if (getTextBgTransparency() < 1.0 && getTextBgTransparency() >= 0 && !textOutside) {
            drawRectangle(g2d, textGlyphs, textLocation);
        }

        if (getTextBgTransparency() < 1.0 && 1 == 2) {
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
                double nudge = height / 2;
                int offsetNudge = (int) nudge;

                // todo Danny adjust this like the one 20 lines  later in the code
                if (textOutside) {
                    labelBounds.setRect(labelBounds.getX() - width - offsetNudge + textOutwardsOffset - 1,
                            labelBounds.getY() + shiftSideways + ty + textSidewardsOffset - 1,
                            labelBounds.getWidth() + 15 + 4,
                            labelBounds.getHeight());
                } else {
                    labelBounds.setRect(labelBounds.getX() + offsetNudge + textOutwardsOffset - 1,
                            labelBounds.getY() + textSidewardsOffset - 1,
                            labelBounds.getWidth() + 4,
                            labelBounds.getHeight());
//                    labelBounds.setRect(labelBounds.getX() + tx +  - 1,
//                            labelBounds.getY() + ty +  - 1,
//                            labelBounds.getWidth() + 4,
//                            labelBounds.getHeight());
                }
                g2d.fill(labelBounds);

                g2d.rotate(-glyph.getAngle());
                g2d.translate(-glyph.getX(), -glyph.getY());
            }

            if (oldComposite != null) {
                g2d.setComposite(oldComposite);
            }
        }

        Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
        double letterWidth = singleLetter.getWidth();
        float halfLetterWidth = (float) (letterWidth / 2.0);

        Rectangle2D longestExpectedLabel = g2d.getFontMetrics().getStringBounds("170° W", g2d);
        double longestExpectedLabelWidth = longestExpectedLabel.getWidth();


        //   double rotate = .25 * Math.PI;
        double rotate = (rotationThetaNorthLabels / 180) * Math.PI;


//        float zPrime = (float) (((letterWidth / 2.0) + (longestExpectedLabelWidth / 2.0)) * Math.cos(theta));
//        float xMod = (float) (zPrime * Math.cos(theta));
//        float yMod = (float) (zPrime * Math.sin(theta));


        g2d.setPaint(getTextFgColor());
        for (Graticule.TextGlyph glyph : textGlyphs) {
            g2d.translate(glyph.getX(), glyph.getY());
            g2d.rotate(glyph.getAngle());

            Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
            String label = glyph.getText();
            float width = (float) labelBounds.getWidth();
            float height = (float) labelBounds.getHeight();

            float shiftSideways = height / 3;


            float xPosShiftedAnchor = -width - halfLetterWidth - textOutwardsOffset;
            float yPosShiftedAnchor = shiftSideways + textSidewardsOffset;

            float xPosNormalAnchor = halfLetterWidth - textOutwardsOffset;
            float yPosNormalAnchor = shiftSideways + textSidewardsOffset;

            float halfLabelWidth = width / 2;
            float thirdLabelHeight = height / 3;
            // effectively half the height of the word, there is some space above the word
            // which needs accounting for hence  1/2 is better represented with 1/3


            //   float zPrime = (float) ((letterWidth + (width / 2.0)) * Math.cos(theta));


            AffineTransform orig = g2d.getTransform();


//            float zPrime = (float) (letterWidth * Math.cos(theta));
//            zPrime = (float) letterWidth;


            double verticalShift = 0;


            if (isTextOutside()) {
                if (textLocation == Graticule.TextLocation.NORTH) {
                    double theta = (rotationThetaNorthLabels / 180) * Math.PI;
                    float xOffset = 0;
                    float yOffset = 0;

                    if (textOutside) {
                        xOffset = 0;
                        yOffset = 0;
                        verticalShift = letterWidth / 2;

                        if (rotationThetaNorthLabels > 85) {
                            xOffset = -halfLabelWidth;
                        }

                        if (rotationThetaNorthLabels < 5) {
                            yOffset = height / 3;
                        }
                    }
//                    else {
//                        xOffset = -width;
//                        yOffset = 2 * height / 3;
//                        verticalShift = -letterWidth / 2;
//
//                        if (rotationThetaNorthLabels > 85) {
//                            xOffset = xOffset + halfLabelWidth;
//                        }
//
//                        if (rotationThetaNorthLabels < 5) {
//                            yOffset = yOffset - height / 3;
//                        }
//                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = -1 * (float) (verticalShift * Math.sin(theta));

                    g2d.rotate(-1 * Math.PI + theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }

                if (textLocation == Graticule.TextLocation.SOUTH) {
                    double theta = (rotationThetaNorthLabels / 180) * Math.PI;
                    float xOffset = 0;
                    float yOffset = 0;

//                    if (!textOutside) {
//                        xOffset = 0;
//                        yOffset = 0;
//                        verticalShift = letterWidth / 2;
//
//                        if (rotationThetaNorthLabels > 85) {
//                            xOffset = -halfLabelWidth;
//                        }
//
//                        if (rotationThetaNorthLabels < 5) {
//                            yOffset = height / 3;
//                        }
//                    } else {
                    xOffset = -width;
                    yOffset = 2 * height / 3;
                    verticalShift = -letterWidth / 2;

                    if (rotationThetaNorthLabels > 85) {
                        xOffset = xOffset + halfLabelWidth;
                    }

                    if (rotationThetaNorthLabels < 5) {
                        yOffset = yOffset - height / 3;
                    }
//                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = -1 * (float) (verticalShift * Math.sin(theta));

                    g2d.rotate(theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }

                if (textLocation == Graticule.TextLocation.EAST) {
                    double theta = (rotationThetaWestLabels / 180) * Math.PI;
                    float xOffset = 0;
                    float yOffset = 0;

                    if (textOutside) {
                        xOffset = 0;
                        yOffset = 2 * height / 3;
                        verticalShift = letterWidth / 2;

                        if (rotationThetaWestLabels > 85) {
                            xOffset = -halfLabelWidth;
                        }

                        if (rotationThetaWestLabels < 5) {
                            yOffset = height / 3;
                        }
                    }
//                    else {
//                        xOffset = -width;
//                        yOffset = 0;
//                        verticalShift = -letterWidth / 2;
//
//                        if (rotationThetaWestLabels > 85) {
//                            xOffset = xOffset + halfLabelWidth;
//                        }
//
//                        if (rotationThetaWestLabels < 5) {
//                            yOffset = yOffset - height / 3;
//                        }
//                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = (float) (verticalShift * Math.sin(theta));


                    g2d.rotate(-1 * Math.PI - theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }


                if (textLocation == Graticule.TextLocation.WEST) {
                    double theta = (rotationThetaWestLabels / 180) * Math.PI;
                    float xOffset = 0;
                    float yOffset = 0;

//                    if (!textOutside) {
//                        xOffset = 0;
//                        yOffset = +2 * height / 3;
//                        verticalShift = -letterWidth / 2;
//
//                        if (rotationThetaWestLabels > 85) {
//                            xOffset = -halfLabelWidth;
//                        }
//
//                        if (rotationThetaWestLabels < 5) {
//                            yOffset = height / 3;
//                        }
//                    } else {
                    xOffset = -width;
                    yOffset = 0;
                    verticalShift = -letterWidth / 2;

                    if (rotationThetaWestLabels > 85) {
                        xOffset = xOffset + halfLabelWidth;
                    }

                    if (rotationThetaWestLabels < 5) {
                        yOffset = yOffset + height / 3;
                    }
//                    }

                    float xMod = (float) (verticalShift * Math.cos(theta));
                    float yMod = (float) (verticalShift * Math.sin(theta));


                    g2d.rotate(-theta);
                    g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                }
            } else {
                if (textLocation == Graticule.TextLocation.WEST ||
                        textLocation == Graticule.TextLocation.SOUTH) {
//                    if (isTextOutside()) {
//                        if (textLocation == Graticule.TextLocation.SOUTH) {
//                            g2d.rotate(0.25 * Math.PI);
//                        }
//                        g2d.drawString(glyph.getText(), -width - halfLetterWidth - textOutwardsOffset, shiftSideways + textSidewardsOffset);
//                    } else {
                    g2d.drawString(glyph.getText(), halfLetterWidth - textOutwardsOffset, shiftSideways + textSidewardsOffset);
//                    }
                } else {
//                    if (textLocation == Graticule.TextLocation.NORTH) {
//                        g2d.rotate(-0.75 * Math.PI);
//                    } else {
                    g2d.rotate(-Math.PI);
//                    }
//                    if (isTextOutside()) {
//                        g2d.drawString(glyph.getText(), +halfLetterWidth + textOutwardsOffset, shiftSideways + textSidewardsOffset);
//                    } else
//                    {
                    g2d.drawString(glyph.getText(), -width - halfLetterWidth + textOutwardsOffset, shiftSideways + textSidewardsOffset);
//                    }
                }
            }

            g2d.setTransform(orig);

            g2d.rotate(-glyph.getAngle());
            g2d.translate(-glyph.getX(), -glyph.getY());
        }
    }

    private void drawRectangle(Graphics2D g2d, final Graticule.TextGlyph[] textGlyphs, Graticule.TextLocation textLocation) {


        Font font = new Font("SansSerif", Font.ITALIC, fontSize);
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
                    if (textOutside) {
                        g2d.drawString(glyph.getText(), -width - halfLetterWidth - textOutwardsOffset, shiftSideways + textSidewardsOffset);
                    } else {
                        //           g2d.drawString(glyph.getText(), halfLetterWidth - textOutwardsOffset, shiftSideways + textSidewardsOffset);
                        labelBounds.setRect(labelBounds.getX() + halfLetterWidth - textOutwardsOffset - 1,
                                labelBounds.getY() + shiftSideways + textSidewardsOffset - 1,
                                labelBounds.getWidth(),
                                labelBounds.getHeight());

                    }
                } else {

                    AffineTransform orig = g2d.getTransform();
                    g2d.rotate(-Math.PI);
                    if (textOutside) {
                        g2d.drawString(glyph.getText(), +halfLetterWidth + textOutwardsOffset, shiftSideways + textSidewardsOffset);
                    } else {
                        //             g2d.drawString(glyph.getText(), - width - halfLetterWidth + textOutwardsOffset, shiftSideways + textSidewardsOffset);
                        labelBounds.setRect(labelBounds.getX() + halfLetterWidth - textOutwardsOffset - 1,
                                labelBounds.getY() + shiftSideways + textSidewardsOffset - 1,
                                labelBounds.getWidth(),
                                labelBounds.getHeight());
                    }

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

    private void drawTickMarks(Graphics2D g2d, final Graticule.TextGlyph[] textGlyphs, Graticule.TextLocation textLocation) {


        //     Stroke drawingStroke = new BasicStroke((float) getLineWidth());
        Stroke drawingStroke = new BasicStroke(3);

        g2d.setStroke(drawingStroke);


        Font font = new Font("SansSerif", Font.ITALIC, fontSize);
        g2d.setFont(font);


        g2d.setPaint(getTextFgColor());
        for (Graticule.TextGlyph glyph : textGlyphs) {
            g2d.translate(glyph.getX(), glyph.getY());
            g2d.rotate(glyph.getAngle());

            Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
            float width = (float) labelBounds.getWidth();
            float height = (float) labelBounds.getHeight();


            float shiftSideways = height / 3;


//            Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
//            double letterWidth = singleLetter.getWidth();
//            float halfLetterWidth = (float) (letterWidth / 2.0);
            float letterWidth = 50;

            GeneralPath path = new GeneralPath();
            path.moveTo(labelBounds.getX(), labelBounds.getY());


            if (textOutside) {
                if (textLocation == Graticule.TextLocation.NORTH) {
                    path.lineTo(labelBounds.getX(), labelBounds.getY() - letterWidth);
                } else if (textLocation == Graticule.TextLocation.SOUTH) {
                    path.lineTo(labelBounds.getX(), labelBounds.getY() + letterWidth);
                } else if (textLocation == Graticule.TextLocation.WEST) {
                    path.lineTo(labelBounds.getX() + letterWidth, labelBounds.getY());
                } else if (textLocation == Graticule.TextLocation.EAST) {
                    path.lineTo(labelBounds.getX() - letterWidth, labelBounds.getY());
                }
            } else {

            }

            path.closePath();
            g2d.draw(path);


            g2d.rotate(-glyph.getAngle());
            g2d.translate(-glyph.getX(), -glyph.getY());
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
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_PIXELS) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_TEXT_OUTSIDE) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LON_WEST) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LON_EAST) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LAT_NORTH) ||
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LAT_SOUTH)
                ) {
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
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE,
                GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);
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

    private boolean isTextOutside() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_OUTSIDE,
                GraticuleLayerType.DEFAULT_TEXT_OUTSIDE);
    }

    private int getTextRotationNorth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH,
                GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH);
    }

//    private double getTextRotationSouth() {
//        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_SOUTH,
//                GraticuleLayerType.DEFAULT_TEXT_ROTATION_SOUTH);
//    }

    private int getTextRotationWest() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST,
                GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST);
    }

//    private double getTextRotationEast() {
//        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_EAST,
//                GraticuleLayerType.DEFAULT_TEXT_ROTATION_EAST);
//    }
//
//    private boolean isTextRotationAnchored() {
//        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_ANCHORED,
//                GraticuleLayerType.DEFAULT_TEXT_ROTATION_ANCHORED);
//    }

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


    private boolean getTextBorderLonWestEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LON_WEST,
                GraticuleLayerType.DEFAULT_TEXT_BORDER_ENABLED_LON_WEST);
    }

    private boolean getTextBorderLonEastEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LON_EAST,
                GraticuleLayerType.DEFAULT_TEXT_BORDER_ENABLED_LON_EAST);
    }

    private boolean getTextBorderLatNorthEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LAT_NORTH,
                GraticuleLayerType.DEFAULT_TEXT_BORDER_ENABLED_LAT_NORTH);
    }

    private boolean getTextBorderLatSouthEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BORDER_ENABLED_LAT_SOUTH,
                GraticuleLayerType.DEFAULT_TEXT_BORDER_ENABLED_LAT_SOUTH);
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
