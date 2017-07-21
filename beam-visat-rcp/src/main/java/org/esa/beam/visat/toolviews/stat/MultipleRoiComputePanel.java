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
import org.esa.beam.framework.datamodel.*;
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
 * @since SeaDAS 7.5
 *
 * Revised by Daniel Knowles for SeaDAS 7.5
 * 1. better handles layout, prevents components from being hidden due to window resizing.
 * 2. validFields added to help govern enablement of the refreshButton.
 *
 */
class MultipleRoiComputePanel extends JPanel {


    private QuickListFilterField maskNameSearchField;
    private QuickListFilterField bandNameSearchField;


    private ComputeMasks method;

    interface ComputeMasks {

        void compute(Mask[] selectedMasks, Band[] selectedBands);
    }

    private final ProductNodeListener productNodeListener;
    private final ProductNodeListener productBandNodeListener;

    private AbstractButton refreshButton;
    private JCheckBox useRoiCheckBox;
    private JCheckBox includeUnmaskedCheckBox;
    private CheckBoxList maskNameList;
    private CheckBoxList bandNameList;
    private JCheckBox selectAllCheckBox;
    private JCheckBox bandNameselectAllCheckBox;
    private JCheckBox selectNoneCheckBox;
    private JCheckBox bandNameselectNoneCheckBox;
    private boolean validFields = true;
    private boolean includeUnmasked = true;
    private boolean isRunning = false;

    private RasterDataNode raster;
    private Product product;
    private boolean useViewBandRaster = true;

    // todo Danny right now this is complicated by creation of a MathBand (bands have been added since initialization)
    // in which case reset is needed so for now setting this to true which is probably better anyway
    // this forces a reset when another band view window is opened
    public boolean forceUpdate = true;


    MultipleRoiComputePanel(final ComputeMasks method, final RasterDataNode rasterDataNode) {
        this.method = method;

        setLayout(new GridBagLayout());
        JPanel topPane = getTopPanel(method, rasterDataNode);

        productNodeListener = new PNL();
        productBandNodeListener = new BandNamePNL();

        JPanel panel = GridBagUtils.createPanel();

        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(topPane, gbc);

        gbc = GridBagUtils.restoreConstraints(gbc);
        includeUnmaskedCheckBox = new JCheckBox("Full Scene");
        includeUnmaskedCheckBox.setSelected(includeUnmasked);
        includeUnmaskedCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                includeUnmasked = includeUnmaskedCheckBox.isSelected();
            }
        });
        includeUnmaskedCheckBox.setMinimumSize(includeUnmaskedCheckBox.getPreferredSize());
        includeUnmaskedCheckBox.setPreferredSize(includeUnmaskedCheckBox.getPreferredSize());

        gbc.insets.top = 5;
        gbc.gridy += 1;
        panel.add(includeUnmaskedCheckBox, gbc);

        gbc.gridy++;
        panel.add(getMaskAndBandTabbedPane(), gbc);

        GridBagConstraints gbcMain = GridBagUtils.createConstraints();
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        add(panel, gbcMain);

        setRaster(rasterDataNode);
    }

    private JTabbedPane getMaskAndBandTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Masks", getMaskROIPanel());
        tabbedPane.setToolTipTextAt(0, "Select region of interest masks for which to create statistics");

        tabbedPane.addTab("Bands", getBandsPanel());
        tabbedPane.setToolTipTextAt(1, "Select bands for which to create statistics");

        return  tabbedPane;

    }

    private JPanel getMaskROIPanel() {
        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        //  useRoiCheckBox = new JCheckBox("Use ROI mask(s):");
        useRoiCheckBox = new JCheckBox("Mask(ROI)");
        useRoiCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnablement();
            }
        });
        useRoiCheckBox.setMinimumSize(useRoiCheckBox.getPreferredSize());
        useRoiCheckBox.setPreferredSize(useRoiCheckBox.getPreferredSize());
        useRoiCheckBox.setSelected(true);

        JPanel maskFilterPane = getMaskFilterPanel();
        JPanel maskNameListPane = getMaskNameListPanel();
        JPanel checkBoxPane = getSelectAllNonePanel();

        // todo Danny commented this out and set selected to true as we may get rid of this
//        panel.add(useRoiCheckBox, gbc);

  //      gbc.gridy++;

        gbc.insets.top = 5;
        panel.add(maskFilterPane, gbc);
        gbc.insets.top = 0;

        gbc.gridy++;
        panel.add(maskNameListPane, gbc);

        gbc.gridy++;
        gbc.insets.bottom = 5;
        panel.add(checkBoxPane, gbc);

        panel.setMinimumSize(panel.getPreferredSize());
        panel.setPreferredSize(panel.getPreferredSize());

        return panel;
    }



    private JPanel getBandsPanel() {


        JPanel bandFilterPane = getBandFilterPanel();
        JPanel bandNameListPane = getBandNameListPanel();
        JPanel bandNameCheckBoxPane = getBandNameSelectAllNonePanel();

        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        // todo Danny uncomment this to get Bands working and to test out this feature

        gbc.insets.top = 5;
        panel.add(bandFilterPane, gbc);
        gbc.insets.top = 0;

        gbc.gridy++;
        panel.add(bandNameListPane, gbc);

        gbc.gridy++;
        gbc.insets.bottom = 5;
        panel.add(bandNameCheckBoxPane, gbc);

        panel.setMinimumSize(panel.getPreferredSize());
        panel.setPreferredSize(panel.getPreferredSize());

        return panel;
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
            updateBandNameListState();
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

        updateEnablement();
    }

    void updateEnablement() {
        boolean hasMasks = (product != null && product.getMaskGroup().getNodeCount() > 0);
        boolean canSelectMasks = hasMasks && useRoiCheckBox.isSelected();
        useRoiCheckBox.setEnabled(hasMasks);
        maskNameSearchField.setEnabled(canSelectMasks);
        maskNameList.setEnabled(canSelectMasks);
        selectAllCheckBox.setEnabled(canSelectMasks && maskNameList.getCheckBoxListSelectedIndices().length < maskNameList.getModel().getSize());
        selectNoneCheckBox.setEnabled(canSelectMasks && maskNameList.getCheckBoxListSelectedIndices().length > 0);
        if (!isRunning()) {
            refreshButton.setEnabled(raster != null);
        }
    }

    void updateRunButton(boolean enabled) {
        refreshButton.setEnabled(enabled);
    //    refreshButton.setEnabled(validFields && raster != null && (useRoiCheckBox.isSelected() || includeUnmaskedCheckBox.isSelected()));
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
                updateEnablement();
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
               run();
            }
        });


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(refreshButton, gbc);
        panel.setMinimumSize(panel.getPreferredSize());
        panel.setPreferredSize(panel.getPreferredSize());

        return panel;
    }

    public void run() {
        setRunning(true);
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

        Band[] selectedBands;
        int[] bandListIndexes = bandNameList.getCheckBoxListSelectedIndices();
        if (bandListIndexes.length > 0) {
            selectedBands = new Band[bandListIndexes.length];
            for (int i = 0; i < bandListIndexes.length; i++) {
                int listIndex = bandListIndexes[i];
                String bandName = bandNameList.getModel().getElementAt(listIndex).toString();
                selectedBands[i] = raster.getProduct().getBandGroup().get(bandName);
            }
        } else {
            selectedBands = new Band[]{null};
        }

        method.compute(selectedMasks, selectedBands);
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
    //    gbc.gridx++;
    //    panel.add(showMaskManagerButton, gbc);

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

    public boolean isIncludeUnmasked() {
        return includeUnmasked;
    }



    //---------------------- Bandnames List ----------------------------------------------

//    void setRaster(final RasterDataNode newRaster) {
//        if (this.raster != newRaster) {
//            this.raster = newRaster;
//            if (newRaster == null) {
//                if (product != null) {
//                    product.removeProductNodeListener(productNodeListener);
//                }
//                product = null;
//            } else if (product != newRaster.getProduct()) {
//                if (product != null) {
//                    product.removeProductNodeListener(productNodeListener);
//                }
//                product = newRaster.getProduct();
//                if (product != null) {
//                    product.addProductNodeListener(productNodeListener);
//                }
//            }
//            updateBandNameListState();
//            refreshButton.setEnabled(raster != null);
//        }
//    }


    private class BandNamePNL implements ProductNodeListener {

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
            final ProductNode sourceNode = event.getSourceNode();
            if (!(sourceNode instanceof Band)) {
                return;
            }
            final String bandName = ((Band) sourceNode).getName();
            final String[] selectedNames = getSelectedBandNames();

            if (StringUtils.contains(selectedNames, bandName)) {
                updateEnablement();
            }
        }

        @Override
        public void nodeRemoved(ProductNodeEvent event) {
            handleEvent(event);
        }

        private void handleEvent(ProductNodeEvent event) {
            ProductNode sourceNode = event.getSourceNode();
            if (sourceNode instanceof Band) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateBandNameListState();
                    }
                });
            }
        }
    }


    private void selectAndEnableBandNameCheckBoxes() {
        final int numEntries = bandNameList.getModel().getSize();
        final int numSelected = bandNameList.getCheckBoxListSelectedIndices().length;
        bandNameselectNoneCheckBox.setEnabled(numSelected > 0);
        bandNameselectAllCheckBox.setEnabled(numSelected < numEntries);
        bandNameselectNoneCheckBox.setSelected(numSelected == 0);
        bandNameselectAllCheckBox.setSelected(numSelected == numEntries);
    }

    private String[] getSelectedBandNames() {
        final Object[] selectedValues = bandNameList.getCheckBoxListSelectedValues();
        return StringUtils.toStringArray(selectedValues);
    }


    private void updateBandNameListState() {

        final DefaultListModel bandNameListModel = new DefaultListModel();
        final String[] currentSelectedBandNames = getSelectedBandNames();

        if (product != null) {
            final ProductNodeGroup<Band> bandGroup = product.getBandGroup();
            final Band[] bands = bandGroup.toArray(new Band[bandGroup.getNodeCount()]);
            for (Band band : bands) {
                bandNameListModel.addElement(band.getName());
            }
        }

        try {
            bandNameSearchField.setListModel(bandNameListModel);
            if (product != null) {
                bandNameList.setModel(bandNameSearchField.getDisplayListModel());
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

        final String[] allNames = StringUtils.toStringArray(bandNameListModel.toArray());
        for (int i = 0; i < allNames.length; i++) {
            String name = allNames[i];
            if (StringUtils.contains(currentSelectedBandNames, name)) {
                bandNameList.getCheckBoxListSelectionModel().addSelectionInterval(i, i);
            }
        }

        if (forceUpdate && raster != null) {
                bandNameList.selectNone();
                bandNameList.clearCheckBoxListSelection();
                bandNameList.clearSelection();
                String[] selectedBandNames = {raster.getName()};
                bandNameList.setSelectedObjects(selectedBandNames);
        }


        updateEnablement();
    }



    private JPanel getBandNameListPanel() {
        bandNameList = new CheckBoxList(bandNameSearchField.getDisplayListModel()) {
            @Override
            public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
                return -1;
            }

            @Override
            public boolean isCheckBoxEnabled(int index) {
                return true;
            }
        };
        SearchableUtils.installSearchable(bandNameList);

        bandNameList.getCheckBoxListSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        bandNameList.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

//                refreshButton.setEnabled(true);
                if (!e.getValueIsAdjusting()) {
                    selectAndEnableBandNameCheckBoxes();
                }
            }
        });
//        maskNameList.setMinimumSize(maskNameList.getPreferredSize());
//        maskNameList.setPreferredSize(maskNameList.getPreferredSize());


        JScrollPane scrollPane = new JScrollPane(bandNameList);
        scrollPane.setMinimumSize(scrollPane.getPreferredSize());
        scrollPane.setPreferredSize(scrollPane.getPreferredSize());


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        return panel;

    }

    private JPanel getBandNameSelectAllNonePanel() {

        bandNameselectAllCheckBox = new JCheckBox("Select All");
        bandNameselectAllCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (bandNameselectAllCheckBox.isSelected()) {
                    bandNameList.selectAll();
                }
                selectAndEnableBandNameCheckBoxes();
            }
        });
        bandNameselectAllCheckBox.setMinimumSize(bandNameselectAllCheckBox.getPreferredSize());
        bandNameselectAllCheckBox.setPreferredSize(bandNameselectAllCheckBox.getPreferredSize());

        bandNameselectNoneCheckBox = new JCheckBox("Select None");
        bandNameselectNoneCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (bandNameselectNoneCheckBox.isSelected()) {
                    bandNameList.selectNone();
                }
                selectAndEnableBandNameCheckBoxes();
            }
        });
        bandNameselectNoneCheckBox.setMinimumSize(bandNameselectNoneCheckBox.getPreferredSize());
        bandNameselectNoneCheckBox.setPreferredSize(bandNameselectNoneCheckBox.getPreferredSize());




        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        panel.add(bandNameselectAllCheckBox, gbc);

        gbc.gridy++;
        panel.add(bandNameselectNoneCheckBox, gbc);

        return panel;
    }


    private JPanel getBandFilterPanel() {


        AbstractButton showMaskManagerButton = VisatApp.getApp().getCommandManager().getCommand("org.esa.beam.visat.toolviews.mask.MaskManagerToolView.showCmd").createToolBarButton();
        showMaskManagerButton.setMinimumSize(showMaskManagerButton.getPreferredSize());
        showMaskManagerButton.setPreferredSize(showMaskManagerButton.getPreferredSize());


        DefaultListModel maskNameListModel = new DefaultListModel();

        bandNameSearchField = new QuickListFilterField(maskNameListModel);
        bandNameSearchField.setHintText("Band Filter");
        bandNameSearchField.setMinimumSize(bandNameSearchField.getPreferredSize());
        bandNameSearchField.setPreferredSize(bandNameSearchField.getPreferredSize());


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.insets.top = 3;
        gbc.insets.bottom = 3;
        panel.add(bandNameSearchField, gbc);
    //    gbc.gridx++;
    //    panel.add(showMaskManagerButton, gbc);

        return panel;
    }


    //--------------------- General-------------------------------------------


    public boolean isUseViewBandRaster() {
        return useViewBandRaster;
    }

    public void setUseViewBandRaster(boolean useViewBandRaster) {
        this.useViewBandRaster = useViewBandRaster;
    }



    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        updateRunButton(!running);
    }


}
