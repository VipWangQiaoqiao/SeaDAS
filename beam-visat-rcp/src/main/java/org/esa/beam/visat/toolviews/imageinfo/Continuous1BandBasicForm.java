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

package org.esa.beam.visat.toolviews.imageinfo;

import com.jidesoft.swing.TitledSeparator;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.util.PropertyMap;
import org.esa.beam.util.math.Range;
import org.esa.beam.visat.VisatApp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

class Continuous1BandBasicForm implements ColorManipulationChildForm {

    private final ColorManipulationForm parentForm;
    private final JPanel contentPanel;
    private final AbstractButton logDisplayButton;

    private final MoreOptionsForm moreOptionsForm;
    private ColorPaletteChooser colorPaletteChooser;
    private JFormattedTextField minField;
    private JFormattedTextField maxField;
    private String currentMinFieldValue = "";
    private String currentMaxFieldValue = "";
    private final DiscreteCheckBox discreteCheckBox;
    private final JCheckBox loadWithCPDFileValuesCheckBox;
    private final ColorPaletteSchemes standardColorPaletteSchemes;
    private JButton bandDataButton;
    private JButton exportButton;
    private JButton paletteInversionButton;
    private JLabel colorSchemeJLabel;
    private JLabel cpdFileNameJLabel;
    private TitledSeparator headerSeparator;


    final Boolean[] minFieldActivated = {new Boolean(false)};
    final Boolean[] maxFieldActivated = {new Boolean(false)};
    final Boolean[] listenToLogDisplayButtonEnabled = {true};
    final Boolean[] basicSwitcherIsActive;


    private final ImageInfoEditor2 imageInfoEditor;

    private enum RangeKey {FromPaletteSource, FromData, FromMinMaxFields, FromCurrentPalette, ToggleLog, InvertPalette, Dummy;}

    private boolean shouldFireChooserEvent;
    private boolean hidden = false;

    Continuous1BandBasicForm(final ColorManipulationForm parentForm, final Boolean[] basicSwitcherIsActive) {

        imageInfoEditor = new ImageInfoEditor2(parentForm);

        ColorPalettesManager.loadAvailableColorPalettes(parentForm.getIODir());

        this.parentForm = parentForm;
        this.basicSwitcherIsActive = basicSwitcherIsActive;

        colorSchemeJLabel = new JLabel("");
        colorSchemeJLabel.setToolTipText("The color data is stored in the band.  Astericks suffix (*) denotes that some parameters have been altered");
        cpdFileNameJLabel = new JLabel("");
        cpdFileNameJLabel.setToolTipText("The color data is stored in the band.  Astericks suffix (*) denotes that some parameters have been altered ");
        //     cpdFileNameJLabel.setToolTipText("Currently loaded cpd file name.  Note that the cpd data has been stored within the current band and any subsequent alterations to the cpd file will not show up unless the file is reloaded");


        standardColorPaletteSchemes = new ColorPaletteSchemes(parentForm.getIODir(), ColorPaletteSchemes.Id.SELECTOR, true, parentForm.getProductSceneView().getSceneImage().getConfiguration());
        VisatApp.getApp().clearStatusBarMessage();

        loadWithCPDFileValuesCheckBox = new JCheckBox("Load cpd file exact values", false);
        loadWithCPDFileValuesCheckBox.setToolTipText("When loading a new cpd file, use it's actual value and overwrite user min/max values");


        colorPaletteChooser = new ColorPaletteChooser();
        colorPaletteChooser.setPreferredSize(new Dimension(180, 30));
        colorPaletteChooser.setMaximumRowCount(20);

        exportButton = new JButton("Save");
        exportButton.setToolTipText("Save color palette"); /*I18N*/
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parentForm.exportColorPaletteDef();
                parentForm.applyChanges();
                colorPaletteChooser.resetRenderer();
                parentForm.applyChanges();
            }
        });
        exportButton.setEnabled(true);


        paletteInversionButton = new JButton("Reverse");
        paletteInversionButton.setToolTipText("Reverse (invert) palette"); /*I18N*/
        paletteInversionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                applyChanges(RangeKey.InvertPalette);
            }
        });
        paletteInversionButton.setEnabled(true);


        JPanel colorPaletteJPanel = getColorPaletteFilePanel("Cpd File");
        colorPaletteJPanel.setToolTipText("Load and/or Save a CPD (Color Palette Definition) File");
        JPanel rangeJPanel = getRangePanel("Range");
        JPanel colorPaletteInfoComboBoxJPanel = getSchemaPanel("Scheme");


        JPanel basicPanel = GridBagUtils.createPanel();
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.gridx = 0;
        basicPanel.add(spacer1(new JLabel()), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        basicPanel.add(colorPaletteInfoComboBoxJPanel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        basicPanel.add(spacer1(new JLabel()), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        basicPanel.add(colorPaletteJPanel, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        basicPanel.add(spacer1(new JLabel()), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        basicPanel.add(rangeJPanel, gbc);


        contentPanel = GridBagUtils.createPanel();

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(basicPanel, gbc);


        shouldFireChooserEvent = true;

        colorPaletteChooser.addActionListener(createPaletteListener());

        bandDataButton.addActionListener(createBandListener(RangeKey.FromData));


        colorPaletteChooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VisatApp.getApp().clearStatusBarMessage();
            }
        });


        maxField.getDocument().addDocumentListener(new DocumentListener() {
            @Override

            public void insertUpdate(DocumentEvent documentEvent) {
                handleMaxTextfield();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }
        });

        minField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                handleMinTextfield();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }
        });


//        bandDataButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                VisatApp.getApp().clearStatusBarMessage();
//            }
//        });


        moreOptionsForm = new MoreOptionsForm(parentForm, true);
        discreteCheckBox = new DiscreteCheckBox(parentForm);
        moreOptionsForm.addRow(discreteCheckBox);


        logDisplayButton = LogDisplay.createButton();
        logDisplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (listenToLogDisplayButtonEnabled[0]) {
                    listenToLogDisplayButtonEnabled[0] = false;
                    logDisplayButton.setSelected(!logDisplayButton.isSelected());

                    applyChanges(RangeKey.ToggleLog);
                    listenToLogDisplayButtonEnabled[0] = true;
                }
            }
        });


        standardColorPaletteSchemes.getjComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (standardColorPaletteSchemes.getjComboBox().getSelectedIndex() != 0) {
                    if (standardColorPaletteSchemes.isjComboBoxShouldFire()) {
                        standardColorPaletteSchemes.setjComboBoxShouldFire(false);

                        handleColorPaletteInfoComboBoxSelection(standardColorPaletteSchemes.getjComboBox(), false);
                        standardColorPaletteSchemes.reset();
                        standardColorPaletteSchemes.setjComboBoxShouldFire(true);
                    }
                }
            }
        });


    }


    private void handleMaxTextfield() {

        if (!currentMaxFieldValue.equals(maxField.getText().toString())) {
            if (!maxFieldActivated[0] && !basicSwitcherIsActive[0]) {
                maxFieldActivated[0] = true;
                applyChanges(RangeKey.FromMinMaxFields);
                maxFieldActivated[0] = false;
            }
        }
    }

    private void handleMinTextfield() {

        if (!currentMinFieldValue.equals(minField.getText().toString())) {
            if (!minFieldActivated[0] && !basicSwitcherIsActive[0]) {
                minFieldActivated[0] = true;
                applyChanges(RangeKey.FromMinMaxFields);
                minFieldActivated[0] = false;
            }
        }
    }

    private JPanel getSchemaPanel(String title) {


        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(title));
        //   jPanel.setToolTipText("Load a preset color scheme (sets the color-palette, min, max, and log fields)");
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 5, 0, 0);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        jPanel.add(colorSchemeJLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        jPanel.add(standardColorPaletteSchemes.getjComboBox(), gbc);

        return jPanel;
    }

    private JPanel getRangePanel(String title) {
        JPanel rangeJPanel = new JPanel(new GridBagLayout());
        rangeJPanel.setBorder(BorderFactory.createTitledBorder(title));
        final GridBagConstraints rangeGbc = new GridBagConstraints();


        minField = getNumberTextField(0);
        maxField = getNumberTextField(1);

        JTextField tmpSizeTextField = new JTextField(("12345678901234567890"));
        minField.setMinimumSize(tmpSizeTextField.getPreferredSize());
        minField.setMaximumSize(tmpSizeTextField.getPreferredSize());
        minField.setPreferredSize(tmpSizeTextField.getPreferredSize());
        maxField.setMinimumSize(tmpSizeTextField.getPreferredSize());
        maxField.setMaximumSize(tmpSizeTextField.getPreferredSize());
        maxField.setPreferredSize(tmpSizeTextField.getPreferredSize());


        JPanel minMaxTextfields = getMinMaxTextfields();


        bandDataButton = new JButton("Set from Band Data");
        bandDataButton.setToolTipText("Set min and max value to corresponding data value in the band statistics");
        bandDataButton.setMaximumSize(bandDataButton.getMinimumSize());
        bandDataButton.setPreferredSize(bandDataButton.getMinimumSize());
        bandDataButton.setMinimumSize(bandDataButton.getMinimumSize());


        rangeGbc.fill = GridBagConstraints.HORIZONTAL;
        rangeGbc.anchor = GridBagConstraints.WEST;
        rangeGbc.gridy = 0;
        rangeGbc.gridx = 0;
        rangeGbc.weightx = 1.0;
        rangeJPanel.add(minMaxTextfields, rangeGbc);

        rangeGbc.fill = GridBagConstraints.NONE;
        rangeGbc.gridy = 1;
        rangeGbc.gridx = 0;
        rangeJPanel.add(bandDataButton, rangeGbc);

        return rangeJPanel;
    }

    private JPanel getColorPaletteFilePanel(String title) {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(title));
        final GridBagConstraints gbc = new GridBagConstraints();


        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        jPanel.add(cpdFileNameJLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        jPanel.add(colorPaletteChooser, gbc);

        gbc.gridy++;
        jPanel.add(loadWithCPDFileValuesCheckBox, gbc);


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1.0;
        headerSeparator = new TitledSeparator("", TitledSeparator.TYPE_PARTIAL_ETCHED, SwingConstants.LEFT);
        gbc.gridy++;
        jPanel.add(headerSeparator, gbc);



//        gbc.gridy++;
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets(4, 0, 0, 0);
//        paletteInversionButton.setMinimumSize(paletteInversionButton.getMinimumSize());
//        paletteInversionButton.setMaximumSize(paletteInversionButton.getMinimumSize());
//        paletteInversionButton.setPreferredSize(paletteInversionButton.getMinimumSize());
//
//        jPanel.add(paletteInversionButton, gbc);
//
//
//
//
//
//
//        gbc.gridy++;
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets(4, 0, 0, 0);
//        exportButton.setMinimumSize(exportButton.getMinimumSize());
//        exportButton.setMaximumSize(exportButton.getMinimumSize());
//        exportButton.setPreferredSize(exportButton.getMinimumSize());
//
//        jPanel.add(exportButton, gbc);


        JPanel buttonsJPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbcButtons = new GridBagConstraints();

        gbcButtons.anchor = GridBagConstraints.WEST;
        gbcButtons.insets = new Insets(0, 5, 0, 0);
        gbcButtons.gridy = 0;
        gbcButtons.gridx = 0;
        gbcButtons.weightx = 1.0;

        gbcButtons.fill = GridBagConstraints.NONE;
        gbcButtons.insets = new Insets(4, 0, 0, 0);
        paletteInversionButton.setMinimumSize(paletteInversionButton.getMinimumSize());
        paletteInversionButton.setMaximumSize(paletteInversionButton.getMinimumSize());
        paletteInversionButton.setPreferredSize(paletteInversionButton.getMinimumSize());

        buttonsJPanel.add(paletteInversionButton, gbcButtons);


        gbcButtons.gridx++;
        gbcButtons.fill = GridBagConstraints.NONE;
        gbcButtons.anchor = GridBagConstraints.EAST;
        gbcButtons.insets = new Insets(4, 0, 0, 0);
        exportButton.setMinimumSize(exportButton.getMinimumSize());
        exportButton.setMaximumSize(exportButton.getMinimumSize());
        exportButton.setPreferredSize(exportButton.getMinimumSize());

        buttonsJPanel.add(exportButton, gbcButtons);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);

        jPanel.add(buttonsJPanel, gbc);

        return jPanel;
    }


    private void handleColorPaletteInfoComboBoxSelection(JComboBox jComboBox, boolean isDefaultList) {
        ColorPaletteInfo colorPaletteInfo = (ColorPaletteInfo) jComboBox.getSelectedItem();

        PropertyMap configuration = parentForm.getProductSceneView().getSceneImage().getConfiguration();
        boolean useColorBlindPalettes = ColorPaletteSchemes.getUseColorBlind(configuration);

        if (colorPaletteInfo.getCpdFilename(useColorBlindPalettes) != null && colorPaletteInfo.isEnabled()) {


            try {

                File cpdFile = new File(parentForm.getIODir(), colorPaletteInfo.getCpdFilename(useColorBlindPalettes));
                ColorPaletteDef colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(cpdFile);


                boolean origShouldFireChooserEvent = shouldFireChooserEvent;
                shouldFireChooserEvent = false;

                colorPaletteChooser.setSelectedColorPaletteDefinition(colorPaletteDef);



                parentForm.getImageInfo().getColorPaletteSourcesInfo().setCpdFileName(colorPaletteInfo.getCpdFilename(useColorBlindPalettes));


                parentForm.getImageInfo().getColorPaletteSourcesInfo().setColorBarLabels(colorPaletteInfo.getColorBarLabels());
                parentForm.getImageInfo().getColorPaletteSourcesInfo().setColorBarTitle(colorPaletteInfo.getColorBarTitle());
                parentForm.getImageInfo().getColorPaletteSourcesInfo().setColorBarMin(colorPaletteInfo.getMinValue());
                parentForm.getImageInfo().getColorPaletteSourcesInfo().setColorBarMax(colorPaletteInfo.getMaxValue());
                parentForm.getImageInfo().getColorPaletteSourcesInfo().setLogScaled(colorPaletteInfo.isLogScaled());


                if (ImageLegend.allowColorbarAutoReset(configuration)) {
                    parentForm.getImageInfo().getColorPaletteSourcesInfo().setColorBarInitialized(false);
                    parentForm.getProductSceneView().getColorBarParamInfo().setParamsInitialized(false);
                }


                applyChanges(colorPaletteInfo.getMinValue(),
                        colorPaletteInfo.getMaxValue(),
                        colorPaletteDef,
                        colorPaletteDef.isLogScaled(),
                        colorPaletteInfo.isLogScaled(), colorPaletteInfo.getRootName(), isDefaultList);


                shouldFireChooserEvent = origShouldFireChooserEvent;

                String id = parentForm.getProductSceneView().getRaster().getDisplayName();
                //   VisatApp.getApp().setStatusBarMessage("Loaded '" + colorPaletteInfo.getName() + "' color schema settings into '" + id);
                String colorPaletteName = (colorPaletteInfo.getName() != null) ? colorPaletteInfo.getName() : "";
                //     VisatApp.getApp().setStatusBarMessage("'" + colorPaletteName + "' color scheme loaded");


            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


    }


    private JLabel spacer1(JLabel lineSpacer) {
        lineSpacer.setText("SPACER");
        Dimension lineSpaceDimension = lineSpacer.getPreferredSize();
        lineSpacer.setPreferredSize(lineSpaceDimension);
        lineSpacer.setMinimumSize(lineSpaceDimension);
        lineSpacer.setText("");
        return lineSpacer;
    }


    private JPanel getMinMaxTextfields() {
        JPanel minMaxTextfields = GridBagUtils.createPanel();
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.anchor = GridBagConstraints.WEST;


        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        minMaxTextfields.add(new JLabel("Min:"), gbc);

        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        minMaxTextfields.add(minField, gbc);


        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        minMaxTextfields.add(new JLabel("Max:"), gbc);
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        minMaxTextfields.add(maxField, gbc);
        return minMaxTextfields;
    }

    @Override
    public Component getContentPanel() {
        return contentPanel;
    }

    @Override
    public void handleFormShown(ProductSceneView productSceneView) {
        hidden = false;
        updateFormModel(productSceneView);
    }

    @Override
    public void handleFormHidden(ProductSceneView productSceneView) {
        hidden = true;
        if (imageInfoEditor.getModel() != null) {
            imageInfoEditor.setModel(null);
        }
    }

    @Override
    public void updateFormModel(ProductSceneView productSceneView) {

        final ImageInfo imageInfo = productSceneView.getImageInfo();

        if (!hidden) {
            ColorPalettesManager.loadAvailableColorPalettes(parentForm.getIODir());
            colorPaletteChooser.reloadPalettes();
        }

        final ColorPaletteDef cpd = imageInfo.getColorPaletteDef();

        final boolean logScaled = imageInfo.isLogScaled();
        final boolean discrete = cpd.isDiscrete();

        colorPaletteChooser.setLog10Display(logScaled);
        colorPaletteChooser.setDiscreteDisplay(discrete);

        shouldFireChooserEvent = false;
        colorPaletteChooser.setSelectedColorPaletteDefinition(cpd);


        discreteCheckBox.setDiscreteColorsMode(discrete);
        logDisplayButton.setSelected(logScaled);

        parentForm.revalidateToolViewPaneControl();

        if (!minFieldActivated[0]) {
            minField.setValue(cpd.getMinDisplaySample());
            currentMinFieldValue = minField.getText().toString();
        }

        if (!maxFieldActivated[0]) {
            maxField.setValue(cpd.getMaxDisplaySample());
            currentMaxFieldValue = maxField.getText().toString();
        }


        boolean originalStandardShouldFire = standardColorPaletteSchemes.isjComboBoxShouldFire();

        standardColorPaletteSchemes.setjComboBoxShouldFire(false);

        standardColorPaletteSchemes.reset();

        String colorPaletteSchemeName = parentForm.getProductSceneView().getImageInfo().getColorPaletteSourcesInfo().getSchemeName();
        if (colorPaletteSchemeName != null && colorPaletteSchemeName.length() > 0) {

            ColorPaletteInfo savedColorPaletteInfo = null;

            double min = imageInfo.getColorPaletteDef().getMinDisplaySample();
            double max = imageInfo.getColorPaletteDef().getMaxDisplaySample();
            //todo Danny
            String currentSchemeName = parentForm.getProductSceneView().getImageInfo().getColorPaletteSourcesInfo().getDescriptiveColorSchemeName(min, max, logScaled);

            if (parentForm.getProductSceneView().getImageInfo().getColorPaletteSourcesInfo().isSchemeDefault()) {

                //   colorSchemeJLabel.setText( currentSchemeName + "*");
                colorSchemeJLabel.setText(currentSchemeName);

            } else {
                //   colorSchemeJLabel.setText(currentSchemeName + "*");
                colorSchemeJLabel.setText(currentSchemeName);

                savedColorPaletteInfo = standardColorPaletteSchemes.setSchemeName(colorPaletteSchemeName);
            }

            // todo Would be good to test here to see if saved scheme actually matches loaded values
//            if (savedColorPaletteInfo != null) {
//               double min = savedColorPaletteInfo.getMinValue();
//               double max = savedColorPaletteInfo.getMaxValue();
//               String cdp = savedColorPaletteInfo.getCpdFilenameStandard();
//                boolean log = savedColorPaletteInfo.isLogScaled();
//            }


        } else {
            colorSchemeJLabel.setText("none");
        }

        // todo this is extra, without this then comboBox will retain selection
        standardColorPaletteSchemes.reset();

        if (parentForm.getImageInfo().getColorPaletteSourcesInfo().getCpdFileName() != null) {
//            cpdFileNameJLabel.setText(parentForm.getImageInfo().getColorPaletteSourcesInfo().getDescriptiveCpdFileName()+"*");
            cpdFileNameJLabel.setText(parentForm.getImageInfo().getColorPaletteSourcesInfo().getDescriptiveCpdFileName());
        } else {
            cpdFileNameJLabel.setText("unknown");
        }


        standardColorPaletteSchemes.setjComboBoxShouldFire(originalStandardShouldFire);


        shouldFireChooserEvent = true;


    }


    @Override
    public void resetFormModel(ProductSceneView productSceneView) {
        updateFormModel(productSceneView);
        parentForm.revalidateToolViewPaneControl();
    }

    @Override
    public void handleRasterPropertyChange(ProductNodeEvent event, RasterDataNode raster) {
        if (event.getPropertyName().equals(RasterDataNode.PROPERTY_NAME_STX)) {
            updateFormModel(parentForm.getProductSceneView());
        }
    }

    @Override
    public RasterDataNode[] getRasters() {
        return parentForm.getProductSceneView().getRasters();
    }

    @Override
    public MoreOptionsForm getMoreOptionsForm() {
        return moreOptionsForm;
    }

    @Override
    public AbstractButton[] getToolButtons() {
        return new AbstractButton[]{
                logDisplayButton,
        };
    }

    private ActionListener createPaletteListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (colorPaletteChooser.getSelectedIndex() != 0) {
                    applyChanges(RangeKey.FromPaletteSource);
                    //    applyChanges(RangeKey.Dummy);
                }

            }
        };
    }

    private ActionListener createBandListener(final RangeKey key) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                applyChanges(key);

            }
        };
    }


    private JFormattedTextField getNumberTextField(double value) {
        final NumberFormatter formatter = new NumberFormatter(new DecimalFormat("0.0###########"));
        formatter.setValueClass(Double.class); // to ensure that double values are returned
        final JFormattedTextField numberField = new JFormattedTextField(formatter);
        numberField.setValue(value);
        final Dimension preferredSize = numberField.getPreferredSize();
        preferredSize.width = 70;
        numberField.setPreferredSize(preferredSize);
        return numberField;
    }

    private void applyChanges(RangeKey key) {
        if (shouldFireChooserEvent) {

            boolean checksOut = true;

            final ColorPaletteDef selectedCPD = colorPaletteChooser.getSelectedColorPaletteDefinition();
            final ImageInfo currentInfo = parentForm.getImageInfo();
            final ColorPaletteDef currentCPD = currentInfo.getColorPaletteDef();
            final ColorPaletteDef deepCopy = selectedCPD.createDeepCopy();
            deepCopy.setDiscrete(currentCPD.isDiscrete());

            final double min;
            final double max;
            final boolean isSourceLogScaled;
            final boolean isTargetLogScaled;
            final ColorPaletteDef cpd;
            final boolean autoDistribute;


            switch (key) {
                case FromData:
                    parentForm.getImageInfo().getColorPaletteSourcesInfo().setAlteredScheme(true);
                    final Stx stx = parentForm.getStx(parentForm.getProductSceneView().getRaster());
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = currentInfo.isLogScaled();
                    min = stx.getMinimum();
                    max = stx.getMaximum();
                    cpd = currentCPD;
                    autoDistribute = true;
                    break;
                case FromMinMaxFields:
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = currentInfo.isLogScaled();
                    parentForm.getImageInfo().getColorPaletteSourcesInfo().setAlteredScheme(true);


                    if (testMinMax(minField.getText().toString(), maxField.getText().toString())) {
                        min = Double.parseDouble(minField.getText().toString());
                        max = Double.parseDouble(maxField.getText().toString());
                    } else {
                        checksOut = false;
                        min = 0; //bogus unused values set just so it is initialized to make idea happy
                        max = 0; //bogus unused values set just so it is initialized to make idea happy
                    }
                    cpd = currentCPD;
                    autoDistribute = true;
                    break;
                case ToggleLog:
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = !currentInfo.isLogScaled();
                    parentForm.getImageInfo().getColorPaletteSourcesInfo().setAlteredScheme(true);


                    min = currentCPD.getMinDisplaySample();
                    max = currentCPD.getMaxDisplaySample();
                    cpd = currentCPD;

                    autoDistribute = true;
                    break;
                case InvertPalette:
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = currentInfo.isLogScaled();
                    parentForm.getImageInfo().getColorPaletteSourcesInfo().toggleInvertedCpd();

                    min = currentCPD.getMinDisplaySample();
                    max = currentCPD.getMaxDisplaySample();
                    cpd = currentCPD;

                    autoDistribute = true;
                    break;
                default:
                    currentInfo.getColorPaletteSourcesInfo().setSchemeName(null);
                    String cpdFileName = ColorPalettesManager.getNameFor(selectedCPD);
                    currentInfo.getColorPaletteSourcesInfo().setCpdFileName(cpdFileName);

                    if (loadWithCPDFileValuesCheckBox.isSelected()) {
                        isSourceLogScaled = selectedCPD.isLogScaled();
                        isTargetLogScaled = selectedCPD.isLogScaled();
                        autoDistribute = false;
                        currentInfo.setLogScaled(isTargetLogScaled);
                        final Range rangeFromFile = colorPaletteChooser.getRangeFromFile();

                        min = rangeFromFile.getMin();
                        max = rangeFromFile.getMax();
//                        min = selectedCPD.getMinDisplaySample();
//                        max = selectedCPD.getMaxDisplaySample();
                        cpd = deepCopy;
                        deepCopy.setLogScaled(isTargetLogScaled);
                        deepCopy.setAutoDistribute(autoDistribute);


                        if (testMinMax(min, max, isTargetLogScaled)) {
                            listenToLogDisplayButtonEnabled[0] = false;
                            logDisplayButton.setSelected(isTargetLogScaled);
                            listenToLogDisplayButtonEnabled[0] = true;
                        }
                    } else {
                        isSourceLogScaled = selectedCPD.isLogScaled();
                        isTargetLogScaled = currentInfo.isLogScaled();
                        min = currentCPD.getMinDisplaySample();
                        max = currentCPD.getMaxDisplaySample();
                        cpd = deepCopy;
                        autoDistribute = true;
                    }


            }


             if (checksOut && testMinMax(min, max, isTargetLogScaled)) {
                if (key == RangeKey.InvertPalette) {
                    currentInfo.setColorPaletteDefInvert(cpd);
                } else {
                    currentInfo.setColorPaletteDef(cpd, min, max, autoDistribute, isSourceLogScaled, isTargetLogScaled);
                }

                if (key == RangeKey.ToggleLog) {
                    currentInfo.setLogScaled(isTargetLogScaled);
                    colorPaletteChooser.setLog10Display(isTargetLogScaled);
                }
                currentMinFieldValue = Double.toString(min);
                currentMaxFieldValue = Double.toString(max);
                parentForm.applyChanges();


            }

        }
    }


    private boolean isNumber(String string) {
        try {
            double d = Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    private boolean testMinMax(String min, String max) {
        boolean checksOut = true;

        if (!isNumber(min)) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING NOT UPDATING IMAGE BECAUSE: Min Textfield is not a number");
        }

        if (!isNumber(max)) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING NOT UPDATING IMAGE BECAUSE: Max Textfield is not a number");
        }

        if (checksOut) {
            VisatApp.getApp().clearStatusBarMessage();
        }

        return checksOut;
    }


    private boolean testMinMax(double min, double max, boolean isLogScaled) {
        boolean checksOut = true;


        if (min == max) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING NOT UPDATING IMAGE BECAUSE: Min cannot equal Max");
        }

        if (min > max) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING NOT UPDATING IMAGE BECAUSE: Min cannot be greater than Max");
        }

        if ((isLogScaled) && min == 0) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING NOT UPDATING IMAGE BECAUSE: Min cannot be zero in log scaling mode");
        }

        if ((isLogScaled) && min < 0) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING NOT UPDATING IMAGE BECAUSE: Min cannot be negative in log scaling mode");
        }

        if (checksOut) {
            VisatApp.getApp().clearStatusBarMessage();
        }
        return checksOut;
    }

    private void applyChanges(double min,
                              double max,
                              ColorPaletteDef selectedCPD,
                              boolean isSourceLogScaled,
                              boolean isTargetLogScaled,
                              String colorSchemaName,
                              boolean isDefaultList) {


        final ImageInfo currentInfo = parentForm.getImageInfo();
        final ColorPaletteDef currentCPD = currentInfo.getColorPaletteDef();
        final ColorPaletteDef deepCopy = selectedCPD.createDeepCopy();
        deepCopy.setDiscrete(currentCPD.isDiscrete());
        deepCopy.setAutoDistribute(true);

        final boolean autoDistribute = true;
        currentInfo.setLogScaled(isTargetLogScaled);
        currentInfo.setColorPaletteDef(selectedCPD, min, max, autoDistribute, isSourceLogScaled, isTargetLogScaled);
        currentInfo.getColorPaletteSourcesInfo().setSchemeName(colorSchemaName);
        currentInfo.getColorPaletteSourcesInfo().setSchemeDefault(isDefaultList);


        currentMinFieldValue = Double.toString(min);
        currentMaxFieldValue = Double.toString(max);

        parentForm.applyChanges();
    }


}