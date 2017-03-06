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
import org.esa.beam.framework.ui.BooleanExpressionConverter;
import org.esa.beam.framework.ui.layer.AbstractLayerConfigurationEditor;
import org.esa.beam.glayer.GraticuleLayerType;
import org.esa.beam.util.PropertyMap;
import org.esa.beam.visat.VisatApp;

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


        final VisatApp visatApp = VisatApp.getApp();
        PropertyMap configuration = visatApp.getPreferences();;



        PropertyDescriptor vd20 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH, Boolean.class);
      //  vd20.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_NORTH);
        vd20.setDefaultValue(configuration.getPropertyBool(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH, GraticuleLayerType.DEFAULT_TEXT_ENABLED_NORTH));
        vd20.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_NORTH);
        vd20.setDefaultConverter();
        addPropertyDescriptor(vd20);


        PropertyDescriptor vd21 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH, Boolean.class);
        vd21.setDefaultValue(configuration.getPropertyBool(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH, GraticuleLayerType.DEFAULT_TEXT_ENABLED_SOUTH));
        vd21.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_SOUTH);
        vd21.setDefaultConverter();
        addPropertyDescriptor(vd21);

        PropertyDescriptor vd22 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST, Boolean.class);
        vd22.setDefaultValue(configuration.getPropertyBool(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST, GraticuleLayerType.DEFAULT_TEXT_ENABLED_WEST));
        vd22.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_WEST);
        vd22.setDefaultConverter();
        addPropertyDescriptor(vd22);

        PropertyDescriptor vd23 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST, Boolean.class);
        vd23.setDefaultValue(configuration.getPropertyBool(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST, GraticuleLayerType.DEFAULT_TEXT_ENABLED_EAST));
        vd23.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_EAST);
        vd23.setDefaultConverter();
        addPropertyDescriptor(vd23);






        PropertyDescriptor vd31 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED, Boolean.class);
        vd31.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_TOP_LON_ENABLED);
        vd31.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_TOP_LON_ENABLED);
        vd31.setDefaultConverter();
        addPropertyDescriptor(vd31);

        PropertyDescriptor vd35 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED, Boolean.class);
        vd35.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED);
        vd35.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_BOTTOM_LON_ENABLED);
        vd35.setDefaultConverter();
        addPropertyDescriptor(vd35);

        PropertyDescriptor vd32 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED, Boolean.class);
        vd32.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED);
        vd32.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_LEFT_LAT_ENABLED);
        vd32.setDefaultConverter();
        addPropertyDescriptor(vd32);

        PropertyDescriptor vd34 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED, Boolean.class);
        vd34.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED);
        vd34.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_RIGHT_LAT_ENABLED);
        vd34.setDefaultConverter();
        addPropertyDescriptor(vd34);




//
//        PropertyDescriptor vd1 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_NUM_GRID_LINES, Integer.class);
//        vd1.setDefaultValue(GraticuleLayerType.DEFAULT_NUM_GRID_LINES);
//        vd1.setValueRange(new ValueRange(2, 50));
//        vd1.setDisplayName("....... Number of Grid Lines");
//        vd1.setDefaultConverter();
//        addPropertyDescriptor(vd1);












//        PropertyDescriptor vd15 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_ITALIC, Boolean.class);
//        vd15.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_FONT_ITALIC);
//        vd15.setDisplayName("Labels (Italic)");
//        vd15.setDefaultConverter();
//        addPropertyDescriptor(vd15);

        PropertyDescriptor vdTickMarkEnabled = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED, Boolean.class);
        vdTickMarkEnabled.setDefaultValue(GraticuleLayerType.DEFAULT_TICKMARK_ENABLED);
        vdTickMarkEnabled.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TICKMARK_ENABLED);
        vdTickMarkEnabled.setDefaultConverter();
        addPropertyDescriptor(vdTickMarkEnabled);

        PropertyDescriptor vdTickMarkInside = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE, Boolean.class);
        vdTickMarkInside.setDefaultValue(GraticuleLayerType.DEFAULT_TICKMARK_INSIDE);
        vdTickMarkInside.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TICKMARK_INSIDE);
        vdTickMarkInside.setDefaultConverter();
        addPropertyDescriptor(vdTickMarkInside);



        PropertyDescriptor vd14 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE, Boolean.class);
        vd14.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_INSIDE);
        vd14.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_INSIDE);
        vd14.setDefaultConverter();
        addPropertyDescriptor(vd14);




        PropertyDescriptor vd24 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, Boolean.class);
        vd24.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_ENABLED);
        vd24.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_LINE_ENABLED);
        vd24.setDefaultConverter();
        addPropertyDescriptor(vd24);

//        PropertyDescriptor vd25 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED, Boolean.class);
//        vd25.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_DASHED);
//        vd25.setDisplayName("Dashed Grid Lines");
//        vd25.setDefaultConverter();
//        addPropertyDescriptor(vd25);






        PropertyDescriptor vd27 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED, Boolean.class);
        vd27.setDefaultValue(GraticuleLayerType.DEFAULT_BORDER_ENABLED);
        vd27.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_BORDER_ENABLED);
        vd27.setDefaultConverter();
        addPropertyDescriptor(vd27);


        PropertyDescriptor vd281 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_FORMAT_COMPASS, Boolean.class);
        vd281.setDefaultValue(configuration.getPropertyBool(GraticuleLayerType.PROPERTY_NAME_FORMAT_COMPASS, GraticuleLayerType.DEFAULT_FORMAT_COMPASS));
        vd281.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_FORMAT_COMPASS);
        vd281.setDefaultConverter();
        addPropertyDescriptor(vd281);

        PropertyDescriptor vd282 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_FORMAT_DECIMAL, Boolean.class);
        vd282.setDefaultValue(configuration.getPropertyBool(GraticuleLayerType.PROPERTY_NAME_FORMAT_DECIMAL, GraticuleLayerType.DEFAULT_FORMAT_DECIMAL));
        vd282.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_FORMAT_DECIMAL);
        vd282.setDefaultConverter();
        addPropertyDescriptor(vd282);






        PropertyDescriptor vd2 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_RES_LAT, Double.class);
        vd2.setDefaultValue(GraticuleLayerType.DEFAULT_RES_LAT);
        vd2.setValueRange(new ValueRange(0.0, 90.00));
        vd2.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_RES_LAT);
        vd2.setDefaultConverter();
        addPropertyDescriptor(vd2);

        PropertyDescriptor vd3 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_RES_LON, Double.class);
        vd3.setDefaultValue(GraticuleLayerType.DEFAULT_RES_LON);
        vd3.setValueRange(new ValueRange(0.0, 180.00));
        vd3.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_RES_LON);
        vd3.setDefaultConverter();
        addPropertyDescriptor(vd3);



        PropertyDescriptor vd16 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH, Integer.class);
        vd16.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH_SOUTH);
        vd16.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_ROTATION_NORTH_SOUTH);
        vd16.setDefaultConverter();
        vd16.setValueRange(new ValueRange(0, 90));
        addPropertyDescriptor(vd16);


        PropertyDescriptor vd18 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST_EAST, Integer.class);
        vd18.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST_EAST);
        vd18.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_ROTATION_WEST_EAST);
        vd18.setDefaultConverter();
        vd18.setValueRange(new ValueRange(0, 90));
        addPropertyDescriptor(vd18);


        PropertyDescriptor vd11 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE, Integer.class);
        vd11.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);
        vd11.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_FONT_SIZE);
        vd11.setDefaultConverter();
        addPropertyDescriptor(vd11);

        PropertyDescriptor vdCornerFontSize = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_SIZE, Integer.class);
        vdCornerFontSize.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_SIZE);
        vdCornerFontSize.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_FONT_SIZE);
        vdCornerFontSize.setDefaultConverter();
        addPropertyDescriptor(vdCornerFontSize);

        PropertyDescriptor vd5 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH, Double.class);
        vd5.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_WIDTH);
        vd5.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_LINE_WIDTH);
        vd5.setDefaultConverter();
        addPropertyDescriptor(vd5);

        PropertyDescriptor vd26 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE, Double.class);
        vd26.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_DASHED_PHASE);
        vd26.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_LINE_DASHED_PHASE);
        vd26.setDefaultConverter();
        addPropertyDescriptor(vd26);

        PropertyDescriptor vdTickMarkLength = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH, Double.class);
        vdTickMarkLength.setDefaultValue(GraticuleLayerType.DEFAULT_TICKMARK_LENGTH);
        vdTickMarkLength.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TICKMARK_LENGTH);
        vdTickMarkLength.setDefaultConverter();
        addPropertyDescriptor(vdTickMarkLength);

        PropertyDescriptor vd29 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH, Double.class);
        vd29.setDefaultValue(GraticuleLayerType.DEFAULT_BORDER_WIDTH);
        vd29.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_BORDER_WIDTH);
        vd29.setDefaultConverter();
        addPropertyDescriptor(vd29);



        PropertyDescriptor vd6 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY, Double.class);
        vd6.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_TRANSPARENCY);
        vd6.setValueRange(new ValueRange(0, 1));
        vd6.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_LINE_TRANSPARENCY);
        vd6.setDefaultConverter();
      //  vd6.setAttribute("propertyEditor", propertyEditorRegistry.getPropertyEditor(RangeEditor.class.getName()));
        addPropertyDescriptor(vd6);


        PropertyDescriptor vd10 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY, Double.class);
        vd10.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_BG_TRANSPARENCY);
        vd10.setValueRange(new ValueRange(0, 1));
        vd10.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_BG_TRANSPARENCY);
        vd10.setDefaultConverter();
   //     vd10.setAttribute("propertyEditor", propertyEditorRegistry.getPropertyEditor(RangeEditor.class.getName()));
        addPropertyDescriptor(vd10);





        PropertyDescriptor vd8 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR, Color.class);
        vd8.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_FG_COLOR);
        vd8.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_FG_COLOR);
        vd8.setDefaultConverter();
        addPropertyDescriptor(vd8);


        PropertyDescriptor vdTextCornerFontColor = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_COLOR, Color.class);
        vdTextCornerFontColor.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_COLOR);
        vdTextCornerFontColor.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_FONT_COLOR);
        vdTextCornerFontColor.setDefaultConverter();
        addPropertyDescriptor(vdTextCornerFontColor);


        PropertyDescriptor vd4 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR, Color.class);
        vd4.setDefaultValue(GraticuleLayerType.DEFAULT_LINE_COLOR);
        vd4.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_LINE_COLOR);
        vd4.setDefaultConverter();
        addPropertyDescriptor(vd4);

        PropertyDescriptor vd28 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR, Color.class);
        vd28.setDefaultValue(GraticuleLayerType.DEFAULT_BORDER_COLOR);
        vd28.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_BORDER_COLOR);
        vd28.setDefaultConverter();
        addPropertyDescriptor(vd28);

        PropertyDescriptor vd9 = new PropertyDescriptor(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR, Color.class);
        vd9.setDefaultValue(GraticuleLayerType.DEFAULT_TEXT_BG_COLOR);
        vd9.setDisplayName(GraticuleLayerType.PROPERTY_LABEL_TEXT_BG_COLOR);
        vd9.setDefaultConverter();
        addPropertyDescriptor(vd9);



        BindingContext bindingContext = getBindingContext();


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



        boolean textInsideEnabled = (Boolean) bindingContext.getPropertySet().getValue(
                GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE);

        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY, textInsideEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE, textInsideEnabled);


        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR, textInsideEnabled,
                GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE, textInsideEnabled);


//        boolean tickMarkEnabled = (Boolean) bindingContext.getPropertySet().getValue(
//                GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED);
//
//        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE, tickMarkEnabled,
//                GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE, tickMarkEnabled);
//
//        bindingContext.bindEnabledState(GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH, tickMarkEnabled,
//                GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH, tickMarkEnabled);

    }

}
