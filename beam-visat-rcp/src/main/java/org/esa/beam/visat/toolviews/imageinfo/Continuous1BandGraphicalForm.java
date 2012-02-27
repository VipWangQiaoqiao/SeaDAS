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

package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ProductNodeEvent;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.ui.ImageInfoEditor;
import org.esa.beam.framework.ui.ImageInfoEditorModel;
import org.esa.beam.framework.ui.product.ProductSceneView;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Continuous1BandGraphicalForm implements ColorManipulationChildForm {

    private final ColorManipulationForm parentForm;
    private final ImageInfoEditor2 imageInfoEditor;
    private final ImageInfoEditorSupport imageInfoEditorSupport;
    private final JPanel contentPanel;
    private final AbstractButton evenDistButton;
    private final MoreOptionsForm moreOptionsForm;
    private ChangeListener applyEnablerCL;

    Continuous1BandGraphicalForm(final ColorManipulationForm parentForm) {
        this.parentForm = parentForm;

        imageInfoEditor = new ImageInfoEditor2(parentForm);
        imageInfoEditorSupport = new ImageInfoEditorSupport(this.imageInfoEditor);
        contentPanel = new JPanel(new BorderLayout(2, 2));
        contentPanel.add(imageInfoEditor, BorderLayout.CENTER);
        moreOptionsForm = new MoreOptionsForm(parentForm, true);

        evenDistButton = ImageInfoEditorSupport.createButton("icons/EvenDistribution24.gif");
        evenDistButton.setName("evenDistButton");
        evenDistButton.setToolTipText("Distribute sliders evenly between first and last slider"); /*I18N*/
        evenDistButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                distributeSlidersEvenly();
            }
        });

        applyEnablerCL = parentForm.createApplyEnablerChangeListener();
    }

    public Component getContentPanel() {
        return contentPanel;
    }

    public ImageInfoEditor getImageInfoEditor() {
        return imageInfoEditor;
    }

    @Override
    public void handleFormShown(ProductSceneView productSceneView) {
        updateFormModel(productSceneView);
    }

    @Override
    public void handleFormHidden(ProductSceneView productSceneView) {
        if (imageInfoEditor.getModel() != null) {
            imageInfoEditor.getModel().removeChangeListener(applyEnablerCL);
            imageInfoEditor.setModel(null);
        }
    }

    @Override
    public void updateFormModel(ProductSceneView productSceneView) {
        System.out.println("is log 10 scaled display in graphical form" + productSceneView.getRaster().isLog10ScaledDisplay());
        ImageInfoEditorModel1B model = new ImageInfoEditorModel1B(parentForm.getImageInfo());
        model.addChangeListener(applyEnablerCL);
        ImageInfoEditorModel oldModel = imageInfoEditor.getModel();
        setDisplayProperties(model, productSceneView.getRaster());
        imageInfoEditor.setModel(model);
        if (oldModel != null) {
            model.setHistogramViewGain(oldModel.getHistogramViewGain());
            model.setMinHistogramViewSample(oldModel.getMinHistogramViewSample());
            model.setMaxHistogramViewSample(oldModel.getMaxHistogramViewSample());
        }
        if (model.getSliderSample(0) < model.getMinHistogramViewSample() ||
                model.getSliderSample(model.getSliderCount() - 1) > model.getMaxHistogramViewSample()) {
            imageInfoEditor.computeZoomInToSliderLimits();
        }
        parentForm.setCurrentMinValue(imageInfoEditor.getFirstSliderSample());
        parentForm.setCurrentMaxValue(imageInfoEditor.getLastSliderSample());
        parentForm.revalidateToolViewPaneControl();
    }

    @Override
    public void resetFormModel(ProductSceneView productSceneView) {
        updateFormModel(productSceneView);
        imageInfoEditor.computeZoomOutToFullHistogramm();
        parentForm.revalidateToolViewPaneControl();
    }

    @Override
    public void handleRasterPropertyChange(ProductNodeEvent event, RasterDataNode raster) {
        if (imageInfoEditor.getModel() != null) {
            setDisplayProperties(imageInfoEditor.getModel(), raster);
            if (event.getPropertyName().equals(RasterDataNode.PROPERTY_NAME_STX)) {
                updateFormModel(parentForm.getProductSceneView());
            }
        }
    }

    @Override
    public RasterDataNode[] getRasters() {
        return parentForm.getProductSceneView().getRasters();
    }

    @Override
    public MoreOptionsForm getMoreOptionsForm() {
        return moreOptionsForm;
    }

    private void distributeSlidersEvenly() {
        imageInfoEditor.distributeSlidersEvenly();
    }


    public AbstractButton[] getToolButtons() {
        return new AbstractButton[]{
                imageInfoEditorSupport.autoStretch95Button,
                imageInfoEditorSupport.autoStretch100Button,
                imageInfoEditorSupport.zoomInVButton,
                imageInfoEditorSupport.zoomOutVButton,
                imageInfoEditorSupport.zoomInHButton,
                imageInfoEditorSupport.zoomOutHButton,
                imageInfoEditorSupport.showExtraInfoButton,
                evenDistButton,
        };
    }

    static void setDisplayProperties(ImageInfoEditorModel model, RasterDataNode raster) {
        System.out.println("raster " + raster.isLog10ScaledDisplay());
        model.setDisplayProperties(raster.getName(), raster.getUnit(), raster.getStx(), raster);
    }


}
