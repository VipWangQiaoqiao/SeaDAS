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

import com.bc.ceres.core.*;
import com.bc.ceres.swing.TableLayout;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.ImageInfoEditorModel;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

class Continuous1BandBasicForm implements ColorManipulationChildForm {

    private final ColorManipulationForm parentForm;
    private final JPanel contentPanel;
    private final AbstractButton logDisplayButton;
    private final MoreOptionsForm moreOptionsForm;
    private final ColorPaletteChooser colorPaletteChooser;
    private final JFormattedTextField minField;
    private final JFormattedTextField maxField;
    private final DiscreteCheckBox discreteCheckBox;
    private ColorPaletteInfoComboBox colorPaletteInfoComboBox;


    final Boolean[] minFieldActivated = {new Boolean(false)};
    final Boolean[] maxFieldActivated = {new Boolean(false)};

    private final ImageInfoEditor2 imageInfoEditor;

    private enum RangeKey {FromPaletteSource, FromData, FromMinMaxFields, FromCurrentPalette;}

    private boolean shouldFireChooserEvent;
    private boolean hidden = false;

    Continuous1BandBasicForm(final ColorManipulationForm parentForm) {

        imageInfoEditor = new ImageInfoEditor2(parentForm);

        ColorPalettesManager.loadAvailableColorPalettes(parentForm.getIODir());

        this.parentForm = parentForm;

        colorPaletteInfoComboBox = new ColorPaletteInfoComboBox(parentForm.getIODir());
        VisatApp.getApp().clearStatusBarMessage();


        JPanel basicPanel = GridBagUtils.createPanel();
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel spacer1 = new JLabel();
        basicPanel.add(spacer1(spacer1), gbc);


        colorPaletteChooser = new ColorPaletteChooser();
        colorPaletteChooser.setPreferredSize(new Dimension(180, 40));


        JPanel colorPaletteJPanel = new JPanel(new GridBagLayout());
        colorPaletteJPanel.setBorder(BorderFactory.createTitledBorder("Color Palette"));
        final GridBagConstraints colorPaletteGbc = new GridBagConstraints();

        colorPaletteGbc.fill = GridBagConstraints.HORIZONTAL;
        colorPaletteGbc.anchor = GridBagConstraints.WEST;
        colorPaletteGbc.gridy = 0;
        colorPaletteGbc.gridx = 0;
        colorPaletteGbc.weightx = 1.0;
        colorPaletteJPanel.add(colorPaletteChooser, colorPaletteGbc);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        basicPanel.add(colorPaletteJPanel, gbc);


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


        final JButton cpdRange = new JButton("Set from CPD File");
        cpdRange.setToolTipText("Set min and max value to be value in cpd file");
        cpdRange.setMaximumSize(cpdRange.getMinimumSize());
        cpdRange.setPreferredSize(cpdRange.getMinimumSize());
        cpdRange.setMinimumSize(cpdRange.getMinimumSize());


        final JButton bandRange = new JButton("Set from Band Data");
        bandRange.setToolTipText("Set min and max value to corresponding data value in the band statistics");
        bandRange.setMaximumSize(bandRange.getMinimumSize());
        bandRange.setPreferredSize(bandRange.getMinimumSize());
        bandRange.setMinimumSize(bandRange.getMinimumSize());


        JPanel rangeJPanel = new JPanel(new GridBagLayout());
        rangeJPanel.setBorder(BorderFactory.createTitledBorder("Range Adjustments"));
        final GridBagConstraints rangeGbc = new GridBagConstraints();

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

        rangeGbc.fill = GridBagConstraints.NONE;
        rangeGbc.gridy = 2;
        rangeGbc.gridx = 0;
        rangeJPanel.add(cpdRange, rangeGbc);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel spacer2 = new JLabel();
        basicPanel.add(spacer2(spacer2), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        basicPanel.add(rangeJPanel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        basicPanel.add(spacer1(new JLabel()), gbc);


        JPanel colorPaletteInfoComboBoxJPanel = new JPanel(new GridBagLayout());
        colorPaletteInfoComboBoxJPanel.setBorder(BorderFactory.createTitledBorder("Preset Color Palette Schemes"));
        colorPaletteInfoComboBoxJPanel.setToolTipText("Load a preset color scheme (sets the color-palette, min, max, and log fields)");
        GridBagConstraints gbc2 = new GridBagConstraints();


        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0;
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        colorPaletteInfoComboBoxJPanel.add(colorPaletteInfoComboBox.getStandardJComboBox(), gbc2);

        gbc2.gridx = 0;
        gbc2.gridy = 1;
        colorPaletteInfoComboBoxJPanel.add(colorPaletteInfoComboBox.getjComboBox(), gbc2);

        gbc2.gridx = 0;
        gbc2.gridy = 2;
        colorPaletteInfoComboBoxJPanel.add(colorPaletteInfoComboBox.getUserJComboBox(), gbc2);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        basicPanel.add(colorPaletteInfoComboBoxJPanel, gbc);


        contentPanel = GridBagUtils.createPanel();

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(basicPanel, gbc);


        shouldFireChooserEvent = true;

        colorPaletteChooser.addActionListener(createListener(RangeKey.FromCurrentPalette));
        //    minField.addActionListener(createListener(RangeKey.FromMinMaxFields));
        //    maxField.addActionListener(createListener(RangeKey.FromMinMaxFields));
        cpdRange.addActionListener(createListener(RangeKey.FromPaletteSource));
        bandRange.addActionListener(createListener(RangeKey.FromData));


        colorPaletteChooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VisatApp.getApp().clearStatusBarMessage();
            }
        });


//        minField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent documentEvent) {
//                if (minDoThis[0]) {
//                    minDoThis[0] = false;
//                    shouldFireChooserEvent = true;
//                    applyChanges(RangeKey.FromMinMaxFields);
//                    shouldFireChooserEvent = false;
//                    minDoThis[0] = true;
//                }
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent documentEvent) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent documentEvent) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });


        maxField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (!maxFieldActivated[0]) {
                    maxFieldActivated[0] = true;
                    shouldFireChooserEvent = true;
                    applyChanges(RangeKey.FromMinMaxFields);
                    shouldFireChooserEvent = false;
                    maxFieldActivated[0] = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        minField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

                double currentVal;
                try {
                    currentVal = Double.parseDouble(minField.getText());

                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(minField, "Please enter valid number.");
                    minField.requestFocusInWindow();
                    return;
                }
                //     boolean valid = sampleName.equals("minSample") ? validateMinMax(currentVal, maxVal) : validateMinMax(minVal, currentVal);
                //      if (valid) {
                minField.setValue(currentVal);

                    minFieldActivated[0] = true;
                    shouldFireChooserEvent = true;
                    applyChanges(RangeKey.FromMinMaxFields);
                    shouldFireChooserEvent = false;
                    minFieldActivated[0] = false;


                //      }


            }
        });


//        maxField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                applyChanges(RangeKey.FromMinMaxFields);
//                colorPaletteInfoComboBox.reset();
//            }
//        });

//        maxField.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                applyChanges(RangeKey.FromMinMaxFields);
//                VisatApp.getApp().clearStatusBarMessage();
//            }
//        });
//
//        minField.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                applyChanges(RangeKey.FromMinMaxFields);
//                VisatApp.getApp().clearStatusBarMessage();
//            }
//        });


        bandRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VisatApp.getApp().clearStatusBarMessage();
            }
        });


        moreOptionsForm = new MoreOptionsForm(parentForm, true);
        discreteCheckBox = new DiscreteCheckBox(parentForm);
        moreOptionsForm.addRow(discreteCheckBox);

        logDisplayButton = LogDisplay.createButton();
        logDisplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean shouldLog10Display = logDisplayButton.isSelected();
                VisatApp.getApp().clearStatusBarMessage();
                final ImageInfo imageInfo = parentForm.getImageInfo();
                if (shouldLog10Display) {
                    final ColorPaletteDef cpd = imageInfo.getColorPaletteDef();
                    if (LogDisplay.checkApplicability(cpd)) {
                        colorPaletteChooser.setLog10Display(shouldLog10Display);
                        imageInfo.setLogScaled(shouldLog10Display);
                        parentForm.applyChanges();
                    } else {
                        LogDisplay.showNotApplicableInfo(parentForm.getContentPanel());
                        logDisplayButton.setSelected(false);
                    }
                } else {
                    colorPaletteChooser.setLog10Display(shouldLog10Display);
                    imageInfo.setLogScaled(shouldLog10Display);
                    parentForm.applyChanges();
                }
            }
        });

        colorPaletteInfoComboBox.getjComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleColorPaletteInfoComboBoxSelection(colorPaletteInfoComboBox.getjComboBox());
            }
        });

        colorPaletteInfoComboBox.getStandardJComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleColorPaletteInfoComboBoxSelection(colorPaletteInfoComboBox.getStandardJComboBox());
            }
        });

        colorPaletteInfoComboBox.getUserJComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleColorPaletteInfoComboBoxSelection(colorPaletteInfoComboBox.getUserJComboBox());
            }
        });

    }


    private void handleColorPaletteInfoComboBoxSelection(JComboBox jComboBox) {
        ColorPaletteInfo colorPaletteInfo = (ColorPaletteInfo) jComboBox.getSelectedItem();

        if (colorPaletteInfo.getCpdFilename() != null) {

            try {
                if (colorPaletteInfoComboBox.isShouldFire()) {
                    File cpdFile = new File(parentForm.getIODir(), colorPaletteInfo.getCpdFilename());
                    ColorPaletteDef colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(cpdFile);


                    boolean origShouldFireChooserEvent = shouldFireChooserEvent;
                    shouldFireChooserEvent = false;

                    colorPaletteChooser.setSelectedColorPaletteDefinition(colorPaletteDef);
                    applyChanges(colorPaletteInfo.getMinValue(),
                            colorPaletteInfo.getMaxValue(),
                            colorPaletteDef,
                            colorPaletteInfo.isLogScaled());


                    shouldFireChooserEvent = origShouldFireChooserEvent;

                    String id = parentForm.getProductSceneView().getRaster().getDisplayName();
                    VisatApp.getApp().setStatusBarMessage("Loaded '" + colorPaletteInfo.getName() + "' color schema settings into '" + id);
                    colorPaletteInfoComboBox.reset();
                }
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

    private JLabel spacer2(JLabel lineSpacer) {
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
        boolean origShouldFireChooserEvent = shouldFireChooserEvent;
        shouldFireChooserEvent = false;
        colorPaletteChooser.setSelectedColorPaletteDefinition(cpd);


        discreteCheckBox.setDiscreteColorsMode(discrete);
        logDisplayButton.setSelected(logScaled);
        parentForm.revalidateToolViewPaneControl();

        if (!minFieldActivated[0]) {
            minField.setValue(cpd.getMinDisplaySample());
        }

        if (!maxFieldActivated[0]) {
            maxField.setValue(cpd.getMaxDisplaySample());
        }

//        colorPaletteInfoComboBox.setShouldFire(false);
//        colorPaletteInfoComboBox.setSelectedByValues(cpd, cpd.getMinDisplaySample(), cpd.getMaxDisplaySample(), logScaled);
//        colorPaletteInfoComboBox.setShouldFire(true);

        shouldFireChooserEvent = origShouldFireChooserEvent;
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

    private ActionListener createListener(final RangeKey key) {
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
            final ColorPaletteDef cpd;
            switch (key) {
                case FromPaletteSource:
                    final Range rangeFromFile = colorPaletteChooser.getRangeFromFile();
                    min = rangeFromFile.getMin();
                    max = rangeFromFile.getMax();
                    cpd = currentCPD;
                    break;
                case FromData:
                    final Stx stx = parentForm.getStx(parentForm.getProductSceneView().getRaster());
                    min = stx.getMinimum();
                    max = stx.getMaximum();
                    cpd = currentCPD;
                    break;
                case FromMinMaxFields:
                    min = new Double(minField.getValue().toString());//(double) minField.getValue();
                    max = new Double(maxField.getValue().toString());//(double) maxField.getValue();
                    cpd = currentCPD;
                    break;
                default:
                    min = currentCPD.getMinDisplaySample();
                    max = currentCPD.getMaxDisplaySample();
                    cpd = deepCopy;
            }
            final boolean autoDistribute = true;
            currentInfo.setColorPaletteDef(cpd, min, max, autoDistribute);
            parentForm.applyChanges();
        }
    }


    private void applyChanges(double min, double max, ColorPaletteDef selectedCPD, boolean isTargetLogScaled) {
        final ImageInfo currentInfo = parentForm.getImageInfo();
        final ColorPaletteDef currentCPD = currentInfo.getColorPaletteDef();
        final ColorPaletteDef deepCopy = selectedCPD.createDeepCopy();
        deepCopy.setDiscrete(currentCPD.isDiscrete());
        deepCopy.setAutoDistribute(true);

        final boolean autoDistribute = true;
        currentInfo.setLogScaled(isTargetLogScaled);
        currentInfo.setColorPaletteDef(selectedCPD, min, max, autoDistribute, isTargetLogScaled);
        parentForm.applyChanges();
    }


}
