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

package org.esa.beam.framework.ui.product;

import com.bc.ceres.grender.Rendering;
import com.bc.ceres.swing.figure.AbstractPointFigure;
import com.bc.ceres.swing.figure.FigureStyle;
import com.bc.ceres.swing.figure.Handle;
import com.bc.ceres.swing.figure.Symbol;
import com.bc.ceres.swing.figure.support.DefaultFigureStyle;
import com.bc.ceres.swing.figure.support.NamedSymbol;
import com.bc.ceres.swing.figure.support.PointHandle;
import com.bc.ceres.swing.figure.support.ShapeSymbol;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.esa.beam.framework.datamodel.Placemark;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;


public class SimpleFeaturePointFigure extends AbstractPointFigure implements SimpleFeatureFigure {

    private Font labelFont = new Font("SansSerif", Font.PLAIN, 16);
    private static final int[] labelOutlineAlphas = new int[]{200};
    // todo Danny commented this out ... though it it useful for fading the outline or background out so I'm leaving comments just in case we go that route
    // private static final int[] labelOutlineAlphas = new int[]{64, 128, 192, 255};
    private static Stroke[] labelOutlineStrokes = new Stroke[labelOutlineAlphas.length];
    private static Color[] labelOutlineColors = new Color[labelOutlineAlphas.length];
    private Color labelFontColor = Color.BLACK;
    private Color labelOutlineColor = Color.WHITE;
    private static String[] labelAttributeNames = new String[]{
            Placemark.PROPERTY_NAME_LABEL,
            "Label",
    };

    private final SimpleFeature simpleFeature;
    private Point geometry;

    {
        for (int i = 0; i < labelOutlineAlphas.length; i++) {
            labelOutlineStrokes[i] = new BasicStroke((labelOutlineAlphas.length - i));
            labelOutlineColors[i] = new Color(labelOutlineColor.getRed(),
                    labelOutlineColor.getGreen(),
                    labelOutlineColor.getBlue(),
                    labelOutlineAlphas[i]);
        }
    }

    public void updateFontColor(Font newFont, Color newLabelFontColor, Color newLabelOutlineColor) {
        setLabelFont(newFont);
        labelFontColor = newLabelFontColor;
        labelOutlineColor = newLabelOutlineColor;
        for (int i = 0; i < labelOutlineAlphas.length; i++) {
            labelOutlineStrokes[i] = new BasicStroke((labelOutlineAlphas.length - i));
            labelOutlineColors[i] = new Color(labelOutlineColor.getRed(),
                    labelOutlineColor.getGreen(),
                    labelOutlineColor.getBlue(),
                    labelOutlineAlphas[i]);
        }
    }

    public SimpleFeaturePointFigure(SimpleFeature simpleFeature, FigureStyle style) {
        this(simpleFeature, style, style);
    }

    public SimpleFeaturePointFigure(SimpleFeature simpleFeature, FigureStyle normalStyle, FigureStyle selectedStyle) {
        super(normalStyle, selectedStyle);
        this.simpleFeature = simpleFeature;
        Object o = simpleFeature.getDefaultGeometry();
        if (!(o instanceof Point)) {
            throw new IllegalArgumentException("simpleFeature");
        }
        geometry = (Point) o;
        setSelectable(true);
    }

    public Font getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
    }

    @Override
    public SimpleFeature getSimpleFeature() {
        return simpleFeature;
    }

    @Override
    public Point getGeometry() {
        return geometry;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        this.geometry = (Point) geometry;
    }

    @Override
    public void forceRegeneration() {
    }

    @Override
    public double getX() {
        return geometry.getX();
    }

    @Override
    public double getY() {
        return geometry.getY();
    }

    @Override
    public void setLocation(double x, double y) {
        Coordinate coordinate = geometry.getCoordinate();
        coordinate.x = x;
        coordinate.y = y;
        geometry.geometryChanged();
        fireFigureChanged();
    }

    @Override
    public double getRadius() {
        return 1E-10; // = any small, non-zero value will be ok
    }

    @Override
    protected void drawPoint(Rendering rendering) {
        super.drawPoint(rendering);
        String label = getLabel();
        if (label != null && !label.trim().isEmpty()) {
            drawLabel(rendering, label);
        }
    }

    private String getLabel() {
        for (String labelAttributeName : labelAttributeNames) {
            Object labelAttribute = simpleFeature.getAttribute(labelAttributeName);
            if (labelAttribute instanceof String) {
                return (String) labelAttribute;
            }
        }
        return null;
    }


    private void drawLabel(Rendering rendering, String label) {

        final Graphics2D graphics = rendering.getGraphics();
        final Font oldFont = graphics.getFont();
        final Stroke oldStroke = graphics.getStroke();
        final Paint oldPaint = graphics.getPaint();

        //todo Danny  this toggles the new mode but is hard coded
        boolean backgroundBox = true;
        //todo Danny  this adjust symbol location ... ideally for text annotation it is over text.
        boolean isTextAnnotation = false;

        try {
            graphics.setFont(getLabelFont());
            GlyphVector glyphVector = getLabelFont().createGlyphVector(graphics.getFontRenderContext(), label);
            Rectangle2D logicalBounds = glyphVector.getLogicalBounds();
            float tx = (float) (logicalBounds.getX() - 0.5 * logicalBounds.getWidth());
            float ty = (float) (getSymbol().getBounds().getMaxY() + logicalBounds.getHeight() + 1.0);

            if (isTextAnnotation) {
                ty = (float) (0.5* logicalBounds.getHeight());
            }
            Shape labelOutline = glyphVector.getOutline(tx, ty);

            if (backgroundBox) {

                logicalBounds.setRect(tx-1.0,
                        ty - logicalBounds.getHeight()+3.0,
                        logicalBounds.getWidth()+2.0,
                        logicalBounds.getHeight());

                for (int i = 0; i < labelOutlineAlphas.length; i++) {
                    graphics.setStroke(labelOutlineStrokes[i]);
                    graphics.setPaint(labelOutlineColors[i]);

                    graphics.fill(logicalBounds);
                    graphics.draw(logicalBounds);

                }
            } else {
                for (int i = 0; i < labelOutlineAlphas.length; i++) {
                    graphics.setStroke(labelOutlineStrokes[i]);
                    graphics.setPaint(labelOutlineColors[i]);

                    graphics.draw(labelOutline);
                    graphics.fill(labelOutline);
                }
            }




            graphics.setPaint(labelFontColor);
            graphics.fill(labelOutline);
        } finally {
            graphics.setPaint(oldPaint);
            graphics.setStroke(oldStroke);
            graphics.setFont(oldFont);
        }
    }

    @Override
    public int getMaxSelectionStage() {
        return 1;
    }

    @Override
    public Handle[] createHandles(int selectionStage) {
        if (selectionStage == 1) {
            DefaultFigureStyle handleStyle = new DefaultFigureStyle();
            handleStyle.setStrokeColor(Color.YELLOW);
            handleStyle.setStrokeOpacity(0.8);
            handleStyle.setStrokeWidth(1.0);
            handleStyle.setFillColor(Color.YELLOW);
            handleStyle.setFillOpacity(0.4);
            Symbol symbol = getSymbol();
            if (symbol instanceof NamedSymbol) {
                NamedSymbol namedSymbol = (NamedSymbol) symbol;
                symbol = namedSymbol.getSymbol();
            }
            if (symbol instanceof ShapeSymbol) {
                ShapeSymbol shapeSymbol = (ShapeSymbol) symbol;
                return new Handle[]{new PointHandle(this, handleStyle, shapeSymbol.getShape())};
            }
            return new Handle[]{new PointHandle(this, handleStyle)};
        }
        return super.createHandles(selectionStage);
    }
}