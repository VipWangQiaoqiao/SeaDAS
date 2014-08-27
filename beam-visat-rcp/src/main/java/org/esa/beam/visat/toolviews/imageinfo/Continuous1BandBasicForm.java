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

import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.product.ProductSceneView;
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
    private final ColorPaletteChooser colorPaletteChooser;
    private JFormattedTextField minField;
    private JFormattedTextField maxField;
    private String currentMinFieldValue = "";
    private String currentMaxFieldValue = "";
    private final DiscreteCheckBox discreteCheckBox;
    private final JCheckBox loadWithCPDFileValuesCheckBox;
    private final ColorPaletteSchemes defaultColorPaletteSchemes;
    private final ColorPaletteSchemes standardColorPaletteSchemes;
    private JButton bandRange;
    private JLabel colorScheme;
    private JLabel cpdFileNameLabel;


    final Boolean[] minFieldActivated = {new Boolean(false)};
    final Boolean[] maxFieldActivated = {new Boolean(false)};
    final Boolean[] listenToLogDisplayButtonEnabled = {true};
    final Boolean[] basicSwitcherIsActive;


    private final ImageInfoEditor2 imageInfoEditor;

    private enum RangeKey {FromPaletteSource, FromData, FromMinMaxFields, FromCurrentPalette, ToggleLog;}

    private boolean shouldFireChooserEvent;
    private boolean hidden = false;

    Continuous1BandBasicForm(final ColorManipulationForm parentForm, final Boolean[] basicSwitcherIsActive) {

        imageInfoEditor = new ImageInfoEditor2(parentForm);

        ColorPalettesManager.loadAvailableColorPalettes(parentForm.getIODir());

        this.parentForm = parentForm;
        this.basicSwitcherIsActive = basicSwitcherIsActive;

        colorScheme = new JLabel("");
        colorScheme.setToolTipText("Last loaded scheme.  Astericks suffix (*) denotes that user may have modified parameters");
        cpdFileNameLabel = new JLabel("");
        //     cpdFileNameLabel.setToolTipText("Currently loaded cpd file name.  Note that the cpd data has been stored within the current band and any subsequent alterations to the cpd file will not show up unless the file is reloaded");


        defaultColorPaletteSchemes = new ColorPaletteSchemes(parentForm.getIODir(), ColorPaletteSchemes.Id.DEFAULTS, true);
        standardColorPaletteSchemes = new ColorPaletteSchemes(parentForm.getIODir(), ColorPaletteSchemes.Id.STANDARD, true);
        VisatApp.getApp().clearStatusBarMessage();

        loadWithCPDFileValuesCheckBox = new JCheckBox("Load with exact file values", false);
        loadWithCPDFileValuesCheckBox.setToolTipText("When loading a new cpd file, use it's actual value and overwrite user min/max values");


        colorPaletteChooser = new ColorPaletteChooser();
        colorPaletteChooser.setPreferredSize(new Dimension(180, 30));
   //     colorPaletteChooser.setMinimumSize(new Dimension(180, 40));
        colorPaletteChooser.setMaximumRowCount(20);


        JPanel colorPaletteJPanel = getColorPaletteFilePanel("Color Palettes");
        JPanel rangeJPanel = getRangePanel("Range Adjustments");
        JPanel colorPaletteInfoComboBoxJPanel = getSchemaPanel("Color Schemes");


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

        colorPaletteChooser.addActionListener(createPaletteListener(RangeKey.FromCurrentPalette));

        bandRange.addActionListener(createBandListener(RangeKey.FromData));


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


//        bandRange.addActionListener(new ActionListener() {
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

                        boolean originalDefaultShouldFire = defaultColorPaletteSchemes.isjComboBoxShouldFire();
                        defaultColorPaletteSchemes.setjComboBoxShouldFire(false);
                        defaultColorPaletteSchemes.reset();
                        defaultColorPaletteSchemes.setjComboBoxShouldFire(originalDefaultShouldFire);

                        standardColorPaletteSchemes.setjComboBoxShouldFire(true);
                    }
                }
            }
        });

        defaultColorPaletteSchemes.getjComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (defaultColorPaletteSchemes.getjComboBox().getSelectedIndex() != 0) {
                    if (defaultColorPaletteSchemes.isjComboBoxShouldFire()) {
                        defaultColorPaletteSchemes.setjComboBoxShouldFire(false);

                        handleColorPaletteInfoComboBoxSelection(defaultColorPaletteSchemes.getjComboBox(), true);

                        boolean originalStandardShouldFire = standardColorPaletteSchemes.isjComboBoxShouldFire();
                        standardColorPaletteSchemes.setjComboBoxShouldFire(false);
                        standardColorPaletteSchemes.reset();
                        standardColorPaletteSchemes.setjComboBoxShouldFire(originalStandardShouldFire);

                        defaultColorPaletteSchemes.setjComboBoxShouldFire(true);
                    }
                }
            }
        });


    }

    private void handleMaxTextfield() {

        if (!currentMaxFieldValue.equals(maxField.getValue().toString())) {
            if (!maxFieldActivated[0] && !basicSwitcherIsActive[0]) {
                maxFieldActivated[0] = true;
                applyChanges(RangeKey.FromMinMaxFields);
                maxFieldActivated[0] = false;
            }
        }
    }

    private void handleMinTextfield() {

        if (!currentMinFieldValue.equals(minField.getValue().toString())) {
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
        gbc.anchor = GridBagConstraints.WEST;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        //   gbc.insets = new Insets(0, 0, 3, 0);


        //    jPanel.add(colorScheme, gbc);
        //    gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        jPanel.add(new JLabel("Common"), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.gridx = 1;
        jPanel.add(standardColorPaletteSchemes.getjComboBox(), gbc);


        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        jPanel.add(new JLabel("Default"), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.gridx = 1;
        jPanel.add(defaultColorPaletteSchemes.getjComboBox(), gbc);

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


        bandRange = new JButton("Set from Band Data");
        bandRange.setToolTipText("Set min and max value to corresponding data value in the band statistics");
        bandRange.setMaximumSize(bandRange.getMinimumSize());
        bandRange.setPreferredSize(bandRange.getMinimumSize());
        bandRange.setMinimumSize(bandRange.getMinimumSize());


        rangeGbc.fill = GridBagConstraints.HORIZONTAL;
        rangeGbc.anchor = GridBagConstraints.WEST;
        rangeGbc.gridy = 0;
        rangeGbc.gridx = 0;
        rangeGbc.weightx = 1.0;
        rangeJPanel.add(minMaxTextfields, rangeGbc);

        rangeGbc.fill = GridBagConstraints.NONE;
        rangeGbc.gridy = 1;
        rangeGbc.gridx = 0;
        rangeJPanel.add(bandRange, rangeGbc);

        return rangeJPanel;
    }

    private JPanel getColorPaletteFilePanel(String title) {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(title));
        final GridBagConstraints gbc = new GridBagConstraints();


        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        jPanel.add(cpdFileNameLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        jPanel.add(colorPaletteChooser, gbc);

        gbc.gridy++;
        jPanel.add(loadWithCPDFileValuesCheckBox, gbc);

        return jPanel;
    }


    private void handleColorPaletteInfoComboBoxSelection(JComboBox jComboBox, boolean isDefaultList) {
        ColorPaletteInfo colorPaletteInfo = (ColorPaletteInfo) jComboBox.getSelectedItem();

        if (colorPaletteInfo.getCpdFilename() != null && colorPaletteInfo.isEnabled()) {

            try {

                File cpdFile = new File(parentForm.getIODir(), colorPaletteInfo.getCpdFilename());
                ColorPaletteDef colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(cpdFile);


                boolean origShouldFireChooserEvent = shouldFireChooserEvent;
                shouldFireChooserEvent = false;

                colorPaletteChooser.setSelectedColorPaletteDefinition(colorPaletteDef);

                parentForm.getImageInfo().getColorPaletteSourcesInfo().setCpdFileName(colorPaletteInfo.getCpdFilename());

                applyChanges(colorPaletteInfo.getMinValue(),
                        colorPaletteInfo.getMaxValue(),
                        colorPaletteDef,
                        colorPaletteInfo.isSourceLogScaled(),
                        colorPaletteInfo.isLogScaled(), colorPaletteInfo.getName(), isDefaultList);


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
            currentMinFieldValue = minField.getValue().toString();
        }

        if (!maxFieldActivated[0]) {
            maxField.setValue(cpd.getMaxDisplaySample());
            currentMaxFieldValue = maxField.getValue().toString();
        }


        boolean originalDefaultShouldFire = defaultColorPaletteSchemes.isjComboBoxShouldFire();
        boolean originalStandardShouldFire = standardColorPaletteSchemes.isjComboBoxShouldFire();

        defaultColorPaletteSchemes.setjComboBoxShouldFire(false);
        standardColorPaletteSchemes.setjComboBoxShouldFire(false);

        defaultColorPaletteSchemes.reset();
        standardColorPaletteSchemes.reset();

        String colorPaletteSchemeName = parentForm.getProductSceneView().getImageInfo().getColorPaletteSourcesInfo().getColorPaletteSchemeName();
        if (colorPaletteSchemeName != null && colorPaletteSchemeName.length() > 0) {

            String whichComboBox;
            ColorPaletteInfo savedColorPaletteInfo = null;
            if (parentForm.getProductSceneView().getImageInfo().getColorPaletteSourcesInfo().isColorPaletteSchemeDefaultList()) {
                whichComboBox = "Default";

                savedColorPaletteInfo = defaultColorPaletteSchemes.setSchemeName(colorPaletteSchemeName);

            } else {
                whichComboBox = "Standard";

                savedColorPaletteInfo = standardColorPaletteSchemes.setSchemeName(colorPaletteSchemeName);
            }

            // todo Would be good to test here to see if saved scheme actually matches loaded values
//            if (savedColorPaletteInfo != null) {
//               double min = savedColorPaletteInfo.getMinValue();
//               double max = savedColorPaletteInfo.getMaxValue();
//               String cdp = savedColorPaletteInfo.getCpdFilename();
//                boolean log = savedColorPaletteInfo.isLogScaled();
//            }


            colorScheme.setText("Current: " + whichComboBox + " " + parentForm.getProductSceneView().getImageInfo().getColorPaletteSourcesInfo().getColorPaletteSchemeName());
        } else {
            colorScheme.setText("");
        }


        if (parentForm.getImageInfo().getColorPaletteSourcesInfo().getCpdFileName() != null) {
            cpdFileNameLabel.setText(parentForm.getImageInfo().getColorPaletteSourcesInfo().getCpdFileName());
            //       cpdFileNameLabel.setVisible(true);
        } else {
            cpdFileNameLabel.setText("Original File Unknown");
            //      cpdFileNameLabel.setVisible(false);
        }


        defaultColorPaletteSchemes.setjComboBoxShouldFire(originalDefaultShouldFire);
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

    private ActionListener createPaletteListener(final RangeKey key) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (colorPaletteChooser.getSelectedIndex() != 0) {
                    applyChanges(key);
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
                case FromPaletteSource:
                    modifyColorPaletteSchemeName();
                    final Range rangeFromFile = colorPaletteChooser.getRangeFromFile();
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = currentInfo.isLogScaled();
                    min = rangeFromFile.getMin();
                    max = rangeFromFile.getMax();
                    cpd = currentCPD;
                    autoDistribute = true;
                    break;
                case FromData:
                    modifyColorPaletteSchemeName();
                    final Stx stx = parentForm.getStx(parentForm.getProductSceneView().getRaster());
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = currentInfo.isLogScaled();
                    min = stx.getMinimum();
                    max = stx.getMaximum();
                    cpd = currentCPD;
                    autoDistribute = true;
                    break;
                case FromMinMaxFields:
                    modifyColorPaletteSchemeName();
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = currentInfo.isLogScaled();
                    //    min = new Double(minField.getValue().toString());//(double) minField.getValue();
                    //   max = new Double(maxField.getValue().toString());//(double) maxField.getValue();
                    min = Double.parseDouble(minField.getValue().toString());
                    max = Double.parseDouble(maxField.getValue().toString());
                    cpd = currentCPD;
                    autoDistribute = true;
                    break;
                case ToggleLog:
                    modifyColorPaletteSchemeName();
                    isSourceLogScaled = currentInfo.isLogScaled();
                    isTargetLogScaled = !currentInfo.isLogScaled();
                    min = currentCPD.getMinDisplaySample();
                    max = currentCPD.getMaxDisplaySample();
                    cpd = currentCPD;
                    autoDistribute = true;
                    break;
                default:
                    currentInfo.getColorPaletteSourcesInfo().setColorPaletteSchemeName(null);
                    String cpdFileName = ColorPalettesManager.getNameFor(selectedCPD);
                    currentInfo.getColorPaletteSourcesInfo().setCpdFileName(cpdFileName);

                    if (loadWithCPDFileValuesCheckBox.isSelected()) {
                        isSourceLogScaled = selectedCPD.isLogScaled();
                        isTargetLogScaled = selectedCPD.isLogScaled();
                        autoDistribute = false;
                        currentInfo.setLogScaled(isTargetLogScaled);
                        min = selectedCPD.getMinDisplaySample();
                        max = selectedCPD.getMaxDisplaySample();
                        cpd = deepCopy;
                        deepCopy.setLogScaled(isTargetLogScaled);
                        deepCopy.setAutoDistribute(autoDistribute);
                        if (testMinMax(min, max, isTargetLogScaled)) {
                            listenToLogDisplayButtonEnabled[0] = false;
                            logDisplayButton.setSelected(isTargetLogScaled);
                            listenToLogDisplayButtonEnabled[0] = true;
                        }
                    } else {
                        isSourceLogScaled = false;
                        isTargetLogScaled = currentInfo.isLogScaled();
                        min = currentCPD.getMinDisplaySample();
                        max = currentCPD.getMaxDisplaySample();
                        cpd = deepCopy;
                        autoDistribute = true;
                    }


            }


            if (testMinMax(min, max, isTargetLogScaled)) {
                currentInfo.setColorPaletteDef(cpd, min, max, autoDistribute, isSourceLogScaled, isTargetLogScaled);

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


    private void modifyColorPaletteSchemeName() {
        parentForm.getImageInfo().getColorPaletteSourcesInfo().setColorPaletteSchemeName(null);

        // todo I was going to add an astericks to indicate altered but not sure so holding off here
//        if (parentForm.getImageInfo().getColorPaletteSchemeName() != null && !parentForm.getImageInfo().getColorPaletteSchemeName().endsWith("*") && parentForm.getImageInfo().getColorPaletteSchemeName().length() > 0) {
//            parentForm.getImageInfo().setColorPaletteSchemeName(parentForm.getImageInfo().getColorPaletteSchemeName() + "*");
//        }
    }


    private boolean testMinMax(double min, double max, boolean isLogScaled) {
        boolean checksOut = true;

        if (min == max) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING: Min cannot equal Max");
        }

        if (isLogScaled && min == 0) {
            checksOut = false;
            VisatApp.getApp().setStatusBarMessage("WARNING: Min cannot be 0 in log scaling mode");
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
        currentInfo.getColorPaletteSourcesInfo().setColorPaletteSchemeName(colorSchemaName);
        currentInfo.getColorPaletteSourcesInfo().setColorPaletteSchemeDefaultList(isDefaultList);

        currentMinFieldValue = Double.toString(min);
        currentMaxFieldValue = Double.toString(max);

        parentForm.applyChanges();
    }


}