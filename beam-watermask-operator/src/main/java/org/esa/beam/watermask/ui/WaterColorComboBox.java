package org.esa.beam.watermask.ui;

import com.jidesoft.combobox.ColorExComboBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/4/12
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class WaterColorComboBox {    private AuxilliaryMasksData auxilliaryMasksData;

    private JLabel jLabel;
    private ColorExComboBox colorExComboBox = new ColorExComboBox();

    public WaterColorComboBox(AuxilliaryMasksData auxilliaryMasksData) {

        this.auxilliaryMasksData = auxilliaryMasksData;

        jLabel = new JLabel("Color");
        jLabel.setToolTipText("Water mask color");

        colorExComboBox.setSelectedColor(auxilliaryMasksData.getWaterMaskColor());
        colorExComboBox.setPreferredSize(colorExComboBox.getPreferredSize());

        addControlListeners();
    }


    private void addControlListeners() {

        colorExComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                auxilliaryMasksData.setWaterMaskColor(colorExComboBox.getSelectedColor());
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
