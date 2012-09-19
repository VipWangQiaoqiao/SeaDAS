package org.esa.beam.watermask.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/4/12
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoastlineTransparencySpinner {

    private AuxiliaryMasksData auxiliaryMasksData;

    private JLabel jLabel;
    private JSpinner jSpinner = new JSpinner();

    public CoastlineTransparencySpinner(AuxiliaryMasksData auxiliaryMasksData) {

        this.auxiliaryMasksData = auxiliaryMasksData;

        jLabel = new JLabel("Transparency");
        jLabel.setToolTipText("Coastline mask transparency");

        jSpinner.setModel(new SpinnerNumberModel(100, 0, 100, 100));

        jSpinner.setPreferredSize(jSpinner.getPreferredSize());
        jSpinner.setSize(jSpinner.getPreferredSize());

        jSpinner.setModel(new SpinnerNumberModel(auxiliaryMasksData.getCoastlineMaskTransparency(), 0.0, 1.0, 0.1));

        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) jSpinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(1);

        addControlListeners();
    }


    private void addControlListeners() {
        jSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                auxiliaryMasksData.setCoastlineMaskTransparency((Double) jSpinner.getValue());
            }
        });
    }


    public JLabel getjLabel() {
        return jLabel;
    }

    public JSpinner getjSpinner() {
        return jSpinner;
    }
}

