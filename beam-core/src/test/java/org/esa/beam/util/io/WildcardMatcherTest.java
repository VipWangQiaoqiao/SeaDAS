package org.esa.beam.util.io;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Norman Fomferra
 */
public class WildcardMatcherTest {

    @Test
    public void testMatchesFileAssumptions() throws Exception {
        String baseDir = new File("").getCanonicalPath();
        assertEquals(baseDir, new File(".").getCanonicalPath());
        assertEquals(baseDir, new File("./").getCanonicalPath());
        assertEquals(baseDir + File.separator + "test.txt",
                     new File("./test.txt").getCanonicalPath());
        assertEquals(baseDir + File.separator + "test.txt",
                     new File("sub/../test.txt").getCanonicalPath());
        assertEquals(baseDir + File.separator + "sub" + File.separator + "test.txt",
                     new File("sub/./test.txt").getCanonicalPath());
    }


    @Test
    public void testMatchesSingleFileNoWildcardUsed() throws Exception {
        WildcardMatcher m = new WildcardMatcher("test.N1");
        assertEquals(m.isWindowsFs() ? "test\\.n1" : "test\\.N1", m.getRegex());

        assertTrue(m.matches("test.N1"));

        assertFalse(m.matches("test.jpg"));
        assertFalse(m.matches("rest.N1"));
        assertFalse(m.matches("x/test.N1"));
    }

    @Test
    public void testMatchesMultiDirectoryNoWildcardUsed_Unix() throws Exception {
        WildcardMatcher m = new WildcardMatcher("/home/norman/meris/data.nc", false);
        assertEquals("/home/norman/meris/data\\.nc", m.getRegex());

        assertTrue(m.matches("/home/norman/meris/data.nc"));

        assertFalse(m.matches("home/norman/meris/data.nc"));
        assertFalse(m.matches("/home/norman/MERIS/data.nc"));
        assertFalse(m.matches("/home/norman/meris/data.NC"));
    }

    @Test
    public void testMatchesMultiDirectoryNoWildcardUsed_Win() throws Exception {
        WildcardMatcher m = new WildcardMatcher("C:\\Users\\Norman\\MERIS\\data.nc", true);
        assertEquals("c:/users/norman/meris/data\\.nc", m.getRegex());

        assertTrue(m.matches("C:\\Users\\Norman\\MERIS\\data.nc"));
        assertTrue(m.matches("C:\\Users\\Norman\\meris\\data.nc"));
        assertTrue(m.matches("C:\\Users\\Norman\\meris\\data.NC"));
        assertTrue(m.matches("c:\\Users\\Norman\\meris\\data.nc"));

        assertTrue(m.matches("C:/Users/Norman/MERIS/data.nc"));
        assertTrue(m.matches("C:/Users/Norman/meris/data.nc"));
        assertTrue(m.matches("C:/Users/Norman/meris/data.NC"));
        assertTrue(m.matches("c:/Users/Norman/meris/data.nc"));

        assertFalse(m.matches("D:\\Users\\Norman\\MERIS\\data.nc"));
        assertFalse(m.matches("\\Users\\Norman\\MERIS\\data.nc"));
    }

    @Test
    public void testMatchesQuoteInFilename() throws Exception {
        WildcardMatcher m = new WildcardMatcher("te?t.N1");
        assertEquals(m.isWindowsFs() ? "te.t\\.n1" : "te.t\\.N1", m.getRegex());

        assertTrue(m.matches("test.N1"));
        assertTrue(m.matches("te?t.N1"));

        assertFalse(m.matches("tet.N1"));
    }

    @Test
    public void testMatchesStarInFilename() throws Exception {
        WildcardMatcher m = new WildcardMatcher("*.N1");
        assertEquals(m.isWindowsFs() ? "[^/:]*\\.n1" : "[^/:]*\\.N1", m.getRegex());

        assertTrue(m.matches("test.N1"));
        assertTrue(m.matches("MER_RR.N1"));

        assertFalse(m.matches("MER_RR"));
        assertFalse(m.matches("MER_RR.txt"));
    }

    @Test
    public void testMatchesStarInBetween() throws Exception {
        WildcardMatcher m = new WildcardMatcher("foo/*/test.txt");
        assertEquals("foo/[^/:]*/test\\.txt", m.getRegex());

        assertTrue(m.matches("foo//test.txt"));
        assertTrue(m.matches("foo/bar/test.txt"));

        assertFalse(m.matches("/foo/test.txt"));
        assertFalse(m.matches("foo/bar/doz/gna/test.txt"));
    }

    @Test
    public void testMatchesStarAtEnd() throws Exception {
        WildcardMatcher m = new WildcardMatcher("foo/*");
        assertEquals("foo/[^/:]*", m.getRegex());

        assertTrue(m.matches("foo/test.txt"));
        assertTrue(m.matches("foo/bar"));

        assertFalse(m.matches("foo"));
        assertFalse(m.matches("foo/bar/test.txt"));
        assertFalse(m.matches("/foo/"));
        assertFalse(m.matches("foo/bar/"));
        assertFalse(m.matches("foo/bar/doz/gna/test.txt"));
    }

    @Test
    public void testMatchesDoubleStarInBetween() throws Exception {
        WildcardMatcher m = new WildcardMatcher("foo/**/test.txt");
        assertEquals("foo((/.*/)?|/)test\\.txt", m.getRegex());

        assertTrue(m.matches("foo/test.txt"));
        assertTrue(m.matches("foo/bar/test.txt"));
        assertTrue(m.matches("foo/bar/doz/test.txt"));
        assertTrue(m.matches("foo/bar/doz/gna/test.txt"));

        assertFalse(m.matches("/foo/test.txt"));
        assertFalse(m.matches("foo/bar/doz/gna/test.zip"));
    }

    @Test
    public void testMatchesDoubleStarAtEnd() throws Exception {
        WildcardMatcher m = new WildcardMatcher("foo/**");
        assertEquals("foo(/.*)?", m.getRegex());

        assertTrue(m.matches("foo"));
        assertTrue(m.matches("foo/"));
        assertTrue(m.matches("foo/bar/doz/test.txt"));
        assertTrue(m.matches("foo/bar/doz/gna/test.txt"));
        assertTrue(m.matches("foo/test.txt"));
        assertTrue(m.matches("foo/bar/doz/gna/test.zip"));
        if (m.isWindowsFs()) {
            assertTrue(m.matches("foo\\bar\\doz\\gna\\test.txt"));
        }

        assertFalse(m.matches("/foo/bar/doz/gna/test.zip"));
        assertFalse(m.matches("bar/doz/gna/test.zip"));
    }

    // see http://ant.apache.org/manual/dirtasks.html#patterns
    @Test
    public void testMatchesAntExamplePattern1() throws Exception {
        WildcardMatcher m = new WildcardMatcher("**/CVS/*");
        assertEquals(m.isWindowsFs() ? "(.*/)?cvs/[^/:]*" : "(.*/)?CVS/[^/:]*", m.getRegex());

        assertTrue(m.matches("CVS/Repository"));
        assertTrue(m.matches("org/apache/CVS/Entries"));
        assertTrue(m.matches("org/apache/jakarta/tools/ant/CVS/Entries"));

        assertFalse(m.matches("org/apache/CVS/foo/bar/Entries"));
    }

    // see http://ant.apache.org/manual/dirtasks.html#patterns
    @Test
    public void testMatchesAntExamplePattern2() throws Exception {
        WildcardMatcher m = new WildcardMatcher("org/apache/jakarta/**");
        assertEquals("org/apache/jakarta(/.*)?", m.getRegex());

        assertTrue(m.matches("org/apache/jakarta/tools/ant/docs/index.html"));
        assertTrue(m.matches("org/apache/jakarta/test.xml"));

        assertFalse(m.matches("org/apache/xyz.java"));
    }

    // see http://ant.apache.org/manual/dirtasks.html#patterns
    @Test
    public void testMatchesAntExamplePattern3() throws Exception {
        WildcardMatcher m = new WildcardMatcher("org/apache/**/CVS/*");
        assertEquals(m.isWindowsFs() ? "org/apache((/.*/)?|/)cvs/[^/:]*" : "org/apache((/.*/)?|/)CVS/[^/:]*", m.getRegex());

        assertTrue(m.matches("org/apache/CVS/Entries"));
        assertTrue(m.matches("org/apache/jakarta/tools/ant/CVS/Entries"));

        assertFalse(m.matches("org/apache/CVS/foo/bar/Entries"));
    }

    // see http://ant.apache.org/manual/dirtasks.html#patterns
    @Test
    public void testMatchesAntExamplePattern4() throws Exception {
        WildcardMatcher m = new WildcardMatcher("**/test/**");
        assertEquals("(.*/)?test(/.*)?", m.getRegex());

        assertTrue(m.matches("test"));
        assertTrue(m.matches("test/java"));
        assertTrue(m.matches("src/test/java"));
        assertTrue(m.matches("src/test"));

        assertFalse(m.matches("src/main/java"));
    }

    @Test
    public void testGlobWithDoubleStar() throws Exception {
        String dir = getTestdataDir();
        File[] files = WildcardMatcher.glob(dir + "/**/*.txt");
        assertNotNull(files);
        for (File file : files) {
            //System.out.println("file = " + file);
        }
        assertEquals(3, files.length);
        Arrays.sort(files);
        assertEquals(new File(dir, "foo/bar/test1.txt"), files[0]);
        assertEquals(new File(dir, "foo/bar/test3.txt"), files[1]);
        assertEquals(new File(dir, "foo/test1.txt"), files[2]);
    }

    @Test
    public void testGlobStarAtEnd() throws Exception {
        String dir = getTestdataDir();
        File[] files = WildcardMatcher.glob(dir + "/foo/bar/*");
        assertNotNull(files);
        for (File file : files) {
            //System.out.println("file = " + file);
        }
        assertEquals(3, files.length);
        Arrays.sort(files);
        assertEquals(new File(dir, "foo/bar/test1.txt"), files[0]);
        assertEquals(new File(dir, "foo/bar/test2.dat"), files[1]);
        assertEquals(new File(dir, "foo/bar/test3.txt"), files[2]);
    }

    @Test
    public void testGlobDoubleStarAtEnd() throws Exception {
        String dir = getTestdataDir();
        File[] files = WildcardMatcher.glob(dir + "/foo/**");
        assertNotNull(files);
        for (File file : files) {
            //System.out.println("file = " + file);
        }
        assertEquals(7, files.length);
        Arrays.sort(files);
        assertEquals(new File(dir, "foo/bar"), files[0]);
        assertEquals(new File(dir, "foo/bar/test1.txt"), files[1]);
        assertEquals(new File(dir, "foo/bar/test2.dat"), files[2]);
        assertEquals(new File(dir, "foo/bar/test3.txt"), files[3]);
        assertEquals(new File(dir, "foo/test1.txt"), files[4]);
        assertEquals(new File(dir, "foo/test2.dat"), files[5]);
        assertEquals(new File(dir, "foo/test3.dat"), files[6]);
    }

    @Test
    public void testGlobAllFiles() throws Exception {
        String dir = getTestdataDir();
        File[] files = WildcardMatcher.glob(dir + "/foo/**/*.*");
        assertNotNull(files);
        for (File file : files) {
            //System.out.println("file = " + file);
        }
        assertEquals(6, files.length);
        Arrays.sort(files);
        assertEquals(new File(dir, "foo/bar/test1.txt"), files[0]);
        assertEquals(new File(dir, "foo/bar/test2.dat"), files[1]);
        assertEquals(new File(dir, "foo/bar/test3.txt"), files[2]);
        assertEquals(new File(dir, "foo/test1.txt"), files[3]);
        assertEquals(new File(dir, "foo/test2.dat"), files[4]);
        assertEquals(new File(dir, "foo/test3.dat"), files[5]);
    }

    @Test
    public void testGlobExistingDir() throws Exception {
        String dir = getTestdataDir();
        File[] files = WildcardMatcher.glob(dir + "/foo");
        assertNotNull(files);
        for (File file : files) {
            //System.out.println("file = " + file);
        }
        assertEquals(1, files.length);
        assertEquals(new File(dir, "foo"), files[0]);
    }

    private String getTestdataDir() throws URISyntaxException {
        URL resource = WildcardMatcherTest.class.getResource("WildcardMatcherTest");
        return new File(resource.toURI()).getPath();
    }
}
