package org.esa.beam.visat.toolviews.placemark.annotation;

import com.jidesoft.grid.ColorCellRenderer;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.visat.toolviews.placemark.PlacemarkManagerToolView;
import org.esa.beam.visat.toolviews.placemark.TableModelFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 6/22/16
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextAnnotationManagerToolView extends PlacemarkManagerToolView {
    public static final String ID = TextAnnotationManagerToolView.class.getName();

    public TextAnnotationManagerToolView() {
        super(TextAnnotationDescriptor.getInstance(), new TableModelFactory() {
            @Override
            public TextAnnotationTableModel createTableModel(PlacemarkDescriptor placemarkDescriptor, Product product,
                                                  Band[] selectedBands, TiePointGrid[] selectedGrids) {
                return new TextAnnotationTableModel(placemarkDescriptor, product, selectedBands, selectedGrids);
            }
        });
    }

}
