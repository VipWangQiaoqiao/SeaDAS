package org.esa.beam.glayer;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerContext;
import com.bc.ceres.glayer.annotations.LayerTypeMetadata;
import com.bc.ceres.glayer.support.ImageLayer;
import com.bc.ceres.glevel.MultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.geotools.referencing.AbstractIdentifiedObject;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Aynur Abdurazik (aabduraz)
 * Date: 7/14/14
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
@LayerTypeMetadata(name = "ColorBarLayerType",
        aliasNames = {"gov.nasa.gsfc.seadas.colorbar.layer.ColorBarLayerType"})
public class ColorBarLayerType extends ImageLayer.Type {

    public static final String PROPERTY_NAME_COLOR_BAR_IMAGE = "renderedImage";
    public static final String PROPERTY_NAME_COLORBAR_TRANSFORM = "colorBarTransform";
    private static final String COLOR_BAR_LABEL = "Color Bar";

    @Override
    public boolean isValidFor(LayerContext ctx) {
        if (ctx.getCoordinateReferenceSystem() instanceof AbstractIdentifiedObject) {
            AbstractIdentifiedObject crs = (AbstractIdentifiedObject) ctx.getCoordinateReferenceSystem();
            return DefaultGeographicCRS.WGS84.equals(crs, false);
        }
        return false;
    }

    @Override
    public Layer createLayer(LayerContext ctx, PropertySet configuration) {
//        final File file = (File) configuration.getValue(PROPERTY_NAME_COLOR_BAR_IMAGE);
//        RenderedImage image = FileLoadDescriptor.create(file.getPath(), null, true, null);
        final RenderedImage colorBarImage = (RenderedImage)configuration.getValue(PROPERTY_NAME_COLOR_BAR_IMAGE);
//        configuration.setValue(PROPERTY_NAME_COLORBAR_TRANSFORM, createTransform(image));
        final AffineTransform transform = (AffineTransform) configuration.getValue(PROPERTY_NAME_COLORBAR_TRANSFORM);

        //final AffineTransform transform = createTransform(colorBarImage);
        //final AffineTransform transform = new AffineTransform();
        final Rectangle2D modelBounds = DefaultMultiLevelModel.getModelBounds(transform, colorBarImage);
        final DefaultMultiLevelModel model = new DefaultMultiLevelModel(1, transform, modelBounds);
        final MultiLevelSource multiLevelSource = new DefaultMultiLevelSource(colorBarImage, model);
        return new ImageLayer(this, multiLevelSource, configuration);
    }

    @Override
    public PropertySet createLayerConfig(LayerContext ctx) {
        final PropertyContainer template = new PropertyContainer();

        final Property renderedImage = Property.create(PROPERTY_NAME_COLOR_BAR_IMAGE, RenderedImage.class);
        renderedImage.getDescriptor().setNotNull(true);
        template.addProperty(renderedImage);

        final Property colorBarTransformModel = Property.create(PROPERTY_NAME_COLORBAR_TRANSFORM, AffineTransform.class);
        colorBarTransformModel.getDescriptor().setNotNull(true);
        template.addProperty(colorBarTransformModel);

        return template;
    }



    private AffineTransform getRotationTransform() {
        AffineTransform rotation = new AffineTransform();
        rotation.setToQuadrantRotation(3);
        return rotation;
    }

//    private AffineTransform getTranslateTransform() {
//        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//        tx.translate(-VisatApp.getApp().getSelectedProductSceneView().getBaseImageLayer().getImage().getWidth(), 0);
//        return tx;
//    }

}
