package org.esa.beam.watermask.ui;

import org.esa.beam.framework.ui.GridBagUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/5/12
 * Time: 8:46 AM
 * To change this template use File | Settings | File Templates.
 */
class AuxilliaryMasksDialog extends JDialog {

    public AuxilliaryMasksData auxilliaryMasksData = null;


    public AuxilliaryMasksDialog(AuxilliaryMasksData auxilliaryMasksData, boolean masksCreated) {
        this.auxilliaryMasksData = auxilliaryMasksData;

        if (masksCreated) {
            notificationUI();
        } else {
            auxilliaryMasksUI();
        }
    }

    public final void notificationUI() {
        JButton jButton = new JButton("Okay");
        jButton.setPreferredSize(jButton.getPreferredSize());
        jButton.setMinimumSize(jButton.getPreferredSize());
        jButton.setMaximumSize(jButton.getPreferredSize());


        jButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        JLabel jLabel = new JLabel("Masks have already been created for this product");

        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.add(jLabel,
                new GridBagConstraintsCustom(0, 0, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE));
        jPanel.add(jButton,
                new GridBagConstraintsCustom(0, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE));


        add(jPanel);

        setModalityType(ModalityType.APPLICATION_MODAL);


        setTitle("Auxilliary Masks");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();


        setPreferredSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setSize(getPreferredSize());

    }

    public final void auxilliaryMasksUI() {

//        JPanel mainPanel = GridBagUtils.createPanel();
//        GridBagConstraints constraints = new GridBagConstraints();

        final CoastlineEnabledAllBandsCheckbox coastlineEnabledAllBandsCheckbox = new CoastlineEnabledAllBandsCheckbox(auxilliaryMasksData);
        final WaterEnabledAllBandsCheckbox waterEnabledAllBandsCheckbox = new WaterEnabledAllBandsCheckbox(auxilliaryMasksData);
        final LandEnabledAllBandsCheckbox landEnabledAllBandsCheckbox = new LandEnabledAllBandsCheckbox(auxilliaryMasksData);

        final CoastlineTransparencySpinner coastlineTransparencySpinner = new CoastlineTransparencySpinner(auxilliaryMasksData);
        final WaterTransparencySpinner waterTransparencySpinner = new WaterTransparencySpinner(auxilliaryMasksData);
        final LandTransparencySpinner landTransparencySpinner = new LandTransparencySpinner(auxilliaryMasksData);


        final CoastlineColorComboBox coastlineColorComboBox = new CoastlineColorComboBox(auxilliaryMasksData);
        final WaterColorComboBox waterColorComboBox = new WaterColorComboBox(auxilliaryMasksData);
        final LandColorComboBox landColorComboBox = new LandColorComboBox(auxilliaryMasksData);

        final SuperSamplingSpinner superSamplingSpinner = new SuperSamplingSpinner(auxilliaryMasksData);


        JPanel resolutionSamplingPanel = new JPanel(new GridBagLayout());
        resolutionSamplingPanel.setBorder(BorderFactory.createTitledBorder(""));

        resolutionSamplingPanel.add(superSamplingSpinner.getjLabel(),
                new GridBagConstraintsCustom(0, 0, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        resolutionSamplingPanel.add(superSamplingSpinner.getjSpinner(),
                new GridBagConstraintsCustom(1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));


        JPanel coastlineJPanel = new JPanel(new GridBagLayout());
        coastlineJPanel.setBorder(BorderFactory.createTitledBorder(""));


        JTextField coastlineNameTextfield = new JTextField(auxilliaryMasksData.getCoastlineMaskName());
        coastlineNameTextfield.setEditable(false);
        coastlineNameTextfield.setToolTipText("Name of the mask (this field is not editable)");

        coastlineJPanel.add(new JLabel("Mask Name"),
                new GridBagConstraintsCustom(0, 0, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineNameTextfield,
                new GridBagConstraintsCustom(1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineColorComboBox.getjLabel(),
                new GridBagConstraintsCustom(0, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineColorComboBox.getColorExComboBox(),
                new GridBagConstraintsCustom(1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineTransparencySpinner.getjLabel(),
                new GridBagConstraintsCustom(0, 2, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineTransparencySpinner.getjSpinner(),
                new GridBagConstraintsCustom(1, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineEnabledAllBandsCheckbox.getjLabel(),
                new GridBagConstraintsCustom(0, 3, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        coastlineJPanel.add(coastlineEnabledAllBandsCheckbox.getjCheckBox(),
                new GridBagConstraintsCustom(1, 3, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));


        JPanel waterJPanel = new JPanel(new GridBagLayout());
        waterJPanel.setBorder(BorderFactory.createTitledBorder(""));

        JTextField waterNameTextfield = new JTextField(auxilliaryMasksData.getWaterMaskName());
        waterNameTextfield.setEditable(false);
        waterNameTextfield.setToolTipText("Name of the mask (this field is not editable)");

        waterJPanel.add(new JLabel("Mask Name"),
                new GridBagConstraintsCustom(0, 0, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        waterJPanel.add(waterNameTextfield,
                new GridBagConstraintsCustom(1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        waterJPanel.add(waterColorComboBox.getjLabel(),
                new GridBagConstraintsCustom(0, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        waterJPanel.add(waterColorComboBox.getColorExComboBox(),
                new GridBagConstraintsCustom(1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        waterJPanel.add(waterTransparencySpinner.getjLabel(),
                new GridBagConstraintsCustom(0, 2, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        waterJPanel.add(waterTransparencySpinner.getjSpinner(),
                new GridBagConstraintsCustom(1, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        waterJPanel.add(waterEnabledAllBandsCheckbox.getjLabel(),
                new GridBagConstraintsCustom(0, 3, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        waterJPanel.add(waterEnabledAllBandsCheckbox.getjCheckBox(),
                new GridBagConstraintsCustom(1, 3, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));




        JPanel landJPanel = new JPanel(new GridBagLayout());
        landJPanel.setBorder(BorderFactory.createTitledBorder(""));

        JTextField landNameTextfield = new JTextField(auxilliaryMasksData.getLandMaskName());
        landNameTextfield.setEditable(false);
        landNameTextfield.setToolTipText("Name of the mask (this field is not editable)");

        landJPanel.add(new JLabel("Mask Name"),
                new GridBagConstraintsCustom(0, 0, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        landJPanel.add(landNameTextfield,
                new GridBagConstraintsCustom(1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        landJPanel.add(landColorComboBox.getjLabel(),
                new GridBagConstraintsCustom(0, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        landJPanel.add(landColorComboBox.getColorExComboBox(),
                new GridBagConstraintsCustom(1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        landJPanel.add(landTransparencySpinner.getjLabel(),
                new GridBagConstraintsCustom(0, 2, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        landJPanel.add(landTransparencySpinner.getjSpinner(),
                new GridBagConstraintsCustom(1, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));

        landJPanel.add(landEnabledAllBandsCheckbox.getjLabel(),
                new GridBagConstraintsCustom(0, 3, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));

        landJPanel.add(landEnabledAllBandsCheckbox.getjCheckBox(),
                new GridBagConstraintsCustom(1, 3, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));


        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.add(resolutionSamplingPanel,
                new GridBagConstraintsCustom(0, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 3));
        mainPanel.add(coastlineJPanel,
                new GridBagConstraintsCustom(0, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 3));
        mainPanel.add(waterJPanel,
                new GridBagConstraintsCustom(0, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 3));
        mainPanel.add(landJPanel,
                new GridBagConstraintsCustom(0, 3, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 3));


//        GridBagUtils.addToPanel(mainPanel, coastlineTransparencySpinner.getjLabel(), constraints,
//                "gridx=0, gridy=0, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, coastlineTransparencySpinner.getjSpinner(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, waterTransparencySpinner.getjLabel(), constraints,
//                "gridx=0, gridy=1, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, waterTransparencySpinner.getjSpinner(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, landTransparencySpinner.getjLabel(), constraints,
//                "gridx=0, gridy=2, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, landTransparencySpinner.getjSpinner(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, coastlineEnabledAllBandsCheckbox.getjLabel(), constraints,
//                "gridx=0, gridy=3, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, coastlineEnabledAllBandsCheckbox.getjCheckBox(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, waterEnabledAllBandsCheckbox.getjLabel(), constraints,
//                "gridx=0, gridy=4, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, waterEnabledAllBandsCheckbox.getjCheckBox(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, landEnabledAllBandsCheckbox.getjLabel(), constraints,
//                "gridx=0, gridy=5, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, landEnabledAllBandsCheckbox.getjCheckBox(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, coastlineColorComboBox.getjLabel(), constraints,
//                "gridx=0, gridy=6, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, coastlineColorComboBox.getColorExComboBox(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, waterColorComboBox.getjLabel(), constraints,
//                "gridx=0, gridy=7, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, waterColorComboBox.getColorExComboBox(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, landColorComboBox.getjLabel(), constraints,
//                "gridx=0, gridy=8, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, landColorComboBox.getColorExComboBox(), constraints,
//                "gridx=1, anchor=WEST");
//
//        GridBagUtils.addToPanel(mainPanel, superSamplingSpinner.getjLabel(), constraints,
//                "gridx=0, gridy=9, anchor=EAST");
//        GridBagUtils.addToPanel(mainPanel, superSamplingSpinner.getjSpinner(), constraints,
//                "gridx=1, anchor=WEST");
//

        JButton createMasks = new JButton("Create Masks");
        createMasks.setPreferredSize(createMasks.getPreferredSize());
        createMasks.setMinimumSize(createMasks.getPreferredSize());
        createMasks.setMaximumSize(createMasks.getPreferredSize());


        createMasks.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                auxilliaryMasksData.setCreateMasks(true);
                dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(cancelButton.getPreferredSize());
        cancelButton.setMinimumSize(cancelButton.getPreferredSize());
        cancelButton.setMaximumSize(cancelButton.getPreferredSize());

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        JLabel filler = new JLabel("                            ");


        JPanel buttonsJPanel = new JPanel(new GridBagLayout());
        buttonsJPanel.add(cancelButton,
                new GridBagConstraintsCustom(0, 0, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));
        buttonsJPanel.add(filler,
                new GridBagConstraintsCustom(1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
        buttonsJPanel.add(createMasks,
                new GridBagConstraintsCustom(2, 0, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE));


//            JPanel buttonsJPanel = GridBagUtils.createPanel();
//            GridBagConstraints buttonConstraints = new GridBagConstraints();
//            GridBagUtils.addToPanel(buttonsJPanel, new JLabel(" "), buttonConstraints,
//                    "gridx=0, gridy=0, anchor=EAST, weightx=1, fill="+GridBagConstraints.HORIZONTAL);
//
//            GridBagUtils.addToPanel(buttonsJPanel, createMasks, buttonConstraints,
//                    "gridx=1, gridy=0, anchor=CENTER, weightx=0");
//
//            GridBagUtils.addToPanel(buttonsJPanel, cancelButton, buttonConstraints,
//                    "gridx=2, gridy=0, anchor=EAST, weightx=0");
//


        createMasks.setAlignmentX(0.5f);

//            mainPanel.add(buttonsJPanel,
//                    new GridBagConstraintsCustom(0, 9, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
//


        //  buttonsJPanel.setPreferredSize(buttonsJPanel.getPreferredSize());

//        GridBagUtils.addToPanel(mainPanel, buttonsJPanel, constraints,
//                "gridx=0, gridy=10, weightx=1, anchor=WEST, fill=HORIZONTAL, gridwidth=2");

        mainPanel.add(buttonsJPanel,
                new GridBagConstraintsCustom(0, 4, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, 5));


        add(mainPanel);

        setModalityType(ModalityType.APPLICATION_MODAL);


        setTitle("Auxilliary Masks");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();


        setPreferredSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setSize(getPreferredSize());
    }
}


