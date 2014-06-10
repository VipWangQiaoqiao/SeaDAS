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
import org.esa.beam.framework.datamodel.Graticule;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeEvent;
import org.esa.beam.framework.datamodel.ProductNodeListenerAdapter;
import org.esa.beam.framework.datamodel.RasterDataNode;

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
                if (linePaths != null) {
                    drawLinePaths(g2d, linePaths);
                }

                drawBorderPath(g2d, raster);

                if (isTextEnabled()) {

                    final Graticule.TextGlyph[] textGlyphsNorth = graticule.getTextGlyphsNorth();
                    if (textGlyphsNorth != null) {
                        drawTextLabels(g2d, textGlyphsNorth, Graticule.TextLocation.NORTH);
                    }

                    final Graticule.TextGlyph[] textGlyphsSouth = graticule.getTextGlyphsSouth();
                    if (textGlyphsSouth != null) {
                        drawTextLabels(g2d, textGlyphsSouth, Graticule.TextLocation.SOUTH);
                    }

                    final Graticule.TextGlyph[] textGlyphsWest = graticule.getTextGlyphsWest();
                    if (textGlyphsWest != null) {
                        drawTextLabels(g2d, textGlyphsWest, Graticule.TextLocation.WEST);
                    }

                    final Graticule.TextGlyph[] textGlyphsEast = graticule.getTextGlyphsEast();
                    if (textGlyphsEast != null) {
                        drawTextLabels(g2d, textGlyphsEast, Graticule.TextLocation.EAST);
                    }
                }
            } finally {
                g2d.setTransform(transformSave);
            }
        }
    }

    private void drawLinePaths(Graphics2D g2d, final GeneralPath[] linePaths) {
        Composite oldComposite = null;
        if (getLineTransparency() > 0.0) {
            oldComposite = g2d.getComposite();
            g2d.setComposite(getAlphaComposite(getLineTransparency()));
        }
        g2d.setPaint(getLineColor());


// DANNY
// if dashed line
        Stroke drawingStroke = new BasicStroke((float) getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);

// else
        //    g2d.setStroke(new BasicStroke((float) getLineWidth()));


        g2d.setStroke(drawingStroke);
        for (GeneralPath linePath : linePaths) {
            g2d.draw(linePath);
        }
        if (oldComposite != null) {
            g2d.setComposite(oldComposite);
        }
    }



    private void drawBorderPath(Graphics2D g2d, RasterDataNode raster) {
        Composite oldComposite = null;
        if (getLineTransparency() > 0.0) {
            oldComposite = g2d.getComposite();
            g2d.setComposite(getAlphaComposite(getLineTransparency()));
        }
        g2d.setPaint(getLineColor());


// DANNY
// if dashed line
        //      Stroke drawingStroke = new BasicStroke((float) getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);

// else
        Stroke drawingStroke = new BasicStroke((float) getLineWidth());


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
        int textOutwardsOffset = getTextOffsetOutward();
        int textSidewardsOffset = getTextOffsetSideward();

        Font font = new Font("SansSerif", Font.ITALIC, getTextFontSize());
        g2d.setFont(font);


        if (getTextBgTransparency() < 1.0) {
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
                if (isTextOutside()) {
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


//        g2d.setFont(getTextFont());
        g2d.setPaint(getTextFgColor());
        for (Graticule.TextGlyph glyph : textGlyphs) {
            g2d.translate(glyph.getX(), glyph.getY());
            g2d.rotate(glyph.getAngle());

            Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(glyph.getText(), g2d);
            float width = (float) labelBounds.getWidth();
            float height = (float) labelBounds.getHeight();

            float shiftSideways = height / 3;
            double nudge = height / 2;
            int offsetNudge = (int) nudge;
            int newNudge = (int) (height * 2);

            Rectangle2D singleLetter = g2d.getFontMetrics().getStringBounds("W", g2d);
            double letterWidth = singleLetter.getWidth();
            float halfLetterWidth = (float) (letterWidth / 2.0);

            boolean rotateSouth = false;
            boolean rotateNorth = false;

            if (isTextOutside()) {
                if (textLocation == Graticule.TextLocation.NORTH && !rotateNorth ||
                        textLocation == Graticule.TextLocation.WEST ||
                        textLocation == Graticule.TextLocation.SOUTH && rotateSouth) {
                    g2d.drawString(glyph.getText(), -width - halfLetterWidth - textOutwardsOffset, shiftSideways + textSidewardsOffset);
                } else {

                    AffineTransform orig = g2d.getTransform();
                    g2d.rotate(-Math.PI);
                    g2d.drawString(glyph.getText(), +halfLetterWidth + textOutwardsOffset, shiftSideways + textSidewardsOffset);

                    g2d.setTransform(orig);
                }

                //        g2d.drawString(glyph.getText(), textOutwardsOffset, shiftSideways + textSidewardsOffset);

            } else {
                g2d.drawString(glyph.getText(), offsetNudge + textOutwardsOffset, ty + textSidewardsOffset);
            }
//
//           if (glyph.getText().endsWith("N") || glyph.getText().endsWith("S")) {
//
//           } else {
//
//           }

            //      g2d.drawString(glyph.getText(), tx, ty);

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

    private double getLineWidth() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH,
                GraticuleLayerType.DEFAULT_LINE_WIDTH);
    }

    private Font getTextFont() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT,
                GraticuleLayerType.DEFAULT_TEXT_FONT);
    }

    private Integer getTextFontSize() {
        return getConfigurationProperty(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE,
                GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);
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
