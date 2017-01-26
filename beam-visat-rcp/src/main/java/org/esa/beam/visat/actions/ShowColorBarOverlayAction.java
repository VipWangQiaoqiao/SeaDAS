package org.esa.beam.visat.actions;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerFilter;
import com.bc.ceres.glayer.LayerType;
import com.bc.ceres.glayer.LayerTypeRegistry;
import com.bc.ceres.glayer.support.LayerUtils;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.glayer.ColorBarLayerType;
import org.esa.beam.util.FeatureUtils;
import org.esa.beam.visat.VisatApp;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

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
    private int orientation;
    private double layerOffset = 0;
    private double layerShift = 0;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection;

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

    public void createColorBarVectorNode(){
        VisatApp visatApp = VisatApp.getApp();
        final ProductSceneView sceneView = VisatApp.getApp().getSelectedProductSceneView();
        Product product = visatApp.getSelectedProduct();
        String vectorName = "Color Bar";

        final CoordinateReferenceSystem mapCRS = product.getGeoCoding().getMapCRS();
//        if (!mapCRS.equals(DefaultGeographicCRS.WGS84)) {
//            try {
//                transformFeatureCollection(featureCollection, product.getGeoCoding().getImageCRS(), mapCRS);
//            } catch (TransformException e) {
//                VisatApp.getApp().showErrorDialog("transformation failed!");
//            }
//        }
        featureCollection = FeatureUtils.transformPixelPosToGeoPos(featureCollection, product.getGeoCoding());
        final PlacemarkDescriptor placemarkDescriptor = PlacemarkDescriptorRegistry.getInstance().getPlacemarkDescriptor(featureCollection.getSchema());
        placemarkDescriptor.setUserDataOf(featureCollection.getSchema());
        VectorDataNode vectorDataNode = new VectorDataNode(vectorName, featureCollection, placemarkDescriptor);
//        String hex = String.format("#%02x%02x%02x", Color.red, Color.green, Color.blue);
//        vectorDataNode.setDefaultStyleCss(String.format( "fill:%s; fill-opacity:0.5; stroke:%s; stroke-opacity:1.0; stroke-width:1.0; symbol:plus", hex, hex));
        if (product.getVectorDataGroup().contains(vectorDataNode.getName())) {
            product.getVectorDataGroup().remove(product.getVectorDataGroup().get(vectorDataNode.getName()));
        }
        product.getVectorDataGroup().add(vectorDataNode);
        if (sceneView != null) {
            sceneView.setLayersVisible(vectorDataNode);
        }
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
        //return createTransform(raster, image);
    }

    private AffineTransform createTransform(RasterDataNode raster, RenderedImage colorBarImage) {

        int colorBarImageWidth = colorBarImage.getWidth();
        int colorBarImageHeight = colorBarImage.getHeight();

        int rasterWidth = raster.getRasterWidth();
        int rasterHeight = raster.getRasterHeight();

        // todo Danny commented these out as orientation was being determined by image dimensions not by actual orientation
        //if color bar is horizontal
       // double scaleX = (colorBarImageHeight < colorBarImageWidth) ? (double) rasterWidth / colorBarImageWidth : 0.6;

        //if color bar is vertical
       // double scaleY = (colorBarImageHeight > colorBarImageWidth) ? (double) rasterHeight / colorBarImageHeight : 0.6;

//        if (scaleX > 1) {
//            scaleY = scaleY + 1;
//        }   else if (scaleY > 1) { //this statement must have the "else" clause, otherwise the scaleX will be problematic.
//            scaleX = scaleX + 1;
//        }
//        int y_axis_translation = (colorBarImageHeight < colorBarImageWidth) ? rasterHeight : (rasterHeight - colorBarImageHeight)/2;
//        int x_axis_translation = (colorBarImageHeight < colorBarImageWidth) ? (rasterWidth - colorBarImageWidth)/2 : rasterWidth ;

        //todo Danny added the following 2 lines and commented out the preceding block


//        double y_axis_translation = (getOrientation() == ImageLegend.HORIZONTAL) ? rasterHeight + (rasterHeight * getLayerOffset()/100) : (rasterHeight - colorBarImageHeight)/2;
//        double x_axis_translation = (getOrientation() == ImageLegend.HORIZONTAL) ? (rasterWidth - colorBarImageWidth)/2 : rasterWidth + (rasterWidth * getLayerOffset()/100) ;

        double offset = (getOrientation() == ImageLegend.HORIZONTAL) ?  (colorBarImageHeight * getLayerOffset()/100) : (colorBarImageWidth * getLayerOffset()/100);
        double shift = (getOrientation() == ImageLegend.HORIZONTAL) ?  (colorBarImageWidth * getLayerShift()/100) : -(colorBarImageHeight * getLayerShift()/100);

        double y_axis_translation = (getOrientation() == ImageLegend.HORIZONTAL) ? rasterHeight + offset : shift+ (rasterHeight - colorBarImageHeight)/2;
        double x_axis_translation = (getOrientation() == ImageLegend.HORIZONTAL) ? shift+ (rasterWidth - colorBarImageWidth)/2 : rasterWidth + offset ;
        //double[] flatmatrix = {scaleX, 0.0, 0.0, scaleY, x_axis_translation, y_axis_translation};
        double[] flatmatrix = {1, 0.0, 0.0, 1, x_axis_translation, y_axis_translation};
        AffineTransform i2mTransform = new AffineTransform(flatmatrix);
        return i2mTransform;
    }

    private static void transformFeatureCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection, CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) throws TransformException {
        final GeometryCoordinateSequenceTransformer transform = FeatureUtils.getTransform(sourceCRS, targetCRS);
        final FeatureIterator<SimpleFeature> features = featureCollection.features();
        final GeometryFactory geometryFactory = new GeometryFactory();
        while (features.hasNext()) {
            final SimpleFeature simpleFeature = features.next();
            final LineString sourceLine = (LineString) simpleFeature.getDefaultGeometry();
            final LineString targetLine = transform.transformLineString(sourceLine, geometryFactory);
            simpleFeature.setDefaultGeometry(targetLine);
        }
    }

    public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatureCollection() {
        return featureCollection;
    }

    public void setFeatureCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
        this.featureCollection = featureCollection;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public double getLayerOffset() {
        return layerOffset;
    }

    public void setLayerOffset(double layerOffset) {
        this.layerOffset = layerOffset;
    }

    public double getLayerShift() {
        return layerShift;
    }

    public void setLayerShift(double layerShift) {
        this.layerShift = layerShift;
    }
}
