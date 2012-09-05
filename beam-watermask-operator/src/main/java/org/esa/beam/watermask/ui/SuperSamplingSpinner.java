package org.esa.beam.watermask.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/5/12
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class SuperSamplingSpinner {

    private AuxilliaryMasksData auxilliaryMasksData;

    private JLabel jLabel;
    private JSpinner jSpinner = new JSpinner();

    public SuperSamplingSpinner(AuxilliaryMasksData auxilliaryMasksData) {

        this.auxilliaryMasksData = auxilliaryMasksData;

        jLabel = new JLabel("Super Sampling");

        jLabel.setToolTipText("Super Sampling");

        jSpinner.setModel(new SpinnerNumberModel(20, 1, 20, 1));

        jSpinner.setPreferredSize(jSpinner.getPreferredSize());
        jSpinner.setSize(jSpinner.getPreferredSize());

        jSpinner.setModel(new SpinnerNumberModel(auxilliaryMasksData.getSuperSampling(), 1, 20, 1));

//        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) jSpinner.getEditor();
//        DecimalFormat format = editor.getFormat();
//        format.setMinimumFractionDigits(1);

        addControlListeners();
    }


    private void addControlListeners() {
        jSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int value =Integer.parseInt(jSpinner.getValue().toString() );
                auxilliaryMasksData.setSuperSampling(value);
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
