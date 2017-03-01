package org.esa.beam.visat.actions;

import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.visat.VisatApp;

/**
 * Created by aabduraz on 2/28/17.
 */
public class ShowTextAnnotationOverlayAction extends AbstractShowOverlayAction{
    @Override
    public void actionPerformed(CommandEvent event) {
        final ProductSceneView view = VisatApp.getApp().getSelectedProductSceneView();
        if (view != null) {
            view.setTextAnnotationOverlayEnabled(isSelected());
        }
    }

    @Override
    protected void updateEnableState(ProductSceneView view) {
        setEnabled(view.getProduct().getTextAnnotationGroup().getNodeCount() > 0);
    }

    @Override
    protected void updateSelectState(ProductSceneView view) {
        setSelected(view.isTextAnnotationOverlayEnabled());
    }
}
