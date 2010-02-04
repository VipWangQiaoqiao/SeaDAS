package org.esa.beam.dataio.geometry;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

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
        final VectorDataNodeReader reader = new VectorDataNodeReader(DefaultGeographicCRS.WGS84);
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
        final SimpleFeatureType type = createFeatureType();
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

    private static SimpleFeatureType createFeatureType() {

        GeometryType gt1 = new GeometryTypeImpl(new NameImpl("geometry"), Polygon.class,
                                                DefaultGeographicCRS.WGS84,
                                                false, false, null, null, null);

        AttributeType at2 = new AttributeTypeImpl(new NameImpl("label"), String.class,
                                                  false, false, null, null, null);

        GeometryDescriptor gd1 = new GeometryDescriptorImpl(gt1,
                                                            new NameImpl("GEOMETRY"),
                                                            0, 1,
                                                            false,
                                                            null);

        AttributeDescriptor ad2 = new AttributeDescriptorImpl(at2,
                                                              new NameImpl(ATTRIBUTE_NAME_LABEL),
                                                              0, 1,
                                                              false,
                                                              null);


        return new SimpleFeatureTypeImpl(new NameImpl("TestType"),
                                         Arrays.asList(gd1, ad2),
                                         gd1,
                                         false, null, null, null);
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
}