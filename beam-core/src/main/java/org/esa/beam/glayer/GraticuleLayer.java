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

    private double NULL_DOUBLE = -1.0;
    private double ptsToPixelsMultiplier = NULL_DOUBLE;


    private int minorStep = 4;


    public GraticuleLayer(RasterDataNode raster) {
        this(LAYER_TYPE, raster, initConfiguration(LAYER_TYPE.createLayerConfig(null), raster));
    }

    public GraticuleLayer(GraticuleLayerType type, RasterDataNode raster, PropertySet configuration) {
        super(type, configuration);
        setName("Graticule Layer");
        this.raster = raster;

        productNodeHandler = new ProductNodeHandler();
        raster.getProduct().addProductNodeListener(productNodeHandler);

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
                    getNumGridLines(),
                    getResLat(),
                    getResLon());
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
                if (linePaths != null && isLineEnabled()) {
                    drawLinePaths(g2d, linePaths);
                }

                if (isBorderEnabled()) {
                    drawBorder(g2d, raster);
                }


                if (isTextEnabledNorth()) {
                    final Graticule.TextGlyph[] textGlyphsNorth = graticule.getTextGlyphsNorth();
                    if (textGlyphsNorth != null) {
                        if (isTickMarkEnabled()) {
                            drawTickMarks(g2d, graticule.getTickPointsNorth(), Graticule.TextLocation.NORTH, false);
                        }

                        drawTextLabels(g2d, textGlyphsNorth, Graticule.TextLocation.NORTH, false, raster);
                    }
                }

                if (isTextEnabledSouth()) {
                    final Graticule.TextGlyph[] textGlyphsSouth = graticule.getTextGlyphsSouth();
                    if (textGlyphsSouth != null) {

                        if (isTickMarkEnabled()) {
                            drawTickMarks(g2d, graticule.getTickPointsSouth(), Graticule.TextLocation.SOUTH, false);
                        }
                        drawTextLabels(g2d, textGlyphsSouth, Graticule.TextLocation.SOUTH, false, raster);
                    }
                }

                if (isTextEnabledWest()) {
                    final Graticule.TextGlyph[] textGlyphsWest = graticule.getTextGlyphsWest();
                    if (textGlyphsWest != null) {
                        if (isTickMarkEnabled()) {
                            drawTickMarks(g2d, graticule.getTickPointsWest(), Graticule.TextLocation.WEST, false);
                        }
                        drawTextLabels(g2d, textGlyphsWest, Graticule.TextLocation.WEST, false, raster);
                    }
                }


                if (isTextEnabledEast()) {
                    final Graticule.TextGlyph[] textGlyphsEast = graticule.getTextGlyphsEast();
                    if (textGlyphsEast != null) {
                        if (isTickMarkEnabled()) {
                            drawTickMarks(g2d, graticule.getTickPointsEast(), Graticule.TextLocation.EAST, false);
                        }
                        drawTextLabels(g2d, textGlyphsEast, Graticule.TextLocation.EAST, false, raster);
                    }
                }


                if (isTextCornerLeftLatEnabled()) {
                    if (isTickMarkEnabled()) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.WEST);
                    }

                    if (!isTextInside()) {
                        drawLeftSideLatCornerLabels(g2d);
                    }
                }

                if (isTextCornerRightLatEnabled()) {
                    if (isTickMarkEnabled()) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.EAST);
                    }

                    if (!isTextInside()) {
                        drawRightSideLatCornerLabels(g2d);
                    }
                }

                if (isTextCornerTopLonEnabled()) {
                    if (isTickMarkEnabled()) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.NORTH);
                    }

                    if (!isTextInside()) {
                        drawNorthSideLonCornerLabels(g2d);
                    }
                }

                if (isTextCornerBottomLonEnabled()) {
                    if (isTickMarkEnabled()) {
                        drawCornerTickMarks(g2d, raster, Graticule.TextLocation.SOUTH);
                    }

                    if (!isTextInside()) {
                        drawSouthSideLonCornerLabels(g2d);
                    }
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
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.WEST, true, raster);
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
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.EAST, true, raster);
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
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.NORTH, true, raster);
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
            drawTextLabels(g2d, textGlyphs, Graticule.TextLocation.SOUTH, true, raster);
        }
    }


    private void getUserValues() {


    }

    private void drawLinePaths(Graphics2D g2d, final GeneralPath[] linePaths) {

        Color origPaint = (Color) g2d.getPaint();
        Stroke origStroke = g2d.getStroke();

        Composite oldComposite = null;
        if (getLineTransparency() > 0.0) {

            oldComposite = g2d.getComposite();
            g2d.setComposite(getAlphaComposite(getLineTransparency()));
        }
        g2d.setPaint(getLineColor());


        Stroke drawingStroke;
        //   if (isDashedLine() || getDashLengthPixels() != 0.0) {
        if (getDashLengthPixels() > 0.0) {
            drawingStroke = new BasicStroke((float) getGridLineWidthPixels(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{(float) getDashLengthPixels()}, 0);
        } else {
            drawingStroke = new BasicStroke((float) getGridLineWidthPixels());
        }

        g2d.setStroke(drawingStroke);
        for (GeneralPath linePath : linePaths) {
            g2d.draw(linePath);
        }

        if (oldComposite != null) {
            g2d.setComposite(oldComposite);
        }
        g2d.setStroke(origStroke);
        g2d.setPaint(origPaint);
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

            boolean drawCornerTicks = false;  // not sure we need this so I'm turning it off
            if (drawCornerTicks) {
                if (!isTickMarkInside()) {
                    drawTickMarks(g2d, pixelPos, textLocation, true);
                }
            }
        }
    }


    private void drawBorder(Graphics2D g2d, RasterDataNode raster) {

        Color origColor = (Color) g2d.getPaint();
        Stroke origStroke = g2d.getStroke();

        double sidewaysShift = getBorderLineWidthPixels() / 2;
        double lengthWiseAddOn = getBorderLineWidthPixels();


        GeneralPath northBorderPath = new GeneralPath();
        GeneralPath southBorderPath = new GeneralPath();
        {
            double xStart = 0 - lengthWiseAddOn;
            double xEnd = raster.getRasterWidth() + lengthWiseAddOn;

            double y = 0 - sidewaysShift;
            northBorderPath.moveTo(xStart, y);
            northBorderPath.lineTo(xEnd, y);
            northBorderPath.closePath();

            y = raster.getRasterHeight() + sidewaysShift;
            southBorderPath.moveTo(xStart, y);
            southBorderPath.lineTo(xEnd, y);
            southBorderPath.closePath();
        }

        GeneralPath westBorderPath = new GeneralPath();
        GeneralPath eastBorderPath = new GeneralPath();
        {
            double yStart = 0 - lengthWiseAddOn;
            double yEnd = raster.getRasterHeight() + lengthWiseAddOn;

            double x = 0 - sidewaysShift;
            westBorderPath.moveTo(x, yStart);
            westBorderPath.lineTo(x, yEnd);
            westBorderPath.closePath();

            x = raster.getRasterWidth() + sidewaysShift;
            eastBorderPath.moveTo(x, yStart);
            eastBorderPath.lineTo(x, yEnd);
            eastBorderPath.closePath();
        }

        Stroke drawingStroke = new BasicStroke((float) getBorderLineWidthPixels());
        g2d.setStroke(drawingStroke);
        g2d.setPaint(getBorderColor());

        g2d.draw(southBorderPath);
        g2d.draw(northBorderPath);
        g2d.draw(westBorderPath);
        g2d.draw(eastBorderPath);

        g2d.setPaint(origColor);
        g2d.setStroke(origStroke);
    }



    private boolean isTextConflict(PixelPos pixelPos, Graphics2D g2d, Graticule.TextLocation textLocation,
                                   boolean isCorner,
                                   RasterDataNode raster) {
        Font origFont = g2d.getFont();
        Font font = null;
        if (isCorner) {
            font = new Font("SansSerif", Font.ITALIC, getCornerFontSizePixels());
        } else {
            font = new Font("SansSerif", Font.PLAIN, getFontSizePixels());
        }
        g2d.setFont(font);

        Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);

        double WRONG_AXIS_BUFFER_PERCENT = 0.02;

        boolean conflict = false;
        double overlapMultiplierBuffer = 1.0;
        double xAllowedMin = 0 + overlapMultiplierBuffer * singleLetter.getHeight();
        double xAllowedMax = raster.getRasterWidth() - overlapMultiplierBuffer * singleLetter.getHeight();
        double yAllowedMin = 0 + overlapMultiplierBuffer * singleLetter.getHeight();
        double yAllowedMax = raster.getRasterHeight() - overlapMultiplierBuffer * singleLetter.getHeight();
        double buffer = 0;

        switch (textLocation) {
            case NORTH:
                if (isTextCornerTopLonEnabled() && !isCorner) {
                    if (pixelPos.getX() < xAllowedMin || pixelPos.getX() > xAllowedMax) {
                        conflict = true;
                    }
                }

                buffer = WRONG_AXIS_BUFFER_PERCENT * raster.getRasterHeight();
                if (pixelPos.getY() > buffer) {
                    conflict = true;
                }
                break;
            case SOUTH:
                if (isTextCornerBottomLonEnabled() && !isCorner) {
                    if (pixelPos.getX() < xAllowedMin || pixelPos.getX() > xAllowedMax) {
                        conflict = true;
                    }
                }

                buffer = WRONG_AXIS_BUFFER_PERCENT * raster.getRasterHeight();
                if (pixelPos.getY() < (raster.getRasterHeight() - buffer)) {
                    conflict = true;
                }
                break;
            case WEST:
                if (isTextCornerLeftLatEnabled() && !isCorner) {
                    if (pixelPos.getY() < yAllowedMin || pixelPos.getY() > yAllowedMax) {
                        conflict = true;
                    }
                }

                buffer = WRONG_AXIS_BUFFER_PERCENT * raster.getRasterWidth();
                if (pixelPos.getX() > buffer) {
                    conflict = true;
                }
                break;
            case EAST:
                if (isTextCornerRightLatEnabled() && !isCorner) {
                    if (pixelPos.getY() < yAllowedMin || pixelPos.getY() > yAllowedMax) {
                        conflict = true;
                    }
                }

                buffer = WRONG_AXIS_BUFFER_PERCENT * raster.getRasterWidth();
                if (pixelPos.getX() < (raster.getRasterWidth() - buffer)) {
                    conflict = true;
                }
                break;

        }

        g2d.setFont(origFont);

        return conflict;
    }


    private void drawTextLabels(Graphics2D g2d,
                                final Graticule.TextGlyph[] textGlyphs,
                                Graticule.TextLocation textLocation,
                                boolean isCorner,
                                RasterDataNode raster) {

        double halfPixelCorrection = 0.0;
        if (!isCorner) {
            halfPixelCorrection = 0.5;
        }


        Color origColor = (Color) g2d.getPaint();
        if (isCorner) {
            g2d.setPaint(getTextCornerFontColor());
        } else {
            g2d.setPaint(getTextFgColor());
        }


        Font origFont = g2d.getFont();
        Font font = null;
        if (isCorner) {
            font = new Font("SansSerif", Font.ITALIC, getCornerFontSizePixels());
        } else {
            font = new Font("SansSerif", Font.PLAIN, getFontSizePixels());
        }
        g2d.setFont(font);


        Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
        double letterWidth = singleLetter.getWidth();
        float spacerBetweenTextAndBorder = (float) (letterWidth / 2.0);


        for (Graticule.TextGlyph glyph : textGlyphs) {


            PixelPos pixelPos = new PixelPos(glyph.getX(), glyph.getY());


            if (!isTextConflict(pixelPos, g2d, textLocation, isCorner, raster)) {

                if (isTextInside() && getTextBgTransparency() < 1.0) {
                    drawRectangle(g2d, glyph);
                }

                g2d.translate(glyph.getX(), glyph.getY());
                g2d.rotate(glyph.getAngle());

                Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
                float width = (float) labelBounds.getWidth();
                float height = (float) labelBounds.getHeight();


                float halfLabelWidth = width / 2;


                AffineTransform orig = g2d.getTransform();


                if (!isTextInside()) {
                    if (textLocation == Graticule.TextLocation.NORTH) {
                        double theta = (getTextRotationNorth() / 180) * Math.PI;

                        float xOffset = 0;
                        float yOffset = 0;
                        double verticalShift = halfPixelCorrection + getBorderLineWidthPixels() + spacerBetweenTextAndBorder;

                        if (isTickMarkEnabled() && !isTickMarkInside()) {
                            verticalShift += getTickMarkLength();
                        }

                        if (getTextRotationNorth() > 85) {
                            xOffset = -halfLabelWidth;
                        }

                        if (getTextRotationNorth() < 5) {
                            yOffset = height / 3;
                        }

                        float xMod = (float) (verticalShift * Math.cos(theta));
                        float yMod = -1 * (float) (verticalShift * Math.sin(theta));

                        g2d.rotate(-1 * Math.PI + theta);
                        g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                    }

                    if (textLocation == Graticule.TextLocation.SOUTH) {
                        double theta = (getTextRotationNorth() / 180) * Math.PI;

                        float xOffset = -width;
                        float yOffset = 2 * height / 3;
                        double verticalShift = -halfPixelCorrection - getBorderLineWidthPixels() - spacerBetweenTextAndBorder;

                        if (isTickMarkEnabled() && !isTickMarkInside()) {
                            verticalShift -= getTickMarkLength();
                        }

                        if (getTextRotationNorth() > 85) {
                            xOffset = xOffset + halfLabelWidth;
                        }

                        if (getTextRotationNorth() < 5) {
                            yOffset = yOffset - height / 3;
                        }

                        float xMod = (float) (verticalShift * Math.cos(theta));
                        float yMod = -1 * (float) (verticalShift * Math.sin(theta));

                        g2d.rotate(theta);
                        g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                    }

                    if (textLocation == Graticule.TextLocation.EAST) {

                        double theta = (getTextRotationWest() / 180) * Math.PI;

                        float xOffset = 0;
                        float yOffset = 2 * height / 3;
                        double verticalShift = halfPixelCorrection + getBorderLineWidthPixels() + spacerBetweenTextAndBorder;

                        if (isTickMarkEnabled() && !isTickMarkInside()) {
                            verticalShift += getTickMarkLength();
                        }

                        if (getTextRotationWest() > 85) {
                            xOffset = -halfLabelWidth;
                        }

                        if (getTextRotationWest() < 5) {
                            yOffset = height / 3;
                        }

                        float xMod = (float) (verticalShift * Math.cos(theta));
                        float yMod = (float) (verticalShift * Math.sin(theta));


                        g2d.rotate(-1 * Math.PI - theta);
                        g2d.drawString(glyph.getText(), xMod + xOffset, +yMod + yOffset);
                    }


                    if (textLocation == Graticule.TextLocation.WEST) {

                        double theta = (getTextRotationWest() / 180) * Math.PI;

                        float xOffset = -width;
                        float yOffset = 0;
                        double verticalShift = -halfPixelCorrection - getBorderLineWidthPixels() - spacerBetweenTextAndBorder;

                        if (isTickMarkEnabled() && !isTickMarkInside()) {
                            verticalShift -= getTickMarkLength();
                        }

                        if (getTextRotationWest() > 85) {
                            xOffset = xOffset + halfLabelWidth;
                        }

                        if (getTextRotationWest() < 5) {
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

                        float xOffset = spacerBetweenTextAndBorder;
                        float yOffset = height / 3;

                        if (isTickMarkEnabled() && isTickMarkInside()) {
                            xOffset += getTickMarkLength();
                        }

                        g2d.drawString(glyph.getText(), xOffset, yOffset);
                    } else {
                        float xOffset = -width - spacerBetweenTextAndBorder;
                        float yOffset = height / 3;

                        if (isTickMarkEnabled() && isTickMarkInside()) {
                            xOffset -= getTickMarkLength();
                        }

                        g2d.rotate(-Math.PI);
                        g2d.drawString(glyph.getText(), xOffset, yOffset);
                    }
                }

                g2d.setTransform(orig);

                g2d.rotate(-glyph.getAngle());
                g2d.translate(-glyph.getX(), -glyph.getY());

            }
        }


        g2d.setPaint(origColor);
        g2d.setFont(origFont);

    }


    private void drawRectangle(Graphics2D g2d, final Graticule.TextGlyph glyph) {

        Composite oldComposite = null;
        if (getTextBgTransparency() > 0.0) {
            oldComposite = g2d.getComposite();
            g2d.setComposite(getAlphaComposite(getTextBgTransparency()));
        }

        Color origPaint = (Color) g2d.getPaint();
        Stroke origStroke = g2d.getStroke();

        g2d.setPaint(getTextBgColor());
        g2d.setStroke(new BasicStroke(0));


        g2d.translate(glyph.getX(), glyph.getY());
        g2d.rotate(glyph.getAngle());

        Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
        double xOffset = singleLetter.getWidth() / 2.0;
        double yOffset = singleLetter.getHeight() / 3.0;

        if (isTickMarkEnabled() && isTickMarkInside()) {
            xOffset += getTickMarkLength();
        }

        Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
        labelBounds.setRect(labelBounds.getX() + xOffset - 1,
                labelBounds.getY() + yOffset - 1,
                labelBounds.getWidth(),
                labelBounds.getHeight());

        g2d.fill(labelBounds);

        g2d.rotate(-glyph.getAngle());
        g2d.translate(-glyph.getX(), -glyph.getY());


        g2d.setPaint(origPaint);
        g2d.setStroke(origStroke);
        if (oldComposite != null) {
            g2d.setComposite(oldComposite);
        }
    }


    private void drawTickMarks(Graphics2D g2d, final PixelPos[] pixelPoses, Graticule.TextLocation textLocation, boolean isCorner) {

        Composite oldComposite = g2d.getComposite();
        Stroke origStroke = g2d.getStroke();

        Stroke drawingStroke = new BasicStroke((float) getGridLineWidthPixels());
        g2d.setStroke(drawingStroke);

        Color origPaint = (Color) g2d.getPaint();
        g2d.setPaint(getTextFgColor());


        double halfPixelCorrection = 0.0;
        if (!isCorner) {
            halfPixelCorrection = 0.5;
        }

        double xStart = 0, xEnd = 0, yStart = 0, yEnd = 0;

        boolean validCase = false;

        for (PixelPos pixelPos : pixelPoses) {

            if (!isTextConflict(pixelPos, g2d, textLocation, isCorner, raster)) {
                switch (textLocation) {
                    case NORTH:
                        xStart = pixelPos.getX();
                        xEnd = pixelPos.getX();

                        if (isTickMarkInside()) {
                            yStart = pixelPos.getY() - halfPixelCorrection;
                            yEnd = yStart + getTickMarkLength();
                        } else {
                            yStart = pixelPos.getY() - halfPixelCorrection - getBorderLineWidthPixels();
                            yEnd = yStart - getTickMarkLength();
                        }
                        validCase = true;
                        break;

                    case SOUTH:
                        xStart = pixelPos.getX();
                        xEnd = pixelPos.getX();

                        if (isTickMarkInside()) {
                            yStart = pixelPos.getY() + halfPixelCorrection;
                            yEnd = yStart - getTickMarkLength();
                        } else {
                            yStart = pixelPos.getY() + halfPixelCorrection + getBorderLineWidthPixels();
                            yEnd = yStart + getTickMarkLength();
                        }

                        validCase = true;
                        break;

                    case WEST:
                        yStart = pixelPos.getY();
                        yEnd = pixelPos.getY();

                        if (isTickMarkInside()) {
                            xStart = pixelPos.getX() - halfPixelCorrection;
                            xEnd = xStart + getTickMarkLength();
                        } else {
                            xStart = pixelPos.getX() - halfPixelCorrection - getBorderLineWidthPixels();
                            xEnd = xStart - getTickMarkLength();
                        }

                        validCase = true;
                        break;

                    case EAST:
                        yStart = pixelPos.getY();
                        yEnd = pixelPos.getY();

                        if (isTickMarkInside()) {
                            xStart = pixelPos.getX() + halfPixelCorrection;
                            xEnd = xStart - getTickMarkLength();
                        } else {
                            xStart = pixelPos.getX() + halfPixelCorrection + getBorderLineWidthPixels();
                            xEnd = xStart + getTickMarkLength();
                        }

                        validCase = true;
                        break;
                }

                if (validCase) {
                    GeneralPath path = new GeneralPath();
                    path.moveTo(xStart, yStart);
                    path.lineTo(xEnd, yEnd);

                    path.closePath();
                    g2d.draw(path);
                }
            }

        }

        g2d.setPaint(origPaint);
        g2d.setStroke(origStroke);
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
        if (
                propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_LAT) ||
                        propertyName.equals(GraticuleLayerType.PROPERTY_NAME_RES_LON) ||
                        propertyName.equals(GraticuleLayerType.PROPERTY_NAME_NUM_GRID_LINES)) {
            graticule = null;
        }
        if (getConfiguration().getProperty(propertyName) != null) {
            getConfiguration().setValue(propertyName, event.getNewValue());
        }
        super.fireLayerPropertyChanged(event);
    }


    private double getResLon() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_RES_LON,
                GraticuleLayerType.DEFAULT_RES_LON);
    }

    private double getResLat() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_RES_LAT,
                GraticuleLayerType.DEFAULT_RES_LAT);
    }

    private int getNumGridLines() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_NUM_GRID_LINES,
                GraticuleLayerType.DEFAULT_NUM_GRID_LINES);
    }


    private Color getLineColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR,
                GraticuleLayerType.DEFAULT_LINE_COLOR);
    }

    private double getLineTransparency() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY,
                GraticuleLayerType.DEFAULT_LINE_TRANSPARENCY);
    }


    private Color getTextFgColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR,
                GraticuleLayerType.DEFAULT_TEXT_FG_COLOR);
    }

    private Color getTextCornerFontColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_COLOR,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_COLOR);
    }

    private Color getTextBgColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR,
                GraticuleLayerType.DEFAULT_TEXT_BG_COLOR);
    }

    private double getTextBgTransparency() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY,
                GraticuleLayerType.DEFAULT_TEXT_BG_TRANSPARENCY);
    }


    private double getGridLineWidthPixels() {
        double gridLineWidthPts = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH,
                GraticuleLayerType.DEFAULT_LINE_WIDTH);

        return getPtsToPixelsMultiplier() * gridLineWidthPts;
    }


    private double getBorderLineWidthPixels() {
        double borderLineWidthPts = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH,
                GraticuleLayerType.DEFAULT_BORDER_WIDTH);

        return getPtsToPixelsMultiplier() * borderLineWidthPts;
    }


    private double getDashLengthPixels() {
        double dashLengthPts = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE,
                GraticuleLayerType.DEFAULT_LINE_DASHED_PHASE);

        return getPtsToPixelsMultiplier() * dashLengthPts;
    }


    private int getFontSizePixels() {
        int fontSizePts = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE,
                GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);

        return (int) Math.round(getPtsToPixelsMultiplier() * fontSizePts);
    }


    private int getCornerFontSizePixels() {
        int cornerFontSizePts = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_SIZE,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_SIZE);

        return (int) Math.round(getPtsToPixelsMultiplier() * cornerFontSizePts);
    }


    private double getPtsToPixelsMultiplier() {

        if (ptsToPixelsMultiplier == NULL_DOUBLE) {
            final double PTS_PER_INCH = 72.0;
            final double PAPER_HEIGHT = 11.0;
            final double PAPER_WIDTH = 8.5;

            double heightToWidthRatioPaper = (PAPER_HEIGHT) / (PAPER_WIDTH);
            double heightToWidthRatioRaster = raster.getRasterHeight() / raster.getRasterWidth();

            if (heightToWidthRatioRaster > heightToWidthRatioPaper) {
                // use height
                ptsToPixelsMultiplier = (1 / PTS_PER_INCH) * (raster.getRasterHeight() / (PAPER_HEIGHT));
            } else {
                // use width
                ptsToPixelsMultiplier = (1 / PTS_PER_INCH) * (raster.getRasterWidth() / (PAPER_WIDTH));
            }
        }

        return ptsToPixelsMultiplier;
    }


    private Boolean isTextFontItalic() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_ITALIC,
                GraticuleLayerType.DEFAULT_TEXT_FONT_ITALIC);
    }


    private boolean isTextInside() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE,
                GraticuleLayerType.DEFAULT_TEXT_INSIDE);
    }

    private double getTextRotationNorth() {
        int textRotationNorthSouth = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH,
                GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH_SOUTH);

        return (double) textRotationNorthSouth;
    }


    private double getTextRotationWest() {
        int textAngleWestEast = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST_EAST,
                GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST_EAST);

        return (double) textAngleWestEast;
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


    private boolean isBorderEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED,
                GraticuleLayerType.DEFAULT_BORDER_ENABLED);
    }


    private Color getBorderColor() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR,
                GraticuleLayerType.DEFAULT_BORDER_COLOR);
    }


    private boolean isTextCornerTopLonEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_TOP_LON_ENABLED);
    }

    private boolean isTextCornerLeftLatEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED);
    }


    private boolean isTextCornerRightLatEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED);
    }


    private boolean isTextCornerBottomLonEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED,
                GraticuleLayerType.DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED);
    }


    private boolean isTickMarkEnabled() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED,
                GraticuleLayerType.DEFAULT_TICKMARK_ENABLED);
    }

    private boolean isTickMarkInside() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE,
                GraticuleLayerType.DEFAULT_TICKMARK_INSIDE);
    }


    private double getTickMarkLength() {
        double tickMarkLengthPts = getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH,
                GraticuleLayerType.DEFAULT_TICKMARK_LENGTH);

        return getPtsToPixelsMultiplier() * tickMarkLengthPts;
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
