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

    public ResolutionComboBox(LandMasksData landMasksData) {

        this.landMasksData = landMasksData;

        ArrayList<ResolutionInfo> jComboBoxArrayList = new ArrayList<ResolutionInfo>();
        ArrayList<String> toolTipsArrayList = new ArrayList<String>();


        for (ResolutionInfo resolutionInfo : landMasksData.getResolutionInfos()) {
            jComboBoxArrayList.add(resolutionInfo);

            if (resolutionInfo.getDescription() != null) {
                toolTipsArrayList.add(resolutionInfo.getDescription());
            } else {
                toolTipsArrayList.add(null);
            }
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


        jComboBox = new JComboBox(jComboBoxArray);

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltips(toolTipsArray);
        jComboBox.setRenderer(myComboBoxRenderer);
        jComboBox.setEditable(false);


        for (ResolutionInfo resolutionInfo : jComboBoxArray) {
            if (resolutionInfo == landMasksData.getResolutionInfo()) {
                jComboBox.setSelectedItem(resolutionInfo);
            }
        }


        jLabel = new JLabel("Resolution");
        jLabel.setToolTipText("Resolution");

        addControlListeners();
    }

    private void addControlListeners() {
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                landMasksData.setResolutionInfo((ResolutionInfo) jComboBox.getSelectedItem());
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

        public void MyComboBoxRenderer(String[] tooltips) {
            this.tooltips = tooltips;
        }


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());

                if (-1 < index && index < tooltips.length) {
                    list.setToolTipText(tooltips[index]);
                }
            } else {
                setBackground(Color.white);
                setForeground(Color.black);
            }

            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());
            return this;
        }

        public void setTooltips(String[] tooltips) {
            this.tooltips = tooltips;
        }
    }

}
