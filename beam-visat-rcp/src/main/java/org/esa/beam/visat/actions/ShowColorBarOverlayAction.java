package org.esa.beam.visat.actions;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerFilter;
import com.bc.ceres.glayer.LayerType;
import com.bc.ceres.glayer.LayerTypeRegistry;
import com.bc.ceres.glayer.support.LayerUtils;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.glayer.ColorBarLayerType;
import org.esa.beam.visat.VisatApp;

import javax.media.jai.TiledImage;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Aynur Abdurazik (aabduraz)
 * Date: 7/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowColorBarOverlayAction extends AbstractShowOverlayAction {

    private static final String COLORBAR_TYPE_PROPERTY_NAME = "colorbar.type";
    private static final String DEFAULT_LAYER_TYPE = "ColorBarLayerType";
    private RenderedImage colorBarImage;

    @Override
    public void actionPerformed(CommandEvent event) {
        final ProductSceneView view = VisatApp.getApp().getSelectedProductSceneView();
        if (view != null) {
            Layer rootLayer = view.getRootLayer();
            Layer colorBarLayer = findColorBarLayer(view);
            //           if (isSelected()) {
            if (colorBarLayer != null) {
                rootLayer.getChildren().remove(colorBarLayer);
            }
            colorBarLayer = createColorBarLayer(colorBarImage);
            colorBarLayer.setName("Color Bar");
            //put color bar layer on top of everything
            rootLayer.getChildren().add(0, colorBarLayer);
            colorBarLayer.setVisible(true);
//            } else {
//                view.updateCurrentLayer(colorBarLayer, false);
//            }
        }
    }

    private Layer createColorBarLayer(RenderedImage colorBarImage) {
        final LayerType layerType = getColorBarLayerType();
        final PropertySet template = layerType.createLayerConfig(null);
        template.setValue(ColorBarLayerType.PROPERTY_NAME_COLOR_BAR_IMAGE, colorBarImage);
        template.setValue(ColorBarLayerType.PROPERTY_NAME_COLORBAR_TRANSFORM, createTransform(colorBarImage));
        return layerType.createLayer(null, template);
    }


    @Override
    protected void updateEnableState(ProductSceneView view) {
        setEnabled(true);
    }

    @Override
    protected void updateSelectState(ProductSceneView view) {
        Layer colorBarLayer = findColorBarLayer(view);
        setSelected(colorBarLayer != null && colorBarLayer.isVisible());
    }

    private LayerType getColorBarLayerType() {
        final VisatApp visatApp = VisatApp.getApp();
        String layerTypeClassName = visatApp.getPreferences().getPropertyString(COLORBAR_TYPE_PROPERTY_NAME,
                DEFAULT_LAYER_TYPE);
        return LayerTypeRegistry.getLayerType(layerTypeClassName);
    }

    private Layer findColorBarLayer(ProductSceneView view) {
        return LayerUtils.getChildLayer(view.getRootLayer(), LayerUtils.SearchMode.DEEP, new LayerFilter() {
            public boolean accept(Layer layer) {
                return layer.getLayerType() instanceof ColorBarLayerType;
            }
        });
    }

    public RenderedImage getColorBarImage() {
        return colorBarImage;
    }

    public void setColorBarImage(RenderedImage colorBarImage) {
        this.colorBarImage = new TiledImage(colorBarImage, true);
    }

    private AffineTransform createTransform(RenderedImage image) {

        VisatApp visatApp = VisatApp.getApp();
        ProductSceneView sceneView = visatApp.getSelectedProductSceneView();
        RasterDataNode raster = sceneView.getSceneImage().getRasters()[0];
        AffineTransform transform = raster.getSourceImage().getModel().getImageToModelTransform(0);
        transform.concatenate(createTransform(raster, image));
        return transform;
    }

    private AffineTransform createTransform(RasterDataNode raster, RenderedImage colorBarImage) {

        int colorBarImageWidth = colorBarImage.getWidth();
        int colorBarImageHeight = colorBarImage.getHeight();

        int rasterWidth = raster.getRasterWidth();
        int rasterHeight = raster.getRasterHeight();
        //if color bar is horizontal
        double scaleX = (colorBarImageHeight < colorBarImageWidth) ? (double) rasterWidth / colorBarImageWidth : 0.6;
        //if color bar is vertical
        double scaleY = (colorBarImageHeight > colorBarImageWidth) ? (double) rasterHeight / colorBarImageHeight : 0.6;
        if (scaleX > 1) scaleY = scaleY + 1;
        if (scaleY > 1) scaleX = scaleX + 1;
        int y_axis_translation = (colorBarImageHeight < colorBarImageWidth) ? rasterHeight - colorBarImageHeight / 4 : 0;
        int x_axis_translation = (colorBarImageHeight < colorBarImageWidth) ? 0 : rasterWidth - colorBarImageWidth / 10;
        double[] flatmatrix = {scaleX, 0.0, 0.0, scaleY, x_axis_translation, y_axis_translation};
        AffineTransform i2mTransform = new AffineTransform(flatmatrix);
        return i2mTransform;
    }

}
