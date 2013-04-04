package org.esa.beam.framework.gpf.main;

import org.junit.Ignore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Norman Fomferra
 */
@Ignore
public class TestCommandLineContext extends DefaultCommandLineContext {

    final StringBuffer printBuffer = new StringBuffer();
    final Map<String, String> textFiles = new HashMap<String, String>();
    final List<StringReader> readers = new ArrayList<StringReader>();
    final Map<String, StringWriter> writers = new HashMap<String, StringWriter>();

    @Override
    public Reader createReader(String fileName) throws FileNotFoundException {
        if (!textFiles.containsKey(fileName)) {
            File file = new File(fileName);
            if (file.exists()) {
                return new FileReader(file);
            } else {
                throw new FileNotFoundException(fileName);
            }
        }
        StringReader stringReader = new StringReader(textFiles.get(fileName));
        readers.add(stringReader);
        return stringReader;
    }

    @Override
    public Writer createWriter(String fileName) throws IOException {
        StringWriter stringWriter = new StringWriter();
        writers.put(fileName, stringWriter);
        return stringWriter;
    }

    @Override
    public boolean fileExists(String fileName) {
        return textFiles.containsKey(fileName);
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("TestCommandLineContext");
    }

    @Override
    public void print(String m) {
        printBuffer.append(m);
        printBuffer.append('\n');
    }

    @Override
    public String[] list(String path) throws IOException {
        Set<String> strings = textFiles.keySet();
        return strings.toArray(new String[strings.size()]);
    }

    @Override
    public boolean isFile(String path) {
        return true;
    }
}
