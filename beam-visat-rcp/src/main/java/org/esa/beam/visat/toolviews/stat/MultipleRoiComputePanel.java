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

package org.esa.beam.visat.toolviews.stat;

import com.jidesoft.list.QuickListFilterField;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.SearchableUtils;
import com.jidesoft.swing.TitledSeparator;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNode;
import org.esa.beam.framework.datamodel.ProductNodeEvent;
import org.esa.beam.framework.datamodel.ProductNodeGroup;
import org.esa.beam.framework.datamodel.ProductNodeListener;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.beam.util.Debug;
import org.esa.beam.util.StringUtils;
import org.esa.beam.visat.VisatApp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * A panel which performs the 'compute' action for the Statistic Tool
 *
 * @author Marco Zuehlke
 * @author Daniel Knowles
 * @since SeaDAS 7.4.1
 *
 * Revised by Daniel Knowles for SeaDAS 7.4.1
 * 1. better handles layout, prevents components from being hidden due to window resizing.
 * 2. validFields added to help govern enablement of the refreshButton.
 *
 */
class MultipleRoiComputePanel extends JPanel {


    private QuickListFilterField maskNameSearchField;

    public boolean isIncludeUnmasked() {
        return includeUnmasked;
    }

    interface ComputeMasks {

        void compute(Mask[] selectedMasks);
    }

    private final ProductNodeListener productNodeListener;

    private AbstractButton refreshButton;
    private JCheckBox useRoiCheckBox;
    private JCheckBox includeUnmaskedCheckBox;
    private CheckBoxList maskNameList;
    private JCheckBox selectAllCheckBox;
    private JCheckBox selectNoneCheckBox;
    private boolean validFields = true;
    private boolean includeUnmasked = true;

    private RasterDataNode raster;
    private Product product;

    MultipleRoiComputePanel(final ComputeMasks method, final RasterDataNode rasterDataNode) {

        setLayout(new GridBagLayout());

        productNodeListener = new PNL();


        JPanel topPane = getTopPanel(method, rasterDataNode);


      //  useRoiCheckBox = new JCheckBox("Use ROI mask(s):");
        useRoiCheckBox = new JCheckBox("ROI Mask(s)");
        useRoiCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnablement(validFields);
            }
        });
        useRoiCheckBox.setMinimumSize(useRoiCheckBox.getPreferredSize());
        useRoiCheckBox.setPreferredSize(useRoiCheckBox.getPreferredSize());



        includeUnmaskedCheckBox = new JCheckBox("Full Scene");
        includeUnmaskedCheckBox.setSelected(includeUnmasked);
        includeUnmaskedCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                includeUnmasked = includeUnmaskedCheckBox.isSelected();
                updateEnablement(validFields);
            }
        });
        includeUnmaskedCheckBox.setMinimumSize(includeUnmaskedCheckBox.getPreferredSize());
        includeUnmaskedCheckBox.setPreferredSize(includeUnmaskedCheckBox.getPreferredSize());


        JPanel maskFilterPane = getMaskFilterPanel();
        JPanel maskNameListPane = getMaskNameListPanel();
        JPanel checkBoxPane = getSelectAllNonePanel();


        JPanel panel = GridBagUtils.createPanel();

        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(topPane, gbc);
        gbc = GridBagUtils.restoreConstraints(gbc);



        gbc.gridy++;
        panel.add(includeUnmaskedCheckBox, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.top = 5;
        panel.add(new TitledSeparator("Masking Options", SwingConstants.CENTER), gbc);
        gbc = GridBagUtils.restoreConstraints(gbc);
        gbc.insets.top = 0;




        gbc.gridy++;
        panel.add(useRoiCheckBox, gbc);

        gbc.gridy++;
        panel.add(maskFilterPane, gbc);

        gbc.gridy++;
        panel.add(maskNameListPane, gbc);

        gbc.gridy++;
        gbc.insets.bottom = 5;
        panel.add(checkBoxPane, gbc);

        panel.setMinimumSize(panel.getPreferredSize());
        panel.setPreferredSize(panel.getPreferredSize());


        GridBagConstraints gbcMain = GridBagUtils.createConstraints();
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        add(panel, gbcMain);

        setRaster(rasterDataNode);
    }



    void setRaster(final RasterDataNode newRaster) {
        if (this.raster != newRaster) {
            this.raster = newRaster;
            if (newRaster == null) {
                if (product != null) {
                    product.removeProductNodeListener(productNodeListener);
                }
                product = null;
            } else if (product != newRaster.getProduct()) {
                if (product != null) {
                    product.removeProductNodeListener(productNodeListener);
                }
                product = newRaster.getProduct();
                if (product != null) {
                    product.addProductNodeListener(productNodeListener);
                }
            }
            updateMaskListState();
            refreshButton.setEnabled(raster != null);
        }
    }

    private void selectAndEnableCheckBoxes() {
        final int numEntries = maskNameList.getModel().getSize();
        final int numSelected = maskNameList.getCheckBoxListSelectedIndices().length;
        selectNoneCheckBox.setEnabled(numSelected > 0);
        selectAllCheckBox.setEnabled(numSelected < numEntries);
        selectNoneCheckBox.setSelected(numSelected == 0);
        selectAllCheckBox.setSelected(numSelected == numEntries);
    }

    private String[] getSelectedMaskNames() {
        final Object[] selectedValues = maskNameList.getCheckBoxListSelectedValues();
        return StringUtils.toStringArray(selectedValues);
    }

    private void updateMaskListState() {

        final DefaultListModel maskNameListModel = new DefaultListModel();
        final String[] currentSelectedMaskNames = getSelectedMaskNames();

        if (product != null) {
            final ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
            final Mask[] masks = maskGroup.toArray(new Mask[maskGroup.getNodeCount()]);
            for (Mask mask : masks) {
                maskNameListModel.addElement(mask.getName());
            }
        }

        try {
            maskNameSearchField.setListModel(maskNameListModel);
            if (product != null) {
                maskNameList.setModel(maskNameSearchField.getDisplayListModel());
            }
        } catch (Throwable e) {

            /*
            We catch everything here, because there seems to be a bug in the combination of
            JIDE QuickListFilterField and FilteredCheckBoxList:

             java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
             	at java.util.BitSet.get(BitSet.java:441)
             	at javax.swing.DefaultListSelectionModel.clear(DefaultListSelectionModel.java:257)
             	at javax.swing.DefaultListSelectionModel.setState(DefaultListSelectionModel.java:567)
             	at javax.swing.DefaultListSelectionModel.removeIndexInterval(DefaultListSelectionModel.java:635)
             	at com.jidesoft.list.CheckBoxListSelectionModelWithWrapper.removeIndexInterval(Unknown Source)
/             */
            Debug.trace(e);
        }

        final String[] allNames = StringUtils.toStringArray(maskNameListModel.toArray());
        for (int i = 0; i < allNames.length; i++) {
            String name = allNames[i];
            if (StringUtils.contains(currentSelectedMaskNames, name)) {
                maskNameList.getCheckBoxListSelectionModel().addSelectionInterval(i, i);
            }
        }

        updateEnablement(validFields);
    }

    void updateEnablement(boolean validFields) {
        this.validFields = validFields;
        boolean hasMasks = (product != null && product.getMaskGroup().getNodeCount() > 0);
        boolean canSelectMasks = hasMasks && useRoiCheckBox.isSelected();
        useRoiCheckBox.setEnabled(hasMasks);
        maskNameSearchField.setEnabled(canSelectMasks);
        maskNameList.setEnabled(canSelectMasks);
        selectAllCheckBox.setEnabled(canSelectMasks && maskNameList.getCheckBoxListSelectedIndices().length < maskNameList.getModel().getSize());
        selectNoneCheckBox.setEnabled(canSelectMasks && maskNameList.getCheckBoxListSelectedIndices().length > 0);
        refreshButton.setEnabled(validFields && raster != null && (useRoiCheckBox.isSelected() || includeUnmaskedCheckBox.isSelected()));
    }

    void updateEnablement() {
        refreshButton.setEnabled(validFields && raster != null && (useRoiCheckBox.isSelected() || includeUnmaskedCheckBox.isSelected()));
    }
    private class PNL implements ProductNodeListener {

        @Override
        public void nodeAdded(ProductNodeEvent event) {
            handleEvent(event);
        }

        @Override
        public void nodeChanged(ProductNodeEvent event) {
//            handleEvent(event);
        }

        @Override
        public void nodeDataChanged(ProductNodeEvent event) {
            if (!useRoiCheckBox.isSelected()) {
                return;
            }
            final ProductNode sourceNode = event.getSourceNode();
            if (!(sourceNode instanceof Mask)) {
                return;
            }
            final String maskName = ((Mask) sourceNode).getName();
            final String[] selectedNames = getSelectedMaskNames();

            if (StringUtils.contains(selectedNames, maskName)) {
                updateEnablement(validFields);
            }
        }

        @Override
        public void nodeRemoved(ProductNodeEvent event) {
            handleEvent(event);
        }

        private void handleEvent(ProductNodeEvent event) {
            ProductNode sourceNode = event.getSourceNode();
            if (sourceNode instanceof Mask) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateMaskListState();
                    }
                });
            }
        }
    }

    private JPanel getTopPanel(final ComputeMasks method, final RasterDataNode rasterDataNode) {

        refreshButton = ToolButtonFactory.createButton(
                UIUtils.loadImageIcon("icons/ViewRefresh22.png"),
                false);
        refreshButton.setEnabled(rasterDataNode != null);
        refreshButton.setToolTipText("Refresh View");
        refreshButton.setName("refreshButton");

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean useRoi = useRoiCheckBox.isSelected();
                Mask[] selectedMasks;
                if (useRoi) {
                    int[] listIndexes = maskNameList.getCheckBoxListSelectedIndices();
                    if (listIndexes.length > 0) {
                        selectedMasks = new Mask[listIndexes.length];
                        for (int i = 0; i < listIndexes.length; i++) {
                            int listIndex = listIndexes[i];
                            String maskName = maskNameList.getModel().getElementAt(listIndex).toString();
                            selectedMasks[i] = raster.getProduct().getMaskGroup().get(maskName);
                        }
                    } else {
                        selectedMasks = new Mask[]{null};
                    }
                } else {
                    selectedMasks = new Mask[]{null};
                }
                method.compute(selectedMasks);
                refreshButton.setEnabled(false);
            }
        });


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(refreshButton, gbc);
        panel.setMinimumSize(panel.getPreferredSize());
        panel.setPreferredSize(panel.getPreferredSize());

        return panel;
    }

    private JPanel getMaskFilterPanel() {


        AbstractButton showMaskManagerButton = VisatApp.getApp().getCommandManager().getCommand("org.esa.beam.visat.toolviews.mask.MaskManagerToolView.showCmd").createToolBarButton();
        showMaskManagerButton.setMinimumSize(showMaskManagerButton.getPreferredSize());
        showMaskManagerButton.setPreferredSize(showMaskManagerButton.getPreferredSize());


        DefaultListModel maskNameListModel = new DefaultListModel();

        maskNameSearchField = new QuickListFilterField(maskNameListModel);
        maskNameSearchField.setHintText("Mask Filter");
        maskNameSearchField.setMinimumSize(maskNameSearchField.getPreferredSize());
        maskNameSearchField.setPreferredSize(maskNameSearchField.getPreferredSize());


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.insets.top = 3;
        gbc.insets.bottom = 3;
        panel.add(maskNameSearchField, gbc);
        gbc.gridx++;
        panel.add(showMaskManagerButton, gbc);

        return panel;
    }


    private JPanel getMaskNameListPanel() {
        maskNameList = new CheckBoxList(maskNameSearchField.getDisplayListModel()) {
            @Override
            public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
                return -1;
            }

            @Override
            public boolean isCheckBoxEnabled(int index) {
                return true;
            }
        };
        SearchableUtils.installSearchable(maskNameList);

        maskNameList.getCheckBoxListSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        maskNameList.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateEnablement(validFields);
//                refreshButton.setEnabled(true);
                if (!e.getValueIsAdjusting()) {
                    selectAndEnableCheckBoxes();
                }
            }
        });
//        maskNameList.setMinimumSize(maskNameList.getPreferredSize());
//        maskNameList.setPreferredSize(maskNameList.getPreferredSize());


        JScrollPane scrollPane = new JScrollPane(maskNameList);
        scrollPane.setMinimumSize(scrollPane.getPreferredSize());
        scrollPane.setPreferredSize(scrollPane.getPreferredSize());


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        return panel;

    }

    private JPanel getSelectAllNonePanel() {

        selectAllCheckBox = new JCheckBox("Select All");
        selectAllCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (selectAllCheckBox.isSelected()) {
                    maskNameList.selectAll();
                }
                selectAndEnableCheckBoxes();
            }
        });
        selectAllCheckBox.setMinimumSize(selectAllCheckBox.getPreferredSize());
        selectAllCheckBox.setPreferredSize(selectAllCheckBox.getPreferredSize());

        selectNoneCheckBox = new JCheckBox("Select None");
        selectNoneCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (selectNoneCheckBox.isSelected()) {
                    maskNameList.selectNone();
                }
                selectAndEnableCheckBoxes();
            }
        });
        selectNoneCheckBox.setMinimumSize(selectNoneCheckBox.getPreferredSize());
        selectNoneCheckBox.setPreferredSize(selectNoneCheckBox.getPreferredSize());




        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        panel.add(selectAllCheckBox, gbc);

        gbc.gridy++;
        panel.add(selectNoneCheckBox, gbc);

        return panel;
    }

}
