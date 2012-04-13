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

package org.esa.beam.dataio.geometry;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.esa.beam.framework.datamodel.Placemark;
import org.esa.beam.framework.datamodel.VectorDataNode;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.*;

import static org.junit.Assert.*;

public class VectorDataNodeIOTest {

    private static final String ATTRIBUTE_NAME_LABEL = "LABEL";
    
    private StringWriter stringWriter;
    private DefaultFeatureCollection testCollection;
    
    @Before
    public void setUp() throws IOException {
        testCollection = createTestCollection();

        stringWriter = new StringWriter(300);
        final VectorDataNodeWriter writer = new VectorDataNodeWriter();
        writer.writeFeatures(testCollection, stringWriter);
    }

    @Test
    public void testEncodingDelimiter() {
        String[] linesOut = stringWriter.toString().split("\n");
        assertEquals(4, linesOut.length);
        assertTrue(linesOut[1].endsWith("with\\tTab"));
        assertTrue(linesOut[2].endsWith("with     spaces"));
        assertTrue(linesOut[3].endsWith("with \\\\t escaped tab"));
    }
    
    @Test
    public void testDecodingDelimiter() throws IOException {        
        final VectorDataNodeReader reader = new VectorDataNodeReader("mem", DefaultGeographicCRS.WGS84);
        final FeatureCollection<SimpleFeatureType,SimpleFeature> readCollection = reader.readFeatures(
                new StringReader(stringWriter.toString()));

        assertEquals(testCollection.size(), readCollection.size());
        final FeatureIterator<SimpleFeature> expectedIterator = testCollection.features();
        final FeatureIterator<SimpleFeature> actualIterator = readCollection.features();
        while(expectedIterator.hasNext()) {
            final SimpleFeature expectedFeature = expectedIterator.next();
            final SimpleFeature actualFeature = actualIterator.next();
            final Object expectedAttribute = expectedFeature.getAttribute(ATTRIBUTE_NAME_LABEL);
            final Object actualAttribute = actualFeature.getAttribute(ATTRIBUTE_NAME_LABEL);
            assertEquals(expectedAttribute, actualAttribute);
        }
    }

    private DefaultFeatureCollection createTestCollection() {
        final SimpleFeatureType type = Placemark.createGeometryFeatureType();
        GeometryFactory gf = new GeometryFactory();
        Object[] data1 = {gf.toGeometry(new Envelope(0, 10, 0, 10)), "with\tTab"};
        Object[] data2 = {gf.toGeometry(new Envelope(20, 30, 0, 10)), "with     spaces"};
        Object[] data3 = {gf.toGeometry(new Envelope(40, 50, 0, 10)), "with \\t escaped tab"};
        SimpleFeatureImpl f1 = new SimpleFeatureImpl(data1, type, new FeatureIdImpl("F1"), true);
        SimpleFeatureImpl f2 = new SimpleFeatureImpl(data2, type, new FeatureIdImpl("F2"), true);
        SimpleFeatureImpl f3 = new SimpleFeatureImpl(data3, type, new FeatureIdImpl("F3"), true);
        final DefaultFeatureCollection collection = new DefaultFeatureCollection("testID", type);
        collection.add(f1);
        collection.add(f2);
        collection.add(f3);
        return collection;
    }
    
    @Test
    public void testEncodeTabString() {
        assertEquals("with\\tTab", VectorDataNodeIO.encodeTabString("with\tTab"));
        assertEquals("with 4    spaces", VectorDataNodeIO.encodeTabString("with 4    spaces"));
        assertEquals("with \\\\t escaped tab", VectorDataNodeIO.encodeTabString("with \\t escaped tab"));
        assertEquals("with\\t2\\ttabs", VectorDataNodeIO.encodeTabString("with\t2\ttabs"));
        assertEquals("with \\d other char", VectorDataNodeIO.encodeTabString("with \\d other char"));
        assertEquals("with \\\\d other char", VectorDataNodeIO.encodeTabString("with \\\\d other char"));
    }
    
    @Test
    public void testDecodeTabString() {
        assertEquals("with\tTab", VectorDataNodeIO.decodeTabString("with\\tTab"));
        assertEquals("with 4    spaces", VectorDataNodeIO.decodeTabString("with 4    spaces"));
        assertEquals("with \\t escaped tab", VectorDataNodeIO.decodeTabString("with \\\\t escaped tab"));
        assertEquals("with\t2\ttabs", VectorDataNodeIO.decodeTabString("with\\t2\\ttabs"));
        assertEquals("with \\d other char", VectorDataNodeIO.encodeTabString("with \\d other char"));
        assertEquals("with \\\\d other char", VectorDataNodeIO.encodeTabString("with \\\\d other char"));
    }
    
    @Test
    public void testProperties() throws Exception {
        VectorDataNode vectorDataNode = new VectorDataNode("aName", Placemark.createGeometryFeatureType());
        vectorDataNode.setDescription("Contains a set of pins");
        vectorDataNode.setDefaultStyleCss("stroke:#ff0000");

        VectorDataNodeWriter vectorDataNodeWriter = new VectorDataNodeWriter();
        File tempFile = File.createTempFile("VectorDataNodeWriterTest_testProperties", "csv");
        tempFile.deleteOnExit();
        vectorDataNodeWriter.write(vectorDataNode, tempFile);
        
        FileReader fileReader = new FileReader(tempFile);
        LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
        
        String firstLine = lineNumberReader.readLine();
        assertNotNull(firstLine);
        assertEquals("#description=Contains a set of pins", firstLine);
        
        String secondLine = lineNumberReader.readLine();
        assertNotNull(secondLine);
        assertEquals("#defaultCSS=stroke:#ff0000", secondLine);
        
        VectorDataNodeReader vectorDataNodeReader = new VectorDataNodeReader("mem", null);
        VectorDataNode vectorDataNode2 = vectorDataNodeReader.read(tempFile);
        
        assertNotNull(vectorDataNode2);
        assertEquals(vectorDataNode.getDescription(), vectorDataNode2.getDescription());
        assertEquals(vectorDataNode.getDefaultStyleCss(), vectorDataNode2.getDefaultStyleCss());
    }
}
