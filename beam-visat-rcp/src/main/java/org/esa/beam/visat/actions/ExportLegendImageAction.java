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

import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.datamodel.ImageLegend;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.param.ParamChangeEvent;
import org.esa.beam.framework.param.ParamChangeListener;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.product.ColorBarParamInfo;
import org.esa.beam.framework.ui.product.ProductSceneView;
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

    private static final String ORIENTATION_PARAM_STR = "legend.orientation";
    private static final String DISTRIBUTION_TYPE_PARAM_STR = "legend.label.distribution.type";
    private static final String NUM_TICKS_PARAM_STR = "legend.numberOfTicks";
    private static final String SHOW_TITLE_PARAM_STR = "legend.usingHeader";
    private static final String TITLE_PARAM_STR = "legend.headerText";
    private static final String TITLE_UNITS_PARAM_STR = "legend.header.units.text";
    private static final String MANUAL_POINTS_PARAM_STR = "legend.fullCustomAddThesePoints";
    //    private static final String LABEL_FONT_SIZE_PARAM_STR = "legend.fontSize";
    private static final String DECIMAL_PLACES_PARAM_STR = "legend.decimalPlaces";
    private static final String DECIMAL_PLACES_FORCE_PARAM_STR = "legend.decimalPlacesForce";
    private static final String FOREGROUND_COLOR_PARAM_STR = "legend.foregroundColor";
    private static final String BACKGROUND_COLOR_PARAM_STR = "legend.backgroundColor";
    //   private static final String BACKGROUND_TRANSPARENCY_PARAM_STR = "legend.backgroundTransparency";
    private static final String TRANSPARENT_PARAM_STR = "legend.transparent";

    private static final String SCALING_FACTOR_PARAM_STR = "legend.scalingFactor";
    private static final String TITLE_FONT_SIZE_PARAM_STR = "legend.titleFontSize";
    private static final String TITLE_UNITS_FONT_SIZE_PARAM_STR = "legend.titleUnitsFontSize";
    private static final String LABELS_FONT_SIZE_PARAM_STR = "legend.labelsFontSize";

    private static final String COLOR_BAR_LENGTH_PARAM_STR = "legend.colorBarLength";
    private static final String COLOR_BAR_THICKNESS_PARAM_STR = "legend.colorBarThickness";
    private static final String LAYER_SCALING_PARAM_STR = "legend.layerScalingThickness";
    private static final String CENTER_ON_LAYER_PARAM_STR = "legend.centerOnLayer";


    private ParamGroup colorBarParamGroup;
    private ImageLegend imageLegend;
    private static int imageHeight;
    private static int imageWidth;
    boolean colorBarLayer = false;

    @Override
    public void actionPerformed(CommandEvent event) {
        ProductSceneView view = getVisatApp().getSelectedProductSceneView();


        imageHeight = view.getRaster().getRasterHeight();
        imageWidth = view.getRaster().getRasterWidth();

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
        imageLegend.initDefaults();
        if (imageLegend.getHeaderText() != null && imageLegend.getHeaderText().length() > 0) {
            colorBarParamGroup.getParameter(TITLE_PARAM_STR).setValue(imageLegend.getHeaderText(), null);
        }
        colorBarParamGroup.getParameter(DISTRIBUTION_TYPE_PARAM_STR).setValue(ImageLegend.DISTRIB_MANUAL_STR, null);
        colorBarParamGroup.getParameter(MANUAL_POINTS_PARAM_STR).setValue(imageLegend.getFullCustomAddThesePoints(), null);


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
        view.getColorBarParamInfo().setBackgroundTransparencyEnabled(new Boolean(imageLegend.isBackgroundTransparencyEnabled()));
        view.getColorBarParamInfo().setShowTitle(new Boolean(imageLegend.isShowTitle()));
        view.getColorBarParamInfo().setTitle(imageLegend.getHeaderText());
        view.getColorBarParamInfo().setTitleUnits(imageLegend.getHeaderUnitsText());
        view.getColorBarParamInfo().setTitleFontSize(imageLegend.getTitleFontSize());
        view.getColorBarParamInfo().setTitleUnitsFontSize(imageLegend.getTitleUnitsFontSize());
        view.getColorBarParamInfo().setScalingFactor(imageLegend.getScalingFactor());
        view.getColorBarParamInfo().setColorBarLength(imageLegend.getColorBarLength());
        view.getColorBarParamInfo().setColorBarThickness(imageLegend.getColorBarThickness());
        view.getColorBarParamInfo().setLayerScaling(imageLegend.getLayerScaling());
        view.getColorBarParamInfo().setCenterOnLayer(new Boolean(imageLegend.isCenterOnLayer()));
        view.getColorBarParamInfo().setManualPoints(imageLegend.getFullCustomAddThesePoints());
        view.getColorBarParamInfo().setDistributionType(imageLegend.getDistributionType());
        view.getColorBarParamInfo().setNumTickMarks(imageLegend.getNumberOfTicks());
        view.getColorBarParamInfo().setDecimalPlaces(imageLegend.getDecimalPlaces());
        view.getColorBarParamInfo().setDecimalPlacesForce(new Boolean(imageLegend.isDecimalPlacesForce()));
        view.getColorBarParamInfo().setForegroundColor(imageLegend.getForegroundColor());
        view.getColorBarParamInfo().setBackgroundColor(imageLegend.getBackgroundColor());
        view.getColorBarParamInfo().setParamsInitialized(true);

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

        param = new Parameter(TRANSPARENT_PARAM_STR, view.getColorBarParamInfo().getBackgroundTransparencyEnabled());
        param.getProperties().setLabel("Transparent");
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
        param.getProperties().setLabel("Scaling (percent of layer image size)");
        param.getProperties().setMinValue(5);
        param.getProperties().setMaxValue(150);
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
        param.getProperties().setLabel("Text Color");
        paramGroup.addParameter(param);

        param = new Parameter(BACKGROUND_COLOR_PARAM_STR, view.getColorBarParamInfo().getBackgroundColor());
        param.getProperties().setLabel("Background Color");
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

        value = legendParamGroup.getParameter(TRANSPARENT_PARAM_STR).getValue();
        imageLegend.setTransparent((Boolean) value);
    }


    public static class ImageLegendDialog extends ModalDialog {

        private static final String _HELP_ID = "";

        private VisatApp visatApp;
        private ImageInfo imageInfo;
        private RasterDataNode raster;
        private boolean transparencyEnabled;

        private ParamGroup paramGroup;

        private Parameter usingHeaderParam;
        private Parameter transparentParam;
        private Parameter headerTextParam;
        private Parameter headerUnitsParam;
        private Parameter orientationParam;
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
            jPanel.add(orientationParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            jPanel.add(orientationParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            evenDistribJPanel = getDistributionPanel("Data Label Distribution & Numeric Formatting");
            jPanel.add(evenDistribJPanel, gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(getTitlePanel("Title"), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(getFormatsPanel("Formatting"), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(getScalingPanel("Layer Scaling"), gbc);


            gbc.gridwidth = 2;
            gbc.gridy++;
            gbc.insets.top = 10;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            jPanel.add(previewButton, gbc);

            jPanel.setBorder(new EmptyBorder(7, 7, 7, 7));

            setContent(jPanel);
        }


        private JPanel getTitlePanel(String title) {
            JPanel jPanel = new JPanel(new GridBagLayout());
            jPanel.setBorder(BorderFactory.createTitledBorder(title));
            final GridBagConstraints gbc = new GridBagConstraints();

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;


            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            jPanel.add(usingHeaderParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            jPanel.add(headerTextParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            jPanel.add(headerTextParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets.top = 3;
            gbc.weightx = 0;
            jPanel.add(headerUnitsParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            jPanel.add(headerUnitsParam.getEditor().getEditorComponent(), gbc);

            return jPanel;
        }

        private JPanel getDistributionPanel(String title) {
            JPanel jPanel = new JPanel(new GridBagLayout());
            jPanel.setBorder(BorderFactory.createTitledBorder(title));
            final GridBagConstraints gbc = new GridBagConstraints();

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.gridwidth = 1;
            jPanel.add(distributionTypeParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            jPanel.add(distributionTypeParam.getEditor().getEditorComponent(), gbc);


            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 1.0;
            jPanel.add(numberOfTicksParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            jPanel.add(numberOfTicksParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            JLabel label = fullCustomAddThesePointsParam.getEditor().getLabelComponent();
            label.setToolTipText("Add values comma delimited.  i.e.  5,7,9");
            jPanel.add(label, gbc);
            gbc.gridx = 1;
            jPanel.add(fullCustomAddThesePointsParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 1.0;
            JLabel scalingFactorlabel = scalingFactorParam.getEditor().getLabelComponent();
            scalingFactorlabel.setToolTipText("Multiplication factor to be applied to colorbar points (Note this will change units so user will need to adjust units accordingly)");

            jPanel.add(scalingFactorlabel, gbc);
            gbc.gridx = 1;
            jPanel.add(scalingFactorParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 1.0;
            JLabel decimalPlacesLabel = decimalPlacesParam.getEditor().getLabelComponent();
            decimalPlacesLabel.setToolTipText("Adds more decimal places if needed for smaller numbers, trims off trailing zeros");
            jPanel.add(decimalPlacesLabel, gbc);
            gbc.gridx = 1;
            jPanel.add(decimalPlacesParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            jPanel.add(decimalPlacesForceParam.getEditor().getEditorComponent(), gbc);
            gbc.gridwidth = 1;


            return jPanel;
        }


        private JPanel getFormatsPanel(String title) {
            JPanel jPanel = new JPanel(new GridBagLayout());
            jPanel.setBorder(BorderFactory.createTitledBorder(title));
            final GridBagConstraints gbc = new GridBagConstraints();

            gbc.anchor = GridBagConstraints.WEST;


            gbc.gridwidth = 1;

            gbc.insets.top = 3;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel colorBarLengthJlabel = colorBarLengthParam.getEditor().getLabelComponent();
            colorBarLengthJlabel.setToolTipText("This is a minimum length, an accumulation of tickmarks and font sizes could increase the actual length");
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.5;
            jPanel.add(colorBarLengthJlabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(colorBarLengthParam.getEditor().getEditorComponent(), gbc);


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


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.5;
            gbc.fill = GridBagConstraints.NONE;
            jPanel.add(colorBarThicknessParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(colorBarThicknessParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.5;
            gbc.fill = GridBagConstraints.NONE;
            jPanel.add(titleFontSizeParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(titleFontSizeParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.5;
            gbc.fill = GridBagConstraints.NONE;
            jPanel.add(titleUnitsFontSizeParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(titleUnitsFontSizeParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.5;
            gbc.fill = GridBagConstraints.NONE;
            jPanel.add(labelsFontSizeParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(labelsFontSizeParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.5;
            gbc.fill = GridBagConstraints.NONE;
            jPanel.add(foregroundColorParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(foregroundColorParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.5;
            gbc.fill = GridBagConstraints.NONE;
            jPanel.add(backgroundColorParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jPanel.add(backgroundColorParam.getEditor().getEditorComponent(), gbc);


            gbc.insets.top = 10;

            gbc.gridx = 0;
            gbc.gridy++;

            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            jPanel.add(transparentParam.getEditor().getEditorComponent(), gbc);

            return jPanel;
        }


        private JPanel getScalingPanel(String title) {
            JPanel jPanel = new JPanel(new GridBagLayout());
            jPanel.setBorder(BorderFactory.createTitledBorder(title));
            final GridBagConstraints gbc = new GridBagConstraints();

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;


            gbc.weightx = 1.0;
            gbc.insets.top = 3;

            gbc.gridx = 0;
            gbc.gridy = 0;
            jPanel.add(layerScalingParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            jPanel.add(layerScalingParam.getEditor().getEditorComponent(), gbc);


//            gbc.gridx = 0;
//            gbc.gridy++;
//            gbc.gridwidth = 2;
//            jPanel.add(centerOnLayerParam.getEditor().getEditorComponent(), gbc);


            return jPanel;
        }


        private void initParams() {
            usingHeaderParam = paramGroup.getParameter(SHOW_TITLE_PARAM_STR);
            headerTextParam = paramGroup.getParameter(TITLE_PARAM_STR);
            orientationParam = paramGroup.getParameter(ORIENTATION_PARAM_STR);
            distributionTypeParam = paramGroup.getParameter(DISTRIBUTION_TYPE_PARAM_STR);
//            fontSizeParam = paramGroup.getParameter(LABEL_FONT_SIZE_PARAM_STR);
            scalingFactorParam = paramGroup.getParameter(SCALING_FACTOR_PARAM_STR);
            titleFontSizeParam = paramGroup.getParameter(TITLE_FONT_SIZE_PARAM_STR);
            titleUnitsFontSizeParam = paramGroup.getParameter(TITLE_UNITS_FONT_SIZE_PARAM_STR);
            labelsFontSizeParam = paramGroup.getParameter(LABELS_FONT_SIZE_PARAM_STR);
            colorBarLengthParam = paramGroup.getParameter(COLOR_BAR_LENGTH_PARAM_STR);
            colorBarThicknessParam = paramGroup.getParameter(COLOR_BAR_THICKNESS_PARAM_STR);
            layerScalingParam = paramGroup.getParameter(LAYER_SCALING_PARAM_STR);
            centerOnLayerParam = paramGroup.getParameter(CENTER_ON_LAYER_PARAM_STR);
            numberOfTicksParam = paramGroup.getParameter(NUM_TICKS_PARAM_STR);
            foregroundColorParam = paramGroup.getParameter(FOREGROUND_COLOR_PARAM_STR);
            backgroundColorParam = paramGroup.getParameter(BACKGROUND_COLOR_PARAM_STR);
            transparentParam = paramGroup.getParameter(TRANSPARENT_PARAM_STR);
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

}