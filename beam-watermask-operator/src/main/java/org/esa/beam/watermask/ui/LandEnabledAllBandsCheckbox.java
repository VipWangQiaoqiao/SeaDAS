package org.esa.beam.watermask.ui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/4/12
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class LandEnabledAllBandsCheckbox {
    private AuxilliaryMasksData auxilliaryMasksData;

    private JLabel jLabel;
    private JCheckBox jCheckBox = new JCheckBox();

    private static String DEFAULT_NAME = "Enabled in All Bands";
    private static String DEFAULT_TOOLTIPS = "Set Land Mask Enabled in All Bands";

    public LandEnabledAllBandsCheckbox(AuxilliaryMasksData auxilliaryMasksData) {

        this.auxilliaryMasksData = auxilliaryMasksData;

        jLabel = new JLabel(DEFAULT_NAME);
        jLabel.setToolTipText(DEFAULT_TOOLTIPS);
        jCheckBox.setSelected(auxilliaryMasksData.isShowLandMaskAllBands());

        addControlListeners();
    }

    private void addControlListeners() {
        jCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                auxilliaryMasksData.setShowLandMaskAllBands(jCheckBox.isSelected());

            }
        });
    }


    public JLabel getjLabel() {
        return jLabel;
    }

    public JCheckBox getjCheckBox() {
        return jCheckBox;
    }
}
