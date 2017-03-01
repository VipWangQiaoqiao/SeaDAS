package org.esa.beam.framework.datamodel;

/**
 * Created by aabduraz on 10/8/15.
 */
public class TextAnnotationDescriptor extends PointPlacemarkDescriptor {

    public static TextAnnotationDescriptor getInstance() {
        return (TextAnnotationDescriptor) PlacemarkDescriptorRegistry.getInstance().getPlacemarkDescriptor(TextAnnotationDescriptor.class.getName());
    }

    public TextAnnotationDescriptor() {
        super("org.esa.beam.TextAnnotation");
    }

    @Override
    @Deprecated
    public String getShowLayerCommandId() {
        return "showTextAnnotationOverlay";
    }

    @Override
    @Deprecated
    public String getRoleName() {
        return "text-annotation";
    }

    @Override
    @Deprecated
    public String getRoleLabel() {
        return "text-annotation";
    }

    @Override
    @Deprecated
    public PlacemarkGroup getPlacemarkGroup(Product product) {
        return product.getTextAnnotationGroup();
    }

    @Override
    @Deprecated
    public PixelPos updatePixelPos(GeoCoding geoCoding, GeoPos geoPos, PixelPos pixelPos) {
        if (geoCoding == null || !geoCoding.canGetPixelPos() || geoPos == null) {
            return pixelPos;
        }
        return geoCoding.getPixelPos(geoPos, pixelPos);
    }

    @Override
    @Deprecated
    public GeoPos updateGeoPos(GeoCoding geoCoding, PixelPos pixelPos, GeoPos geoPos) {
        if (geoCoding == null || !geoCoding.canGetGeoPos()) {
            return geoPos;
        }
        return geoCoding.getGeoPos(pixelPos, geoPos);
    }
}
