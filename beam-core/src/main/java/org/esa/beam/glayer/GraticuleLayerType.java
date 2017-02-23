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

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerContext;
import com.bc.ceres.glayer.LayerType;
import com.bc.ceres.glayer.annotations.LayerTypeMetadata;
import org.esa.beam.framework.datamodel.RasterDataNode;

import java.awt.Color;
import java.awt.geom.AffineTransform;


@LayerTypeMetadata(name = "GraticuleLayerType",
        aliasNames = {"org.esa.beam.glayer.GraticuleLayerType"})
public class GraticuleLayerType extends LayerType {

    public static final String PROPERTY_NAME_RASTER = "raster";
    public static final String PROPERTY_NAME_NUM_GRID_LINES = "graticule.num.grid.lines"; // todo Danny changed this to number of lines so need to change variable names




    // DANNY added these

    public static final String PROPERTY_NAME_TEXT_FONT_ITALIC = "graticule.text.font.italic";
    public static final String PROPERTY_NAME_LINE_DASHED = "graticule.line.dashed";


    public static final String RESET_TO_DEFAULTS_PARAM_STR = "graticule.resetToDefaults";
    public static final String PROPERTY_NAME_TEXT_ENABLED_NORTH = "graticule.text.enabled.north";
    public static final String PROPERTY_NAME_TEXT_ENABLED_SOUTH = "graticule.text.enabled.south";
    public static final String PROPERTY_NAME_TEXT_ENABLED_WEST = "graticule.text.enabled.west";
    public static final String PROPERTY_NAME_TEXT_ENABLED_EAST = "graticule.text.enabled.east";
    public static final String PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED =  "graticule.text.corner.top.left.lon.enabled";
    public static final String PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED = "graticule.text.corner.top.left.lat.enabled";
    public static final String PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED = "graticule.text.corner.top.right.lat.enabled";
    public static final String PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED =  "graticule.text.corner.bottom.left.lon.enabled";
    public static final String PROPERTY_NAME_TICKMARK_ENABLED = "graticule.tickmark.enabled";
    public static final String PROPERTY_NAME_TICKMARK_INSIDE = "graticule.tickmark.inside";
    public static final String PROPERTY_NAME_TEXT_INSIDE = "graticule.text.inside";
    public static final String PROPERTY_NAME_LINE_ENABLED = "graticule.line.enabled";
    public static final String PROPERTY_NAME_BORDER_ENABLED = "graticule.border.enabled";
    public static final String PROPERTY_NAME_FORMAT_COMPASS = "graticule.format.compass";
    public static final String PROPERTY_NAME_FORMAT_DECIMAL = "graticule.format.decimal";

    public static final String PROPERTY_NAME_RES_LAT = "graticule.res.lat";
    public static final String PROPERTY_NAME_RES_LON = "graticule.res.lon";
    public static final String PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH = "graticule.text.rotation.north.south";
    public static final String PROPERTY_NAME_TEXT_ROTATION_WEST_EAST = "graticule.text.rotation.west.east";
    public static final String PROPERTY_NAME_TEXT_FONT_SIZE = "graticule.text.font.size";
    public static final String PROPERTY_NAME_TEXT_CORNER_FONT_SIZE = "graticule.text.corner.font.size";
    public static final String PROPERTY_NAME_LINE_WIDTH = "graticule.line.width";
    public static final String PROPERTY_NAME_LINE_DASHED_PHASE = "graticule.line.dashed.phase";
    public static final String PROPERTY_NAME_TICKMARK_LENGTH = "graticule.tickmark.length";
    public static final String PROPERTY_NAME_BORDER_WIDTH = "graticule.border.width";
    public static final String PROPERTY_NAME_LINE_TRANSPARENCY = "graticule.line.transparency";
    public static final String PROPERTY_NAME_TEXT_BG_TRANSPARENCY = "graticule.text.bg.transparency";
    public static final String PROPERTY_NAME_TEXT_FG_COLOR = "graticule.text.fg.color";
    public static final String PROPERTY_NAME_TEXT_CORNER_FONT_COLOR = "graticule.text.corner.font.color";
    public static final String PROPERTY_NAME_LINE_COLOR = "graticule.line.color";
    public static final String PROPERTY_NAME_BORDER_COLOR = "graticule.border.color";
    public static final String PROPERTY_NAME_TEXT_BG_COLOR = "graticule.text.bg.color";


    public static final String PROPERTY_LABEL_RESET_TO_DEFAULTS = "RESTORE DEFAULTS (Map Gridline Preferences)";
    public static final String PROPERTY_LABEL_TEXT_ENABLED_NORTH = "Show Longitude Labels - North";
    public static final String PROPERTY_LABEL_TEXT_ENABLED_SOUTH = "Show Longitude Labels - South";
    public static final String PROPERTY_LABEL_TEXT_ENABLED_WEST = "Show Longitude Labels - West";
    public static final String PROPERTY_LABEL_TEXT_ENABLED_EAST = "Show Longitude Labels - East";
    public static final String PROPERTY_LABEL_TEXT_CORNER_TOP_LON_ENABLED =  "Show Longitude Corner Labels - Top";
    public static final String PROPERTY_LABEL_TEXT_CORNER_LEFT_LAT_ENABLED = "Show Longitude Corner Labels - Left";
    public static final String PROPERTY_LABEL_TEXT_CORNER_RIGHT_LAT_ENABLED = "Show Longitude Corner Labels - Right";
    public static final String PROPERTY_LABEL_TEXT_CORNER_BOTTOM_LON_ENABLED =  "Show Longitude Corner Labels - Bottom";
    public static final String PROPERTY_LABEL_TICKMARK_ENABLED = "Include Tick Marks (with labels)";
    public static final String PROPERTY_LABEL_TICKMARK_INSIDE = "Put Tick Marks Inside";
    public static final String PROPERTY_LABEL_TEXT_INSIDE = "Put Labels on Inside";
    public static final String PROPERTY_LABEL_LINE_ENABLED = "Show Grid Lines";
    public static final String PROPERTY_LABEL_BORDER_ENABLED = "Show Border";
    public static final String PROPERTY_LABEL_FORMAT_COMPASS = "Labels Format: Compass";
    public static final String PROPERTY_LABEL_FORMAT_DECIMAL = "Labels Format: Decimal";

    public static final String PROPERTY_LABEL_RES_LAT = "Latitude Step Size (0=AUTO-SIZING)";
    public static final String PROPERTY_LABEL_RES_LON = "Longitude Step Size (0=AUTO-SIZING)";
    public static final String PROPERTY_LABEL_TEXT_ROTATION_NORTH_SOUTH = "Label Angle - Longitude";
    public static final String PROPERTY_LABEL_TEXT_ROTATION_WEST_EAST = "Label Angle - Latitude";
    public static final String PROPERTY_LABEL_TEXT_FONT_SIZE = "Font Size";
    public static final String PROPERTY_LABEL_TEXT_CORNER_FONT_SIZE = "Corner Font Size";
    public static final String PROPERTY_LABEL_LINE_WIDTH = "Grid Line / Tick Mark Width";
    public static final String PROPERTY_LABEL_LINE_DASHED_PHASE = "Grid Line Dash Length (0=SOLID)";
    public static final String PROPERTY_LABEL_TICKMARK_LENGTH = "Tick Mark Length";
    public static final String PROPERTY_LABEL_BORDER_WIDTH = "Border Width";
    public static final String PROPERTY_LABEL_LINE_TRANSPARENCY = "Grid Line Transparency";
    public static final String PROPERTY_LABEL_TEXT_BG_TRANSPARENCY = "Inside Label Backdrop Transparency";
    public static final String PROPERTY_LABEL_TEXT_FG_COLOR = "Font / Tick Mark Color";
    public static final String PROPERTY_LABEL_TEXT_CORNER_FONT_COLOR = "Corner Font Color";
    public static final String PROPERTY_LABEL_LINE_COLOR = "Grid Line Color";
    public static final String PROPERTY_LABEL_BORDER_COLOR = "Border Color";
    public static final String PROPERTY_LABEL_TEXT_BG_COLOR = "Inside Label Backdrop Color";








    public static final boolean DEFAULT_RESET_TO_DEFAULTS = false;


    public static final int DEFAULT_NUM_GRID_LINES = 4;
    public static final double DEFAULT_RES_LAT = 0.0;
    public static final double DEFAULT_RES_LON = 0.0;
    public static final Color DEFAULT_LINE_COLOR = new Color(0,0,80);
    public static final double DEFAULT_LINE_TRANSPARENCY = 0.6;
    public static final double DEFAULT_LINE_WIDTH = 0.8;
    public static final Color DEFAULT_TEXT_FG_COLOR = Color.BLACK;
    public static final Color DEFAULT_TEXT_CORNER_FONT_COLOR = Color.BLACK;
    public static final Color DEFAULT_TEXT_BG_COLOR = Color.WHITE;
    public static final double DEFAULT_TEXT_BG_TRANSPARENCY = 0.3;


    public static final int DEFAULT_TEXT_FONT_SIZE = 12;
    public static final int DEFAULT_TEXT_CORNER_FONT_SIZE = 12;
    public static final boolean DEFAULT_TEXT_FONT_ITALIC = false;
    public static final boolean DEFAULT_TEXT_INSIDE = false;
    public static final int DEFAULT_TEXT_ROTATION_NORTH_SOUTH = 45;
    public static final int DEFAULT_TEXT_ROTATION_WEST_EAST = 0;
    public static final boolean DEFAULT_TEXT_ENABLED_NORTH = true;
    public static final boolean DEFAULT_TEXT_ENABLED_SOUTH = true;
    public static final boolean DEFAULT_TEXT_ENABLED_WEST = true;
    public static final boolean DEFAULT_TEXT_ENABLED_EAST = true;
    public static final boolean DEFAULT_LINE_ENABLED = true;
    public static final boolean DEFAULT_LINE_DASHED = true;
    public static final double DEFAULT_LINE_DASHED_PHASE = 3;
    public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    public static final boolean DEFAULT_BORDER_ENABLED = true;
    public static final boolean DEFAULT_FORMAT_COMPASS = true;
    public static final boolean DEFAULT_FORMAT_DECIMAL = false;
    public static final double DEFAULT_BORDER_WIDTH = 1.2;

    public static final boolean DEFAULT_TEXT_CORNER_TOP_LON_ENABLED =  false;
    public static final boolean DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED = false;
    public static final boolean DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED = false;
    public static final boolean DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED =  false;


    public static final boolean DEFAULT_TICKMARK_ENABLED = true;
    public static final boolean DEFAULT_TICKMARK_INSIDE = false;
    public static final double DEFAULT_TICKMARK_LENGTH = 3.0;

    private static final String ALIAS_NAME_NUM_GRID_LINES = "numGridLines";
    private static final String ALIAS_NAME_RES_LAT = "resLat";
    private static final String ALIAS_NAME_RES_LON = "resLon";
    private static final String ALIAS_NAME_LINE_COLOR = "lineColor";
    private static final String ALIAS_NAME_LINE_TRANSPARENCY = "lineTransparency";
    private static final String ALIAS_NAME_LINE_WIDTH = "lineWidth";

    private static final String ALIAS_NAME_TEXT_FG_COLOR = "textFgColor";
    private static final String ALIAS_NAME_TEXT_CORNER_FONT_COLOR = "textCornerFontColor";
    private static final String ALIAS_NAME_TEXT_BG_COLOR = "textBgColor";
    private static final String ALIAS_NAME_TEXT_BG_TRANSPARENCY = "textBgTransparency";


    //     DANNY added these
    private static final String ALIAS_NAME_TEXT_FONT_SIZE = "textFontSize";
    private static final String ALIAS_NAME_TEXT_CORNER_FONT_SIZE = "textCornerFontSize";

    private static final String ALIAS_NAME_TEXT_INSIDE = "textInside";
    private static final String ALIAS_NAME_TEXT_ENABLED_NORTH = "textEnabledNorth";
    private static final String ALIAS_NAME_TEXT_ENABLED_SOUTH = "textEnabledSouth";
    private static final String ALIAS_NAME_TEXT_ENABLED_WEST = "textEnabledWest";
    private static final String ALIAS_NAME_TEXT_ENABLED_EAST = "textEnabledEast";
    private static final String ALIAS_NAME_TEXT_ROTATION_NORTH_SOUTH = "textRotationNorthSouth";
    private static final String ALIAS_NAME_TEXT_ROTATION_WEST_EAST = "textRotationWestEast";

    private static final String ALIAS_NAME_LINE_ENABLED = "graticuleLineEnabled";
    private static final String ALIAS_NAME_LINE_DASHED = "graticuleLineDashed";
    private static final String ALIAS_NAME_LINE_DASHED_PHASE = "graticuleLineDashedPhase";
    private static final String ALIAS_NAME_BORDER_ENABLED = "graticuleBorderEnabled";
    private static final String ALIAS_NAME_BORDER_WIDTH = "graticuleBorderWidth";
    private static final String ALIAS_NAME_BORDER_COLOR = "graticuleBorderColor";
    private static final String ALIAS_NAME_FORMAT_COMPASS = "graticuleFormatCompass";
    private static final String ALIAS_NAME_FORMAT_DECIMAL = "graticuleFormatDecimal";

    public static final String ALIAS_NAME_TEXT_CORNER_TOP_LON_ENABLED =  "graticuleTextCornerTopLonEnabled";
    public static final String ALIAS_NAME_TEXT_CORNER_LEFT_LAT_ENABLED = "graticuleTextCornerLeftLatEnabled";
    public static final String ALIAS_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED = "graticuleTextCornerRightLatEnabled";
    public static final String ALIAS_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED =  "graticuleTextCornerBottomLonEnabled";

    public static final String ALIAS_NAME_TICKMARK_ENABLED = "graticuleTickMarkEnabled";
    public static final String ALIAS_NAME_TICKMARK_INSIDE = "graticuleTickMarkInside";
    public static final String ALIAS_NAME_TICKMARK_LENGTH = "graticuleTickMarkLength";

    private static final String ALIAS_NAME_TEXT_FONT_ITALIC = "graticuleTextFontItalic";



    /**
     * @deprecated since BEAM 4.7, no replacement; kept for compatibility of sessions
     */
    @Deprecated
    private static final String PROPERTY_NAME_TRANSFORM = "imageToModelTransform";


    @Override
    public boolean isValidFor(LayerContext ctx) {
        return true;
    }

    @Override
    public Layer createLayer(LayerContext ctx, PropertySet configuration) {
        return new GraticuleLayer(this, (RasterDataNode) configuration.getValue(PROPERTY_NAME_RASTER),
                configuration);
    }

    @Override
    public PropertySet createLayerConfig(LayerContext ctx) {
        final PropertyContainer vc = new PropertyContainer();

        final Property rasterModel = Property.create(PROPERTY_NAME_RASTER, RasterDataNode.class);
        rasterModel.getDescriptor().setNotNull(true);
        vc.addProperty(rasterModel);
        final Property transformModel = Property.create(PROPERTY_NAME_TRANSFORM, new AffineTransform());
        transformModel.getDescriptor().setTransient(true);
        vc.addProperty(transformModel);

        final Property resPixelsModel = Property.create(PROPERTY_NAME_NUM_GRID_LINES, Integer.class, DEFAULT_NUM_GRID_LINES, true);
        resPixelsModel.getDescriptor().setAlias(ALIAS_NAME_NUM_GRID_LINES);
        vc.addProperty(resPixelsModel);

        final Property resLatModel = Property.create(PROPERTY_NAME_RES_LAT, Double.class, DEFAULT_RES_LAT, true);
        resLatModel.getDescriptor().setAlias(ALIAS_NAME_RES_LAT);
        vc.addProperty(resLatModel);

        final Property resLonModel = Property.create(PROPERTY_NAME_RES_LON, Double.class, DEFAULT_RES_LON, true);
        resLonModel.getDescriptor().setAlias(ALIAS_NAME_RES_LON);
        vc.addProperty(resLonModel);

        final Property lineColorModel = Property.create(PROPERTY_NAME_LINE_COLOR, Color.class, DEFAULT_LINE_COLOR, true);
        lineColorModel.getDescriptor().setAlias(ALIAS_NAME_LINE_COLOR);
        vc.addProperty(lineColorModel);

        final Property lineTransparencyModel = Property.create(PROPERTY_NAME_LINE_TRANSPARENCY, Double.class, DEFAULT_LINE_TRANSPARENCY, true);
        lineTransparencyModel.getDescriptor().setAlias(ALIAS_NAME_LINE_TRANSPARENCY);
        vc.addProperty(lineTransparencyModel);

        final Property lineWidthModel = Property.create(PROPERTY_NAME_LINE_WIDTH, Double.class, DEFAULT_LINE_WIDTH, true);
        lineWidthModel.getDescriptor().setAlias(ALIAS_NAME_LINE_WIDTH);
        vc.addProperty(lineWidthModel);


        final Property textFgColorModel = Property.create(PROPERTY_NAME_TEXT_FG_COLOR, Color.class, DEFAULT_TEXT_FG_COLOR, true);
        textFgColorModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_FG_COLOR);
        vc.addProperty(textFgColorModel);

        final Property textCornerFontColorModel = Property.create(PROPERTY_NAME_TEXT_CORNER_FONT_COLOR, Color.class, DEFAULT_TEXT_CORNER_FONT_COLOR, true);
        textCornerFontColorModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_CORNER_FONT_COLOR);
        vc.addProperty(textCornerFontColorModel);


        final Property textBgColorModel = Property.create(PROPERTY_NAME_TEXT_BG_COLOR, Color.class, DEFAULT_TEXT_BG_COLOR, true);
        textBgColorModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_BG_COLOR);
        vc.addProperty(textBgColorModel);

        final Property textBgTransparencyModel = Property.create(PROPERTY_NAME_TEXT_BG_TRANSPARENCY, Double.class, DEFAULT_TEXT_BG_TRANSPARENCY, true);
        textBgTransparencyModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_BG_TRANSPARENCY);
        vc.addProperty(textBgTransparencyModel);



        // DANNY added these

        final Property textFontSizeModel = Property.create(PROPERTY_NAME_TEXT_FONT_SIZE, Integer.class, DEFAULT_TEXT_FONT_SIZE, true);
        textFontSizeModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_FONT_SIZE);
        vc.addProperty(textFontSizeModel);

        final Property textCornerFontSizeModel = Property.create(PROPERTY_NAME_TEXT_CORNER_FONT_SIZE, Integer.class, DEFAULT_TEXT_CORNER_FONT_SIZE, true);
        textCornerFontSizeModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_CORNER_FONT_SIZE);
        vc.addProperty(textCornerFontSizeModel);

        final Property textFontItalicModel = Property.create(PROPERTY_NAME_TEXT_FONT_ITALIC, Boolean.class, DEFAULT_TEXT_FONT_ITALIC, true);
        textFontItalicModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_FONT_ITALIC);
        vc.addProperty(textFontItalicModel);


        final Property textOutsideModel = Property.create(PROPERTY_NAME_TEXT_INSIDE, Boolean.class, DEFAULT_TEXT_INSIDE, true);
        textOutsideModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_INSIDE);
        vc.addProperty(textOutsideModel);

        final Property textRotationNorthModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH, Integer.class, DEFAULT_TEXT_ROTATION_NORTH_SOUTH, true);
        textRotationNorthModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_NORTH_SOUTH);
        vc.addProperty(textRotationNorthModel);

        final Property textRotationWestModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_WEST_EAST, Integer.class, DEFAULT_TEXT_ROTATION_WEST_EAST, true);
        textRotationWestModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_WEST_EAST);
        vc.addProperty(textRotationWestModel);


        final Property textEnabledNorthModel = Property.create(PROPERTY_NAME_TEXT_ENABLED_NORTH, Boolean.class, DEFAULT_TEXT_ENABLED_NORTH, true);
        textEnabledNorthModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ENABLED_NORTH);
        vc.addProperty(textEnabledNorthModel);

        final Property textEnabledSouthModel = Property.create(PROPERTY_NAME_TEXT_ENABLED_SOUTH, Boolean.class, DEFAULT_TEXT_ENABLED_SOUTH, true);
        textEnabledSouthModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ENABLED_SOUTH);
        vc.addProperty(textEnabledSouthModel);

        final Property textEnabledWestModel = Property.create(PROPERTY_NAME_TEXT_ENABLED_WEST, Boolean.class, DEFAULT_TEXT_ENABLED_WEST, true);
        textEnabledWestModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ENABLED_WEST);
        vc.addProperty(textEnabledWestModel);

        final Property textEnabledEastModel = Property.create(PROPERTY_NAME_TEXT_ENABLED_EAST, Boolean.class, DEFAULT_TEXT_ENABLED_EAST, true);
        textEnabledEastModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ENABLED_EAST);
        vc.addProperty(textEnabledEastModel);

        final Property lineEnabledModel = Property.create(PROPERTY_NAME_LINE_ENABLED, Boolean.class, DEFAULT_LINE_ENABLED, true);
        lineEnabledModel.getDescriptor().setAlias(ALIAS_NAME_LINE_ENABLED);
        vc.addProperty(lineEnabledModel);

        final Property lineDashedModel = Property.create(PROPERTY_NAME_LINE_DASHED, Boolean.class, DEFAULT_LINE_DASHED, true);
        lineDashedModel.getDescriptor().setAlias(ALIAS_NAME_LINE_DASHED);
        vc.addProperty(lineDashedModel);

        final Property lineDashedPhaseModel = Property.create(PROPERTY_NAME_LINE_DASHED_PHASE, Double.class, DEFAULT_LINE_DASHED_PHASE, true);
        lineDashedPhaseModel.getDescriptor().setAlias(ALIAS_NAME_LINE_DASHED_PHASE);
        vc.addProperty(lineDashedPhaseModel);

        final Property borderEnabledModel = Property.create(PROPERTY_NAME_BORDER_ENABLED, Boolean.class, DEFAULT_BORDER_ENABLED, true);
        borderEnabledModel.getDescriptor().setAlias(ALIAS_NAME_BORDER_ENABLED);
        vc.addProperty(borderEnabledModel);

        final Property formatCompassModel = Property.create(PROPERTY_NAME_FORMAT_COMPASS, Boolean.class, DEFAULT_FORMAT_COMPASS, false);
        formatCompassModel.getDescriptor().setAlias(ALIAS_NAME_FORMAT_COMPASS);
        vc.addProperty(formatCompassModel);

        final Property formatDecimalModel = Property.create(PROPERTY_NAME_FORMAT_DECIMAL, Boolean.class, DEFAULT_FORMAT_DECIMAL, false);
        formatDecimalModel.getDescriptor().setAlias(ALIAS_NAME_FORMAT_DECIMAL);
        vc.addProperty(formatDecimalModel);


        final Property borderColorModel = Property.create(PROPERTY_NAME_BORDER_COLOR, Color.class, DEFAULT_BORDER_COLOR, true);
        borderColorModel.getDescriptor().setAlias(ALIAS_NAME_BORDER_COLOR);
        vc.addProperty(borderColorModel);

        final Property borderWidthModel = Property.create(PROPERTY_NAME_BORDER_WIDTH, Double.class, DEFAULT_BORDER_WIDTH, true);
        borderWidthModel.getDescriptor().setAlias(ALIAS_NAME_BORDER_WIDTH);
        vc.addProperty(borderWidthModel);


        final Property textCornerTopLeftLonEnabledModel = Property.create(PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED, Boolean.class, DEFAULT_TEXT_CORNER_TOP_LON_ENABLED, true);
        textCornerTopLeftLonEnabledModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_CORNER_TOP_LON_ENABLED);
        vc.addProperty(textCornerTopLeftLonEnabledModel);

        final Property textCornerTopLeftLatEnabledModel = Property.create(PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED, Boolean.class, DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED, true);
        textCornerTopLeftLatEnabledModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_CORNER_LEFT_LAT_ENABLED);
        vc.addProperty(textCornerTopLeftLatEnabledModel);


        final Property textCornerTopRightLatEnabledModel = Property.create(PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED, Boolean.class, DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED, true);
        textCornerTopRightLatEnabledModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED);
        vc.addProperty(textCornerTopRightLatEnabledModel);


        final Property textCornerBottomLeftLonEnabledModel = Property.create(PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED, Boolean.class, DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED, true);
        textCornerBottomLeftLonEnabledModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED);
        vc.addProperty(textCornerBottomLeftLonEnabledModel);


        final Property tickMarkEnabledModel = Property.create(PROPERTY_NAME_TICKMARK_ENABLED, Boolean.class, DEFAULT_TICKMARK_ENABLED, true);
        tickMarkEnabledModel.getDescriptor().setAlias(ALIAS_NAME_TICKMARK_ENABLED);
        vc.addProperty(tickMarkEnabledModel);

        final Property tickMarkInsideModel = Property.create(PROPERTY_NAME_TICKMARK_INSIDE, Boolean.class, DEFAULT_TICKMARK_INSIDE, true);
        tickMarkInsideModel.getDescriptor().setAlias(ALIAS_NAME_TICKMARK_INSIDE);
        vc.addProperty(tickMarkInsideModel);

        final Property tickMarkLengthModel = Property.create(PROPERTY_NAME_TICKMARK_LENGTH, Double.class, DEFAULT_TICKMARK_LENGTH, true);
        tickMarkLengthModel.getDescriptor().setAlias(ALIAS_NAME_TICKMARK_LENGTH);
        vc.addProperty(tickMarkLengthModel);

        return vc;
    }
}
