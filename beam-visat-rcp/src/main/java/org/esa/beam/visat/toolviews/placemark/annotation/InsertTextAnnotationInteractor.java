package org.esa.beam.visat.toolviews.placemark.annotation;

import org.esa.beam.framework.datamodel.TextAnnotationDescriptor;
import org.esa.beam.visat.toolviews.placemark.InsertPlacemarkInteractor;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 6/22/16
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class InsertTextAnnotationInteractor extends InsertPlacemarkInteractor {
    public InsertTextAnnotationInteractor() {
        super(TextAnnotationDescriptor.getInstance());
    }
}
