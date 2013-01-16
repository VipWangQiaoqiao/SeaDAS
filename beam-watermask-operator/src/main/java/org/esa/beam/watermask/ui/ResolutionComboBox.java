package org.esa.beam.watermask.ui;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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

    private String INSTALL_FILE_EVENT = "INSTALL_FILE_EVENT";

    private final SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);




    public ResolutionComboBox(final LandMasksData landMasksData) {

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


        final SourceFileInfo[]  jComboBoxArray = new SourceFileInfo[jComboBoxArrayList.size()];

        int i = 0;
        for (SourceFileInfo sourceFileInfo : landMasksData.getSourceFileInfos()) {
            jComboBoxArray[i] = sourceFileInfo;
            i++;
        }

        final String[]  toolTipsArray = new String[jComboBoxArrayList.size()];

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

        this.addPropertyChangeListener(INSTALL_FILE_EVENT, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SourceFileInfo sourceFileInfo = (SourceFileInfo) jComboBox.getSelectedItem();

                InstallResolutionFileDialog dialog = new InstallResolutionFileDialog(landMasksData, sourceFileInfo, InstallResolutionFileDialog.Step.INSTALLATION);
                dialog.setVisible(true);
                dialog.setEnabled(true);
            }
        });
    }


    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }


    private void addControlListeners() {
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SourceFileInfo sourceFileInfo = (SourceFileInfo) jComboBox.getSelectedItem();
                landMasksData.setSourceFileInfo(sourceFileInfo);
                selectedIndex = jComboBox.getSelectedIndex();


                if (!sourceFileInfo.isEnabled()) {
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, INSTALL_FILE_EVENT, null, null));
                        }
                    });
                }

//                if (fileAvailable) {
//                landMasksData.setSourceFileInfo(sourceFileInfo);
//                selectedIndex = jComboBox.getSelectedIndex();
//                } else {
//                    jComboBox.setSelectedIndex(selectedIndex);
//                }
            }
        });

    }

    public JLabel getjLabel() {
        return jLabel;
    }

    public JComboBox getjComboBox() {
        return jComboBox;
    }


    public void updateJComboBox() {

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


        final SourceFileInfo[]  jComboBoxArray = new SourceFileInfo[jComboBoxArrayList.size()];

        int i = 0;
        for (SourceFileInfo sourceFileInfo : landMasksData.getSourceFileInfos()) {
            jComboBoxArray[i] = sourceFileInfo;
            i++;
        }

        final String[]  toolTipsArray = new String[jComboBoxArrayList.size()];

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







//        int i = 0;
//        for (SourceFileInfo sourceFileInfo : jComboBoxArray) {
//            enabledArray[i] = sourceFileInfo.isEnabled();
//            i++;
//        }
//
//
//        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
//        myComboBoxRenderer.setTooltipList(toolTipsArray);
//        myComboBoxRenderer.setEnabledList(enabledArray);
//
//        jComboBox.setRenderer(myComboBoxRenderer);
//
//
//        for (SourceFileInfo sourceFileInfo : jComboBoxArray) {
//            if (sourceFileInfo == landMasksData.getSourceFileInfo()) {
//                jComboBox.setSelectedItem(sourceFileInfo);
//            }
//        }
//
//        selectedIndex = jComboBox.getSelectedIndex();
//

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
                        setBackground(Color.blue);
                        setForeground(Color.gray);
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
