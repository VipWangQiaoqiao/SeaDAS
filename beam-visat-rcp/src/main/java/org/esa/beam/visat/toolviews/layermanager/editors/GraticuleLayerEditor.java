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
package org.esa.beam.visat.toolviews.layermanager.editors;

import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.ValueRange;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;
import com.bc.ceres.swing.binding.internal.RangeEditor;
import org.esa.beam.framework.ui.layer.AbstractLayerConfigurationEditor;
import org.esa.beam.glayer.GraticuleLayerType;

import java.awt.Color;

/**
 * Editor for graticule layer.
 *
 * @author Marco Zuehlke
 * @version $Revision$ $Date$
 * @since BEAM 4.6
 */
public class GraticuleLayerEditor extends AbstractLayerConfigurationEditor {

    @Override
    protected void addEditablePropertyDescriptors() {

        final PropertyEditorRegistry propertyEditorRegistry = PropertyEditorRegistry.getInstance();

        PropertyDescriptor vd7 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, Boolean.class);
        vd7.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED);
        vd7.setDisplayName("Show Labels");
        vd7.setDefaultConverter();
        addPropertyDescriptor(vd7);

        PropertyDescriptor vd20 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH, Boolean.class);
        vd20.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_NORTH);
        vd20.setDisplayName("Show Labels - Northside");
        vd20.setDefaultConverter();
        addPropertyDescriptor(vd20);



        PropertyDescriptor vd21 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH, Boolean.class);
        vd21.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_SOUTH);
        vd21.setDisplayName("Show Labels - Southside");
        vd21.setDefaultConverter();
        addPropertyDescriptor(vd21);

        PropertyDescriptor vd22 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST, Boolean.class);
        vd22.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_WEST);
        vd22.setDisplayName("Show Labels - Westside");
        vd22.setDefaultConverter();
        addPropertyDescriptor(vd22);

        PropertyDescriptor vd23 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST, Boolean.class);
        vd23.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_EAST);
        vd23.setDisplayName("Show Labels - Eastside");
        vd23.setDefaultConverter();
        addPropertyDescriptor(vd23);

        PropertyDescriptor vd14 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_OUTSIDE, Boolean.class);
        vd14.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_OUTSIDE);
        vd14.setDisplayName("Labels on Outside");
        vd14.setDefaultConverter();
        addPropertyDescriptor(vd14);



//        PropertyDescriptor vd45 = new PropertyDescriptor("test", null);
//     //   vd45.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED);
//        vd45.setDisplayName("Show Labels");
//   //     vd45.setDefaultConverter();
//        addPropertyDescriptor(vd45);


        PropertyDescriptor vd24 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, Boolean.class);
        vd24.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_ENABLED);
        vd24.setDisplayName("Show Grid Lines");
        vd24.setDefaultConverter();
        addPropertyDescriptor(vd24);


        PropertyDescriptor vd27 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED, Boolean.class);
        vd27.setDefaultValue(GraticuleLayerType.DEFAULT_BORDER_ENABLED);
        vd27.setDisplayName("Show Border");
        vd27.setDefaultConverter();
        addPropertyDescriptor(vd27);



        PropertyDescriptor vd0 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_RES_AUTO, Boolean.class);
        vd0.setDefaultValue(GraticuleLayerType.DEFAULT_RES_AUTO);
        vd0.setDisplayName("Grid Line Spacing (Number of Grid Lines)");
        vd0.setDefaultConverter();
        addPropertyDescriptor(vd0);

        PropertyDescriptor vd1 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_RES_PIXELS, Integer.class);
        vd1.setDefaultValue(GraticuleLayerType.DEFAULT_RES_PIXELS);
        vd1.setValueRange(new ValueRange(2, 50));
        vd1.setDisplayName("Grid Line Spacing (Number of Grid Lines)");
        vd1.setDefaultConverter();
        addPropertyDescriptor(vd1);

        PropertyDescriptor vd2 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_RES_LAT, Double.class);
        vd2.setDefaultValue(GraticuleLayerType.DEFAULT_RES_LAT);
        vd2.setValueRange(new ValueRange(0.01, 90.00));
        vd2.setDisplayName("Grid Line Spacing (Latitude Step Size)");
        vd2.setDefaultConverter();
        addPropertyDescriptor(vd2);

        PropertyDescriptor vd3 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_RES_LON, Double.class);
        vd3.setDefaultValue(GraticuleLayerType.DEFAULT_RES_LON);
        vd3.setValueRange(new ValueRange(0.01, 180.00));
        vd3.setDisplayName("Grid Line Spacing (Longitude Step Size)");
        vd3.setDefaultConverter();
        addPropertyDescriptor(vd3);



        PropertyDescriptor vd11 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE, Integer.class);
        vd11.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);
        vd11.setDisplayName("Font Size");
        vd11.setDefaultConverter();
        addPropertyDescriptor(vd11);

        PropertyDescriptor vd8 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR, Color.class);
        vd8.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_FG_COLOR);
        vd8.setDisplayName("Font Color");
        vd8.setDefaultConverter();
        addPropertyDescriptor(vd8);



        PropertyDescriptor vd5 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH, Double.class);
        vd5.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_WIDTH);
        vd5.setDisplayName("Grid Line Width");
        vd5.setDefaultConverter();
        addPropertyDescriptor(vd5);

        PropertyDescriptor vd4 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR, Color.class);
        vd4.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_COLOR);
        vd4.setDisplayName("Grid Line Color");
        vd4.setDefaultConverter();
        addPropertyDescriptor(vd4);

        PropertyDescriptor vd6 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY, Double.class);
        vd6.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_TRANSPARENCY);
        vd6.setValueRange(new ValueRange(0, 1));
        vd6.setDisplayName("Grid Line Transparency");
        vd6.setDefaultConverter();
        vd6.setAttribute("propertyEditor", propertyEditorRegistry.getPropertyEditor(RangeEditor.class.getName()));
        addPropertyDescriptor(vd6);

        PropertyDescriptor vd25 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED, Boolean.class);
        vd25.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_DASHED);
        vd25.setDisplayName("Dashed Grid Lines");
        vd25.setDefaultConverter();
        addPropertyDescriptor(vd25);

        PropertyDescriptor vd26 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE, Double.class);
        vd26.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_DASHED_PHASE);
        vd26.setDisplayName("Dashed Grid Lines (Dash Lengths)");
        vd26.setDefaultConverter();
        addPropertyDescriptor(vd26);












        PropertyDescriptor vd15 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_ITALIC, Boolean.class);
        vd15.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_FONT_ITALIC);
        vd15.setDisplayName("Labels (Italic)");
        vd15.setDefaultConverter();
        addPropertyDescriptor(vd15);






        PropertyDescriptor vd16 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH, Double.class);
        vd16.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH);
        vd16.setDisplayName("   Rotate Longitude Labels");
        vd16.setDefaultConverter();
        vd16.setValueRange(new ValueRange(0, 90));
        vd16.setAttribute("propertyEditor", propertyEditorRegistry.getPropertyEditor(RangeEditor.class.getName()));
        addPropertyDescriptor(vd16);

//        PropertyDescriptor vd17 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_SOUTH, Double.class);
//        vd17.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_SOUTH);
//        vd17.setDisplayName("   Rotate Labels - Southside (degrees)");
//        vd17.setDefaultConverter();
//
//        addPropertyDescriptor(vd17);

        PropertyDescriptor vd18 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST, Double.class);
        vd18.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST);
        vd18.setDisplayName("   Rotate Latitude Labels");
        vd18.setDefaultConverter();
        vd18.setValueRange(new ValueRange(0, 90));
        vd18.setAttribute("propertyEditor", propertyEditorRegistry.getPropertyEditor(RangeEditor.class.getName()));
        addPropertyDescriptor(vd18);
//
//        PropertyDescriptor vd19 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_EAST, Double.class);
//        vd19.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_EAST);
//        vd19.setDisplayName("   Rotate Labels - Eastside (degrees)");
//        vd19.setDefaultConverter();
//        addPropertyDescriptor(vd19);
//
//        PropertyDescriptor vd19b = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_ANCHORED, Boolean.class);
//        vd19b.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_ANCHORED);
//        vd19b.setDisplayName("  Anchor Rotated Labels");
//        vd19b.setDefaultConverter();
//        addPropertyDescriptor(vd19b);





        PropertyDescriptor vd12 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_OFFSET_OUTWARD, Integer.class);
        vd12.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_OFFSET_OUTWARD);
        vd12.setDisplayName("Label Offset Inward");
        vd12.setDefaultConverter();
        addPropertyDescriptor(vd12);

        PropertyDescriptor vd13 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_OFFSET_SIDEWARD, Integer.class);
        vd13.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_OFFSET_SIDEWARD);
        vd13.setDisplayName("Label Offset Sideward");
        vd13.setDefaultConverter();
        addPropertyDescriptor(vd13);


        PropertyDescriptor vd9 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR, Color.class);
        vd9.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_BG_COLOR);
        vd9.setDisplayName("Label Background Color");
        vd9.setDefaultConverter();
        addPropertyDescriptor(vd9);

        PropertyDescriptor vd10 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY, Double.class);
        vd10.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_BG_TRANSPARENCY);
        vd10.setValueRange(new ValueRange(0, 1));
        vd10.setDisplayName("Label Background Transparency");
        vd10.setDefaultConverter();
        vd10.setAttribute("propertyEditor", propertyEditorRegistry.getPropertyEditor(RangeEditor.class.getName()));
        addPropertyDescriptor(vd10);






        PropertyDescriptor vd29 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH, Double.class);
        vd29.setDefaultValue(GraticuleLayerType.DEFAULT_BORDER_WIDTH);
        vd29.setDisplayName("Border Width");
        vd29.setDefaultConverter();
        addPropertyDescriptor(vd29);


        PropertyDescriptor vd28 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR, Color.class);
        vd28.setDefaultValue(GraticuleLayerType.DEFAULT_BORDER_COLOR);
        vd28.setDisplayName("Border Color");
        vd28.setDefaultConverter();
        addPropertyDescriptor(vd28);





        BindingContext bindingContext = getBindingContext();
        boolean resAuto = (Boolean) bindingContext.getPropertySet().getValue(
                GraticuleLayerType.PROPERTY_NAME_RES_AUTO);
        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_RES_PIXELS, resAuto,
                                        GraticuleLayerType.PROPERTY_NAME_RES_AUTO, resAuto);
        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_RES_LAT, !resAuto,
                                        GraticuleLayerType.PROPERTY_NAME_RES_AUTO, resAuto);
        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_RES_LON, !resAuto,
                                        GraticuleLayerType.PROPERTY_NAME_RES_AUTO, resAuto);

        boolean textEnabled = (Boolean) bindingContext.getPropertySet().getValue(
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED);


        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR, textEnabled,
                                        GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);
        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR, textEnabled,
                                        GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);
        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY, textEnabled,
                                        GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);


        // DANNY added these
        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_ITALIC, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_OFFSET_OUTWARD, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_OFFSET_SIDEWARD, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_OUTSIDE, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

//        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_SOUTH, textEnabled,
//                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

//        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_EAST, textEnabled,
//                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);
//
//        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_ANCHORED, textEnabled,
//                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST, textEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED, textEnabled);



        boolean lineEnabled = (Boolean) bindingContext.getPropertySet().getValue(
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED);


        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH, lineEnabled,
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, lineEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR, lineEnabled,
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, lineEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY, lineEnabled,
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, lineEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED, lineEnabled,
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, lineEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE, lineEnabled,
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, lineEnabled);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH, lineEnabled,
                GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, lineEnabled);



        boolean borderEnabled = (Boolean) bindingContext.getPropertySet().getValue(
                GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR, borderEnabled,
                GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED, borderEnabled);


        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH, borderEnabled,
                GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED, borderEnabled);



    }

}
