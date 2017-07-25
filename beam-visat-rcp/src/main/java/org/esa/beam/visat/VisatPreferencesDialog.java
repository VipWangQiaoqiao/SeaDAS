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

package org.esa.beam.visat;

import com.bc.ceres.glayer.LayerType;
import com.bc.ceres.glayer.LayerTypeRegistry;
import com.bc.ceres.glayer.support.ImageLayer;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.update.ConnectionConfigData;
import com.bc.ceres.swing.update.ConnectionConfigPane;
import org.esa.beam.framework.datamodel.ColorPaletteSchemes;
import org.esa.beam.framework.datamodel.ImageLegend;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.param.*;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.PixelInfoView;
import org.esa.beam.framework.ui.RGBImageProfilePane;
import org.esa.beam.framework.ui.SuppressibleOptionPane;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.config.ConfigDialog;
import org.esa.beam.framework.ui.config.DefaultConfigPage;
import org.esa.beam.framework.ui.product.ColorBarParamInfo;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.glayer.GraticuleLayerType;
import org.esa.beam.glayer.NoDataLayerType;
import org.esa.beam.glayer.WorldMapLayerType;
import org.esa.beam.util.PropertyMap;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.visat.actions.ExportLegendImageAction;
import org.esa.beam.visat.actions.ShowModuleManagerAction;
import org.esa.beam.visat.toolviews.imageinfo.ColorManipulationToolView;
import org.esa.beam.visat.toolviews.stat.StatisticsToolView;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bc.ceres.swing.TableLayout.cell;
import static org.esa.beam.visat.VisatApp.DEFAULT_VALUE_SAVE_INCREMENTAL;
import static org.esa.beam.visat.VisatApp.DEFAULT_VALUE_SAVE_PRODUCT_ANNOTATIONS;
import static org.esa.beam.visat.VisatApp.DEFAULT_VALUE_SAVE_PRODUCT_HEADERS;
import static org.esa.beam.visat.VisatApp.DEFAULT_VALUE_SAVE_PRODUCT_HISTORY;
import static org.esa.beam.visat.VisatApp.PROPERTY_DEFAULT_DISPLAY_GEOLOCATION_AS_DECIMAL;
import static org.esa.beam.visat.VisatApp.PROPERTY_DEFAULT_GEOLOCATION_EPS;
import static org.esa.beam.visat.VisatApp.PROPERTY_DEFAULT_PIXEL_OFFSET_FOR_DISPLAY;
import static org.esa.beam.visat.VisatApp.PROPERTY_DEFAULT_PIXEL_OFFSET_FOR_DISPLAY_SHOW_DECIMALS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_DEBUG_ENABLED;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_LOG_ECHO;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_LOG_ENABLED;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_LOG_LEVEL;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_LOG_PREFIX;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_UI_FONT_NAME;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_UI_FONT_SIZE;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_UI_LAF;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_APP_UI_USE_SYSTEM_FONT_SETTINGS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_AUTO_SHOW_NAVIGATION;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_AUTO_SHOW_NEW_BANDS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_DISPLAY_GEOLOCATION_AS_DECIMAL;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_GEOLOCATION_EPS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_JAI_TILE_CACHE_CAPACITY;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_LOW_MEMORY_LIMIT;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_PIXEL_OFFSET_FOR_DISPLAY_SHOW_DECIMALS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_PIXEL_OFFSET_FOR_DISPLAY_X;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_PIXEL_OFFSET_FOR_DISPLAY_Y;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_SAVE_INCREMENTAL;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_SAVE_PRODUCT_ANNOTATIONS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_SAVE_PRODUCT_HEADERS;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_SAVE_PRODUCT_HISTORY;
import static org.esa.beam.visat.VisatApp.PROPERTY_KEY_VERSION_CHECK_ENABLED;

import static org.esa.beam.visat.VisatApp.getApp;

public class VisatPreferencesDialog extends ConfigDialog {

    private static final int _PAGE_INSET_TOP = 15;
    private static final int _LINE_INSET_TOP = 4;

    public VisatPreferencesDialog(VisatApp visatApp, String helpId) {
        super(visatApp.getMainFrame(), helpId);
        setTitleBase(visatApp.getAppName() + " Preferences");  /*I18N*/

        addRootPage(new ColorPalettesConfigPage());

        addRootPage(new DataIO());
        addRootPage(new GeolocationDisplayPage());



        final LayerPropertiesPage layerPropertiesPage = new LayerPropertiesPage();
        layerPropertiesPage.addSubPage(new ColorBarConfigPage());
        layerPropertiesPage.addSubPage(new ImageDisplayPage());
        layerPropertiesPage.addSubPage(new NoDataOverlayPage());
        layerPropertiesPage.addSubPage(new GraticuleOverlayPage());
        layerPropertiesPage.addSubPage(new MaskOverlayPage());
        layerPropertiesPage.addSubPage(new WorldMapLayerPage());
        addRootPage(layerPropertiesPage);
        addRootPage(new LoggingPage());
        addRootPage(new RepositoryConnectionConfigPage());
        addRootPage(new ProductSettings());
        addRootPage(new RGBImageProfilePage());
        addRootPage(new StatisticsPage());
        addRootPage(new AppearancePage());
        addRootPage(new BehaviorPage());

        expandAllPages();
    }

    public static class BehaviorPage extends DefaultConfigPage {

        private Parameter _unsupressParam;
        private SuppressibleOptionPane _suppressibleOptionPane;

        public BehaviorPage() {
            setTitle("UI Behavior"); /*I18N*/
        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            _suppressibleOptionPane = getApp().getSuppressibleOptionPane();
            _unsupressParam = new Parameter("unsuppress", Boolean.FALSE);
            _unsupressParam.getProperties().setLabel("Show all suppressed tips and messages again");/*I18N*/

            Parameter param;

            param = new Parameter(PROPERTY_KEY_LOW_MEMORY_LIMIT, 20);
            param.getProperties().setLabel("On low memory, warn if free RAM falls below: "); /*I18N*/
            param.getProperties().setPhysicalUnit("M"); /*I18N*/
            param.getProperties().setMinValue(0);
            param.getProperties().setMaxValue(500);
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_AUTO_SHOW_NEW_BANDS, Boolean.TRUE);
            param.getProperties().setLabel("Open image view for new (virtual) bands"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_AUTO_SHOW_NAVIGATION, Boolean.TRUE);
            param.getProperties().setLabel("Show navigation window when image views are opened"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PixelInfoView.PROPERTY_KEY_SHOW_ONLY_DISPLAYED_BAND_PIXEL_VALUES,
                                  PixelInfoView.PROPERTY_DEFAULT_SHOW_DISPLAYED_BAND_PIXEL_VALUES);
            param.getProperties().setLabel("Show only pixel values of loaded or displayed bands"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_VERSION_CHECK_ENABLED, Boolean.TRUE);
            final String labelText = String.format("Check for new version on %s start", getApp().getAppName());
            param.getProperties().setLabel(labelText); /*I18N*/
            configParams.addParameter(param);
        }

        private JPanel createPageUI() {
            Parameter param;
            GridBagConstraints gbc;

            //////////////////////////////////////////////////////////////////////////////////////
            // Display Settings

            JPanel displaySettingsPane = GridBagUtils.createPanel();
            displaySettingsPane.setBorder(UIUtils.createGroupBorder("Display Settings")); /*I18N*/
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_AUTO_SHOW_NAVIGATION);
            displaySettingsPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_AUTO_SHOW_NEW_BANDS);
            displaySettingsPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PixelInfoView.PROPERTY_KEY_SHOW_ONLY_DISPLAYED_BAND_PIXEL_VALUES);
            displaySettingsPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            //////////////////////////////////////////////////////////////////////////////////////
            // Memory Management

            JPanel memorySettingsPane = GridBagUtils.createPanel();
            memorySettingsPane.setBorder(UIUtils.createGroupBorder("Memory Management")); /*I18N*/
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");
            gbc.gridy++;

            gbc.insets.top = _LINE_INSET_TOP;

            param = getConfigParam(PROPERTY_KEY_LOW_MEMORY_LIMIT);
            GridBagUtils.addToPanel(memorySettingsPane, param.getEditor().getLabelComponent(), gbc, "weightx=0");
            GridBagUtils.addToPanel(memorySettingsPane, param.getEditor().getEditorComponent(), gbc, "weightx=1");
            GridBagUtils.addToPanel(memorySettingsPane, param.getEditor().getPhysUnitLabelComponent(), gbc,
                                    "weightx=0");
            gbc.gridy++;

            //////////////////////////////////////////////////////////////////////////////////////
            // Other Settings

            JPanel otherSettingsPane = GridBagUtils.createPanel();
            otherSettingsPane.setBorder(UIUtils.createGroupBorder("Message Settings")); /*I18N*/
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_VERSION_CHECK_ENABLED);
            otherSettingsPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            otherSettingsPane.add(_unsupressParam.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            //////////////////////////////////////////////////////////////////////////////////////
            // All together

            JPanel pageUI = GridBagUtils.createPanel();
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");

            pageUI.add(displaySettingsPane, gbc);
            gbc.gridy++;
            gbc.insets.top = _LINE_INSET_TOP;

            pageUI.add(memorySettingsPane, gbc);
            gbc.gridy++;
            pageUI.add(otherSettingsPane, gbc);
            gbc.gridy++;

            return createPageUIContentPane(pageUI);
        }

        @Override
        public void onOK() {
            if ((Boolean) _unsupressParam.getValue()) {
                _suppressibleOptionPane.unSuppressDialogs();
            }
        }

        @Override
        public void updatePageUI() {
            boolean supressed = _suppressibleOptionPane.areDialogsSuppressed();
            _unsupressParam.setUIEnabled(supressed);
        }
    }

    public static class AppearancePage extends DefaultConfigPage {

        private static final String PROPERTY_KEY_APP_UI_LAF_NAME = PROPERTY_KEY_APP_UI_LAF + ".name";

        public AppearancePage() {
            setTitle("UI Appearance"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    updatePageUI();
                }
            };

            Parameter param;

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            param = new Parameter(PROPERTY_KEY_APP_UI_USE_SYSTEM_FONT_SETTINGS, Boolean.TRUE);
            param.getProperties().setLabel("Use system font settings");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_APP_UI_FONT_NAME, "SansSerif");
            param.getProperties().setValueSet(ge.getAvailableFontFamilyNames());
            param.getProperties().setValueSetBound(true);
            param.getProperties().setLabel("Font name");
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_APP_UI_FONT_SIZE, 9);
            param.getProperties().setValueSet(new String[]{"8", "9", "10", "11", "12", "13", "14", "15", "16"});
            param.getProperties().setValueSetBound(false);
            param.getProperties().setLabel("Font size");
            configParams.addParameter(param);

            String[] lafNames = getAvailableLafNames();
            param = new Parameter(PROPERTY_KEY_APP_UI_LAF_NAME, lafNames[0]);
            param.getProperties().setValueSetBound(true);
            param.getProperties().setValueSet(lafNames);
            param.getProperties().setLabel("Look and feel name");
            configParams.addParameter(param);
        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        private JPanel createPageUI() {
            Parameter param;

            // Font
            JPanel fontPane = GridBagUtils.createPanel();
            fontPane.setBorder(UIUtils.createGroupBorder("UI Font")); /*I18N*/
            GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST");
            gbc.gridy = 0;
            gbc.insets.bottom = 10;

            param = getConfigParam(PROPERTY_KEY_APP_UI_USE_SYSTEM_FONT_SETTINGS);
            gbc.weightx = 0;
            gbc.gridwidth = 2;
            fontPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridwidth = 1;
            gbc.gridy++;
            gbc.insets.bottom = 4;

            param = getConfigParam(PROPERTY_KEY_APP_UI_FONT_NAME);
            gbc.weightx = 0;
            fontPane.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            fontPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_APP_UI_FONT_SIZE);
            gbc.weightx = 0;
            fontPane.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            fontPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            // Look and Feel
            JPanel lafPane = GridBagUtils.createPanel();
            lafPane.setBorder(UIUtils.createGroupBorder("UI Look and Feel")); /*I18N*/
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST");
            gbc.gridy = 0;
            gbc.insets.bottom = 10;

            param = getConfigParam(PROPERTY_KEY_APP_UI_LAF_NAME);
            gbc.weightx = 0;
            lafPane.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            lafPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            //////////////////////////////////////////////////////////////////////////////////////
            // All together
            JPanel pageUI = GridBagUtils.createPanel();
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");

            pageUI.add(fontPane, gbc);
            gbc.gridy++;
            gbc.insets.top = _LINE_INSET_TOP;

            pageUI.add(lafPane, gbc);

            return createPageUIContentPane(pageUI);
        }

        @Override
        public void updatePageUI() {
            Parameter param1 = getConfigParam(PROPERTY_KEY_APP_UI_USE_SYSTEM_FONT_SETTINGS);
            boolean enabled = !(Boolean) param1.getValue();
            getConfigParam(PROPERTY_KEY_APP_UI_FONT_NAME).setUIEnabled(enabled);
            getConfigParam(PROPERTY_KEY_APP_UI_FONT_SIZE).setUIEnabled(enabled);
        }

        @Override
        public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
            propertyMap = super.getConfigParamValues(propertyMap);
            final String lafName = (String) getConfigParams().getParameter(PROPERTY_KEY_APP_UI_LAF_NAME).getValue();
            propertyMap.setPropertyString(PROPERTY_KEY_APP_UI_LAF, getLafClassName(lafName));
            return propertyMap;
        }

        @Override
        public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {
            String lafClassName = propertyMap.getPropertyString(PROPERTY_KEY_APP_UI_LAF,
                                                                getDefaultLafClassName());
            getConfigParams().getParameter(PROPERTY_KEY_APP_UI_LAF_NAME).setValue(getLafName(lafClassName),
                                                                                  errorHandler);
            super.setConfigParamValues(propertyMap, errorHandler);
        }

        private String[] getAvailableLafNames() {
            UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
            ArrayList<String> lafNames = new ArrayList<String>(installedLookAndFeels.length);
            for (UIManager.LookAndFeelInfo installedLaf : installedLookAndFeels) {
                final String lafName = installedLaf.getName();
                // This should fix a problem occurring in JIDE 3.3.5 with the GTKLookAndFeel (nf, 2012-03-02)
                if (!"com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(lafName)) {
                    lafNames.add(lafName);
                }
            }
            return lafNames.toArray(new String[lafNames.size()]);
        }

        private String getLafClassName(String name) {
            UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo installedLookAndFeel : installedLookAndFeels) {
                if (installedLookAndFeel.getName().equalsIgnoreCase(name)) {
                    return installedLookAndFeel.getClassName();
                }
            }
            return getDefaultLafClassName();
        }

        private String getLafName(String lafClassName) {
            UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo installedLookAndFeel : installedLookAndFeels) {
                if (installedLookAndFeel.getClassName().equals(lafClassName)) {
                    return installedLookAndFeel.getName();
                }
            }
            return getDefaultLafName();
        }

        private String getDefaultLafName() {
            return UIManager.getLookAndFeel().getName();
        }

        private String getDefaultLafClassName() {
            return UIManager.getLookAndFeel().getClass().getName();
        }
    }



    public static class ColorPalettesConfigPage extends DefaultConfigPage {


        public ColorPalettesConfigPage() {
            setTitle("Color Palettes"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    updatePageUI();
                }
            };

            Parameter param;

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();


            param = new Parameter(ColorPaletteSchemes.PROPERTY_NAME_PALETTES_COLOR_BLIND_ENABLED, ColorPaletteSchemes.DEFAULT_PALETTES_COLOR_BLIND_ENABLED);
            param.getProperties().setLabel("Use color-blind compliant palettes as scheme default");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);



        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        private JPanel createPageUI() {
            Parameter param;

            // Font
            JPanel fontPane = GridBagUtils.createPanel();
            fontPane.setBorder(UIUtils.createGroupBorder("Color Palette Schemes")); /*I18N*/
            GridBagConstraints gbcColorPaletteSchemes = GridBagUtils.createConstraints("");
            gbcColorPaletteSchemes.gridy = 0;

            param = getConfigParam(ColorPaletteSchemes.PROPERTY_NAME_PALETTES_COLOR_BLIND_ENABLED);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;




            JPanel contentsPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcContents = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST");
            gbcContents.gridx = 0;
            gbcContents.gridy = 0;
            gbcContents.insets.bottom = 10;
            contentsPanel.add(fontPane, gbcContents);





            //////////////////////////////////////////////////////////////////////////////////////
            // All together
            JPanel pageUI = GridBagUtils.createPanel();
            GridBagConstraints gbcMain = GridBagUtils.createConstraints("fill=NONE, anchor=WEST, weightx=1, gridy=1");
            gbcMain.insets.bottom = 4;

            pageUI.add(contentsPanel, gbcMain);
//
//            pageUI.add(fontPane, gbcMain);
//            gbcMain.gridy++;
//            pageUI.add(fontPane2, gbcMain);
//            gbcMain.gridy++;
//            pageUI.add(fontPane3, gbcMain);
            gbcMain.insets.top = _LINE_INSET_TOP;

            return createPageUIContentPane(pageUI);
        }


        @Override
        public void updatePageUI() {
//            Parameter param1 = getConfigParam(PROPERTY_KEY_APP_USE_COLOR_BLIND_PALETTES);
//            boolean enabled = !(Boolean) param1.getValue();
//            getConfigParam(PROPERTY_KEY_APP_UI_FONT_NAME).setUIEnabled(enabled);
//            getConfigParam(PROPERTY_KEY_APP_UI_FONT_SIZE).setUIEnabled(enabled);
//            if (ColorBarParamInfo.HORIZONTAL_STR.equals(getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION))) {
//                getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION).setUIEnabled(true);
//                getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION).setUIEnabled(false);
//            } else {
//                getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION).setUIEnabled(false);
//                getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION).setUIEnabled(true);
//            }

        }

        @Override
        public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
            propertyMap = super.getConfigParamValues(propertyMap);
            return propertyMap;
        }

        @Override
        public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {

            super.setConfigParamValues(propertyMap, errorHandler);
        }


    }



    public static class ColorBarConfigPage extends DefaultConfigPage {

        private boolean resettingDefaults = false;

        public ColorBarConfigPage() {
            setTitle("Color Bar"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    if (!resettingDefaults) {
                        updatePageUI();
                    }
                }
            };
            final ParamChangeListener paramChangeListenerResetToDefaults = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                  if ((Boolean) getConfigParam(ExportLegendImageAction.RESET_TO_DEFAULTS_PARAM_STR).getValue()) {
                      resettingDefaults = true;
                      resetToDefaults();
                      resettingDefaults = false;
                  }
                }
            };
            Parameter param;

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            param = new Parameter(ExportLegendImageAction.RESET_TO_DEFAULTS_PARAM_STR, false);
            param.getProperties().setLabel("RESTORE DEFAULTS (Color Bar Preferences)");
            param.addParamChangeListener(paramChangeListenerResetToDefaults);
            configParams.addParameter(param);




            param = new Parameter(ImageLegend.PROPERTY_NAME_COLORBAR_TITLE_OVERRIDE, ImageLegend.DEFAULT_COLORBAR_TITLE_OVERRIDE);
            param.getProperties().setLabel("Allow color bar title override from scheme definition");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ImageLegend.PROPERTY_NAME_COLORBAR_LABELS_OVERRIDE, ImageLegend.DEFAULT_COLORBAR_LABELS_OVERRIDE);
            param.getProperties().setLabel("Allow color bar labels override from scheme definition");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ImageLegend.PROPERTY_NAME_COLORBAR_ALLOW_RESET, ImageLegend.DEFAULT_COLORBAR_ALLOW_RESET);
            param.getProperties().setLabel("Allow automated color bar reset on any scheme change");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);



            param = new Parameter(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION, ColorBarParamInfo.DEFAULT_HORIZONTAL_LOCATION);
            param.getProperties().setLabel("Location & Alignment (if horizontal)");
            param.getProperties().setValueSet(ColorBarParamInfo.getHorizontalLocationArray());
            param.getProperties().setValueSetBound(true);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION, ColorBarParamInfo.DEFAULT_VERTICAL_LOCATION);
            param.getProperties().setLabel("Location & Alignment (if vertical)");
            param.getProperties().setValueSet(ColorBarParamInfo.getVerticalLocationArray());
            param.getProperties().setValueSetBound(true);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ExportLegendImageAction.SHOW_TITLE_PARAM_STR, ColorBarParamInfo.DEFAULT_SHOW_TITLE_ENABLED);
            param.getProperties().setLabel("Show Title");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ExportLegendImageAction.TRANSPARENCY_PARAM_STR, ColorBarParamInfo.DEFAULT_BACKGROUND_TRANSPARENCY);
            param.getProperties().setLabel("Transparency of Backdrop");
            param.getProperties().setMinValue(0.0f);
            param.getProperties().setMaxValue(1.0f);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ExportLegendImageAction.ORIENTATION_PARAM_STR, ColorBarParamInfo.DEFAULT_ORIENTATION);
            param.getProperties().setLabel("Orientation");
            param.getProperties().setValueSet(new String[]{ColorBarParamInfo.HORIZONTAL_STR, ColorBarParamInfo.VERTICAL_STR});
            param.getProperties().setValueSetBound(true);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION, ColorBarParamInfo.DEFAULT_INSIDE_OUTSIDE_LOCATION);
            param.getProperties().setLabel("Location (Inside/Outside)");
            param.getProperties().setValueSet(new String[]{ColorBarParamInfo.LOCATION_INSIDE_STR, ColorBarParamInfo.LOCATION_OUTSIDE_STR});
            param.getProperties().setValueSetBound(true);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ExportLegendImageAction.FOREGROUND_COLOR_PARAM_STR, ColorBarParamInfo.DEFAULT_FOREGROUND_COLOR);
            param.getProperties().setLabel("Text/Tick color"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ExportLegendImageAction.BACKGROUND_COLOR_PARAM_STR, ColorBarParamInfo.DEFAULT_BACKGROUND_COLOR);
            param.getProperties().setLabel("Backdrop color"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ExportLegendImageAction.LAYER_SCALING_PARAM_STR, ColorBarParamInfo.DEFAULT_LAYER_SCALING);
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setLabel("Size Scaling"); /*I18N*/
            param.getProperties().setMinValue(5.0);
            param.getProperties().setMaxValue(150.0);
            configParams.addParameter(param);

        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        private JPanel createPageUI() {
            Parameter param;



            JPanel colorBarPane = GridBagUtils.createPanel();
            colorBarPane.setBorder(UIUtils.createGroupBorder("Settings")); /*I18N*/
            GridBagConstraints gbcColorBar = GridBagUtils.createConstraints("");
            gbcColorBar.gridy = 0;



            param = getConfigParam(ExportLegendImageAction.ORIENTATION_PARAM_STR);
            addParamToPane(colorBarPane, param, gbcColorBar);
            gbcColorBar.gridy++;



            param = getConfigParam(ExportLegendImageAction.SHOW_TITLE_PARAM_STR);
            addParamToPane(colorBarPane, param, gbcColorBar);
            gbcColorBar.gridy++;


            param = getConfigParam(ExportLegendImageAction.TRANSPARENCY_PARAM_STR);
            addParamToPane(colorBarPane, param, gbcColorBar);
            gbcColorBar.gridy++;



            param = getConfigParam(ExportLegendImageAction.FOREGROUND_COLOR_PARAM_STR);
            addParamToPane(colorBarPane, param, gbcColorBar);
            gbcColorBar.gridy++;

            param = getConfigParam(ExportLegendImageAction.BACKGROUND_COLOR_PARAM_STR);
            addParamToPane(colorBarPane, param, gbcColorBar);
            gbcColorBar.gridy++;



            JPanel layerPane = GridBagUtils.createPanel();
            layerPane.setBorder(UIUtils.createGroupBorder("Layer Specific Settings")); /*I18N*/
            GridBagConstraints gbcLayer = GridBagUtils.createConstraints("");
            gbcLayer.gridy = 0;

            param = getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION);
            addParamToPane(layerPane, param, gbcLayer);
            gbcLayer.gridy++;

            param = getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION);
            addParamToPane(layerPane, param, gbcLayer);
            gbcLayer.gridy++;

            param = getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION);
            addParamToPane(layerPane, param, gbcLayer);
            gbcLayer.gridy++;

            param = getConfigParam(ExportLegendImageAction.LAYER_SCALING_PARAM_STR);
            addParamToPane(layerPane, param, gbcLayer);




            JPanel schemesPane = GridBagUtils.createPanel();
            schemesPane.setBorder(UIUtils.createGroupBorder("Scheme Configurations")); /*I18N*/
            GridBagConstraints gbcSchemes = GridBagUtils.createConstraints("");
            gbcSchemes.gridy = 0;

            param = getConfigParam(ImageLegend.PROPERTY_NAME_COLORBAR_TITLE_OVERRIDE);
            addParamToPane(schemesPane, param, gbcSchemes);
            gbcSchemes.gridy++;


            param = getConfigParam(ImageLegend.PROPERTY_NAME_COLORBAR_LABELS_OVERRIDE);
            addParamToPane(schemesPane, param, gbcSchemes);
            gbcSchemes.gridy++;

            param = getConfigParam(ImageLegend.PROPERTY_NAME_COLORBAR_ALLOW_RESET);
            addParamToPane(schemesPane, param, gbcSchemes);
            gbcSchemes.gridy++;


            JPanel resetPane = GridBagUtils.createPanel();
            GridBagConstraints gbcReset = GridBagUtils.createConstraints("");
            gbcReset.gridy = 0;
            param = getConfigParam(ExportLegendImageAction.RESET_TO_DEFAULTS_PARAM_STR);
            addParamToPane(resetPane, param, gbcReset);



            JPanel contentsPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcContents = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST");
            gbcContents.gridx = 0;
            gbcContents.gridy = 0;
            gbcContents.insets.bottom = 10;
            contentsPanel.add(colorBarPane, gbcContents);
            gbcContents.gridy++;
            contentsPanel.add(layerPane, gbcContents);
            gbcContents.gridy++;
            contentsPanel.add(schemesPane, gbcContents);
            gbcContents.gridy++;
            contentsPanel.add(resetPane, gbcContents);





            //////////////////////////////////////////////////////////////////////////////////////
            // All together
            JPanel pageUI = GridBagUtils.createPanel();
            GridBagConstraints gbcMain = GridBagUtils.createConstraints("fill=NONE, anchor=WEST, weightx=1, gridy=1");
            gbcMain.insets.bottom = 4;

            pageUI.add(contentsPanel, gbcMain);

            return createPageUIContentPane(pageUI);
        }


        public void resetToDefaults() {

            ParamExceptionHandler errorHandler = new ParamExceptionHandler() {
                @Override
                public boolean handleParamException(ParamException e) {
                    return false;
                }
            };

            getConfigParam(ImageLegend.PROPERTY_NAME_COLORBAR_TITLE_OVERRIDE).setValue(ImageLegend.DEFAULT_COLORBAR_TITLE_OVERRIDE, errorHandler);
            getConfigParam(ImageLegend.PROPERTY_NAME_COLORBAR_LABELS_OVERRIDE).setValue(ImageLegend.DEFAULT_COLORBAR_LABELS_OVERRIDE, errorHandler);
            getConfigParam(ImageLegend.PROPERTY_NAME_COLORBAR_ALLOW_RESET).setValue(ImageLegend.DEFAULT_COLORBAR_ALLOW_RESET, errorHandler);
            getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_HORIZONTAL_LOCATION).setValue(ColorBarParamInfo.DEFAULT_HORIZONTAL_LOCATION, errorHandler);
            getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_VERTICAL_LOCATION).setValue(ColorBarParamInfo.DEFAULT_VERTICAL_LOCATION, errorHandler);
            getConfigParam(ExportLegendImageAction.SHOW_TITLE_PARAM_STR).setValue(ColorBarParamInfo.DEFAULT_SHOW_TITLE_ENABLED, errorHandler);
            getConfigParam(ExportLegendImageAction.TRANSPARENCY_PARAM_STR).setValue(ColorBarParamInfo.DEFAULT_BACKGROUND_TRANSPARENCY, errorHandler);
            getConfigParam(ExportLegendImageAction.ORIENTATION_PARAM_STR).setValue(ColorBarParamInfo.DEFAULT_ORIENTATION, errorHandler);
            getConfigParam(ExportLegendImageAction.PARAMETER_NAME_COLORBAR_INSIDE_OUTSIDE_LOCATION).setValue(ColorBarParamInfo.DEFAULT_INSIDE_OUTSIDE_LOCATION, errorHandler);
            getConfigParam(ExportLegendImageAction.FOREGROUND_COLOR_PARAM_STR).setValue(ColorBarParamInfo.DEFAULT_FOREGROUND_COLOR, errorHandler);
            getConfigParam(ExportLegendImageAction.BACKGROUND_COLOR_PARAM_STR).setValue(ColorBarParamInfo.DEFAULT_BACKGROUND_COLOR, errorHandler);
            getConfigParam(ExportLegendImageAction.LAYER_SCALING_PARAM_STR).setValue(ColorBarParamInfo.DEFAULT_LAYER_SCALING, errorHandler);


        }

        @Override
        public void updatePageUI() {
            ParamExceptionHandler errorHandler = new ParamExceptionHandler() {
                @Override
                public boolean handleParamException(ParamException e) {
                    return false;
                }
            };

            getConfigParam(ExportLegendImageAction.RESET_TO_DEFAULTS_PARAM_STR).setValue(false, errorHandler);
        }


        @Override
        public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
            propertyMap = super.getConfigParamValues(propertyMap);
            return propertyMap;
        }

        @Override
        public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {

            super.setConfigParamValues(propertyMap, errorHandler);
        }


    }






    public static class StatisticsPage extends DefaultConfigPage {

        private boolean resettingDefaults = false;

        public StatisticsPage() {
            setTitle("Statistics"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    if (!resettingDefaults) {
                        updatePageUI();
                    }
                }
            };
            final ParamChangeListener paramChangeListenerResetToDefaults = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    if ((Boolean) getConfigParam(StatisticsToolView.PARAM_KEY_RESET_TO_DEFAULTS).getValue()) {
                        resettingDefaults = true;
                        resetToDefaults();
                        resettingDefaults = false;
                    }
                }
            };
            Parameter param;

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();


            param = new Parameter(StatisticsToolView.PARAM_KEY_RESET_TO_DEFAULTS, false);
            param.getProperties().setLabel("RESTORE DEFAULTS (Statistics Preferences)");
            param.addParamChangeListener(paramChangeListenerResetToDefaults);
            configParams.addParameter(param);



            param = new Parameter(StatisticsToolView.PARAM_KEY_HISTOGRAM_PLOT_ENABLED, StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_PLOT_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_HISTOGRAM_PLOT_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);



            param = new Parameter(StatisticsToolView.PARAM_KEY_PERCENT_PLOT_ENABLED, StatisticsToolView.PARAM_DEFVAL_PERCENT_PLOT_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_PERCENT_PLOT_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(StatisticsToolView.PARAM_KEY_STATS_LIST_ENABLED, StatisticsToolView.PARAM_DEFVAL_STATS_LIST_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_STATS_LIST_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(StatisticsToolView.PARAM_KEY_STATS_SPREADSHEET_ENABLED, StatisticsToolView.PARAM_DEFVAL_STATS_SPREADSHEET_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_STATS_SPREADSHEET_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_PERCENT_THRESHOLDS, StatisticsToolView.PARAM_DEFVAL_PERCENT_THRESHOLDS);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_PERCENT_THRESHOLDS);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_NUM_BINS, StatisticsToolView.PARAM_DEFVAL_NUM_BINS);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_NUM_BINS);
            param.getProperties().setMinValue(StatisticsToolView.PARAM_MINVAL_NUM_BINS);
            param.getProperties().setMaxValue(StatisticsToolView.PARAM_MAXVAL_NUM_BINS);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_PLOTS_THRESH_DOMAIN_LOW, StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_LOW);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_PLOTS_THRESH_DOMAIN_LOW);
            param.getProperties().setMinValue(StatisticsToolView.PARAM_MINVAL_PLOTS_THRESH_DOMAIN_LOW);
            param.getProperties().setMaxValue(StatisticsToolView.PARAM_MAXVAL_PLOTS_THRESH_DOMAIN_LOW);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(StatisticsToolView.PARAM_KEY_PLOTS_THRESH_DOMAIN_HIGH, StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_HIGH);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_PLOTS_THRESH_DOMAIN_HIGH);
            param.getProperties().setMinValue(StatisticsToolView.PARAM_MINVAL_PLOTS_THRESH_DOMAIN_HIGH);
            param.getProperties().setMaxValue(StatisticsToolView.PARAM_MAXVAL_PLOTS_THRESH_DOMAIN_HIGH);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);



            param = new Parameter(StatisticsToolView.PARAM_KEY_FILE_METADATA_ENABLED, StatisticsToolView.PARAM_DEFVAL_FILE_METADATA_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_FILE_METADATA_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_PROJECTION_PARAMETERS_METADATA_ENABLED, StatisticsToolView.PARAM_DEFVAL_PROJECTION_PARAMETERS_METADATA_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_PROJECTION_PARAMETERS_METADATA_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_MASK_METADATA_ENABLED, StatisticsToolView.PARAM_DEFVAL_MASK_METADATA_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_MASK_METADATA_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_BAND_METADATA_ENABLED, StatisticsToolView.PARAM_DEFVAL_BAND_METADATA_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_BAND_METADATA_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_TIME_SERIES_METADATA_ENABLED, StatisticsToolView.PARAM_DEFVAL_TIME_SERIES_METADATA_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_TIME_SERIES_METADATA_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_TIME_METADATA_ENABLED, StatisticsToolView.PARAM_DEFVAL_TIME_METADATA_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_TIME_METADATA_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_MEDIAN_ENABLED, StatisticsToolView.PARAM_DEFVAL_MEDIAN_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_MEDIAN_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_HISTOGRAM_STATS_ENABLED, StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_STATS_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_HISTOGRAM_STATS_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_SPREADSHEET_DECIMAL_PLACES, StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_DECIMAL_PLACES);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_SPREADSHEET_DECIMAL_PLACES);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_SPREADSHEET_COL_WIDTH, StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_COL_WIDTH);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_SPREADSHEET_COL_WIDTH);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(StatisticsToolView.PARAM_KEY_COL_BREAKS_ENABLED, StatisticsToolView.PARAM_DEFVAL_COL_BREAKS_ENABLED);
            param.getProperties().setLabel(StatisticsToolView.PARAM_LABEL_COL_BREAKS_ENABLED);
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);






        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        private JPanel createPageUI() {
            Parameter param;

            JPanel binsPane = GridBagUtils.createPanel();
            binsPane.setBorder(UIUtils.createGroupBorder("Bins")); /*I18N*/
            GridBagConstraints gbcBins = GridBagUtils.createConstraints("");
            gbcBins.gridy = 0;

            JPanel fieldsPane = GridBagUtils.createPanel();
            fieldsPane.setBorder(UIUtils.createGroupBorder("Fields")); /*I18N*/
            GridBagConstraints gbcFields = GridBagUtils.createConstraints("");
            gbcFields.gridy = 0;


            JPanel textPane = GridBagUtils.createPanel();
            textPane.setBorder(UIUtils.createGroupBorder("Text")); /*I18N*/
            GridBagConstraints gbcText = GridBagUtils.createConstraints("");
            gbcText.gridy = 0;


            JPanel plotsPane = GridBagUtils.createPanel();
            plotsPane.setBorder(UIUtils.createGroupBorder("Plots")); /*I18N*/
            GridBagConstraints gbcPlots = GridBagUtils.createConstraints("");
            gbcPlots.gridy = 0;


            JPanel viewPane = GridBagUtils.createPanel();
            viewPane.setBorder(UIUtils.createGroupBorder("View")); /*I18N*/
            GridBagConstraints gbcView = GridBagUtils.createConstraints("");
            gbcView.gridy = 0;








            param = getConfigParam(StatisticsToolView.PARAM_KEY_HISTOGRAM_PLOT_ENABLED);
            addParamToPane(viewPane, param, gbcView);
            gbcView.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_PERCENT_PLOT_ENABLED);
            addParamToPane(viewPane, param, gbcView);
            gbcView.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_STATS_LIST_ENABLED);
            addParamToPane(viewPane, param, gbcView);
            gbcView.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_STATS_SPREADSHEET_ENABLED);
            addParamToPane(viewPane, param, gbcView);
            gbcView.gridy++;



            param = getConfigParam(StatisticsToolView.PARAM_KEY_SPREADSHEET_DECIMAL_PLACES);
            addParamToPane(textPane, param, gbcText);
            gbcText.gridy++;



            GridBagConstraints gbcDivider = GridBagUtils.createConstraints("");
            gbcDivider.gridy = gbcText.gridy;

            gbcDivider.fill = GridBagConstraints.HORIZONTAL;
            gbcDivider.insets.right = 10;
            gbcDivider.insets.left = 10;
            gbcDivider.insets.top = -5;
            gbcDivider.insets.bottom = 5;
            gbcDivider.gridwidth = 2;
            gbcDivider.gridx = 0;
            textPane.add(new JSeparator(), gbcDivider);
            gbcText.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_SPREADSHEET_COL_WIDTH);
            addParamToPane(textPane, param, gbcText);
            gbcText.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_COL_BREAKS_ENABLED);
            addParamToPane(textPane, param, gbcText);
            gbcText.gridy++;



            param = getConfigParam(StatisticsToolView.PARAM_KEY_NUM_BINS);
            addParamToPane(binsPane, param, gbcBins);
            gbcBins.gridy++;




            param = getConfigParam(StatisticsToolView.PARAM_KEY_HISTOGRAM_STATS_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;


            param = getConfigParam(StatisticsToolView.PARAM_KEY_MEDIAN_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;



            param = getConfigParam(StatisticsToolView.PARAM_KEY_PERCENT_THRESHOLDS);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;


            GridBagConstraints gbcDivider2 = GridBagUtils.createConstraints("");
            gbcDivider2.gridy = gbcFields.gridy;

            gbcDivider2.fill = GridBagConstraints.HORIZONTAL;
            gbcDivider2.insets.right = 10;
            gbcDivider2.insets.left = 10;
            gbcDivider2.insets.top = -5;
            gbcDivider2.insets.bottom = 5;
            gbcDivider2.gridwidth = 2;
            gbcDivider2.gridx = 0;
            fieldsPane.add(new JSeparator(), gbcDivider2);
            gbcFields.gridy++;


            param = getConfigParam(StatisticsToolView.PARAM_KEY_BAND_METADATA_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_FILE_METADATA_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_MASK_METADATA_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;


            param = getConfigParam(StatisticsToolView.PARAM_KEY_PROJECTION_PARAMETERS_METADATA_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;


            param = getConfigParam(StatisticsToolView.PARAM_KEY_TIME_METADATA_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_TIME_SERIES_METADATA_ENABLED);
            addParamToPane(fieldsPane, param, gbcFields);
            gbcFields.gridy++;









            param = getConfigParam(StatisticsToolView.PARAM_KEY_PLOTS_THRESH_DOMAIN_LOW);
            addParamToPane(plotsPane, param, gbcPlots);
            gbcPlots.gridy++;

            param = getConfigParam(StatisticsToolView.PARAM_KEY_PLOTS_THRESH_DOMAIN_HIGH);
            addParamToPane(plotsPane, param, gbcPlots);
            gbcPlots.gridy++;



//            JPanel resetPane = GridBagUtils.createPanel();
//            GridBagConstraints gbcReset = GridBagUtils.createConstraints("");
//            gbcReset.gridy = 0;
//            param = getConfigParam(StatisticsToolView.PARAM_KEY_RESET_TO_DEFAULTS);
//            addParamToPane(resetPane, param, gbcReset);





            JPanel leftPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcLeft = GridBagUtils.createConstraints("");
//            leftPanel.setBorder(UIUtils.createGroupBorder("")); /*I18N*/
            gbcLeft.gridx = 0;
            gbcLeft.gridy = 0;
            gbcLeft.weightx = 0.0;
            gbcLeft.fill = GridBagConstraints.HORIZONTAL;
            gbcLeft.anchor = GridBagConstraints.NORTHWEST;
            gbcLeft.insets.bottom=5;
            gbcLeft.insets.top=0;



            JPanel rightPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcRight = GridBagUtils.createConstraints("");
//            rightPanel.setBorder(UIUtils.createGroupBorder("")); /*I18N*/
            gbcRight.gridx = 0;
            gbcRight.gridy = 0;
            gbcRight.weightx = 0.0;
            gbcRight.fill = GridBagConstraints.HORIZONTAL;
            gbcRight.anchor = GridBagConstraints.NORTHWEST;
            gbcRight.insets.bottom=5;
            gbcRight.insets.top=0;



            gbcLeft.weighty = 0;
            leftPanel.add(binsPane, gbcLeft);
            gbcLeft.gridy++;
            leftPanel.add(fieldsPane, gbcLeft);
            // Add filler panel at bottom which expands as needed to force all components within this panel to the top
            gbcLeft = GridBagUtils.restoreConstraints(gbcLeft);
            gbcLeft.weighty = 1;
            gbcLeft.gridy += 1;
            gbcLeft.fill = GridBagConstraints.BOTH;
            leftPanel.add(new JPanel(), gbcLeft);


            gbcRight.weighty = 0;
            rightPanel.add(textPane, gbcRight);
            gbcRight.gridy++;
            rightPanel.add(plotsPane, gbcRight);
            gbcRight.gridy++;
            rightPanel.add(viewPane, gbcRight);
            // Add filler panel at bottom which expands as needed to force all components within this panel to the top
            gbcRight = GridBagUtils.restoreConstraints(gbcRight);
            gbcRight.weighty = 1;
            gbcRight.gridy += 1;
            gbcRight.fill = GridBagConstraints.BOTH;
            rightPanel.add(new JPanel(), gbcRight);





            JPanel resetPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcReset = GridBagUtils.createConstraints("");
            gbcReset.gridx = 0;
            gbcReset.gridy = 0;
            gbcReset.weightx = 0.0;
            gbcReset.fill = GridBagConstraints.HORIZONTAL;
            gbcReset.anchor = GridBagConstraints.NORTHWEST;
            gbcReset.insets.bottom=0;
            gbcReset.insets.top=0;


            param = getConfigParam(StatisticsToolView.PARAM_KEY_RESET_TO_DEFAULTS);
            addParamToPane(resetPanel, param, gbcReset);
            gbcReset.gridy++;



            JPanel leftFullPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcLeftFull = GridBagUtils.createConstraints("");
            gbcLeftFull.gridx = 0;
            gbcLeftFull.gridy = 0;
            gbcLeftFull.weightx = 0.0;
            gbcLeftFull.fill = GridBagConstraints.HORIZONTAL;
            gbcLeftFull.anchor = GridBagConstraints.NORTHWEST;
            gbcLeftFull.insets.bottom=0;
            gbcLeftFull.insets.top=0;
            leftFullPanel.add(leftPanel, gbcLeftFull);
            gbcLeftFull.gridy++;
            gbcLeftFull.insets.top=10;
            leftFullPanel.add(resetPanel, gbcLeftFull);



            JPanel groupPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcGroup = GridBagUtils.createConstraints("");
            gbcGroup.gridx = 0;
            gbcGroup.gridy = 0;
            gbcGroup.weightx = 0.5;
            gbcGroup.weighty = 1.0;
            gbcGroup.fill = GridBagConstraints.NONE;
            gbcGroup.anchor = GridBagConstraints.NORTHWEST;
            gbcGroup.insets.bottom=10;
            gbcGroup.insets.right=20;
            groupPanel.add(leftFullPanel, gbcGroup);
            gbcGroup.gridx++;
            gbcGroup.weightx = 0.5;
            gbcGroup.weighty = 1.0;
            gbcGroup.insets.right=0;
            gbcGroup.fill = GridBagConstraints.NONE;
//            gbcGroup.insets.top = _LINE_INSET_TOP;

            groupPanel.add(rightPanel, gbcGroup);


            return createPageUIContentPane(groupPanel);










//            JPanel contentsPanel = GridBagUtils.createPanel();
//            GridBagConstraints gbcContents = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST");
//            gbcContents.gridx = 0;
//            gbcContents.gridy = 0;
//            gbcContents.insets.bottom = 10;
//            contentsPanel.add(binsPane, gbcContents);
//            gbcContents.gridy++;
//            contentsPanel.add(fieldsPane, gbcContents);
//            gbcContents.gridy++;
//            contentsPanel.add(textPane, gbcContents);
//            gbcContents.gridy++;
//            contentsPanel.add(plotsPane, gbcContents);
//            gbcContents.gridy++;
//            contentsPanel.add(viewPane, gbcContents);
//            gbcContents.gridy++;
//            contentsPanel.add(resetPane, gbcContents);



//
//
//
//
//            //////////////////////////////////////////////////////////////////////////////////////
//            // All together
//            JPanel pageUI = GridBagUtils.createPanel();
//            GridBagConstraints gbcMain = GridBagUtils.createConstraints("fill=NONE, anchor=WEST, weightx=1, gridy=1");
//            gbcMain.insets.bottom = 4;
//
//            pageUI.add(contentsPanel, gbcMain);
//
//            return createPageUIContentPane(pageUI);
        }


        public void resetToDefaults() {

            ParamExceptionHandler errorHandler = new ParamExceptionHandler() {
                @Override
                public boolean handleParamException(ParamException e) {
                    return false;
                }
            };

            getConfigParam(StatisticsToolView.PARAM_KEY_HISTOGRAM_PLOT_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_PLOT_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_PERCENT_PLOT_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_PERCENT_PLOT_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_STATS_LIST_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_STATS_LIST_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_STATS_SPREADSHEET_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_STATS_SPREADSHEET_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_PERCENT_THRESHOLDS).setValue(StatisticsToolView.PARAM_DEFVAL_PERCENT_THRESHOLDS, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_NUM_BINS).setValue(StatisticsToolView.PARAM_DEFVAL_NUM_BINS, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_PLOTS_THRESH_DOMAIN_LOW).setValue(StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_LOW, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_PLOTS_THRESH_DOMAIN_HIGH).setValue(StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_HIGH, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_FILE_METADATA_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_FILE_METADATA_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_MASK_METADATA_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_MASK_METADATA_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_BAND_METADATA_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_BAND_METADATA_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_TIME_METADATA_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_TIME_METADATA_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_TIME_SERIES_METADATA_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_TIME_SERIES_METADATA_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_PROJECTION_PARAMETERS_METADATA_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_PROJECTION_PARAMETERS_METADATA_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_MEDIAN_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_MEDIAN_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_HISTOGRAM_STATS_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_STATS_ENABLED, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_SPREADSHEET_DECIMAL_PLACES).setValue(StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_DECIMAL_PLACES, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_SPREADSHEET_COL_WIDTH).setValue(StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_COL_WIDTH, errorHandler);
            getConfigParam(StatisticsToolView.PARAM_KEY_COL_BREAKS_ENABLED).setValue(StatisticsToolView.PARAM_DEFVAL_COL_BREAKS_ENABLED, errorHandler);


        }

        @Override
        public void updatePageUI() {
            ParamExceptionHandler errorHandler = new ParamExceptionHandler() {
                @Override
                public boolean handleParamException(ParamException e) {
                    return false;
                }
            };

            getConfigParam(StatisticsToolView.PARAM_KEY_RESET_TO_DEFAULTS).setValue(false, errorHandler);
        }


        @Override
        public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
            propertyMap = super.getConfigParamValues(propertyMap);
            return propertyMap;
        }

        @Override
        public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {

            super.setConfigParamValues(propertyMap, errorHandler);
        }


    }







    public static class RepositoryConnectionConfigPage extends DefaultConfigPage {

        private ConnectionConfigData connectionConfigData;
        private ConnectionConfigPane connectionConfigPane;

        public RepositoryConnectionConfigPage() {
            setTitle("Module Repository"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
        }

        @Override
        protected void initPageUI() {
            connectionConfigData = new ConnectionConfigData();
            connectionConfigPane = new ConnectionConfigPane(connectionConfigData);
            setPageUI(createPageUIContentPane(connectionConfigPane));
        }

        @Override
        public void updatePageUI() {
            connectionConfigPane.updateUiState();
        }

        @Override
        public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
            propertyMap = super.getConfigParamValues(propertyMap);
            if (connectionConfigPane.validateUiState()) {
                connectionConfigPane.transferUiToConfigData();
                ShowModuleManagerAction.transferConnectionData(connectionConfigData, propertyMap);
            }
            return propertyMap;
        }

        @Override
        public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {
            ShowModuleManagerAction.transferConnectionData(propertyMap, connectionConfigData);
            connectionConfigPane.transferConfigDataToUi();
            super.setConfigParamValues(propertyMap, errorHandler);
        }
    }

    public static class GeolocationDisplayPage extends DefaultConfigPage {

        private Parameter paramOffsetX;
        private Parameter paramOffsetY;
        private JComponent visualizer;
        private Parameter paramShowDecimals;
        private Parameter paramGeolocationAsDecimal;

        public GeolocationDisplayPage() {
            setTitle("Geo-location Display"); /*I18N*/
        }

        @Override
        protected void initPageUI() {
            visualizer = createOffsetVisualizer();
            visualizer.setPreferredSize(new Dimension(60, 60));
            visualizer.setOpaque(true);
            visualizer.setBorder(BorderFactory.createLoweredBevelBorder());

            final TableLayout tableLayout = new TableLayout(3);
            tableLayout.setTableAnchor(TableLayout.Anchor.WEST);
            tableLayout.setTablePadding(4, 4);
            tableLayout.setTableFill(TableLayout.Fill.HORIZONTAL);

            final JPanel pageUI = new JPanel(tableLayout);
            pageUI.add(paramOffsetX.getEditor().getLabelComponent());
            tableLayout.setCellWeightX(0, 1, 1.0);
            pageUI.add(paramOffsetX.getEditor().getEditorComponent());

            tableLayout.setCellRowspan(0, 2, 2);
            tableLayout.setCellWeightX(0, 2, 1.0);
            tableLayout.setCellAnchor(0, 2, TableLayout.Anchor.CENTER);
            tableLayout.setCellFill(0, 2, TableLayout.Fill.NONE);
            pageUI.add(visualizer);

            pageUI.add(paramOffsetY.getEditor().getLabelComponent());
            tableLayout.setCellWeightX(1, 1, 1.0);
            pageUI.add(paramOffsetY.getEditor().getEditorComponent());

            tableLayout.setRowPadding(2, new Insets(10, 0, 4, 4));
            pageUI.add(paramShowDecimals.getEditor().getEditorComponent(), cell(2, 0, 1, 3));
            tableLayout.setRowPadding(3, new Insets(10, 0, 4, 4));
            pageUI.add(paramGeolocationAsDecimal.getEditor().getEditorComponent(), cell(3, 0, 1, 3));

            setPageUI(createPageUIContentPane(pageUI));

        }

        private JComponent createOffsetVisualizer() {
            return new JPanel() {

                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    final int totWidth = getWidth();
                    final int totHeight = getHeight();

                    if (totWidth == 0 || totHeight == 0) {
                        return;
                    }
                    if (!(g instanceof Graphics2D)) {
                        return;
                    }

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setStroke(new BasicStroke(2));
                    final int borderSize = 10;
                    final int maxPixelWidth = totWidth - 2 * borderSize;
                    final int maxPixelHeight = totHeight - 2 * borderSize;
                    final int pixelSize = Math.min(maxPixelHeight, maxPixelWidth);
                    final Rectangle pixel = new Rectangle((totWidth - pixelSize) / 2, (totHeight - pixelSize) / 2,
                                                          pixelSize, pixelSize);
                    g2d.setColor(Color.blue);
                    g2d.drawRect(pixel.x, pixel.y, pixel.width, pixel.height);

                    final float offsetX = (Float) paramOffsetX.getValue();
                    final float offsetY = (Float) paramOffsetY.getValue();
                    final int posX = Math.round(pixelSize * offsetX + pixel.x);
                    final int posY = Math.round(pixelSize * offsetY + pixel.y);
                    drawPos(g2d, posX, posY);
                }

                private void drawPos(Graphics2D g2d, final int posX, final int posY) {
                    g2d.setColor(Color.yellow);
                    final int crossLength = 8;
                    g2d.drawLine(posX - crossLength, posY, posX + crossLength, posY);
                    g2d.drawLine(posX, posY - crossLength, posX, posY + crossLength);
                    g2d.setColor(Color.red);

                    final int diameter = 3;
                    g2d.fillOval(posX - diameter / 2, posY - diameter / 2, diameter, diameter);
                }
            };
        }


        @Override
        protected void initConfigParams(ParamGroup configParams) {
            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    visualizer.repaint();
                }
            };

            final ParamProperties propertiesX = new ParamProperties(Float.class);
            propertiesX.setDefaultValue(PROPERTY_DEFAULT_PIXEL_OFFSET_FOR_DISPLAY);
            propertiesX.setMinValue(0.0f);
            propertiesX.setMaxValue(1.0f);
            propertiesX.setLabel("Relative pixel-X offset");
            paramOffsetX = new Parameter(PROPERTY_KEY_PIXEL_OFFSET_FOR_DISPLAY_X, propertiesX);
            paramOffsetX.addParamChangeListener(paramChangeListener);
            configParams.addParameter(paramOffsetX);


            final ParamProperties propertiesY = new ParamProperties(Float.class);
            propertiesY.setDefaultValue(PROPERTY_DEFAULT_PIXEL_OFFSET_FOR_DISPLAY);
            propertiesY.setMinValue(0.0f);
            propertiesY.setMaxValue(1.0f);
            propertiesY.setLabel("Relative pixel-Y offset");
            paramOffsetY = new Parameter(PROPERTY_KEY_PIXEL_OFFSET_FOR_DISPLAY_Y, propertiesY);
            paramOffsetY.addParamChangeListener(paramChangeListener);
            configParams.addParameter(paramOffsetY);

            final ParamProperties propShowDecimals = new ParamProperties(Boolean.class);
            propShowDecimals.setDefaultValue(PROPERTY_DEFAULT_PIXEL_OFFSET_FOR_DISPLAY_SHOW_DECIMALS);
            propShowDecimals.setLabel("Show floating-point image coordinates");
            paramShowDecimals = new Parameter(PROPERTY_KEY_PIXEL_OFFSET_FOR_DISPLAY_SHOW_DECIMALS,
                                              propShowDecimals);
            configParams.addParameter(paramShowDecimals);

            final ParamProperties propGeoLocationDisplay = new ParamProperties(Boolean.class);
            propGeoLocationDisplay.setDefaultValue(PROPERTY_DEFAULT_DISPLAY_GEOLOCATION_AS_DECIMAL);
            propGeoLocationDisplay.setLabel("Show geo-location coordinates in decimal degrees");
            paramGeolocationAsDecimal = new Parameter(PROPERTY_KEY_DISPLAY_GEOLOCATION_AS_DECIMAL,
                                                      propGeoLocationDisplay);
            configParams.addParameter(paramGeolocationAsDecimal);
        }
    }

    public static class DataIO extends DefaultConfigPage {

        public DataIO() {
            setTitle("Data Input/Output");  /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param;

            param = new Parameter(PROPERTY_KEY_SAVE_PRODUCT_HEADERS, DEFAULT_VALUE_SAVE_PRODUCT_HEADERS);
            param.getProperties().setLabel("Save product header (MPH, SPH, Global_Attributes)"); /*I18N*/
            param.addParamChangeListener(new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    Parameter configParam = getConfigParam(PROPERTY_KEY_SAVE_PRODUCT_ANNOTATIONS);
                    boolean b = (Boolean) event.getParameter().getValue();
                    configParam.setUIEnabled(b);
                    if (!b) {
                        configParam.setValue(false, null);
                    }
                }
            });
            configParams.addParameter(param);


            param = new Parameter(PROPERTY_KEY_SAVE_PRODUCT_HISTORY, DEFAULT_VALUE_SAVE_PRODUCT_HISTORY);
            param.getProperties().setLabel("Save product history (History)"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_SAVE_PRODUCT_ANNOTATIONS, DEFAULT_VALUE_SAVE_PRODUCT_ANNOTATIONS);
            param.getProperties().setLabel("Save product annotation datasets (ADS)"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_SAVE_INCREMENTAL, DEFAULT_VALUE_SAVE_INCREMENTAL);
            param.getProperties().setLabel("Use incremental save (only save modified items)"); /*I18N*/
            configParams.addParameter(param);
        }

        @Override
        protected void initPageUI() {
            setPageUI(createPageUI());
        }

        private JPanel createPageUI() {
            Parameter param;
            GridBagConstraints gbc;

            JPanel beamDimap = GridBagUtils.createPanel();
            //beamDimap.setBorder(UIUtils.createGroupBorder("Default Format"));  /*I18N*/
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST, weightx=1");

            gbc.gridy = 0;
            param = getConfigParam(PROPERTY_KEY_SAVE_PRODUCT_HEADERS);
            beamDimap.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_SAVE_PRODUCT_HISTORY);
            beamDimap.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_SAVE_PRODUCT_ANNOTATIONS);
            GridBagUtils.addToPanel(beamDimap, param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_SAVE_INCREMENTAL);
            GridBagUtils.addToPanel(beamDimap, param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            JPanel pageUI = GridBagUtils.createPanel();
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST, weightx=1");

            pageUI.add(beamDimap, gbc);

            return createPageUIContentPane(pageUI);
        }
    }

    public static class LayerPropertiesPage extends DefaultConfigPage {

        public LayerPropertiesPage() {
            setTitle("Layer Properties"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param;

            param = new Parameter(ProductSceneView.PROPERTY_KEY_GRAPHICS_ANTIALIASING, Boolean.TRUE);
            param.getProperties().setLabel("Use anti-aliasing for rendering text and vector graphics"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(ProductSceneView.PROPERTY_KEY_IMAGE_NAV_CONTROL_SHOWN, Boolean.TRUE);
            param.getProperties().setLabel("Show a navigation control widget in image views"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(ProductSceneView.PROPERTY_KEY_IMAGE_SCROLL_BARS_SHOWN, Boolean.FALSE);
            param.getProperties().setLabel("Show scroll bars in image views"); /*I18N*/
            configParams.addParameter(param);

        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        private JPanel createPageUI() {
            Parameter param;

            GridBagConstraints gbc;
            // UI
            JPanel pageUI = GridBagUtils.createPanel();
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1");
            gbc.gridy = 0;
            gbc.insets.bottom = 8;

            param = getConfigParam(ProductSceneView.PROPERTY_KEY_GRAPHICS_ANTIALIASING);
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            final JLabel note = new JLabel("Note: For best performance turn anti-aliasing off.");
            configureNoteLabel(note);
            pageUI.add(note, gbc);
            gbc.gridy++;

            gbc.insets.top = _LINE_INSET_TOP;
            param = getConfigParam(ProductSceneView.PROPERTY_KEY_IMAGE_NAV_CONTROL_SHOWN);
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            gbc.insets.top = _LINE_INSET_TOP;
            param = getConfigParam(ProductSceneView.PROPERTY_KEY_IMAGE_SCROLL_BARS_SHOWN);
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            return createPageUIContentPane(pageUI);
        }
    }

    public static class ImageDisplayPage extends DefaultConfigPage {

        public ImageDisplayPage() {
            setTitle("Image Layer"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param;

            param = new Parameter(PROPERTY_KEY_JAI_TILE_CACHE_CAPACITY, 512);
            param.getProperties().setLabel("Tile cache capacity"); /*I18N*/
            param.getProperties().setPhysicalUnit("M"); /*I18N*/
            param.getProperties().setMinValue(32);
            param.getProperties().setMaxValue(16384);
            configParams.addParameter(param);

            param = new Parameter("image.background.color", ProductSceneView.DEFAULT_IMAGE_BACKGROUND_COLOR);
            param.getProperties().setLabel("Background colour"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter("image.border.shown", ImageLayer.DEFAULT_BORDER_SHOWN);
            param.getProperties().setLabel("Show image border"); /*I18N*/
            param.addParamChangeListener(new ParamChangeListener() {

                @Override
                public void parameterValueChanged(ParamChangeEvent event) {
                    updatePageUI();
                }
            });
            configParams.addParameter(param);

            param = new Parameter("image.border.size", ImageLayer.DEFAULT_BORDER_WIDTH);
            param.getProperties().setLabel("Image border size"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter("image.border.color", ImageLayer.DEFAULT_BORDER_COLOR);
            param.getProperties().setLabel("Image border colour"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter("pixel.border.shown", ImageLayer.DEFAULT_PIXEL_BORDER_SHOWN);
            param.getProperties().setLabel("Show pixel borders in magnified views"); /*I18N*/
            param.addParamChangeListener(new ParamChangeListener() {

                @Override
                public void parameterValueChanged(ParamChangeEvent event) {
                    updatePageUI();
                }
            });
            configParams.addParameter(param);

            param = new Parameter("pixel.border.size", ImageLayer.DEFAULT_PIXEL_BORDER_WIDTH);
            param.getProperties().setLabel("Pixel border size"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter("pixel.border.color", ImageLayer.DEFAULT_PIXEL_BORDER_COLOR);
            param.getProperties().setLabel("Pixel border colour"); /*I18N*/
            configParams.addParameter(param);
        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
            updatePageUI();
        }

        private JPanel createPageUI() {
            Parameter param;

            JPanel pageUI = GridBagUtils.createPanel();

            GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST");
            gbc.gridy = 0;

            gbc.insets.top = _LINE_INSET_TOP;

            param = getConfigParam(PROPERTY_KEY_JAI_TILE_CACHE_CAPACITY);
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            JPanel p = new JPanel(new BorderLayout(4, 4));
            p.add(param.getEditor().getEditorComponent(), BorderLayout.CENTER);
            p.add(param.getEditor().getPhysUnitLabelComponent(), BorderLayout.EAST);
            pageUI.add(p, gbc);
            gbc.gridy++;

            gbc.insets.top = _LINE_INSET_TOP;

            gbc.gridwidth = 2;
            final JLabel note2 = new JLabel("Note: If you have enough memory select values > 256 M.");
            configureNoteLabel(note2);
            pageUI.add(note2, gbc);
            gbc.gridwidth = 1;
            gbc.gridy++;

            gbc.insets.top = 3 * _LINE_INSET_TOP;

            param = getConfigParam("image.background.color");
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            gbc.insets.top = 3 * _LINE_INSET_TOP;

            param = getConfigParam("image.border.shown");
            gbc.gridwidth = 2;
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;
            gbc.gridwidth = 1;

            gbc.insets.top = _LINE_INSET_TOP;

            param = getConfigParam("image.border.size");
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam("image.border.color");
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            gbc.insets.top = 3 * _LINE_INSET_TOP;

            param = getConfigParam("pixel.border.shown");
            gbc.gridwidth = 2;
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;
            gbc.gridwidth = 1;

            gbc.insets.top = _LINE_INSET_TOP;

            param = getConfigParam("pixel.border.size");
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam("pixel.border.color");
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            return createPageUIContentPane(pageUI);
        }

        @Override
        public void updatePageUI() {
            boolean imageBorderEnabled = (Boolean) getConfigParam("image.border.shown").getValue();
            setConfigParamUIEnabled("image.border.size", imageBorderEnabled);
            setConfigParamUIEnabled("image.border.color", imageBorderEnabled);

            boolean pixelBorderEnabled = (Boolean) getConfigParam("pixel.border.shown").getValue();
            setConfigParamUIEnabled("pixel.border.size", pixelBorderEnabled);
            setConfigParamUIEnabled("pixel.border.color", pixelBorderEnabled);
        }
    }

    public static class GraticuleOverlayPage extends DefaultConfigPage {

        boolean resettingDefaults = false;

        public GraticuleOverlayPage() {
            setTitle("Map Gridlines Layer"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param;


            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    if (!resettingDefaults) {
                        updatePageUI();
                    }
                }
            };
            final ParamChangeListener paramChangeListenerResetToDefaults = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    if ((Boolean) getConfigParam(GraticuleLayerType.RESET_TO_DEFAULTS_PARAM_STR).getValue()) {
                        resettingDefaults = true;
                        resetToDefaults();
                        resettingDefaults = false;
                    }
                }
            };


            param = new Parameter(GraticuleLayerType.RESET_TO_DEFAULTS_PARAM_STR, GraticuleLayerType.DEFAULT_RESET_TO_DEFAULTS);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_RESET_TO_DEFAULTS); /*I18N*/
            param.addParamChangeListener(paramChangeListenerResetToDefaults);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH, GraticuleLayerType.DEFAULT_TEXT_ENABLED_NORTH);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_NORTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH, GraticuleLayerType.DEFAULT_TEXT_ENABLED_SOUTH);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_SOUTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST, GraticuleLayerType.DEFAULT_TEXT_ENABLED_WEST);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_WEST); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST, GraticuleLayerType.DEFAULT_TEXT_ENABLED_EAST);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ENABLED_EAST); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED, GraticuleLayerType.DEFAULT_TEXT_CORNER_TOP_LON_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_TOP_LON_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED, GraticuleLayerType.DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_BOTTOM_LON_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED, GraticuleLayerType.DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_LEFT_LAT_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED, GraticuleLayerType.DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_RIGHT_LAT_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED, GraticuleLayerType.DEFAULT_TICKMARK_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TICKMARK_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE, GraticuleLayerType.DEFAULT_TICKMARK_INSIDE);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TICKMARK_INSIDE); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE, GraticuleLayerType.DEFAULT_TEXT_INSIDE);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_INSIDE); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED, GraticuleLayerType.DEFAULT_LINE_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_LINE_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED, GraticuleLayerType.DEFAULT_BORDER_ENABLED);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_BORDER_ENABLED); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_FORMAT_COMPASS, GraticuleLayerType.DEFAULT_FORMAT_COMPASS);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_FORMAT_COMPASS); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_FORMAT_DECIMAL, GraticuleLayerType.DEFAULT_FORMAT_DECIMAL);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_FORMAT_DECIMAL); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_RES_LAT, GraticuleLayerType.DEFAULT_RES_LAT);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_RES_LAT); /*I18N*/
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ROTATION_NORTH_SOUTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setMinValue(0.0);
            param.getProperties().setMaxValue(90.0);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_RES_LON, GraticuleLayerType.DEFAULT_RES_LON);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_RES_LON); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setMinValue(0.0);
            param.getProperties().setMaxValue(180.0);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH, GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH_SOUTH);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ROTATION_NORTH_SOUTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setMinValue(0);
            param.getProperties().setMaxValue(90);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST_EAST, GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST_EAST);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_ROTATION_WEST_EAST); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setMinValue(0);
            param.getProperties().setMaxValue(180);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE, GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_FONT_SIZE); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_SIZE, GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_SIZE);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_FONT_SIZE); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH, GraticuleLayerType.DEFAULT_LINE_WIDTH);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_LINE_WIDTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE, GraticuleLayerType.DEFAULT_LINE_DASHED_PHASE);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_LINE_DASHED_PHASE); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH, GraticuleLayerType.DEFAULT_TICKMARK_LENGTH);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TICKMARK_LENGTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH, GraticuleLayerType.DEFAULT_BORDER_WIDTH);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_BORDER_WIDTH); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY, GraticuleLayerType.DEFAULT_LINE_TRANSPARENCY);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_LINE_TRANSPARENCY); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setMinValue(0);
            param.getProperties().setMaxValue(1);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY, GraticuleLayerType.DEFAULT_TEXT_BG_TRANSPARENCY);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_BG_TRANSPARENCY); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            param.getProperties().setMinValue(0);
            param.getProperties().setMaxValue(1);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR, GraticuleLayerType.DEFAULT_TEXT_FG_COLOR);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_FG_COLOR); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_COLOR, GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_COLOR);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_CORNER_FONT_COLOR); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR, GraticuleLayerType.DEFAULT_LINE_COLOR);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_LINE_COLOR); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR, GraticuleLayerType.DEFAULT_BORDER_COLOR);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_BORDER_COLOR); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR, GraticuleLayerType.DEFAULT_TEXT_BG_COLOR);
            param.getProperties().setLabel(GraticuleLayerType.PROPERTY_LABEL_TEXT_BG_COLOR); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        private JPanel createPageUI() {
            Parameter param;


            JPanel leftPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcLeft = GridBagUtils.createConstraints("");
            leftPanel.setBorder(UIUtils.createGroupBorder("")); /*I18N*/
            gbcLeft.gridx = 0;
            gbcLeft.gridy = 0;
            gbcLeft.weightx = 0.0;
            gbcLeft.fill = GridBagConstraints.HORIZONTAL;
            gbcLeft.anchor = GridBagConstraints.NORTHWEST;
            gbcLeft.insets.bottom=0;
            gbcLeft.insets.top=0;



            JPanel rightPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcRight = GridBagUtils.createConstraints("");
            rightPanel.setBorder(UIUtils.createGroupBorder("")); /*I18N*/
            gbcRight.gridx = 0;
            gbcRight.gridy = 0;
            gbcRight.weightx = 0.0;
            gbcRight.fill = GridBagConstraints.HORIZONTAL;
            gbcRight.anchor = GridBagConstraints.NORTHWEST;
            gbcRight.insets.bottom=0;
            gbcRight.insets.top=0;



            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_FORMAT_COMPASS);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_FORMAT_DECIMAL);
            addParamToPane(leftPanel, param, gbcLeft);
            gbcLeft.gridy++;





            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_RES_LAT);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_RES_LON);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST_EAST);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_SIZE);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_COLOR);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;

            param = getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR);
            addParamToPane(rightPanel, param, gbcRight);
            gbcRight.gridy++;



            JPanel resetPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcReset = GridBagUtils.createConstraints("");
            gbcReset.gridx = 0;
            gbcReset.gridy = 0;
            gbcReset.weightx = 0.0;
            gbcReset.fill = GridBagConstraints.HORIZONTAL;
            gbcReset.anchor = GridBagConstraints.NORTHWEST;
            gbcReset.insets.bottom=0;
            gbcReset.insets.top=0;


            param = getConfigParam(GraticuleLayerType.RESET_TO_DEFAULTS_PARAM_STR);
            addParamToPane(resetPanel, param, gbcReset);
            gbcReset.gridy++;



            JPanel leftFullPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcLeftFull = GridBagUtils.createConstraints("");
            gbcLeftFull.gridx = 0;
            gbcLeftFull.gridy = 0;
            gbcLeftFull.weightx = 0.0;
            gbcLeftFull.fill = GridBagConstraints.HORIZONTAL;
            gbcLeftFull.anchor = GridBagConstraints.NORTHWEST;
            gbcLeftFull.insets.bottom=0;
            gbcLeftFull.insets.top=0;
            leftFullPanel.add(leftPanel, gbcLeftFull);
            gbcLeftFull.gridy++;
            gbcLeftFull.insets.top=10;
            leftFullPanel.add(resetPanel, gbcLeftFull);



            JPanel groupPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcGroup = GridBagUtils.createConstraints("");
            gbcGroup.gridx = 0;
            gbcGroup.gridy = 0;
            gbcGroup.weightx = 0.0;
            gbcGroup.fill = GridBagConstraints.NONE;
            gbcGroup.anchor = GridBagConstraints.NORTHWEST;
            gbcGroup.insets.bottom=10;
            gbcGroup.insets.right=20;
            groupPanel.add(leftFullPanel, gbcGroup);
            gbcGroup.gridx++;
            gbcGroup.weightx = 1.0;
            gbcGroup.weighty = 1.0;
            gbcGroup.insets.right=0;
            gbcGroup.fill = GridBagConstraints.NONE;
//            gbcGroup.insets.top = _LINE_INSET_TOP;

            groupPanel.add(rightPanel, gbcGroup);


            return createPageUIContentPane(groupPanel);
        }

        public void resetToDefaults() {

            ParamExceptionHandler errorHandler = new ParamExceptionHandler() {
                @Override
                public boolean handleParamException(ParamException e) {
                    return false;
                }
            };


            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_NORTH).setValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_NORTH, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_SOUTH).setValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_SOUTH, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_WEST).setValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_WEST, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ENABLED_EAST).setValue(GraticuleLayerType.DEFAULT_TEXT_ENABLED_EAST, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_TOP_LON_ENABLED).setValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_TOP_LON_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_BOTTOM_LON_ENABLED).setValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_BOTTOM_LON_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_LEFT_LAT_ENABLED).setValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_LEFT_LAT_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_RIGHT_LAT_ENABLED).setValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_RIGHT_LAT_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TICKMARK_ENABLED).setValue(GraticuleLayerType.DEFAULT_TICKMARK_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TICKMARK_INSIDE).setValue(GraticuleLayerType.DEFAULT_TICKMARK_INSIDE, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_INSIDE).setValue(GraticuleLayerType.DEFAULT_TEXT_INSIDE, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_ENABLED).setValue(GraticuleLayerType.DEFAULT_LINE_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_BORDER_ENABLED).setValue(GraticuleLayerType.DEFAULT_BORDER_ENABLED, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_FORMAT_COMPASS).setValue(GraticuleLayerType.DEFAULT_FORMAT_COMPASS, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_FORMAT_DECIMAL).setValue(GraticuleLayerType.DEFAULT_FORMAT_DECIMAL, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_RES_LAT).setValue(GraticuleLayerType.DEFAULT_RES_LAT, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_RES_LON).setValue(GraticuleLayerType.DEFAULT_RES_LON, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_NORTH_SOUTH).setValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_NORTH_SOUTH, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_ROTATION_WEST_EAST).setValue(GraticuleLayerType.DEFAULT_TEXT_ROTATION_WEST_EAST, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_FONT_SIZE).setValue(GraticuleLayerType.DEFAULT_TEXT_FONT_SIZE, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_SIZE).setValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_SIZE, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_WIDTH).setValue(GraticuleLayerType.DEFAULT_LINE_WIDTH, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_DASHED_PHASE).setValue(GraticuleLayerType.DEFAULT_LINE_DASHED_PHASE, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TICKMARK_LENGTH).setValue(GraticuleLayerType.DEFAULT_TICKMARK_LENGTH, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_BORDER_WIDTH).setValue(GraticuleLayerType.DEFAULT_BORDER_WIDTH, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_TRANSPARENCY).setValue(GraticuleLayerType.DEFAULT_LINE_TRANSPARENCY, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_TRANSPARENCY).setValue(GraticuleLayerType.DEFAULT_TEXT_BG_TRANSPARENCY, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_FG_COLOR).setValue(GraticuleLayerType.DEFAULT_TEXT_FG_COLOR, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_CORNER_FONT_COLOR).setValue(GraticuleLayerType.DEFAULT_TEXT_CORNER_FONT_COLOR, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_LINE_COLOR).setValue(GraticuleLayerType.DEFAULT_LINE_COLOR, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_BORDER_COLOR).setValue(GraticuleLayerType.DEFAULT_BORDER_COLOR, errorHandler);
            getConfigParam(GraticuleLayerType.PROPERTY_NAME_TEXT_BG_COLOR).setValue(GraticuleLayerType.DEFAULT_TEXT_BG_COLOR, errorHandler);


        }


        @Override
        public void updatePageUI() {


            ParamExceptionHandler errorHandler = new ParamExceptionHandler() {
                @Override
                public boolean handleParamException(ParamException e) {
                    return false;
                }
            };

            getConfigParam(GraticuleLayerType.RESET_TO_DEFAULTS_PARAM_STR).setValue(false, errorHandler);


//            final boolean resAuto = (Boolean) getConfigParam("graticule.res.auto").getValue();
//       //     getConfigParam("graticule.res.pixels").setUIEnabled(resAuto);
//            getConfigParam("graticule.res.lat").setUIEnabled(!resAuto);
//            getConfigParam("graticule.res.lon").setUIEnabled(!resAuto);

//            final boolean textEnabled = (Boolean) getConfigParam("graticule.text.enabled").getValue();
//            getConfigParam("graticule.text.fg.color").setUIEnabled(textEnabled);
//            getConfigParam("graticule.text.bg.color").setUIEnabled(textEnabled);
//            getConfigParam("graticule.text.bg.transparency").setUIEnabled(textEnabled);
//            getConfigParam("graticule.text.font.size").setUIEnabled(textEnabled);
//            getConfigParam("graticule.text.inside").setUIEnabled(textEnabled);
//            getConfigParam("graticule.text.offset.outward").setUIEnabled(textEnabled);
//            getConfigParam("graticule.text.offset.sideward").setUIEnabled(textEnabled);
        }
    }

    public static class MaskOverlayPage extends DefaultConfigPage {

        private static final String PARAMETER_NAME_MASK_COLOR = "mask.color";
        private static final String PARAMETER_NAME_MASK_TRANSPARENCY = "mask.transparency";

        public MaskOverlayPage() {
            setTitle("Mask Layer");
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {

            Parameter param = new Parameter(PARAMETER_NAME_MASK_COLOR, Mask.ImageType.DEFAULT_COLOR);
            param.getProperties().setLabel("Mask colour"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PARAMETER_NAME_MASK_TRANSPARENCY, Mask.ImageType.DEFAULT_TRANSPARENCY);
            param.getProperties().setLabel("Mask transparency"); /*I18N*/
            param.getProperties().setMinValue(0.0);
            param.getProperties().setMaxValue(0.95);
            configParams.addParameter(param);

            param = new Parameter(Mask.ImageType.PARAMETER_NAME_MASK_L2_FLAGNAMES_ENABLED, Mask.ImageType.DEFAULT_L2_FLAGNAMES_ENABLED);
            param.getProperties().setLabel("Name Level2 masks the same as Level2 flags"); /*I18N*/
            configParams.addParameter(param);

        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
            updatePageUI();
        }

        private JPanel createPageUI() {
            Parameter param;

            JPanel pageUI = GridBagUtils.createPanel();
            //pageUI.setBorder(UIUtils.createGroupBorder("ROI Overlay")); /*I18N*/

            GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST");
            gbc.gridy = 0;

            param = getConfigParam(PARAMETER_NAME_MASK_COLOR);
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            gbc.insets.top = _LINE_INSET_TOP;

            param = getConfigParam(PARAMETER_NAME_MASK_TRANSPARENCY);
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(Mask.ImageType.PARAMETER_NAME_MASK_L2_FLAGNAMES_ENABLED);
            gbc.weightx = 1;
            gbc.anchor=GridBagConstraints.WEST;
            gbc.gridwidth = 2;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;


            return createPageUIContentPane(pageUI);
        }
    }

    private class WorldMapLayerPage extends DefaultConfigPage {

        private static final String PARAMETER_NAME_WORLDMAP_TYPE = "worldmap.type";

        private JComboBox box;
        private List<WorldMapLayerType> worldMapLayerTypes;

        public WorldMapLayerPage() {
            setTitle("World Map Layer");
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            worldMapLayerTypes = new ArrayList<WorldMapLayerType>();
            Set<LayerType> allLayerTypes = LayerTypeRegistry.getLayerTypes();
            for (LayerType layerType : allLayerTypes) {
                if (layerType instanceof WorldMapLayerType) {
                    WorldMapLayerType worldMapLayerType = (WorldMapLayerType) layerType;
                    worldMapLayerTypes.add(worldMapLayerType);
                }
            }
            Parameter param = new Parameter(PARAMETER_NAME_WORLDMAP_TYPE, "BlueMarbleLayerType");
            configParams.addParameter(param);
        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
            updatePageUI();
        }

        private JPanel createPageUI() {
            JPanel pageUI = GridBagUtils.createPanel();

            box = new JComboBox();
            box.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                              boolean cellHasFocus) {
                    Component rendererComponent = super.getListCellRendererComponent(list, value, index, isSelected,
                            cellHasFocus);
                    if (value instanceof WorldMapLayerType && rendererComponent instanceof JLabel) {
                        WorldMapLayerType worldMapLayerType = (WorldMapLayerType) value;
                        JLabel label = (JLabel) rendererComponent;
                        label.setText(worldMapLayerType.getLabel());
                    }
                    return rendererComponent;
                }
            });
            GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST");
            gbc.gridy = 0;
            gbc.weightx = 0;

            pageUI.add(new JLabel("World Map Layer: "), gbc);
            gbc.weightx = 1;

            pageUI.add(box, gbc);
            gbc.insets.top = _LINE_INSET_TOP;
            return createPageUIContentPane(pageUI);
        }

        @Override
        public void updatePageUI() {
            DefaultComboBoxModel aModel = new DefaultComboBoxModel(worldMapLayerTypes.toArray());
            final Parameter param = getConfigParam(PARAMETER_NAME_WORLDMAP_TYPE);
            String valueAsText = param.getValueAsText();
            WorldMapLayerType selected = null;
            for (WorldMapLayerType worldMapLayerType : worldMapLayerTypes) {
                if (worldMapLayerType.getName().equals(valueAsText)) {
                    selected = worldMapLayerType;
                    break;
                }
            }
            aModel.setSelectedItem(selected);
            box.setModel(aModel);
        }

        @Override
        public void onOK() {
            WorldMapLayerType selected = (WorldMapLayerType) box.getModel().getSelectedItem();
            final Parameter param = getConfigParam(PARAMETER_NAME_WORLDMAP_TYPE);
            try {
                param.setValue(selected.getName());
            } catch (ParamValidateException ignore) {
            }
        }
    }

    public static class NoDataOverlayPage extends DefaultConfigPage {

        public NoDataOverlayPage() {
            setTitle("No-Data Layer");
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param;

            param = new Parameter("noDataOverlay.color", NoDataLayerType.DEFAULT_COLOR);
            param.getProperties().setLabel("No-data overlay colour"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter("noDataOverlay.transparency", 0.3);
            param.getProperties().setLabel("No-data overlay transparency"); /*I18N*/
            param.getProperties().setMinValue(0.0);
            param.getProperties().setMaxValue(0.95);
            configParams.addParameter(param);
        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
            updatePageUI();
        }

        private JPanel createPageUI() {
            Parameter param;

            JPanel pageUI = GridBagUtils.createPanel();

            GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST");
            gbc.gridy = 0;

            param = getConfigParam("noDataOverlay.color");
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            gbc.insets.top = _LINE_INSET_TOP;

            param = getConfigParam("noDataOverlay.transparency");
            gbc.weightx = 0;
            pageUI.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            pageUI.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            return createPageUIContentPane(pageUI);
        }

        @Override
        public void updatePageUI() {
        }
    }

    public static class LoggingPage extends DefaultConfigPage {

        public LoggingPage() {
            setTitle("Logging"); /*I18N*/
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param;

            param = new Parameter(PROPERTY_KEY_APP_LOG_ENABLED, Boolean.FALSE);
            param.addParamChangeListener(new ParamChangeListener() {

                public void parameterValueChanged(ParamChangeEvent event) {
                    updatePageUI();
                }
            });
            param.getProperties().setLabel("Enable logging"); /*I18N*/
            configParams.addParameter(param);

//            param = new Parameter(VisatApp.PROPERTY_KEY_APP_LOG_PATH, new File("logging.txt"));
//            param.getProperties().setFileSelectionMode(ParamProperties.FSM_FILES_ONLY);
//            param.getProperties().setLabel("Logfile path");
//            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_APP_LOG_PREFIX, getApp().getAppName().toLowerCase());
            param.getProperties().setFileSelectionMode(ParamProperties.FSM_FILES_ONLY);
            param.getProperties().setLabel("Log filename prefix");
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_APP_LOG_ECHO, Boolean.FALSE);
            param.getProperties().setLabel("Echo log output (effective only with console)"); /*I18N*/
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_APP_LOG_LEVEL, SystemUtils.LLS_INFO);
            param.getProperties().setLabel("Log level"); /*I18N*/
            param.getProperties().setReadOnly(true);
            configParams.addParameter(param);

            param = new Parameter(PROPERTY_KEY_APP_DEBUG_ENABLED, Boolean.FALSE);
            param.getProperties().setLabel("Log extra debugging information"); /*I18N*/
            param.addParamChangeListener(new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    Parameter configParam = getConfigParam(PROPERTY_KEY_APP_LOG_LEVEL);
                    if (configParam != null) {
                        boolean isLogDebug = (Boolean) event.getParameter().getValue();
                        if (isLogDebug) {
                            configParam.setValue(SystemUtils.LLS_DEBUG, null);
                        } else {
                            configParam.setValue(SystemUtils.LLS_INFO, null);
                        }
                    }
                }
            });
            configParams.addParameter(param);
        }

        @Override
        protected void initPageUI() {
            JPanel panel = createPageUI();

            setPageUI(panel);
            updatePageUI();
        }

        private JPanel createPageUI() {
            Parameter param;

            JPanel logConfigPane = GridBagUtils.createPanel();
            GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=WEST,weightx=1");
            gbc.gridy = 0;


            param = getConfigParam(PROPERTY_KEY_APP_LOG_ENABLED);
            gbc.insets.top = _LINE_INSET_TOP;
            gbc.gridwidth = 2;
            logConfigPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_APP_LOG_PREFIX);
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            logConfigPane.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            logConfigPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_APP_LOG_ECHO);
            gbc.gridwidth = 2;
            logConfigPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            param = getConfigParam(PROPERTY_KEY_APP_DEBUG_ENABLED);
            gbc.gridwidth = 2;
            logConfigPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            GridBagUtils.setAttributes(gbc, "gridwidth=1,weightx=0");
            param = getConfigParam(PROPERTY_KEY_APP_LOG_LEVEL);
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            logConfigPane.add(param.getEditor().getLabelComponent(), gbc);
            gbc.weightx = 1;
            logConfigPane.add(param.getEditor().getEditorComponent(), gbc);
            gbc.gridy++;

            gbc.gridwidth = 2;
            gbc.insets.top = 25;
            final JLabel label = new JLabel(
                    "Note: changes on this page are not effective until restart of " + getApp().getAppName());
            configureNoteLabel(label);
            logConfigPane.add(label, gbc);
            gbc.gridy++;

            return createPageUIContentPane(logConfigPane);
        }

        @Override
        public void updatePageUI() {
            boolean enabled = (Boolean) getConfigParam(PROPERTY_KEY_APP_LOG_ENABLED).getValue();
            setConfigParamUIEnabled(PROPERTY_KEY_APP_LOG_PREFIX, enabled);
            setConfigParamUIEnabled(PROPERTY_KEY_APP_LOG_ECHO, enabled);
            setConfigParamUIEnabled(PROPERTY_KEY_APP_DEBUG_ENABLED, enabled);
            setConfigParamUIEnabled(PROPERTY_KEY_APP_LOG_LEVEL, enabled);
        }
    }

    public static class RGBImageProfilePage extends DefaultConfigPage {

        public RGBImageProfilePage() {
            setTitle("RGB Image"); /*I18N*/
        }

        @Override
        public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
            propertyMap = super.getConfigParamValues(propertyMap);
            return propertyMap;
        }

        @Override
        public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {

            super.setConfigParamValues(propertyMap, errorHandler);
        }



        @Override
        protected void initConfigParams(final ParamGroup configParams) {
            final ParamChangeListener paramChangeListener = new ParamChangeListener() {
                public void parameterValueChanged(ParamChangeEvent event) {
                    updatePageUI();
                }
            };

            Parameter param;

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();


            param = new Parameter(ImageLegend.PROPERTY_NAME_COLORBAR_LABELS_OVERRIDE, ImageLegend.DEFAULT_COLORBAR_LABELS_OVERRIDE);
            param.getProperties().setLabel("Allow color bar labels override from scheme definition");
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MANUAL_MINMAX, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MANUAL_MINMAX);
            param.getProperties().setLabel("Set Range (Default uses band statistics)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_RED, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MIN_RED);
            param.getProperties().setLabel("Min (Red Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_RED, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MAX_RED);
            param.getProperties().setLabel("Max (Red Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_GREEN, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MIN_GREEN);
            param.getProperties().setLabel("Min (Green Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_GREEN, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MAX_RED);
            param.getProperties().setLabel("Max (Green Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_BLUE, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MIN_BLUE);
            param.getProperties().setLabel("Min (Blue Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_BLUE, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_MAX_RED);
            param.getProperties().setLabel("Max (Blue Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);


            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_SET_GAMMA, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_SET_GAMMA);
            param.getProperties().setLabel("Set Gamma"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_RED, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_GAMMA_RED);
            param.getProperties().setLabel("Gamma (Red Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_GREEN, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_GAMMA_GREEN);
            param.getProperties().setLabel("Gamma (Green Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);

            param = new Parameter(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_BLUE, ColorManipulationToolView.PREFERENCES_DEFAULT_RGB_GAMMA_BLUE);
            param.getProperties().setLabel("Gamma (Blue Channel)"); /*I18N*/
            param.addParamChangeListener(paramChangeListener);
            configParams.addParameter(param);
        }

        @Override
        public void updatePageUI() {
            boolean enabled = (Boolean) getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MANUAL_MINMAX).getValue();
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_RED, enabled);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_GREEN, enabled);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_BLUE, enabled);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_RED, enabled);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_GREEN, enabled);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_BLUE, enabled);

            boolean setGamma = (Boolean) getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_SET_GAMMA).getValue();
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_RED, setGamma);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_GREEN, setGamma);
            setConfigParamUIEnabled(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_BLUE, setGamma);
        }

        @Override
        protected void initPageUI() {
            RGBImageProfilePane _profilePane = new RGBImageProfilePane(new PropertyMap());

            Parameter param;

            // Font
            JPanel fontPane = GridBagUtils.createPanel();
            fontPane.setBorder(UIUtils.createGroupBorder("Palette Settings")); /*I18N*/
            GridBagConstraints gbcColorPaletteSchemes = GridBagUtils.createConstraints("");
            gbcColorPaletteSchemes.gridy = 0;


            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MANUAL_MINMAX);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;

            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_RED);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;


            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_GREEN);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;



            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MIN_BLUE);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;

            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_RED);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;

            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_GREEN);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;


            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_MAX_BLUE);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;



            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_SET_GAMMA);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;

            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_RED);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;

            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_GREEN);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;

            param = getConfigParam(ColorManipulationToolView.PREFERENCES_KEY_RGB_GAMMA_BLUE);
            addParamToPane(fontPane, param, gbcColorPaletteSchemes);
            gbcColorPaletteSchemes.gridy++;


            JPanel fontPane2 = GridBagUtils.createPanel();
            fontPane2.setBorder(UIUtils.createGroupBorder("Profile Settings")); /*I18N*/
            GridBagConstraints gbcPane2 = GridBagUtils.createConstraints("");
            gbcPane2.gridy = 0;
            fontPane2.add(_profilePane);



            JPanel contentsPanel = GridBagUtils.createPanel();
            GridBagConstraints gbcContents = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST");
            gbcContents.gridx = 0;
            gbcContents.gridy = 0;
            gbcContents.insets.bottom = 10;
            contentsPanel.add(fontPane, gbcContents);
            gbcContents.gridy++;
            contentsPanel.add(fontPane2, gbcContents);









            //////////////////////////////////////////////////////////////////////////////////////
            // All together
            JPanel pageUI = GridBagUtils.createPanel();
            GridBagConstraints gbcMain = GridBagUtils.createConstraints("fill=NONE");
            gbcMain.insets.bottom = 4;
            gbcMain.anchor = GridBagConstraints.NORTHWEST;
            gbcMain.weighty = 1.0;
            gbcMain.weightx = 1.0;

            gbcMain.insets.top = _LINE_INSET_TOP;

            pageUI.add(contentsPanel, gbcMain);





            setPageUI(pageUI);
        }
    }

    public static class ProductSettings extends DefaultConfigPage {

        public ProductSettings() {
            setTitle("Product Settings"); /*I18N*/
        }

        @Override
        protected void initPageUI() {
            JPanel pageUI = createPageUI();
            setPageUI(pageUI);
        }

        @Override
        protected void initConfigParams(ParamGroup configParams) {
            Parameter param = new Parameter(PROPERTY_KEY_GEOLOCATION_EPS,
                                            new Float(PROPERTY_DEFAULT_GEOLOCATION_EPS));
            param.getProperties().setLabel("If their geo-locations differ less than: ");/*I18N*/
            param.getProperties().setPhysicalUnit("deg"); /*I18N*/
            param.getProperties().setMinValue(0.0f);
            param.getProperties().setMaxValue(360.0f);
            configParams.addParameter(param);
        }

        private JPanel createPageUI() {
            Parameter param;
            GridBagConstraints gbc;

            final JPanel productCompatibility = GridBagUtils.createPanel();
            productCompatibility.setBorder(UIUtils.createGroupBorder("Product Compatibility")); /*I18N*/
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");

            param = getConfigParam(PROPERTY_KEY_GEOLOCATION_EPS);
            gbc.insets.bottom += 8;
            gbc.gridwidth = 3;
            productCompatibility.add(new JLabel("Consider products as spatially compatible:"), gbc); /*I18N*/
            gbc.insets.bottom -= 8;
            gbc.gridy++;
            GridBagUtils.addToPanel(productCompatibility, param.getEditor().getLabelComponent(), gbc, "gridwidth=1");
            GridBagUtils.addToPanel(productCompatibility, param.getEditor().getEditorComponent(), gbc, "weightx=1");
            GridBagUtils.addToPanel(productCompatibility, param.getEditor().getPhysUnitLabelComponent(), gbc,
                                    "weightx=0");

            // UI
            JPanel pageUI = GridBagUtils.createPanel();
            gbc = GridBagUtils.createConstraints("fill=HORIZONTAL, anchor=WEST, weightx=1, gridy=1");

            pageUI.add(productCompatibility, gbc);
            gbc.gridy++;

            return createPageUIContentPane(pageUI);
        }
    }

    public static JPanel createPageUIContentPane(JPanel pane) {
        JPanel contentPane = GridBagUtils.createPanel();
        final GridBagConstraints gbc = GridBagUtils.createConstraints("fill=HORIZONTAL,anchor=NORTHWEST");
        gbc.insets.top = _PAGE_INSET_TOP;
        gbc.weightx = 1;
        gbc.weighty = 0;
        contentPane.add(pane, gbc);
        GridBagUtils.addVerticalFiller(contentPane, gbc);
        return contentPane;
    }

    public static void configureNoteLabel(final JLabel noteLabel) {
        if (noteLabel.getFont() != null) {
            noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC));
        }
        noteLabel.setForeground(new Color(0, 0, 92));
    }





    public static JPanel createLabelComponentPane(JLabel jLabel, JComponent jComponent)  {
        JPanel pane = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints("fill=NONE,anchor=WEST");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets.bottom = 0;

        if (jLabel != null) {
            pane.add(jLabel, gbc);
            gbc.gridx++;
        }

        if (jComponent != null) {
            gbc.weightx = 1;
            pane.add(jComponent, gbc);
        }

        return pane;
    }

    public static JPanel createParamPane(Parameter param)  {

        JLabel jLabel = param.getEditor().getLabelComponent();
        JComponent jComponent = param.getEditor().getEditorComponent();

        JPanel jPanel = createLabelComponentPane(jLabel, jComponent);

        return jPanel;
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
