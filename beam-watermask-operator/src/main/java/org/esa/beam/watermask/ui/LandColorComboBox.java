package org.esa.beam.watermask.ui;

import com.jidesoft.combobox.ColorExComboBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/4/12
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class LandColorComboBox {
    private AuxiliaryMasksData auxiliaryMasksData;

    private JLabel jLabel;
    private ColorExComboBox colorExComboBox = new ColorExComboBox();

    public LandColorComboBox(AuxiliaryMasksData auxiliaryMasksData) {

        this.auxiliaryMasksData = auxiliaryMasksData;

        jLabel = new JLabel("Color");
        jLabel.setToolTipText("Land mask color");

        colorExComboBox.setSelectedColor(auxiliaryMasksData.getLandMaskColor());
        colorExComboBox.setPreferredSize(colorExComboBox.getPreferredSize());

        addControlListeners();
    }


    private void addControlListeners() {

        colorExComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                auxiliaryMasksData.setLandMaskColor(colorExComboBox.getSelectedColor());
            }
        });
    }


    public JLabel getjLabel() {
        return jLabel;
    }

    public ColorExComboBox getColorExComboBox() {
        return colorExComboBox;
    }
}
