package org.esa.beam.visat.toolviews.placemark.annotation;

import com.bc.ceres.core.Assert;
import com.bc.ceres.swing.figure.FigureStyle;
import com.bc.ceres.swing.figure.support.DefaultFigureStyle;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.visat.toolviews.placemark.AbstractPlacemarkTableModel;
import org.esa.beam.visat.toolviews.placemark.PlacemarkUtils;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 6/22/16
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextAnnotationTableModel  extends AbstractPlacemarkTableModel {
    private final int xIndex = 0;
    private final int yIndex = 1;
    private final int lonIndex = 2;
    private final int latIndex = 3;
    private final int colorIndex = 4;
    private final int labelIndex = 5;

    public TextAnnotationTableModel(PlacemarkDescriptor placemarkDescriptor, Product product, Band[] selectedBands,
                         TiePointGrid[] selectedGrids) {
        super(placemarkDescriptor, product, selectedBands, selectedGrids);
    }

    @Override
    public String[] getStandardColumnNames() {
        return new String[]{"X", "Y", "Lon", "Lat", "Color", "Text"};
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (getProduct().getGeoCoding() == null &&
                (columnIndex == lonIndex || columnIndex == latIndex)) {
            return false;
        }
        return columnIndex < getStandardColumnNames().length;
    }

    @Override
    protected Object getStandardColumnValueAt(int rowIndex, int columnIndex) {
        Assert.notNull(getProduct());
        final Placemark placemark = getPlacemarkDescriptor().getPlacemarkGroup(getProduct()).get(rowIndex);
        float x = Float.NaN;
        float y = Float.NaN;

        final PixelPos pixelPos = placemark.getPixelPos();
        if (pixelPos != null) {
            x = pixelPos.x;
            y = pixelPos.y;
        }

        float lon = Float.NaN;
        float lat = Float.NaN;

        final GeoPos geoPos = placemark.getGeoPos();
        if (geoPos != null) {
            lon = geoPos.lon;
            lat = geoPos.lat;
        }

        switch (columnIndex) {
            case xIndex:
                return x;
            case yIndex:
                return y;
            case lonIndex:
                return lon;
            case latIndex:
                return lat;
            case colorIndex:
                return PlacemarkUtils.getPlacemarkColor(placemark);
            case labelIndex:
                return placemark.getLabel();
            default:
                return "";
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case xIndex:
                return Float.class;
            case yIndex:
                return Float.class;
            case lonIndex:
                return Float.class;
            case latIndex:
                return Float.class;
            case colorIndex:
                return Color.class;
            case labelIndex:
                return String.class;
        }
        return Object.class;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == colorIndex) {
            final String colorName = DefaultFigureStyle.FILL_COLOR.getName();
            final Placemark pin = getPlacemarkAt(rowIndex);
            final String styleCss = pin.getStyleCss();
            FigureStyle style = new DefaultFigureStyle();
            style.fromCssString(styleCss);
            style.setValue(colorName, value);
            pin.setStyleCss(style.toCssString());
        } else {
            super.setValueAt(value, rowIndex, columnIndex);
        }
    }
}
