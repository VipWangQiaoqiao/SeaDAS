/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.esa.beam.visat.toolviews.mask;

import com.bc.ceres.core.*;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import org.esa.beam.BeamUiActivator;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.application.support.AbstractToolView;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.visat.VisatApp;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;

class MaskManagerForm extends MaskForm {

    private final AbstractButton helpButton;
    private final MaskFormActions actions;
    private VisatApp visatApp;

    private boolean defaultMasksInstalled = false;

    MaskManagerForm(AbstractToolView maskToolView, ListSelectionListener selectionListener) {
        super(true, selectionListener);

        helpButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("icons/Help22.png"), false);
        helpButton.setName("helpButton");
        visatApp = VisatApp.getApp();
        actions = new MaskFormActions(maskToolView, this);

        if (!defaultMasksInstalled) {
            installDefaultMaskProfiles(maskToolView);
        }

        updateState();


    }

    @Override
    public Action getDoubleClickAction() {
        return actions.getEditAction();
    }

    @Override
    public AbstractButton getHelpButton() {
        return helpButton;
    }

    @Override
    public final void updateState() {
        for (MaskAction maskAction : actions.getAllActions()) {
            maskAction.updateState();
        }
    }

    @Override
    public JPanel createContentPanel() {

        JPanel contentPane1 = GridBagUtils.createPanel();
//        contentPane1.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        contentPane1.add(new JScrollPane(getMaskTable()), gbc);

        JPanel buttonPanel = getButtons();

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx=0.0;
        gbc.weighty=1.0;
        contentPane1.add(buttonPanel, gbc);


       contentPane1.setMinimumSize(new Dimension(100, 400));
       contentPane1.setPreferredSize(new Dimension(320, 400));

        updateState();

        return contentPane1;
    }



    private JPanel getButtons() {
        JPanel buttonPanel = GridBagUtils.createPanel();

        GridBagConstraints gbc = new GridBagConstraints();


        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        //       gbc.weightx = 0.5;

        //       gbc.insets.bottom = 0;
        //       gbc.gridwidth = 1;

        gbc.gridy = 0;
        gbc.gridx = 0;
        final MaskAction[] allActions = actions.getAllActions();
        for (int i = 0; i < allActions.length; i += 1) {
            if (allActions[i].toString().contains("NullAction")) {
                buttonPanel.add(new JLabel(" "), gbc); // filler
                gbc.gridy++;
            } else {
                buttonPanel.add(allActions[i].createComponent(), gbc);
                gbc.gridy++;
            }

        }


        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        buttonPanel.add(new JLabel(" "), gbc); // filler

        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy++;
        buttonPanel.add(helpButton, gbc);
        return buttonPanel;
    }


    private void installDefaultMaskProfiles(AbstractToolView maskToolView) {
        final URL codeSourceUrl = BeamUiActivator.class.getProtectionDomain().getCodeSource().getLocation();
        final File auxdataDir = getMasksAuxdataDir();

        final ResourceInstaller resourceInstaller = new ResourceInstaller(codeSourceUrl, "auxdata/masks/",
                auxdataDir);
        ProgressMonitorSwingWorker swingWorker = new ProgressMonitorSwingWorker(maskToolView.getPaneControl(),
                "Installing Masks Auxdata...") {
            @Override
            protected Object doInBackground(com.bc.ceres.core.ProgressMonitor progressMonitor) throws Exception {
                resourceInstaller.install(".*.xml", progressMonitor, false);
                defaultMasksInstalled = true;
                return Boolean.TRUE;
            }

            /**
             * Executed on the <i>Event Dispatch Thread</i> after the {@code doInBackground}
             * method is finished. The default
             * implementation does nothing. Subclasses may override this method to
             * perform completion actions on the <i>Event Dispatch Thread</i>. Note
             * that you can query status inside the implementation of this method to
             * determine the result of this task or whether this task has been cancelled.
             *
             * @see #doInBackground
             * @see #isCancelled()
             * @see #get
             */
            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    visatApp.getLogger().log(Level.SEVERE, "Could not install Masks auxdata", e);
                }
            }
        };
        swingWorker.executeWithBlocking();
    }


    private File getMasksAuxdataDir() {
        return new File(SystemUtils.getApplicationDataDir(), "beam-ui/auxdata/masks");
    }



//    public JPanel createContentPanel() {
//        JPanel buttonPanel = GridBagUtils.createPanel();
//        GridBagConstraints gbc = new GridBagConstraints();
//
//
//
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.CENTER;
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.weightx = 0.5;
//
//        gbc.insets.bottom = 0;
//        gbc.gridwidth = 1;
//
//        gbc.gridx = 0;
//        final MaskAction[] allActions = actions.getAllActions();
//        for (int i = 0; i < allActions.length; i += 1) {
//            if (allActions[i].toString().contains("NullAction")) {
//                if (gbc.gridy == 0) {
//                    gbc.gridx += 3;
//                    buttonPanel.add(helpButton, gbc);
//                }
//                gbc.gridx = 0;
//                gbc.gridy++;
//            } else {
////                if (gbc.gridx > 5) {
////                    gbc.gridx = 0;
////                    gbc.gridy++;
////                }
//
//                buttonPanel.add(allActions[i].createComponent(), gbc);
//                //          buttonPanel.add(allActions[i + 1].createComponent(), gbc);
//                gbc.gridx++;
//            }
//
//        }
//
//
////        gbc.fill = GridBagConstraints.VERTICAL;
////        gbc.weighty = 1.0;
////        gbc.gridwidth = 2;
////        buttonPanel.add(new JLabel(" "), gbc); // filler
////        gbc.fill = GridBagConstraints.NONE;
////        gbc.weighty = 0.0;
////        gbc.gridx = 1;
////        gbc.gridy++;
////        gbc.gridwidth = 1;
////        buttonPanel.add(helpButton, gbc);
//
//        JPanel tablePanel = new JPanel(new BorderLayout(4, 4));
//        tablePanel.add(new JScrollPane(getMaskTable()), BorderLayout.CENTER);
//
//        JPanel contentPane1 = new JPanel(new BorderLayout(4, 4));
//        contentPane1.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
//        contentPane1.add(BorderLayout.CENTER, tablePanel);
//        contentPane1.add(BorderLayout.NORTH, buttonPanel);
//
//        contentPane1.setMinimumSize(new Dimension(250, 400));
//
//        updateState();
//
//        return contentPane1;
//    }
}