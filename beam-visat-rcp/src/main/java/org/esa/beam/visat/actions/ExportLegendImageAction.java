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
package org.esa.beam.visat.actions;

import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.ValueRange;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.internal.RangeEditor;
import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.datamodel.ImageLegend;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.param.ParamChangeEvent;
import org.esa.beam.framework.param.ParamChangeListener;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.product.ColorBarParamInfo;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.util.PropertyMap;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.BeamFileChooser;
import org.esa.beam.util.io.BeamFileFilter;
import org.esa.beam.visat.VisatApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

public class ExportLegendImageAction extends AbstractExportImageAction {


    public static final String RESET_TO_DEFAULTS_PARAM_STR = "legend.resetToDefaults";


    public static final String PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION = "legend.horizontalLocation";
    public static final String PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION = "legend.verticalLocation";
    public static final String PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION = "legend.insideOutsideLocation";



    public static final String ORIENTATION_PARAM_STR = "legend.orientation";
    private static final String DISTRIBUTION_TYPE_PARAM_STR = "legend.label.distribution.type";
    private static final String NUM_TICKS_PARAM_STR = "legend.numberOfTicks";
    public static final String SHOW_TITLE_PARAM_STR = "legend.usingHeader";
    private static final String TITLE_PARAM_STR = "legend.headerText";
    private static final String TITLE_UNITS_PARAM_STR = "legend.header.units.text";
    private static final String MANUAL_POINTS_PARAM_STR = "legend.fullCustomAddThesePoints";
    //    private static final String LABEL_FONT_SIZE_PARAM_STR = "legend.fontSize";
    private static final String DECIMAL_PLACES_PARAM_STR = "legend.decimalPlaces";
    private static final String DECIMAL_PLACES_FORCE_PARAM_STR = "legend.decimalPlacesForce";
    public static final String FOREGROUND_COLOR_PARAM_STR = "legend.foregroundColor";
    public static final String BACKGROUND_COLOR_PARAM_STR = "legend.backgroundColor";
    //   private static final String BACKGROUND_TRANSPARENCY_PARAM_STR = "legend.backgroundTransparency";
    public static final String TRANSPARENCY_PARAM_STR = "legend.transparent";

    public static final String SCALING_FACTOR_PARAM_STR = "legend.scalingFactor";
    private static final String TITLE_FONT_SIZE_PARAM_STR = "legend.titleFontSize";
    private static final String TITLE_UNITS_FONT_SIZE_PARAM_STR = "legend.titleUnitsFontSize";
    private static final String LABELS_FONT_SIZE_PARAM_STR = "legend.labelsFontSize";

    private static final String COLOR_BAR_LENGTH_PARAM_STR = "legend.colorBarLength";
    private static final String COLOR_BAR_THICKNESS_PARAM_STR = "legend.colorBarThickness";
    public static final String LAYER_SCALING_PARAM_STR = "legend.layerScalingThickness";
    private static final String LAYER_OFFSET_PARAM_STR = "legend.layerOffset";
    private static final String LAYER_SHIFT_PARAM_STR = "legend.layerShift";
    private static final String CENTER_ON_LAYER_PARAM_STR = "legend.centerOnLayer";




    private ParamGroup colorBarParamGroup;
    private ImageLegend imageLegend;
    private static int imageHeight;
    private static int imageWidth;
    boolean colorBarLayer = false;

    private VisatApp visatApp;
    private PropertyMap configuration;


    @Override
    public void actionPerformed(CommandEvent event) {
        ProductSceneView view = getVisatApp().getSelectedProductSceneView();


        imageHeight = view.getRaster().getRasterHeight();
        imageWidth = view.getRaster().getRasterWidth();


        // config with preferences
        visatApp = VisatApp.getApp();
        this.configuration = visatApp.getPreferences();


        if (!view.getColorBarParamInfo().isParamsInitialized()) {
            //        if (getColorBarLocationInsidePreference()) {
//            view.getColorBarParamInfo().setLayerOffset(-100);
//        }


            view.getColorBarParamInfo().setOrientation(ColorBarParamInfo.HORIZONTAL_STR.equals(getColorBarOrientationPreference()) ? ColorBarParamInfo.HORIZONTAL_STR : ColorBarParamInfo.VERTICAL_STR);
            view.getColorBarParamInfo().setInsideOutsideLocation(getColorBarInsideOutsideLocationPreference());
            view.getColorBarParamInfo().setShowTitle(getColorBarShowTitlePreference());
            view.getColorBarParamInfo().setBackgroundTransparency((float) getColorBarTransparencyPreference());
            view.getColorBarParamInfo().setBackgroundColor(getBackgroundColorPreference());
            view.getColorBarParamInfo().setForegroundColor(getForegroundColorPreference());
            view.getColorBarParamInfo().setLayerScaling(getLayerScalingFactorPreference());
            view.getColorBarParamInfo().setHorizontalLocation(getHorizontalLocationPreference());
            view.getColorBarParamInfo().setVerticalLocation(getVerticalLocationPreference());
        }

        colorBarParamGroup = createColorBarParamGroup(view);
//        if (!view.getColorBarParamInfo().isParamsInitialized()) {
//            // originally the title contains a title and units
//            // we only need to split this apart initially
//            // once the color bar is created, the settings get stored and reloaded and this step is skipped
//            splitTitleAndUnits(colorBarParamGroup, view.getRaster());
//        }


        final RasterDataNode raster = view.getRaster();


        String schemeName = raster.getImageInfo().getColorPaletteSourcesInfo().getSchemeName();
        if (!view.getColorBarParamInfo().isParamsInitialized()) {
            // originally the title contains a title and units
            // we only need to split this apart initially
            // once the color bar is created, the settings get stored and reloaded and this step is skipped
            splitTitleAndUnits(colorBarParamGroup, view.getRaster());

        }


        imageLegend = new ImageLegend(raster.getImageInfo(), raster);


        // Double check to be sure paramGroup is initialized
        // Right now we are always populating the manualPoints param so using this for the check

        String manualPointsStr = colorBarParamGroup.getParameter(MANUAL_POINTS_PARAM_STR).getValue().toString();
        boolean paramsInitialized = (manualPointsStr != null && manualPointsStr.length() > 0) ? true : false;


        if (!view.getColorBarParamInfo().isParamsInitialized() || !paramsInitialized) {
 //           imageLegend.setInitialized(false);
//            view.getSceneImage().getConfiguration().getPropertyBool(PROPERTY_NAME_COLORBAR_TITLE_OVERRIDE, true);

           PropertyMap configuration = view.getSceneImage().getConfiguration();
            imageLegend.initDefaults(configuration);


            if (imageLegend.getHeaderText() != null && imageLegend.getHeaderText().length() > 0) {
                colorBarParamGroup.getParameter(TITLE_PARAM_STR).setValue(imageLegend.getHeaderText(), null);
            }

            colorBarParamGroup.getParameter(DISTRIBUTION_TYPE_PARAM_STR).setValue(ImageLegend.DISTRIB_MANUAL_STR, null);
            colorBarParamGroup.getParameter(MANUAL_POINTS_PARAM_STR).setValue(imageLegend.getFullCustomAddThesePoints(), null);

 //           imageLegend.setInitialized(true);

        }



        // this will open a dialog before the file chooser
        final ImageLegendDialog dialog = new ImageLegendDialog(getVisatApp(),
                colorBarParamGroup,
                imageLegend,
                true);

        dialog.show();


        if (dialog.okWasClicked) {
            colorBarLayer = false;
            exportImage(getVisatApp(), getImageFileFilters(), event.getSelectableCommand());
        } else if (dialog.getButtonID() == ModalDialog.ID_APPLY) {
            colorBarLayer = true;
            ShowColorBarOverlayAction showColorBarOverlayAction = new ShowColorBarOverlayAction();
            RenderedImage colorBarImage = createImage("PNG", view);
            showColorBarOverlayAction.setColorBarImage(colorBarImage);
            showColorBarOverlayAction.setOrientation(imageLegend.getOrientation());
            showColorBarOverlayAction.setTransparency(imageLegend.getBackgroundTransparency());
            showColorBarOverlayAction.setLayerOffset(imageLegend.getLayerOffset());
            showColorBarOverlayAction.setHorizontalLocation(imageLegend.getHorizontalLocation());
            showColorBarOverlayAction.setVerticalLocation(imageLegend.getVerticalLocation());
            showColorBarOverlayAction.setInsideOutsideLocation(imageLegend.getInsideOutsideLocation());
            showColorBarOverlayAction.setLayerShift(imageLegend.getLayerShift());
            //showColorBarOverlayAction.setFeatureCollection(imageLegend.getFeatureCollection());
            showColorBarOverlayAction.actionPerformed(event);
            //showColorBarOverlayAction.createColorBarVectorNode();
        }

    }

    @Override
    public void updateState(final CommandEvent event) {
        ProductSceneView view = getVisatApp().getSelectedProductSceneView();
        boolean enabled = view != null && !view.isRGB();
        event.getSelectableCommand().setEnabled(enabled);

    }

    @Override
    protected void configureFileChooser(BeamFileChooser fileChooser, ProductSceneView view, String imageBaseName) {
//        colorBarParamGroup = createColorBarParamGroup();
//        colorBarParamGroup.setParameterValues(getVisatApp().getPreferences(), null);
//        splitTitleAndUnits(colorBarParamGroup, view.getRaster());
        fileChooser.setDialogTitle(getVisatApp().getAppName() + " - Color Bar File"); /*I18N*/
        fileChooser.setCurrentFilename(imageBaseName + "_colorbar.png");
//        final RasterDataNode raster = view.getRaster();
//        imageLegend = new ImageLegend(raster.getImageInfo(), raster);

        // this will open a dialog before the file chooser
//        final ImageLegendDialog dialog = new ImageLegendDialog(getVisatApp(),
//                colorBarParamGroup,
//                imageLegend,
//                true);
//
//        dialog.show();


//        if (dialog.okWasClicked) {
        fileChooser.setAccessory(createImageLegendAccessory(getVisatApp(),
                fileChooser,
                colorBarParamGroup,
                imageLegend));
//        }
    }

    @Override
    protected RenderedImage createImage(String imageFormat, ProductSceneView view) {

        transferParamsToImageLegend(colorBarParamGroup, imageLegend);
        // todo DANNY
        //  if (colorBarLayer) {
        view.getColorBarParamInfo().setLabelsFontSize(imageLegend.getLabelsFontSize());
        view.getColorBarParamInfo().setBackgroundTransparency(imageLegend.getBackgroundTransparency());
        view.getColorBarParamInfo().setShowTitle(new Boolean(imageLegend.isShowTitle()));
        view.getColorBarParamInfo().setTitle(imageLegend.getHeaderText());
        view.getColorBarParamInfo().setTitleUnits(imageLegend.getHeaderUnitsText());
        view.getColorBarParamInfo().setTitleFontSize(imageLegend.getTitleFontSize());
        view.getColorBarParamInfo().setTitleUnitsFontSize(imageLegend.getTitleUnitsFontSize());
        view.getColorBarParamInfo().setScalingFactor(imageLegend.getScalingFactor());
        view.getColorBarParamInfo().setColorBarLength(imageLegend.getColorBarLength());
        view.getColorBarParamInfo().setColorBarThickness(imageLegend.getColorBarThickness());
        view.getColorBarParamInfo().setLayerScaling(imageLegend.getLayerScaling());
        view.getColorBarParamInfo().setLayerOffset(imageLegend.getLayerOffset());
        view.getColorBarParamInfo().setHorizontalLocation(imageLegend.getHorizontalLocation());
        view.getColorBarParamInfo().setVerticalLocation(imageLegend.getVerticalLocation());
        view.getColorBarParamInfo().setLayerShift(imageLegend.getLayerShift());
        view.getColorBarParamInfo().setCenterOnLayer(new Boolean(imageLegend.isCenterOnLayer()));
        view.getColorBarParamInfo().setManualPoints(imageLegend.getFullCustomAddThesePoints());
        view.getColorBarParamInfo().setDistributionType(imageLegend.getDistributionType());
        view.getColorBarParamInfo().setNumTickMarks(imageLegend.getNumberOfTicks());
        view.getColorBarParamInfo().setDecimalPlaces(imageLegend.getDecimalPlaces());
        view.getColorBarParamInfo().setDecimalPlacesForce(new Boolean(imageLegend.isDecimalPlacesForce()));
        view.getColorBarParamInfo().setForegroundColor(imageLegend.getForegroundColor());
        view.getColorBarParamInfo().setBackgroundColor(imageLegend.getBackgroundColor());
        view.getColorBarParamInfo().setParamsInitialized(true);
        view.getColorBarParamInfo().setInsideOutsideLocation(imageLegend.getInsideOutsideLocation());

        int orientation = imageLegend.getOrientation();
        if (orientation == ImageLegend.VERTICAL) {
            view.getColorBarParamInfo().setOrientation(ColorBarParamInfo.VERTICAL_STR);
        } else {
            view.getColorBarParamInfo().setOrientation(ColorBarParamInfo.HORIZONTAL_STR);
        }


        //   }

        imageLegend.setBackgroundTransparencyEnabled(isTransparencySupportedByFormat(imageFormat));
        return imageLegend.createImage(new Dimension(imageWidth, imageHeight), colorBarLayer);
    }


    @Override
    protected boolean isEntireImageSelected() {
        return true;
    }


    private static ParamGroup createColorBarParamGroup(ProductSceneView view) {

        ParamGroup paramGroup = new ParamGroup();

        Parameter param = new Parameter(SHOW_TITLE_PARAM_STR, view.getColorBarParamInfo().getShowTitle());
        param.getProperties().setLabel("Show Title");
        paramGroup.addParameter(param);


        param = new Parameter(TRANSPARENCY_PARAM_STR, view.getColorBarParamInfo().getBackgroundTransparency());
        param.getProperties().setLabel("Transparency of Backdrop");
        param.getProperties().setMinValue(0.0f);
        param.getProperties().setMaxValue(1.0f);
        paramGroup.addParameter(param);


        param = new Parameter(TITLE_PARAM_STR, view.getColorBarParamInfo().getTitle());
        param.getProperties().setLabel("Title");
        param.getProperties().setNumCols(24);
        param.getProperties().setNullValueAllowed(true);
        paramGroup.addParameter(param);

        param = new Parameter(TITLE_UNITS_PARAM_STR, view.getColorBarParamInfo().getTitleUnits());
        param.getProperties().setLabel("Units");
        param.getProperties().setNumCols(24);
        param.getProperties().setNullValueAllowed(true);
        paramGroup.addParameter(param);

        param = new Parameter(TITLE_FONT_SIZE_PARAM_STR, view.getColorBarParamInfo().getTitleFontSize());
        param.getProperties().setLabel("Title Size");
        param.getProperties().setMinValue(4);
        param.getProperties().setMaxValue(100);
        paramGroup.addParameter(param);

        param = new Parameter(TITLE_UNITS_FONT_SIZE_PARAM_STR, view.getColorBarParamInfo().getTitleUnitsFontSize());
        param.getProperties().setLabel("Units Size");
        param.getProperties().setMinValue(4);
        param.getProperties().setMaxValue(100);
        paramGroup.addParameter(param);

        param = new Parameter(LABELS_FONT_SIZE_PARAM_STR, view.getColorBarParamInfo().getLabelsFontSize());
        param.getProperties().setLabel("Labels Size");
        param.getProperties().setMinValue(4);
        param.getProperties().setMaxValue(100);
        paramGroup.addParameter(param);

        param = new Parameter(SCALING_FACTOR_PARAM_STR, view.getColorBarParamInfo().getScalingFactor());
        param.getProperties().setLabel("Data Scaling Factor*");
        param.getProperties().setMinValue(.000001);
        param.getProperties().setMaxValue(1000000);
        paramGroup.addParameter(param);

        param = new Parameter(COLOR_BAR_LENGTH_PARAM_STR, view.getColorBarParamInfo().getColorBarLength());
        param.getProperties().setLabel("Color Bar Length");
        param.getProperties().setMinValue(300);
        param.getProperties().setMaxValue(5000);
        paramGroup.addParameter(param);

        param = new Parameter(COLOR_BAR_THICKNESS_PARAM_STR, view.getColorBarParamInfo().getColorBarThickness());
        param.getProperties().setLabel("Color Bar Thickness");
        param.getProperties().setMinValue(5);
        param.getProperties().setMaxValue(500);
        paramGroup.addParameter(param);


        param = new Parameter(LAYER_SCALING_PARAM_STR, view.getColorBarParamInfo().getLayerScaling());
        param.getProperties().setLabel("Size Scaling (percent of layer image size)");
        param.getProperties().setMinValue(5);
        param.getProperties().setMaxValue(150);
        paramGroup.addParameter(param);

        param = new Parameter(LAYER_OFFSET_PARAM_STR, view.getColorBarParamInfo().getLayerOffset());
        param.getProperties().setLabel("Location Offset (percent of color bar height)");
        param.getProperties().setMinValue(-2000);
        param.getProperties().setMaxValue(2000);
        paramGroup.addParameter(param);




        param = new Parameter(PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION, view.getColorBarParamInfo().getHorizontalLocation());
        param.getProperties().setLabel("Location & Alignment (if Horizontal)");
        param.getProperties().setValueSet(ColorBarParamInfo.getHorizontalLocationArray());
        param.getProperties().setValueSetBound(true);
        paramGroup.addParameter(param);


        param = new Parameter(PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION, view.getColorBarParamInfo().getVerticalLocation());
        param.getProperties().setLabel("Location & Alignment (if Vertical)");
        param.getProperties().setValueSet(ColorBarParamInfo.getVerticalLocationArray());
        param.getProperties().setValueSetBound(true);
        paramGroup.addParameter(param);




        param = new Parameter(LAYER_SHIFT_PARAM_STR, view.getColorBarParamInfo().getLayerShift());
        param.getProperties().setLabel("Location Shift (percent of color bar width)");
        param.getProperties().setMinValue(-2000);
        param.getProperties().setMaxValue(2000);
        paramGroup.addParameter(param);


        param = new Parameter(CENTER_ON_LAYER_PARAM_STR, view.getColorBarParamInfo().getCenterOnLayer());
        param.getProperties().setLabel("Center on layer");
        paramGroup.addParameter(param);

        param = new Parameter(MANUAL_POINTS_PARAM_STR, view.getColorBarParamInfo().getManualPoints());
        param.getProperties().setLabel("Manually Entered Points");
        param.getProperties().setNumCols(24);
        param.getProperties().setNullValueAllowed(true);
        paramGroup.addParameter(param);

        param = new Parameter(ORIENTATION_PARAM_STR, view.getColorBarParamInfo().getOrientation());
        param.getProperties().setLabel("Orientation");
        param.getProperties().setValueSet(new String[]{ColorBarParamInfo.HORIZONTAL_STR, ColorBarParamInfo.VERTICAL_STR});
        param.getProperties().setValueSetBound(true);
        paramGroup.addParameter(param);

        param = new Parameter(PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION, view.getColorBarParamInfo().getInsideOutsideLocation());
        param.getProperties().setLabel("Location (Inside/Outside)");
        param.getProperties().setValueSet(new String[]{ColorBarParamInfo.LOCATION_INSIDE_STR, ColorBarParamInfo.LOCATION_OUTSIDE_STR});
        param.getProperties().setValueSetBound(true);
        paramGroup.addParameter(param);



        param = new Parameter(DISTRIBUTION_TYPE_PARAM_STR, view.getColorBarParamInfo().getDistributionType());
        param.getProperties().setLabel("Mode");
        param.getProperties().setValueSet(new String[]{
                ImageLegend.DISTRIB_EVEN_STR,
                ImageLegend.DISTRIB_MANUAL_STR,
                ImageLegend.DISTRIB_EXACT_STR
        });
        param.getProperties().setValueSetBound(true);
        paramGroup.addParameter(param);

        param = new Parameter(NUM_TICKS_PARAM_STR, view.getColorBarParamInfo().getNumTickMarks());
        param.getProperties().setLabel("Tick Mark Count");
        param.getProperties().setMinValue(0);
        param.getProperties().setMaxValue(40);
        paramGroup.addParameter(param);

        param = new Parameter(DECIMAL_PLACES_PARAM_STR, view.getColorBarParamInfo().getDecimalPlaces());
        param.getProperties().setLabel("Decimal Places*");
        param.getProperties().setMinValue(0);
        param.getProperties().setMaxValue(8);
        paramGroup.addParameter(param);


        param = new Parameter(DECIMAL_PLACES_FORCE_PARAM_STR, view.getColorBarParamInfo().isDecimalPlacesForce());
        param.getProperties().setLabel("Force Decimal Places");
        paramGroup.addParameter(param);


        param = new Parameter(FOREGROUND_COLOR_PARAM_STR, view.getColorBarParamInfo().getForegroundColor());
        param.getProperties().setLabel("Text/Tick Color");
        paramGroup.addParameter(param);

        param = new Parameter(BACKGROUND_COLOR_PARAM_STR, view.getColorBarParamInfo().getBackgroundColor());
        param.getProperties().setLabel("Backdrop Color");
        paramGroup.addParameter(param);


        return paramGroup;
    }

    private static void splitTitleAndUnits(ParamGroup legendParamGroup, RasterDataNode raster) {
        String name = raster.getName();
        String unit = raster.getUnit() != null ? raster.getUnit() : "-";
        unit = unit.replace('*', ' ');

        String headerText = name;
        legendParamGroup.getParameter(TITLE_PARAM_STR).setValue(headerText, null);

        String headerUnitsText = "(" + unit + ")";
        legendParamGroup.getParameter(TITLE_UNITS_PARAM_STR).setValue(headerUnitsText, null);
    }

    public static void modifyManualPoints(ParamGroup legendParamGroup, String value) {
        legendParamGroup.getParameter(MANUAL_POINTS_PARAM_STR).setValue(value, null);

    }

    public static void modifyTitle(ParamGroup legendParamGroup, String value) {
        legendParamGroup.getParameter(TITLE_PARAM_STR).setValue(value, null);
    }

    private static JComponent createImageLegendAccessory(final VisatApp visatApp,
                                                         final JFileChooser fileChooser,
                                                         final ParamGroup legendParamGroup,
                                                         final ImageLegend imageLegend) {
        final JButton button = new JButton("Edit Colorbar Settings ...");
        button.setMnemonic('P');
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final BeamFileFilter fileFilter = (BeamFileFilter) fileChooser.getFileFilter();
//                final ImageLegendDialog dialog = new ImageLegendDialog(visatApp,
//                                                                       colorBarParamGroup,
//                                                                       imageLegend,
//                                                                       isTransparencySupportedByFormat(
//                                                                               fileFilter.getFormatName()));
                final ImageLegendDialog dialog = new ImageLegendDialog(visatApp,
                        legendParamGroup,
                        imageLegend,
                        true);
                dialog.show();
            }
        });
        final JPanel accessory = new JPanel(new BorderLayout());
        accessory.setBorder(new EmptyBorder(3, 3, 3, 3));
        accessory.add(button, BorderLayout.NORTH);

        return accessory;
    }


    private static void transferParamsToImageLegend(ParamGroup legendParamGroup, ImageLegend imageLegend) {
        Object value;

        value = legendParamGroup.getParameter(SHOW_TITLE_PARAM_STR).getValue();
        imageLegend.setShowTitle((Boolean) value);

        value = legendParamGroup.getParameter(TITLE_PARAM_STR).getValue();
        imageLegend.setHeaderText((String) value);
        // DANNY
        value = legendParamGroup.getParameter(TITLE_UNITS_PARAM_STR).getValue();
        imageLegend.setHeaderUnitsText((String) value);

        value = legendParamGroup.getParameter(MANUAL_POINTS_PARAM_STR).getValue();
        imageLegend.setFullCustomAddThesePoints((String) value);

        value = legendParamGroup.getParameter(ORIENTATION_PARAM_STR).getValue();
        imageLegend.setOrientation(ColorBarParamInfo.HORIZONTAL_STR.equals(value) ? ImageLegend.HORIZONTAL : ImageLegend.VERTICAL);

        value = legendParamGroup.getParameter(PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION).getValue();
        imageLegend.setInsideOutsideLocation((String) value);

        value = legendParamGroup.getParameter(DISTRIBUTION_TYPE_PARAM_STR).getValue();
        imageLegend.setDistributionType((String) value);


        value = legendParamGroup.getParameter(SCALING_FACTOR_PARAM_STR).getValue();
        imageLegend.setScalingFactor((Double) value);

        value = legendParamGroup.getParameter(TITLE_FONT_SIZE_PARAM_STR).getValue();
        imageLegend.setTitleFontSize((Integer) value);

        value = legendParamGroup.getParameter(TITLE_UNITS_FONT_SIZE_PARAM_STR).getValue();
        imageLegend.setTitleUnitsFontSize((Integer) value);

        value = legendParamGroup.getParameter(LABELS_FONT_SIZE_PARAM_STR).getValue();
        imageLegend.setLabelsFontSize((Integer) value);

        value = legendParamGroup.getParameter(COLOR_BAR_LENGTH_PARAM_STR).getValue();
        imageLegend.setColorBarLength((Integer) value);

        value = legendParamGroup.getParameter(COLOR_BAR_THICKNESS_PARAM_STR).getValue();
        imageLegend.setColorBarThickness((Integer) value);

        value = legendParamGroup.getParameter(LAYER_SCALING_PARAM_STR).getValue();
        imageLegend.setLayerScaling((Double) value);

        value = legendParamGroup.getParameter(LAYER_OFFSET_PARAM_STR).getValue();
        imageLegend.setLayerOffset((Double) value);


        value = legendParamGroup.getParameter(PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION).getValue();
        imageLegend.setHorizontalLocation((String) value);

        value = legendParamGroup.getParameter(PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION).getValue();
        imageLegend.setVerticalLocation((String) value);


        value = legendParamGroup.getParameter(LAYER_SHIFT_PARAM_STR).getValue();
        imageLegend.setLayerShift((Double) value);

        value = legendParamGroup.getParameter(CENTER_ON_LAYER_PARAM_STR).getValue();
        imageLegend.setCenterOnLayer((Boolean) value);

        value = legendParamGroup.getParameter(NUM_TICKS_PARAM_STR).getValue();
        imageLegend.setNumberOfTicks((Integer) value);


        value = legendParamGroup.getParameter(DECIMAL_PLACES_PARAM_STR).getValue();
        imageLegend.setDecimalPlaces((Integer) value);

        value = legendParamGroup.getParameter(DECIMAL_PLACES_FORCE_PARAM_STR).getValue();
        imageLegend.setDecimalPlacesForce((Boolean) value);

        value = legendParamGroup.getParameter(BACKGROUND_COLOR_PARAM_STR).getValue();
        imageLegend.setBackgroundColor((Color) value);

        value = legendParamGroup.getParameter(FOREGROUND_COLOR_PARAM_STR).getValue();
        imageLegend.setForegroundColor((Color) value);

        value = legendParamGroup.getParameter(TRANSPARENCY_PARAM_STR).getValue();
        imageLegend.setBackgroundTransparency((Float) value);
    }


    public static class ImageLegendDialog extends ModalDialog {

        private static final String _HELP_ID = "";

        private VisatApp visatApp;
        private ImageInfo imageInfo;
        private RasterDataNode raster;
        private boolean transparencyEnabled;

        private ParamGroup paramGroup;

        private Parameter usingHeaderParam;
        private Parameter transparencyParam;
        private Parameter headerTextParam;
        private Parameter headerUnitsParam;
        private Parameter orientationParam;
        private Parameter insideOutsideLocationParam;
        private Parameter distributionTypeParam;
        private Parameter numberOfTicksParam;
        private Parameter backgroundColorParam;
        private Parameter foregroundColorParam;
        private Parameter decimalPlacesParam;
        private Parameter decimalPlacesForceParam;
        private Parameter fullCustomAddThesePointsParam;

        private JPanel evenDistribJPanel;
        private JPanel fullCustomJPanel;

        private Parameter scalingFactorParam;
        private Parameter titleFontSizeParam;
        private Parameter titleUnitsFontSizeParam;
        private Parameter labelsFontSizeParam;
        private Parameter colorBarLengthParam;
        private Parameter colorBarThicknessParam;
        private Parameter layerScalingParam;
        private Parameter layerOffsetParam;
        private Parameter horizontalLocationParam;
        private Parameter verticalLocationParam;
        private Parameter layerShiftParam;
        private Parameter centerOnLayerParam;

        private boolean okWasClicked = false;


        public ImageLegendDialog(VisatApp visatApp, ParamGroup paramGroup, ImageLegend imageLegend,
                                 boolean transparencyEnabled) {
//            super(visatApp.getMainFrame(), visatApp.getAppName() + " - Color Bar Settings", ID_OK_APPLY_CANCEL, _HELP_ID);
            super(visatApp.getMainFrame(), "Create/Edit Color Bar", ID_OK_APPLY_CANCEL, _HELP_ID);
            this.visatApp = visatApp;
            okWasClicked = false;

            imageInfo = imageLegend.getImageInfo();
            raster = imageLegend.getRaster();
            this.transparencyEnabled = transparencyEnabled;
            this.paramGroup = paramGroup;
            initParams();
            // rename the OK button
            JButton okButton = (JButton) getButton(ID_OK);
            okButton.setText("Save to File");
            JButton applyButton = (JButton) getButton(ID_APPLY);
//            applyButton.setText("Attach to Image");
            applyButton.setText("Create Layer");

            initUI();
            updateUIState();
            this.paramGroup.addParamChangeListener(new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    updateUIState();
                }
            });


        }


        private void updateUIState() {
            boolean headerTextEnabled = (Boolean) usingHeaderParam.getValue();
            headerTextParam.setUIEnabled(headerTextEnabled);
            headerUnitsParam.setUIEnabled(headerTextEnabled);

            if (ColorBarParamInfo.HORIZONTAL_STR.equals(orientationParam.getValue())) {
                horizontalLocationParam.setUIEnabled(true);
                verticalLocationParam.setUIEnabled(false);
            } else {
                horizontalLocationParam.setUIEnabled(false);
                verticalLocationParam.setUIEnabled(true);
            }

            if (ImageLegend.DISTRIB_EVEN_STR.equals(distributionTypeParam.getValue())) {
                numberOfTicksParam.setUIEnabled(true);


                // todo perhaps here is where adjusting num ticks could affect the font sizes?
//                if ((Integer) (paramGroup.getParameter(NUM_TICKS_PARAM_STR).getValue()) > 8) {
//                } else {
//                    paramGroup.getParameter(LABELS_FONT_SIZE_PARAM_STR).setValue(newValue, null);
//                }

                decimalPlacesParam.setUIEnabled(true);
                decimalPlacesForceParam.setUIEnabled(true);
                fullCustomAddThesePointsParam.setUIEnabled(false);
            } else if (ImageLegend.DISTRIB_EXACT_STR.equals(distributionTypeParam.getValue())) {
                numberOfTicksParam.setUIEnabled(false);
                decimalPlacesParam.setUIEnabled(true);
                decimalPlacesForceParam.setUIEnabled(true);
                fullCustomAddThesePointsParam.setUIEnabled(false);
            } else if (ImageLegend.DISTRIB_MANUAL_STR.equals(distributionTypeParam.getValue())) {
                numberOfTicksParam.setUIEnabled(false);
                decimalPlacesParam.setUIEnabled(false);
                decimalPlacesForceParam.setUIEnabled(false);
                fullCustomAddThesePointsParam.setUIEnabled(true);
            }
        }


        public ParamGroup getParamGroup() {
            return paramGroup;
        }

        public void setHeaderText(String text) {
            headerTextParam.setValue(text, null);
        }

        public boolean isTransparencyEnabled() {
            return transparencyEnabled;
        }

        public void setTransparencyEnabled(boolean transparencyEnabled) {
            this.transparencyEnabled = transparencyEnabled;
            updateUIState();
        }

        public void getImageLegend(ImageLegend imageLegend) {
            transferParamsToImageLegend(getParamGroup(), imageLegend);
        }

        public ImageInfo getImageInfo() {
            return imageInfo;
        }

        @Override
        protected void onOK() {
            //           getParamGroup().getParameterValues(visatApp.getPreferences());
            super.onOK();
            okWasClicked = true;
        }

        @Override
        protected void onApply() {
            hide();
        }

        private void initUI() {


            final JButton previewButton = new JButton("Preview...");
            previewButton.setMnemonic('v');
            previewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showPreview();
                }
            });

            final GridBagConstraints gbc = new GridBagConstraints();
            final JPanel jPanel = GridBagUtils.createPanel();

            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets.top = 10;
            gbc.weightx=0;
            gbc.weighty=0;
//            jPanel.add(orientationParam.getEditor().getLabelComponent(), gbc);
//            gbc.gridx = 1;
//            jPanel.add(orientationParam.getEditor().getEditorComponent(), gbc);
//            gbc.gridy++;

            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.gridy++;
            gbc.insets.top = 10;
            gbc.gridx = 0;



            JPanel jPanelGroup = GridBagUtils.createPanel();
            GridBagConstraints gbcGroup = GridBagUtils.createConstraints("");
            gbcGroup.gridx = 0;
            gbcGroup.gridy = 0;
            gbcGroup.weightx = 1.0;
            gbcGroup.fill = GridBagConstraints.HORIZONTAL;
            gbcGroup.anchor = GridBagConstraints.NORTHWEST;
            gbcGroup.insets.bottom=10;

            jPanelGroup.add(getFormatsPanel("Formatting"), gbcGroup);

            gbcGroup.gridy++;
            gbcGroup.weighty = 1.0;

            jPanelGroup.add(getTitlePanel("Title"), gbcGroup);

            gbcGroup.gridy++;


            JPanel jPanelGroup2 = GridBagUtils.createPanel();
            GridBagConstraints gbcGroup2 = GridBagUtils.createConstraints("");
            gbcGroup2.gridx = 0;
            gbcGroup2.gridy = 0;
            gbcGroup2.weightx = 1.0;
            gbcGroup2.fill = GridBagConstraints.HORIZONTAL;
            gbcGroup2.anchor = GridBagConstraints.NORTHWEST;
            gbcGroup2.insets.bottom=10;


            jPanelGroup2.add(getScalingPanel("Scaling and Location (For Layer Only)"), gbcGroup);


            gbcGroup.gridy++;

            gbcGroup2.weighty = 1.0;
            jPanelGroup2.add(getDistributionPanel("Data Label Distribution & Numeric Formatting"), gbcGroup);


            JPanel jPanelGroupAll = GridBagUtils.createPanel();
            GridBagConstraints gbcGroupAll = GridBagUtils.createConstraints("");
            gbcGroupAll.gridx = 0;
            gbcGroupAll.gridy = 0;
            gbcGroupAll.weightx = 1.0;
            gbcGroupAll.fill = GridBagConstraints.HORIZONTAL;
            gbcGroupAll.anchor = GridBagConstraints.NORTHWEST;
            gbcGroupAll.insets.bottom=10;
            gbcGroupAll.insets.right=10;
            jPanelGroupAll.add(jPanelGroup, gbcGroupAll);
            gbcGroupAll.gridx++;
            gbcGroupAll.weighty = 1.0;
            gbcGroupAll.insets.right=0;


            jPanelGroupAll.add(jPanelGroup2, gbcGroupAll);





            final JScrollPane paramsScroll = new JScrollPane(jPanelGroupAll);
            paramsScroll.setBorder(null);

            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx=1.0;
            gbc.weighty=.5;
            jPanel.add(paramsScroll, gbc);


            gbc.gridwidth = 2;
            gbc.gridy++;
            gbc.insets.top = 10;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            gbc.weightx=0;
            gbc.weighty=0;
            jPanel.add(previewButton, gbc);

            jPanel.setBorder(new EmptyBorder(7, 7, 7, 7));

            jPanel.setMaximumSize(new Dimension(UIUtils.getScreenWidth(),UIUtils.getScreenHeight()));


            setContent(jPanel);
        }


        private JPanel getTitlePanel(String title) {
            JPanel jPanel = GridBagUtils.createPanel();
            jPanel.setBorder(UIUtils.createGroupBorder(title)); /*I18N*/
            GridBagConstraints gbc = GridBagUtils.createConstraints("");
            gbc.gridy = 0;

            addParamToPane(jPanel, usingHeaderParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, headerTextParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, headerUnitsParam, gbc);
            gbc.gridy++;

            return jPanel;
        }

        private JPanel getDistributionPanel(String title) {
            JPanel jPanel = GridBagUtils.createPanel();
            jPanel.setBorder(UIUtils.createGroupBorder(title)); /*I18N*/
            GridBagConstraints gbc = GridBagUtils.createConstraints("");
            gbc.gridy = 0;

            addParamToPane(jPanel, distributionTypeParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, fullCustomAddThesePointsParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, numberOfTicksParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, scalingFactorParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, decimalPlacesParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, decimalPlacesForceParam, gbc);
            gbc.gridy++;

            return jPanel;
        }


        private JPanel getFormatsPanel(String title) {
            JPanel jPanel = GridBagUtils.createPanel();
            jPanel.setBorder(UIUtils.createGroupBorder(title)); /*I18N*/
            GridBagConstraints gbc = GridBagUtils.createConstraints("");
            gbc.gridy = 0;



            addParamToPane(jPanel, orientationParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, foregroundColorParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, backgroundColorParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, transparencyParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, titleFontSizeParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, titleUnitsFontSizeParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, labelsFontSizeParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, colorBarLengthParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, colorBarThicknessParam, gbc);
            gbc.gridy++;



//            gbc.gridx = 0;
//            gbc.gridy++;
//            gbc.gridwidth = 2;
//            gbc.insets.bottom = 10;
//            JLabel dimensionsJLabel = new JLabel("   * NOTE: Image Scene Dimensions (" + imageWidth + " x " + imageHeight + ")");
//            dimensionsJLabel.setToolTipText("Dimensions (width x height).  Useful if creating a colorBar layer");
//            dimensionsJLabel.setForeground(Color.BLUE);
//            jPanel.add(dimensionsJLabel, gbc);
//            gbc.gridwidth = 1;
//            gbc.insets.bottom = 0;

            return jPanel;
        }



        private JPanel getScalingPanel(String title) {
            JPanel jPanel = GridBagUtils.createPanel();
            jPanel.setBorder(UIUtils.createGroupBorder(title)); /*I18N*/
            GridBagConstraints gbc = GridBagUtils.createConstraints("");
            gbc.gridy = 0;


            addParamToPane(jPanel, layerScalingParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, insideOutsideLocationParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, horizontalLocationParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, verticalLocationParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, layerOffsetParam, gbc);
            gbc.gridy++;

            addParamToPane(jPanel, layerShiftParam, gbc);
            gbc.gridy++;




            return jPanel;
        }


        private void initParams() {
            usingHeaderParam = paramGroup.getParameter(SHOW_TITLE_PARAM_STR);
            headerTextParam = paramGroup.getParameter(TITLE_PARAM_STR);
            orientationParam = paramGroup.getParameter(ORIENTATION_PARAM_STR);
            insideOutsideLocationParam = paramGroup.getParameter(PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION);
            distributionTypeParam = paramGroup.getParameter(DISTRIBUTION_TYPE_PARAM_STR);
//            fontSizeParam = paramGroup.getParameter(LABEL_FONT_SIZE_PARAM_STR);
            scalingFactorParam = paramGroup.getParameter(SCALING_FACTOR_PARAM_STR);
            titleFontSizeParam = paramGroup.getParameter(TITLE_FONT_SIZE_PARAM_STR);
            titleUnitsFontSizeParam = paramGroup.getParameter(TITLE_UNITS_FONT_SIZE_PARAM_STR);
            labelsFontSizeParam = paramGroup.getParameter(LABELS_FONT_SIZE_PARAM_STR);
            colorBarLengthParam = paramGroup.getParameter(COLOR_BAR_LENGTH_PARAM_STR);
            colorBarThicknessParam = paramGroup.getParameter(COLOR_BAR_THICKNESS_PARAM_STR);
            layerScalingParam = paramGroup.getParameter(LAYER_SCALING_PARAM_STR);
            layerOffsetParam = paramGroup.getParameter(LAYER_OFFSET_PARAM_STR);
            horizontalLocationParam = paramGroup.getParameter(PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION);
            verticalLocationParam = paramGroup.getParameter(PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION);
            layerShiftParam = paramGroup.getParameter(LAYER_SHIFT_PARAM_STR);
            centerOnLayerParam = paramGroup.getParameter(CENTER_ON_LAYER_PARAM_STR);
            numberOfTicksParam = paramGroup.getParameter(NUM_TICKS_PARAM_STR);
            foregroundColorParam = paramGroup.getParameter(FOREGROUND_COLOR_PARAM_STR);
            backgroundColorParam = paramGroup.getParameter(BACKGROUND_COLOR_PARAM_STR);
            transparencyParam = paramGroup.getParameter(TRANSPARENCY_PARAM_STR);
            decimalPlacesParam = paramGroup.getParameter(DECIMAL_PLACES_PARAM_STR);
            decimalPlacesForceParam = paramGroup.getParameter(DECIMAL_PLACES_FORCE_PARAM_STR);
            headerUnitsParam = paramGroup.getParameter(TITLE_UNITS_PARAM_STR);
            fullCustomAddThesePointsParam = paramGroup.getParameter(MANUAL_POINTS_PARAM_STR);


        }

        private void showPreview() {

            final ImageLegend imageLegend = new ImageLegend(getImageInfo(), raster);
            getImageLegend(imageLegend);
            final BufferedImage image = imageLegend.createPreviewImage();
            final JLabel imageDisplay = new JLabel(new ImageIcon(image));
            imageDisplay.setOpaque(true);
            imageDisplay.addMouseListener(new MouseAdapter() {
                // Both events (releases & pressed) must be checked, otherwise it won't work on all
                // platforms

                /**
                 * Invoked when a mouse button has been released on a component.
                 */
                @Override
                public void mouseReleased(MouseEvent e) {
                    // On Windows
                    showPopup(e, image, imageDisplay);
                }

                /**
                 * Invoked when a mouse button has been pressed on a component.
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    // On Linux
                    // todo - clipboard does not work on linux.
                    // todo - better not to show popup until it works correctly
//                    showPopup(e, image, imageDisplay);
                }
            });

            modifyManualPoints(paramGroup, imageLegend.getFullCustomAddThesePoints());
            //         modifyTitle(paramGroup, imageLegend.getHeaderText());


            final ModalDialog dialog = new ModalDialog(getParent(), VisatApp.getApp().getAppName() + " - Color Bar Preview", imageDisplay,
                    ID_OK, null);
            dialog.getJDialog().setResizable(false);
            dialog.show();


        }

        private static void showPopup(final MouseEvent e, final BufferedImage image, final JComponent imageDisplay) {
            if (e.isPopupTrigger()) {
                final JPopupMenu popupMenu = new JPopupMenu();
                final JMenuItem menuItem = new JMenuItem("Copy image to clipboard");
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SystemUtils.copyToClipboard(image);
                    }
                });
                popupMenu.add(menuItem);
                popupMenu.show(imageDisplay, e.getX(), e.getY());
            }
        }
    }



    public String getHorizontalLocationPreference() {
        return configuration.getPropertyString(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION, ColorBarParamInfo.DEFAULT_HORIZONTAL_LOCATION);
    }
    public String getVerticalLocationPreference() {
        return configuration.getPropertyString(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION, ColorBarParamInfo.DEFAULT_VERTICAL_LOCATION);
    }

    public boolean getColorBarShowTitlePreference() {
        return configuration.getPropertyBool(ExportLegendImageAction.SHOW_TITLE_PARAM_STR, ColorBarParamInfo.DEFAULT_SHOW_TITLE_ENABLED);
    }



    public double getColorBarTransparencyPreference() {
        return configuration.getPropertyDouble(ExportLegendImageAction.TRANSPARENCY_PARAM_STR, ColorBarParamInfo.DEFAULT_BACKGROUND_TRANSPARENCY);
    }

    public String getColorBarOrientationPreference() {
        return configuration.getPropertyString(ExportLegendImageAction.ORIENTATION_PARAM_STR, ColorBarParamInfo.DEFAULT_ORIENTATION);
    }

    public String getColorBarInsideOutsideLocationPreference() {
        return configuration.getPropertyString(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION, ColorBarParamInfo.DEFAULT_INSIDE_OUTSIDE_LOCATION_);
    }

    public Color getForegroundColorPreference() {
        return configuration.getPropertyColor(ExportLegendImageAction.FOREGROUND_COLOR_PARAM_STR, ColorBarParamInfo.DEFAULT_FOREGROUND_COLOR);
    }

    public Color getBackgroundColorPreference() {
        return configuration.getPropertyColor(ExportLegendImageAction.BACKGROUND_COLOR_PARAM_STR, ColorBarParamInfo.DEFAULT_BACKGROUND_COLOR);
    }

    public double getLayerScalingFactorPreference() {
        return configuration.getPropertyDouble(ExportLegendImageAction.LAYER_SCALING_PARAM_STR, ColorBarParamInfo.DEFAULT_LAYER_SCALING);
    }



    public static void addLabelComponentToPane(JPanel pane, JLabel jLabel, JComponent jComponent, GridBagConstraints gbc)  {
        gbc.gridx = 0;
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.insets.bottom = 10;

        if (jLabel != null) {
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            gbc.insets.left = 5;
            pane.add(jLabel, gbc);
            gbc.gridx = 1;
            gbc.insets.left = 0;

        } else {
            gbc.insets.left = 0;
            gbc.gridwidth = 2;
        }

        if (jComponent != null) {
            gbc.weightx = 1;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            pane.add(jComponent, gbc);
        }

    }

    public static void addParamToPane(JPanel jPanel, Parameter param, GridBagConstraints gbc)  {

        JLabel jLabel = param.getEditor().getLabelComponent();
        JComponent jComponent = param.getEditor().getEditorComponent();

        addLabelComponentToPane(jPanel, jLabel, jComponent, gbc);

    }
}