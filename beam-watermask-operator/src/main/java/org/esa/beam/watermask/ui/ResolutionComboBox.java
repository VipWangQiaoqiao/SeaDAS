package org.esa.beam.watermask.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/5/12
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResolutionComboBox {
    private LandMasksData landMasksData;

    private JLabel jLabel;
    private JComboBox jComboBox;
    private int selectedIndex;

    public ResolutionComboBox(LandMasksData landMasksData) {

        this.landMasksData = landMasksData;

        ArrayList<SourceFileInfo> jComboBoxArrayList = new ArrayList<SourceFileInfo>();
        ArrayList<String> toolTipsArrayList = new ArrayList<String>();
        ArrayList<Boolean> enabledArrayList = new ArrayList<Boolean>();

        for (SourceFileInfo sourceFileInfo : landMasksData.getSourceFileInfos()) {
            jComboBoxArrayList.add(sourceFileInfo);

            if (sourceFileInfo.getDescription() != null) {
                toolTipsArrayList.add(sourceFileInfo.getDescription());
            } else {
                toolTipsArrayList.add(null);
            }

            enabledArrayList.add(new Boolean(sourceFileInfo.isEnabled()));
        }

        final SourceFileInfo[] jComboBoxArray;
        jComboBoxArray = new SourceFileInfo[jComboBoxArrayList.size()];

        int i = 0;
        for (SourceFileInfo sourceFileInfo : landMasksData.getSourceFileInfos()) {
            jComboBoxArray[i] = sourceFileInfo;
            i++;
        }

        final String[] toolTipsArray = new String[jComboBoxArrayList.size()];

        int j = 0;
        for (String validValuesToolTip : toolTipsArrayList) {
            toolTipsArray[j] = validValuesToolTip;
            j++;
        }

        final Boolean[] enabledArray = new Boolean[jComboBoxArrayList.size()];

        int k = 0;
        for (Boolean enabled : enabledArrayList) {
            enabledArray[k] = enabled;
            k++;
        }


        jComboBox = new JComboBox(jComboBoxArray);

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);
        myComboBoxRenderer.setEnabledList(enabledArray);

        jComboBox.setRenderer(myComboBoxRenderer);
        jComboBox.setEditable(false);


        for (SourceFileInfo sourceFileInfo : jComboBoxArray) {
            if (sourceFileInfo == landMasksData.getSourceFileInfo()) {
                jComboBox.setSelectedItem(sourceFileInfo);
            }
        }

        selectedIndex = jComboBox.getSelectedIndex();


        jLabel = new JLabel("Coastline Source Dataset");
        jLabel.setToolTipText("Determines which shoreline source dataset to use when generating the masks");

        addControlListeners();
    }

    private void addControlListeners() {
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SourceFileInfo sourceFileInfo = (SourceFileInfo) jComboBox.getSelectedItem();
                if (sourceFileInfo.isEnabled()) {
                    landMasksData.setSourceFileInfo(sourceFileInfo);
                    selectedIndex = jComboBox.getSelectedIndex();
                } else {
                    jComboBox.setSelectedIndex(selectedIndex);
                }
            }
        });

    }

    public JLabel getjLabel() {
        return jLabel;
    }

    public JComboBox getjComboBox() {
        return jComboBox;
    }


    class MyComboBoxRenderer extends BasicComboBoxRenderer {

        private String[] tooltips;
        private Boolean[] enabledList;


//        public void MyComboBoxRenderer(String[] tooltips) {
//            this.tooltips = tooltips;
//        }


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {


            if (isSelected) {


                if (-1 < index && index < tooltips.length) {
                    list.setToolTipText(tooltips[index]);
                }

                if (-1 < index && index < enabledList.length) {
                    if (enabledList[index] == true) {
//                        list.setSelectionBackground(Color.white);
//                        list.setSelectionForeground(Color.black);
                        setBackground(Color.blue);
                        setForeground(Color.white);
//                        setEnabled(true);
//                        setFocusable(true);
//
                    } else {
//                        list.setSelectionBackground(Color.white);
//                        list.setSelectionForeground(Color.gray);
//                        setBackground(Color.white);
//                        setForeground(Color.gray);
//                        setEnabled(false);
//                        setFocusable(false);
                    }
                }


            } else {

                if (-1 < index && index < enabledList.length) {
                    if (enabledList[index] == true) {
//                    list.setSelectionBackground(Color.white);
//                    list.setSelectionForeground(Color.black);
                        setBackground(Color.white);
                        setForeground(Color.black);
//                        setEnabled(true);
//                        setFocusable(true);
                    } else {
//                    list.setSelectionBackground(Color.white);
//                    list.setSelectionForeground(Color.gray);
                        setBackground(Color.white);
                        setForeground(Color.gray);
//                        setEnabled(false);
//                        setFocusable(false);
                    }

                }

            }
//
//            list.setSelectionBackground(Color.white);
//            list.setSelectionForeground(Color.black);

            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());
            return this;
        }

        public void setTooltipList(String[] tooltipList) {
            this.tooltips = tooltipList;
        }

        public void setEnabledList(Boolean[] enabledList) {
            this.enabledList = enabledList;
        }
    }

}
