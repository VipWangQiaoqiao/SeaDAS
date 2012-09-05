package org.esa.beam.watermask.ui;

import com.jidesoft.combobox.ColorExComboBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/4/12
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoastlineColorComboBox {
    private AuxilliaryMasksData auxilliaryMasksData;

    private JLabel jLabel;
    private ColorExComboBox colorExComboBox = new ColorExComboBox();

    public CoastlineColorComboBox(AuxilliaryMasksData auxilliaryMasksData) {

        this.auxilliaryMasksData = auxilliaryMasksData;

        jLabel = new JLabel("Color");
        jLabel.setToolTipText("Coastline mask color");

        colorExComboBox.setSelectedColor(auxilliaryMasksData.getCoastlineMaskColor());
        colorExComboBox.setPreferredSize(colorExComboBox.getPreferredSize());

        addControlListeners();
    }


    private void addControlListeners() {

        colorExComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                auxilliaryMasksData.setCoastlineMaskColor(colorExComboBox.getSelectedColor());
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
