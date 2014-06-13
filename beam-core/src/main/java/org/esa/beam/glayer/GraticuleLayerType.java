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
import java.awt.Font;
import java.awt.geom.AffineTransform;


@LayerTypeMetadata(name = "GraticuleLayerType",
        aliasNames = {"org.esa.beam.glayer.GraticuleLayerType"})
public class GraticuleLayerType extends LayerType {

    public static final String PROPERTY_NAME_RASTER = "raster";
    public static final String PROPERTY_NAME_RES_AUTO = "graticule.res.auto";
    public static final String PROPERTY_NAME_RES_PIXELS = "graticule.res.pixels";
    public static final String PROPERTY_NAME_RES_LAT = "graticule.res.lat";
    public static final String PROPERTY_NAME_RES_LON = "graticule.res.lon";
    public static final String PROPERTY_NAME_LINE_COLOR = "graticule.line.color";
    public static final String PROPERTY_NAME_LINE_TRANSPARENCY = "graticule.line.transparency";
    public static final String PROPERTY_NAME_LINE_WIDTH = "graticule.line.width";
    public static final String PROPERTY_NAME_TEXT_ENABLED = "graticule.text.enabled";
    public static final String PROPERTY_NAME_TEXT_FONT = "graticule.text.font";
    public static final String PROPERTY_NAME_TEXT_FG_COLOR = "graticule.text.fg.color";
    public static final String PROPERTY_NAME_TEXT_BG_COLOR = "graticule.text.bg.color";
    public static final String PROPERTY_NAME_TEXT_BG_TRANSPARENCY = "graticule.text.bg.transparency";


    // DANNY added these
    public static final String PROPERTY_NAME_TEXT_FONT_SIZE = "graticule.text.font.size";
    public static final String PROPERTY_NAME_TEXT_FONT_ITALIC = "graticule.text.font.italic";
    public static final String PROPERTY_NAME_TEXT_OUTSIDE = "graticule.text.outside";
    public static final String PROPERTY_NAME_TEXT_OFFSET_OUTWARD = "graticule.text.offset.outward";
    public static final String PROPERTY_NAME_TEXT_OFFSET_SIDEWARD = "graticule.text.offset.sideward";
    public static final String PROPERTY_NAME_TEXT_ROTATION_NORTH = "graticule.text.rotation.north";
//    public static final String PROPERTY_NAME_TEXT_ROTATION_SOUTH = "graticule.text.rotation.south";
    public static final String PROPERTY_NAME_TEXT_ROTATION_WEST = "graticule.text.rotation.west";
//    public static final String PROPERTY_NAME_TEXT_ROTATION_EAST = "graticule.text.rotation.east";
//    public static final String PROPERTY_NAME_TEXT_ROTATION_ANCHORED = "graticule.text.rotation.anchor";
    public static final String PROPERTY_NAME_TEXT_ENABLED_NORTH = "graticule.text.enabled.north";
    public static final String PROPERTY_NAME_TEXT_ENABLED_SOUTH = "graticule.text.enabled.south";
    public static final String PROPERTY_NAME_TEXT_ENABLED_WEST = "graticule.text.enabled.west";
    public static final String PROPERTY_NAME_TEXT_ENABLED_EAST = "graticule.text.enabled.east";
    public static final String PROPERTY_NAME_LINE_ENABLED = "graticule.line.enabled";
    public static final String PROPERTY_NAME_LINE_DASHED = "graticule.line.dashed";
    public static final String PROPERTY_NAME_LINE_DASHED_PHASE = "graticule.line.dashed.phase";
    public static final String PROPERTY_NAME_BORDER_ENABLED = "graticule.border.enabled";
    public static final String PROPERTY_NAME_BORDER_WIDTH = "graticule.border.width";
    public static final String PROPERTY_NAME_BORDER_COLOR = "graticule.border.color";


    public static final boolean DEFAULT_RES_AUTO = true;
    public static final int DEFAULT_RES_PIXELS = 128;
    public static final double DEFAULT_RES_LAT = 1.0;
    public static final double DEFAULT_RES_LON = 1.0;
    public static final Color DEFAULT_LINE_COLOR = new Color(204, 204, 255);
    public static final double DEFAULT_LINE_TRANSPARENCY = 0.0;
    public static final double DEFAULT_LINE_WIDTH = 0.5;
    public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.ITALIC, 12);
    public static final boolean DEFAULT_TEXT_ENABLED = true;
    public static final Color DEFAULT_TEXT_FG_COLOR = Color.BLACK;
    public static final Color DEFAULT_TEXT_BG_COLOR = Color.WHITE;
    public static final double DEFAULT_TEXT_BG_TRANSPARENCY = 1.0;


    // DANNY added these
    public static final int DEFAULT_TEXT_FONT_SIZE = 12;
    public static final boolean DEFAULT_TEXT_FONT_ITALIC = true;
    public static final int DEFAULT_TEXT_OFFSET_OUTWARD = 0;
    public static final int DEFAULT_TEXT_OFFSET_SIDEWARD = 0;
    public static final boolean DEFAULT_TEXT_OUTSIDE = false;
    public static final double DEFAULT_TEXT_ROTATION_NORTH = 45;
//    public static final double DEFAULT_TEXT_ROTATION_SOUTH = 45;
    public static final double DEFAULT_TEXT_ROTATION_WEST = 90;
//    public static final double DEFAULT_TEXT_ROTATION_EAST = 90;
//    public static final boolean DEFAULT_TEXT_ROTATION_ANCHORED = true;
    public static final boolean DEFAULT_TEXT_ENABLED_NORTH = true;
    public static final boolean DEFAULT_TEXT_ENABLED_SOUTH = true;
    public static final boolean DEFAULT_TEXT_ENABLED_WEST = true;
    public static final boolean DEFAULT_TEXT_ENABLED_EAST = true;
    public static final boolean DEFAULT_LINE_ENABLED = true;
    public static final boolean DEFAULT_LINE_DASHED = true;
    public static final double DEFAULT_LINE_DASHED_PHASE = 9;
    public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    public static final boolean DEFAULT_BORDER_ENABLED = true;
    public static final double DEFAULT_BORDER_WIDTH = 1.0;


    private static final String ALIAS_NAME_RES_AUTO = "resAuto";
    private static final String ALIAS_NAME_RES_PIXELS = "resPixels";
    private static final String ALIAS_NAME_RES_LAT = "resLat";
    private static final String ALIAS_NAME_RES_LON = "resLon";
    private static final String ALIAS_NAME_LINE_COLOR = "lineColor";
    private static final String ALIAS_NAME_LINE_TRANSPARENCY = "lineTransparency";
    private static final String ALIAS_NAME_LINE_WIDTH = "lineWidth";
    private static final String ALIAS_NAME_TEXT_ENABLED = "textEnabled";
    private static final String ALIAS_NAME_TEXT_FONT = "textFont";

    private static final String ALIAS_NAME_TEXT_FG_COLOR = "textFgColor";
    private static final String ALIAS_NAME_TEXT_BG_COLOR = "textBgColor";
    private static final String ALIAS_NAME_TEXT_BG_TRANSPARENCY = "textBgTransparency";


    //     DANNY added these
    private static final String ALIAS_NAME_TEXT_FONT_SIZE = "textFontSize";
    private static final String ALIAS_NAME_TEXT_FONT_ITALIC = "graticuleTextFontItalic";
    private static final String ALIAS_NAME_TEXT_OFFSET_OUTWARD = "textOffsetOutward";
    private static final String ALIAS_NAME_TEXT_OFFSET_SIDEWARD = "textOffsetSideward";
    private static final String ALIAS_NAME_TEXT_OUTSIDE = "textOutside";
    private static final String ALIAS_NAME_TEXT_ENABLED_NORTH = "textEnabledNorth";
    private static final String ALIAS_NAME_TEXT_ENABLED_SOUTH = "textEnabledSouth";
    private static final String ALIAS_NAME_TEXT_ENABLED_WEST = "textEnabledWest";
    private static final String ALIAS_NAME_TEXT_ENABLED_EAST = "textEnabledEast";
    private static final String ALIAS_NAME_TEXT_ROTATION_NORTH = "textRotationNorth";
//    private static final String ALIAS_NAME_TEXT_ROTATION_SOUTH = "textRotationSouth";
    private static final String ALIAS_NAME_TEXT_ROTATION_WEST = "textRotationWest";
//    private static final String ALIAS_NAME_TEXT_ROTATION_EAST = "textRotationEast";
//    private static final String ALIAS_NAME_TEXT_ROTATION_ANCHORED = "textRotationAnchored";
    private static final String ALIAS_NAME_LINE_ENABLED = "graticuleLineEnabled";
    private static final String ALIAS_NAME_LINE_DASHED = "graticuleLineDashed";
    private static final String ALIAS_NAME_LINE_DASHED_PHASE = "graticuleLineDashedPhase";
    private static final String ALIAS_NAME_BORDER_ENABLED = "graticuleBorderEnabled";
    private static final String ALIAS_NAME_BORDER_WIDTH = "graticuleBorderWidth";
    private static final String ALIAS_NAME_BORDER_COLOR = "graticuleBorderColor";


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

        final Property resAutoModel = Property.create(PROPERTY_NAME_RES_AUTO, Boolean.class, DEFAULT_RES_AUTO, true);
        resAutoModel.getDescriptor().setAlias(ALIAS_NAME_RES_AUTO);
        vc.addProperty(resAutoModel);

        final Property resPixelsModel = Property.create(PROPERTY_NAME_RES_PIXELS, Integer.class, DEFAULT_RES_PIXELS, true);
        resPixelsModel.getDescriptor().setAlias(ALIAS_NAME_RES_PIXELS);
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

        final Property textEnabledModel = Property.create(PROPERTY_NAME_TEXT_ENABLED, Boolean.class, DEFAULT_TEXT_ENABLED, true);
        textEnabledModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ENABLED);
        vc.addProperty(textEnabledModel);

        final Property textFontModel = Property.create(PROPERTY_NAME_TEXT_FONT, Font.class, DEFAULT_TEXT_FONT, true);
        textFontModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_FONT);
        vc.addProperty(textFontModel);


        final Property textFgColorModel = Property.create(PROPERTY_NAME_TEXT_FG_COLOR, Color.class, DEFAULT_TEXT_FG_COLOR, true);
        textFgColorModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_FG_COLOR);
        vc.addProperty(textFgColorModel);

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

        final Property textFontItalicModel = Property.create(PROPERTY_NAME_TEXT_FONT_ITALIC, Boolean.class, DEFAULT_TEXT_FONT_ITALIC, true);
        textFontItalicModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_FONT_ITALIC);
        vc.addProperty(textFontItalicModel);

        final Property textOffsetOutwardModel = Property.create(PROPERTY_NAME_TEXT_OFFSET_OUTWARD, Integer.class, DEFAULT_TEXT_OFFSET_OUTWARD, true);
        textOffsetOutwardModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_OFFSET_OUTWARD);
        vc.addProperty(textOffsetOutwardModel);

        final Property textOffsetSidewardModel = Property.create(PROPERTY_NAME_TEXT_OFFSET_SIDEWARD, Integer.class, DEFAULT_TEXT_OFFSET_SIDEWARD, true);
        textOffsetSidewardModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_OFFSET_SIDEWARD);
        vc.addProperty(textOffsetSidewardModel);

        final Property textOutsideModel = Property.create(PROPERTY_NAME_TEXT_OUTSIDE, Boolean.class, DEFAULT_TEXT_OUTSIDE, true);
        textOutsideModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_OUTSIDE);
        vc.addProperty(textOutsideModel);

        final Property textRotationNorthModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_NORTH, Double.class, DEFAULT_TEXT_ROTATION_NORTH, true);
        textRotationNorthModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_NORTH);
        vc.addProperty(textRotationNorthModel);

//        final Property textRotationSouthModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_SOUTH, Double.class, DEFAULT_TEXT_ROTATION_SOUTH, true);
//        textRotationSouthModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_SOUTH);
//        vc.addProperty(textRotationSouthModel);

        final Property textRotationWestModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_WEST, Double.class, DEFAULT_TEXT_ROTATION_WEST, true);
        textRotationWestModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_WEST);
        vc.addProperty(textRotationWestModel);

//        final Property textRotationEastModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_EAST, Double.class, DEFAULT_TEXT_ROTATION_EAST, true);
//        textRotationEastModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_EAST);
//        vc.addProperty(textRotationEastModel);
//
//        final Property textRotationAnchoredModel = Property.create(PROPERTY_NAME_TEXT_ROTATION_ANCHORED, Boolean.class, DEFAULT_TEXT_ROTATION_ANCHORED, true);
//        textRotationAnchoredModel.getDescriptor().setAlias(ALIAS_NAME_TEXT_ROTATION_ANCHORED);
//        vc.addProperty(textRotationAnchoredModel);

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

        final Property borderColorModel = Property.create(PROPERTY_NAME_BORDER_COLOR, Color.class, DEFAULT_BORDER_COLOR, true);
        borderColorModel.getDescriptor().setAlias(ALIAS_NAME_BORDER_COLOR);
        vc.addProperty(borderColorModel);

        final Property borderWidthModel = Property.create(PROPERTY_NAME_BORDER_WIDTH, Double.class, DEFAULT_BORDER_WIDTH, true);
        borderWidthModel.getDescriptor().setAlias(ALIAS_NAME_BORDER_WIDTH);
        vc.addProperty(borderWidthModel);


        return vc;
    }
}
