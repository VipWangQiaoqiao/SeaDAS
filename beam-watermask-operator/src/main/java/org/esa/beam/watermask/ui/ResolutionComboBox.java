package org.esa.beam.watermask.ui;

import org.esa.beam.watermask.operator.WatermaskClassifier;

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

        ArrayList<ResolutionInfo> jComboBoxArrayList = new ArrayList<ResolutionInfo>();
        ArrayList<String> toolTipsArrayList = new ArrayList<String>();
        ArrayList<Boolean> enabledArrayList = new ArrayList<Boolean>();

        for (ResolutionInfo resolutionInfo : landMasksData.getResolutionInfos()) {
            jComboBoxArrayList.add(resolutionInfo);

            if (resolutionInfo.getDescription() != null) {
                toolTipsArrayList.add(resolutionInfo.getDescription());
            } else {
                toolTipsArrayList.add(null);
            }

            enabledArrayList.add(new Boolean(resolutionInfo.isEnabled()));
        }

        final ResolutionInfo[] jComboBoxArray;
        jComboBoxArray = new ResolutionInfo[jComboBoxArrayList.size()];

        int i = 0;
        for (ResolutionInfo resolutionInfo : landMasksData.getResolutionInfos()) {
            jComboBoxArray[i] = resolutionInfo;
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


        for (ResolutionInfo resolutionInfo : jComboBoxArray) {
            if (resolutionInfo == landMasksData.getResolutionInfo()) {
                jComboBox.setSelectedItem(resolutionInfo);
            }
        }

        selectedIndex = jComboBox.getSelectedIndex();


        jLabel = new JLabel("Resolution");
        jLabel.setToolTipText("Resolution");

        addControlListeners();
    }

    private void addControlListeners() {
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResolutionInfo resolutionInfo = (ResolutionInfo) jComboBox.getSelectedItem();
                if (resolutionInfo.isEnabled()) {
                    landMasksData.setResolutionInfo(resolutionInfo);
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
//                        setBackground(Color.white);
//                        setForeground(Color.black);
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


            }

            if (-1 < index && index < enabledList.length) {
                if (enabledList[index] == true) {
                    list.setSelectionBackground(Color.white);
                    list.setSelectionForeground(Color.black);
                    setBackground(Color.white);
                    setForeground(Color.black);
                    setEnabled(true);
                    setFocusable(true);
                } else {
                    list.setSelectionBackground(Color.white);
                    list.setSelectionForeground(Color.gray);
                    setBackground(Color.white);
                    setForeground(Color.gray);
                    setEnabled(false);
                    setFocusable(false);
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
