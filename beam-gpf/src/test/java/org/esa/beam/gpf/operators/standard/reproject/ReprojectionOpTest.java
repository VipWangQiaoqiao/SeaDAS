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

package org.esa.beam.gpf.operators.standard.reproject;

import org.esa.beam.framework.datamodel.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ReprojectionOpTest extends AbstractReprojectionOpTest {

    @Test
    public void testGeoLatLon() throws IOException {
        parameterMap.put("crs", WGS84_CODE);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        // because source is rectangular the size of source is preserved
        assertEquals(50, targetPoduct.getSceneRasterWidth());
        assertEquals(50, targetPoduct.getSceneRasterHeight());
        assertNotNull(targetPoduct.getGeoCoding());

        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testUTMWithWktText() throws IOException {
        parameterMap.put("crs", UTM33N_WKT);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testWithWktFile() throws IOException {
        parameterMap.put("wktFile", wktFile);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testWithCollocationProduct() {
        Map<String, Product> productMap = new HashMap<String, Product>(5);
        productMap.put("source", sourceProduct);
        parameterMap.put("crs", "AUTO:42002");
        final Product collocationProduct = createReprojectedProduct(productMap);

        productMap = new HashMap<String, Product>(5);
        productMap.put("source", sourceProduct);
        productMap.put("collocateWith", collocationProduct);
        parameterMap.remove("crs");
        final Product targetProduct = createReprojectedProduct(productMap);
        assertNotNull(targetProduct);
        assertTrue(targetProduct.isCompatibleProduct(collocationProduct, 1.0e-6f));
    }

    @Test
    public void testUTM() throws IOException {
        parameterMap.put("crs", UTM33N_CODE);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testStartAndEndTime() throws Exception {
        parameterMap.put("crs", UTM33N_CODE);
        final Product targetPoduct = createReprojectedProduct();
        assertNotNull(targetPoduct.getStartTime());
        assertNotNull(targetPoduct.getEndTime());
        String meanTime = "02-JAN-2008 10:30:30.000000";
        assertEquals(meanTime, targetPoduct.getStartTime().format());
        assertEquals(meanTime, targetPoduct.getEndTime().format());
    }

    @Test
    public void testUTM_Bilinear() throws IOException {
        parameterMap.put("crs", UTM33N_CODE);
        parameterMap.put("resampling", "Bilinear");
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        assertNotNull(targetPoduct.getGeoCoding());
        // 299, 312
        // 322, 336
        // interpolated = 317.25 for pixel (24, 14)
        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 24.0f, 14.0f, 317.25, 1.0e-2);
    }

    @Test
    public void testSpecifyingTargetDimension() throws IOException {
        final int width = 200;
        final int height = 300;
        parameterMap.put("crs", WGS84_CODE);
        parameterMap.put("width", width);
        parameterMap.put("height", height);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        assertEquals(width, targetPoduct.getSceneRasterWidth());
        assertEquals(height, targetPoduct.getSceneRasterHeight());

        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testSpecifyingPixelSize() throws IOException {
        final double sizeX = 5; // degree
        final double sizeY = 10;// degree
        parameterMap.put("crs", WGS84_CODE);
        parameterMap.put("pixelSizeX", sizeX);
        parameterMap.put("pixelSizeY", sizeY);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        assertEquals(5, targetPoduct.getSceneRasterWidth());
        assertEquals(3, targetPoduct.getSceneRasterHeight());
    }

    @Test
    public void testSpecifyingReferencing() throws IOException {
        parameterMap.put("crs", WGS84_CODE);
        parameterMap.put("referencePixelX", 0.5);
        parameterMap.put("referencePixelY", 0.5);
        parameterMap.put("easting", 9.0);   // just move it 3° degrees eastward
        parameterMap.put("northing", 52.0); // just move it 2° degrees up
        parameterMap.put("orientation", 0.0);
        final Product targetPoduct = createReprojectedProduct();

        assertNotNull(targetPoduct);
        final GeoPos geoPos = targetPoduct.getGeoCoding().getGeoPos(new PixelPos(0.5f, 0.5f), null);
        assertEquals(new GeoPos(52.0f, 9.0f), geoPos);
        assertPixelValue(targetPoduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testIncludeTiePointGrids() throws Exception {
        parameterMap.put("crs", WGS84_CODE);
        Product targetPoduct = createReprojectedProduct();

        TiePointGrid[] tiePointGrids = targetPoduct.getTiePointGrids();
        assertNotNull(tiePointGrids);
        assertEquals(0, tiePointGrids.length);
        Band latGrid = targetPoduct.getBand("latGrid");
        assertNotNull(latGrid);

        parameterMap.put("includeTiePointGrids", false);
        targetPoduct = createReprojectedProduct();
        tiePointGrids = targetPoduct.getTiePointGrids();
        assertNotNull(tiePointGrids);
        assertEquals(0, tiePointGrids.length);
        latGrid = targetPoduct.getBand("latGrid");
        assertNull(latGrid);
    }

    @Test
    public void testCopyPlacemarkGroups() throws IOException {
        final PlacemarkDescriptor pinDescriptor = PinDescriptor.getInstance();
        final Placemark pin = Placemark.createPointPlacemark(pinDescriptor, "P1", "", "", new PixelPos(1.5f, 1.5f), null, sourceProduct.getGeoCoding());
        final Placemark gcp = Placemark.createPointPlacemark(pinDescriptor, "G1", "", "", new PixelPos(2.5f, 2.5f), null, sourceProduct.getGeoCoding());

        sourceProduct.getPinGroup().add(pin);
        sourceProduct.getGcpGroup().add(gcp);

        parameterMap.put("crs", WGS84_CODE);
        Product targetProduct = createReprojectedProduct();

        assertEquals(1, targetProduct.getPinGroup().getNodeCount());
        assertEquals(1, targetProduct.getGcpGroup().getNodeCount());
        final Placemark pin2 = targetProduct.getPinGroup().get(0);
        final Placemark gcp2 = targetProduct.getGcpGroup().get(0);

        assertEquals("P1", pin2.getName());
        assertEquals("G1", gcp2.getName());

        assertEquals(pin.getGeoPos(), pin2.getGeoPos());
        assertEquals(gcp.getGeoPos(), gcp2.getGeoPos());
        assertNotNull(pin2.getPixelPos());
        assertNotNull(gcp2.getPixelPos());
    }
}
