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

    private static final String HORIZONTAL_STR = "Horizontal";
    private static final String VERTICAL_STR = "Vertical";

    private ParamGroup legendParamGroup;
    private ImageLegend imageLegend;

    @Override
    public void actionPerformed(CommandEvent event) {
        exportImage(getVisatApp(), getImageFileFilters(), event.getSelectableCommand());
    }

    @Override
    public void updateState(final CommandEvent event) {
        ProductSceneView view = getVisatApp().getSelectedProductSceneView();
        boolean enabled = view != null && !view.isRGB();
        event.getSelectableCommand().setEnabled(enabled);

    }

    @Override
    protected void configureFileChooser(BeamFileChooser fileChooser, ProductSceneView view, String imageBaseName) {
        legendParamGroup = createLegendParamGroup();
        legendParamGroup.setParameterValues(getVisatApp().getPreferences(), null);
        modifyHeaderText(legendParamGroup, view.getRaster());
        fileChooser.setDialogTitle(getVisatApp().getAppName() + " - Export Colour Legend Image"); /*I18N*/
        fileChooser.setCurrentFilename(imageBaseName + "_legend");
        final RasterDataNode raster = view.getRaster();
        imageLegend = new ImageLegend(raster.getImageInfo(), raster);
        fileChooser.setAccessory(createImageLegendAccessory(getVisatApp(),
                fileChooser,
                legendParamGroup,
                imageLegend));
    }

    @Override
    protected RenderedImage createImage(String imageFormat, ProductSceneView view) {
        transferParamsToImageLegend(legendParamGroup, imageLegend);
        imageLegend.setBackgroundTransparencyEnabled(isTransparencySupportedByFormat(imageFormat));
        return imageLegend.createImage();
    }

    @Override
    protected boolean isEntireImageSelected() {
        return true;
    }

    private static ParamGroup createLegendParamGroup() {
        ParamGroup paramGroup = new ParamGroup();

        Parameter param = new Parameter("legend.usingHeader", Boolean.TRUE);
        param.getProperties().setLabel("Show header text");
        paramGroup.addParameter(param);

        param = new Parameter("legend.evenDistribution", Boolean.TRUE);
        param.getProperties().setLabel("Show Auto Labels");
        paramGroup.addParameter(param);

        param = new Parameter("legend.headerText", "");
        param.getProperties().setLabel("Header text");
        param.getProperties().setNumCols(24);
        param.getProperties().setNullValueAllowed(true);
        paramGroup.addParameter(param);

        // DANNY
        param = new Parameter("legend.header.units.text", "");
        param.getProperties().setLabel("Header Units text");
        param.getProperties().setNumCols(24);
        param.getProperties().setNullValueAllowed(true);
        paramGroup.addParameter(param);

        param = new Parameter("legend.fullCustomAddThesePoints", "");
        param.getProperties().setLabel("Add These Points");
        param.getProperties().setNumCols(24);
        param.getProperties().setNullValueAllowed(true);
        paramGroup.addParameter(param);


        param = new Parameter("legend.orientation", HORIZONTAL_STR);
        param.getProperties().setLabel("Orientation");
        param.getProperties().setValueSet(new String[]{HORIZONTAL_STR, VERTICAL_STR});
        param.getProperties().setValueSetBound(true);
        paramGroup.addParameter(param);

        param = new Parameter("legend.fontSize", 14);
        param.getProperties().setLabel("Font size");
        param.getProperties().setMinValue(4);
        param.getProperties().setMaxValue(100);
        paramGroup.addParameter(param);


        param = new Parameter("legend.numberOfTicks", 8);
        param.getProperties().setLabel("Number of Ticks");
        param.getProperties().setMinValue(0);
        param.getProperties().setMaxValue(40);
        paramGroup.addParameter(param);

        param = new Parameter("legend.decimalPlaces", 1);
        param.getProperties().setLabel("Decimal Places");
        param.getProperties().setMinValue(0);
        param.getProperties().setMaxValue(5);
        paramGroup.addParameter(param);

        param = new Parameter("legend.foregroundColor", Color.black);
        param.getProperties().setLabel("Foreground colour");
        paramGroup.addParameter(param);

        param = new Parameter("legend.backgroundColor", Color.white);
        param.getProperties().setLabel("Background colour");
        paramGroup.addParameter(param);

        param = new Parameter("legend.backgroundTransparency", 0.0f);
        param.getProperties().setLabel("Background transparency");
        param.getProperties().setMinValue(0.0f);
        param.getProperties().setMaxValue(1.0f);
        paramGroup.addParameter(param);

        param = new Parameter("legend.antialiasing", Boolean.TRUE);
        param.getProperties().setLabel("Perform anti-aliasing");
        paramGroup.addParameter(param);

        return paramGroup;
    }

    private static void modifyHeaderText(ParamGroup legendParamGroup, RasterDataNode raster) {
        String name = raster.getName();
        String unit = raster.getUnit() != null ? raster.getUnit() : "-";
        unit = unit.replace('*', ' ');
        // DANNY
        //   String headerText = name + "   [" + unit + "]";
        String headerText = name;
        legendParamGroup.getParameter("legend.headerText").setValue(headerText, null);

        String headerUnitsText = "     (" + unit + ")";
        legendParamGroup.getParameter("legend.header.units.text").setValue(headerUnitsText, null);
    }

    private static JComponent createImageLegendAccessory(final VisatApp visatApp,
                                                         final JFileChooser fileChooser,
                                                         final ParamGroup legendParamGroup,
                                                         final ImageLegend imageLegend) {
        final JButton button = new JButton("Edit Colorbar Parameters...");
        button.setMnemonic('P');
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final BeamFileFilter fileFilter = (BeamFileFilter) fileChooser.getFileFilter();
//                final ImageLegendDialog dialog = new ImageLegendDialog(visatApp,
//                                                                       legendParamGroup,
//                                                                       imageLegend,
//                                                                       isTransparencySupportedByFormat(
//                                                                               fileFilter.getFormatName()));
                final ImageLegendDialog dialog = new ImageLegendDialog(visatApp,
                        legendParamGroup,
                        imageLegend,
                        false);
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

        value = legendParamGroup.getParameter("legend.usingHeader").getValue();
        imageLegend.setUsingHeader((Boolean) value);

        value = legendParamGroup.getParameter("legend.headerText").getValue();
        imageLegend.setHeaderText((String) value);
        // DANNY
        value = legendParamGroup.getParameter("legend.header.units.text").getValue();
        imageLegend.setHeaderUnitsText((String) value);

        value = legendParamGroup.getParameter("legend.fullCustomAddThesePoints").getValue();
        imageLegend.setFullCustomAddThesePoints((String) value);

        value = legendParamGroup.getParameter("legend.orientation").getValue();
        imageLegend.setOrientation(HORIZONTAL_STR.equals(value) ? ImageLegend.HORIZONTAL : ImageLegend.VERTICAL);

        value = legendParamGroup.getParameter("legend.fontSize").getValue();
        imageLegend.setFont(imageLegend.getFont().deriveFont(((Number) value).floatValue()));


        value = legendParamGroup.getParameter("legend.numberOfTicks").getValue();
        imageLegend.setNumberOfTicks((Integer) value);


        value = legendParamGroup.getParameter("legend.decimalPlaces").getValue();
        imageLegend.setDecimalPlaces((Integer) value);

        value = legendParamGroup.getParameter("legend.backgroundColor").getValue();
        imageLegend.setBackgroundColor((Color) value);

        value = legendParamGroup.getParameter("legend.foregroundColor").getValue();
        imageLegend.setForegroundColor((Color) value);

        value = legendParamGroup.getParameter("legend.backgroundTransparency").getValue();
        imageLegend.setBackgroundTransparency(((Number) value).floatValue());

        value = legendParamGroup.getParameter("legend.antialiasing").getValue();
        imageLegend.setAntialiasing((Boolean) value);

        value = legendParamGroup.getParameter("legend.evenDistribution").getValue();
        imageLegend.setEvenDistribution((Boolean) value);
    }


    public static class ImageLegendDialog extends ModalDialog {

        private static final String _HELP_ID = "";

        private VisatApp visatApp;
        private ImageInfo imageInfo;
        private RasterDataNode raster;
        private boolean transparencyEnabled;

        private ParamGroup paramGroup;

        private Parameter usingHeaderParam;
        private Parameter evenDistributionParam;
        private Parameter headerTextParam;
        private Parameter headerUnitsParam;
        private Parameter orientationParam;
        private Parameter fontSizeParam;
        private Parameter numberOfTicksParam;
        private Parameter backgroundColorParam;
        private Parameter foregroundColorParam;
        private Parameter antialiasingParam;
        private Parameter backgroundTransparencyParam;
        private Parameter decimalPlacesParam;
        private Parameter fullCustomAddThesePointsParam;

        public ImageLegendDialog(VisatApp visatApp, ParamGroup paramGroup, ImageLegend imageLegend,
                                 boolean transparencyEnabled) {
            super(visatApp.getMainFrame(), visatApp.getAppName() + " - Colour Legend Properties", ID_OK_CANCEL, _HELP_ID);
            this.visatApp = visatApp;
            imageInfo = imageLegend.getImageInfo();
            raster = imageLegend.getRaster();
            this.transparencyEnabled = transparencyEnabled;
            this.paramGroup = paramGroup;
            initParams();
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

            numberOfTicksParam.setUIEnabled((Boolean) evenDistributionParam.getValue());
            decimalPlacesParam.setUIEnabled((Boolean) evenDistributionParam.getValue());


            backgroundTransparencyParam.setUIEnabled(transparencyEnabled);
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
            getParamGroup().getParameterValues(visatApp.getPreferences());
            super.onOK();
        }


        private void initUI() {

            JLabel lineSpacer = new JLabel("SPACER");
            Dimension lineSpaceDimension = lineSpacer.getPreferredSize();
            lineSpacer.setPreferredSize(lineSpaceDimension);
            lineSpacer.setMinimumSize(lineSpaceDimension);
            lineSpacer.setText("");


            JLabel lineSpacer2 = new JLabel(" ");
            lineSpacer2.setMinimumSize(lineSpaceDimension);
            lineSpacer2.setPreferredSize(lineSpaceDimension);
            JLabel lineSpacer3 = new JLabel(" ");
            lineSpacer3.setMinimumSize(lineSpaceDimension);
            lineSpacer3.setPreferredSize(lineSpaceDimension);


            final JButton previewButton = new JButton("Preview...");
            previewButton.setMnemonic('v');
            previewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showPreview();
                }
            });

            final GridBagConstraints gbc = new GridBagConstraints();
            final JPanel p = GridBagUtils.createPanel();

            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets.top = 0;


            JPanel jPanelTitle = new JPanel(new GridBagLayout());
            jPanelTitle.setBorder(BorderFactory.createTitledBorder("Color Bar Title"));
            final GridBagConstraints gbcTitle = new GridBagConstraints();

            gbcTitle.fill = GridBagConstraints.HORIZONTAL;
            gbcTitle.anchor = GridBagConstraints.WEST;

            gbcTitle.gridx = 0;
            gbcTitle.gridy = 0;
            jPanelTitle.add(usingHeaderParam.getEditor().getEditorComponent(), gbcTitle);

            gbcTitle.gridx = 0;
            gbcTitle.gridy++;
            jPanelTitle.add(headerTextParam.getEditor().getLabelComponent(), gbcTitle);
            gbcTitle.gridx = 1;
            jPanelTitle.add(headerTextParam.getEditor().getEditorComponent(), gbcTitle);

            gbcTitle.gridx = 0;
            gbcTitle.gridy++;
            gbcTitle.insets.top = 3;
            jPanelTitle.add(headerUnitsParam.getEditor().getLabelComponent(), gbcTitle);
            gbcTitle.gridx = 1;
            jPanelTitle.add(headerUnitsParam.getEditor().getEditorComponent(), gbcTitle);


            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(jPanelTitle, gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            p.add(lineSpacer, gbc);


            JPanel jPanelAutoDistrib = new JPanel(new GridBagLayout());
            jPanelAutoDistrib.setBorder(BorderFactory.createTitledBorder("Auto Distributed Points"));
            final GridBagConstraints gbcAutoDistrib = new GridBagConstraints();

            gbcAutoDistrib.fill = GridBagConstraints.HORIZONTAL;
            gbcAutoDistrib.anchor = GridBagConstraints.WEST;

            gbcAutoDistrib.gridx = 0;
            gbcAutoDistrib.gridy = 0;
            jPanelAutoDistrib.add(evenDistributionParam.getEditor().getEditorComponent(), gbcAutoDistrib);

            gbcAutoDistrib.gridx = 0;
            gbcAutoDistrib.gridy++;
            jPanelAutoDistrib.add(numberOfTicksParam.getEditor().getLabelComponent(), gbcAutoDistrib);
            gbcAutoDistrib.gridx = 1;
            jPanelAutoDistrib.add(numberOfTicksParam.getEditor().getEditorComponent(), gbcAutoDistrib);

            gbcAutoDistrib.gridx = 0;
            gbcAutoDistrib.gridy++;
            gbcAutoDistrib.insets.top = 3;
            jPanelAutoDistrib.add(decimalPlacesParam.getEditor().getLabelComponent(), gbcAutoDistrib);
            gbcAutoDistrib.gridx = 1;
            jPanelAutoDistrib.add(decimalPlacesParam.getEditor().getEditorComponent(), gbcAutoDistrib);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(jPanelAutoDistrib, gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            p.add(lineSpacer2, gbc);


            JPanel jPanelFullCustom = new JPanel(new GridBagLayout());
            jPanelFullCustom.setBorder(BorderFactory.createTitledBorder("Full Custom Points"));
            final GridBagConstraints gbcFullCustom = new GridBagConstraints();

            gbcFullCustom.fill = GridBagConstraints.HORIZONTAL;
            gbcFullCustom.anchor = GridBagConstraints.WEST;

            gbcFullCustom.gridx = 0;
            gbcFullCustom.gridy = 0;
            JLabel label = fullCustomAddThesePointsParam.getEditor().getLabelComponent();
            label.setToolTipText("Add values comma delimited.  i.e.  5,7,9");
            jPanelFullCustom.add(label, gbcFullCustom);
            gbcFullCustom.gridx = 1;
            gbcFullCustom.weightx = 1;
            jPanelFullCustom.add(fullCustomAddThesePointsParam.getEditor().getEditorComponent(), gbcFullCustom);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(jPanelFullCustom, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            p.add(lineSpacer3, gbc);


//            gbc.gridx = 0;
//            gbc.gridy++;
//            gbc.insets.top = 3;
//            p.add(numberOfTicksParam.getEditor().getLabelComponent(), gbc);
//            gbc.gridx = 1;
//            p.add(numberOfTicksParam.getEditor().getEditorComponent(), gbc);


//            gbc.gridx = 0;
//            gbc.gridy++;
//            gbc.insets.top = 3;
//            p.add(decimalPlacesParam.getEditor().getLabelComponent(), gbc);
//            gbc.gridx = 1;
//            p.add(decimalPlacesParam.getEditor().getEditorComponent(), gbc);

//            gbc.gridwidth = 1;
//            gbc.gridy++;
//            gbc.gridx = 0;
//            //    gbc.gridwidth = 1;
//            Label label2 = new Label("Full Custom Points Baby");
//            p.add(label2, gbc);
//
//            gbc.gridx = 0;
//            gbc.gridy++;
//            //    gbc.gridwidth = 1;
//            p.add(fullCustomAddThesePointsParam.getEditor().getLabelComponent(), gbc);
//            gbc.gridx = 1;
//            p.add(fullCustomAddThesePointsParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets.top = 10;
            p.add(orientationParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            p.add(orientationParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets.top = 3;
            p.add(fontSizeParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            p.add(fontSizeParam.getEditor().getEditorComponent(), gbc);


            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets.top = 10;
            p.add(foregroundColorParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            p.add(foregroundColorParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets.top = 3;
            p.add(backgroundColorParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            p.add(backgroundColorParam.getEditor().getEditorComponent(), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets.top = 3;
            p.add(backgroundTransparencyParam.getEditor().getLabelComponent(), gbc);
            gbc.gridx = 1;
            p.add(backgroundTransparencyParam.getEditor().getEditorComponent(), gbc);


            gbc.gridy++;

            gbc.insets.top = 10;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            p.add(antialiasingParam.getEditor().getEditorComponent(), gbc);

            gbc.insets.top = 10;
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            p.add(previewButton, gbc);

            p.setBorder(new EmptyBorder(7, 7, 7, 7));

            setContent(p);
        }


        private void initParams() {
            usingHeaderParam = paramGroup.getParameter("legend.usingHeader");
            headerTextParam = paramGroup.getParameter("legend.headerText");
            orientationParam = paramGroup.getParameter("legend.orientation");
            fontSizeParam = paramGroup.getParameter("legend.fontSize");
            numberOfTicksParam = paramGroup.getParameter("legend.numberOfTicks");
            foregroundColorParam = paramGroup.getParameter("legend.foregroundColor");
            backgroundColorParam = paramGroup.getParameter("legend.backgroundColor");
            backgroundTransparencyParam = paramGroup.getParameter("legend.backgroundTransparency");
            antialiasingParam = paramGroup.getParameter("legend.antialiasing");
            evenDistributionParam = paramGroup.getParameter("legend.evenDistribution");
            decimalPlacesParam = paramGroup.getParameter("legend.decimalPlaces");
            headerUnitsParam = paramGroup.getParameter("legend.header.units.text");
            fullCustomAddThesePointsParam = paramGroup.getParameter("legend.fullCustomAddThesePoints");

        }

        private void showPreview() {
            final ImageLegend imageLegend = new ImageLegend(getImageInfo(), raster);
            getImageLegend(imageLegend);
            final BufferedImage image = imageLegend.createImage();
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
            final ModalDialog dialog = new ModalDialog(getParent(), VisatApp.getApp().getAppName() + " - Colour Legend Preview", imageDisplay,
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