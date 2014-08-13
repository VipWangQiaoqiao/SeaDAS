package org.esa.beam.framework.datamodel;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: Aynur Abdurazik (aabduraz)
* Date: 8/1/14
* Time: 3:53 PM
* To change this template use File | Settings | File Templates.
*/
class ColoredShape {

    private Geometry geometry;
    private Color color;

    public ColoredShape(Geometry geometry, Color color) {
        this.color = color;
        this.geometry = geometry;
    }

    public Color getColor() {
        return color;
    }

    public Geometry getGeometry() {
        return geometry;
    }

}
