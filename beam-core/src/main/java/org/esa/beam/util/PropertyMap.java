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
package org.esa.beam.util;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.esa.beam.util.logging.BeamLogManager;

/**
 * The <code>PropertyMap</code> class can be used instead of the standard JDK <code>java.util.Properties</code>
 * class.<code>PropertyMap</code> provides a generally more useful interface by adding a couple type conversion methods
 * for a set of most frequently used data types, such as <code>Boolean</code>, <code>Integer</code>,
 * <code>Double</code>, <code>Color</code> and <code>Font</code>.
 * <p/>
 * <p>Additionally the class provides property change support.
 *
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 */
public class PropertyMap {

    private final Properties _properties;
    private PropertyChangeSupport _propertyChangeSupport;
    private Logger _logger;

    /**
     * Constructs a new and empty property map.
     */
    public PropertyMap() {
        this(null);
    }

    /**
     * Constructs a property map which uses the given <code>Properties</code> as a key/value container.
     */
    public PropertyMap(Properties properties) {
        _properties = (properties != null) ? properties : new Properties();
        _logger = BeamLogManager.getSystemLogger();
    }

    /**
     * Loads key/value pairs from a text file into this property map.
     *
     * @param file the text file
     *
     * @throws IOException if an I/O error occurs
     */
    public void load(File file) throws IOException {
        Guardian.assertNotNull("file", file);
        FileInputStream istream = new FileInputStream(file);
        getProperties().load(istream);
        istream.close();
    }


    /**
     * Stores the key/value pairs of this property map into a text file.
     *
     * @param file   the text file
     * @param header an optional file header
     *
     * @throws IOException if an I/O error occurs
     */
    public void store(File file, String header) throws IOException {
        Guardian.assertNotNull("file", file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(160000);
        getProperties().store(baos, header);
        baos.close();

        final String text = baos.toString();
        final StringTokenizer st = new StringTokenizer(text, "\n\r", false);
        final String[] lines = new String[st.countTokens()];
        for (int i = 0; st.hasMoreElements(); i++) {
            lines[i] = st.nextElement().toString();
        }

        for (int i = 0; i < lines.length; i++) {

            if (lines[i].startsWith("numPoints="))
            {
                lines[i] = lines[i].replaceFirst("numPoints=", "a1_numPoints=");
            }
            if (lines[i].startsWith("isLogScaled="))
            {
                lines[i] = lines[i].replaceFirst("isLogScaled=", "a2_isLogScaled=");
            }
            if (lines[i].startsWith("autoDistribute="))
            {
                lines[i] = lines[i].replaceFirst("autoDistribute=", "a3_autoDistribute=");
            }



            if (lines[i].startsWith("sample0="))
            {
                lines[i] = lines[i].replaceFirst("sample0=", "sample00=");
            }
            if (lines[i].startsWith("sample1="))
            {
                lines[i] = lines[i].replaceFirst("sample1=", "sample01=");
            }
            if (lines[i].startsWith("sample2="))
            {
                lines[i] = lines[i].replaceFirst("sample2=", "sample02=");
            }
            if (lines[i].startsWith("sample3="))
            {
                lines[i] = lines[i].replaceFirst("sample3=", "sample03=");
            }
            if (lines[i].startsWith("sample4="))
            {
                lines[i] = lines[i].replaceFirst("sample4=", "sample04=");
            }
            if (lines[i].startsWith("sample5="))
            {
                lines[i] = lines[i].replaceFirst("sample5=", "sample05=");
            }
            if (lines[i].startsWith("sample6="))
            {
                lines[i] = lines[i].replaceFirst("sample6=", "sample06=");
            }
            if (lines[i].startsWith("sample7="))
            {
                lines[i] = lines[i].replaceFirst("sample7=", "sample07=");
            }
            if (lines[i].startsWith("sample8="))
            {
                lines[i] = lines[i].replaceFirst("sample8=", "sample08=");
            }
            if (lines[i].startsWith("sample9="))
            {
                lines[i] = lines[i].replaceFirst("sample9=", "sample09=");
            }

            if (lines[i].startsWith("color0="))
            {
                lines[i] = lines[i].replaceFirst("color0=", "color00=");
            }
            if (lines[i].startsWith("color1="))
            {
                lines[i] = lines[i].replaceFirst("color1=", "color01=");
            }
            if (lines[i].startsWith("color2="))
            {
                lines[i] = lines[i].replaceFirst("color2=", "color02=");
            }
            if (lines[i].startsWith("color3="))
            {
                lines[i] = lines[i].replaceFirst("color3=", "color03=");
            }
            if (lines[i].startsWith("color4="))
            {
                lines[i] = lines[i].replaceFirst("color4=", "color04=");
            }
            if (lines[i].startsWith("color5="))
            {
                lines[i] = lines[i].replaceFirst("color5=", "color05=");
            }
            if (lines[i].startsWith("color6="))
            {
                lines[i] = lines[i].replaceFirst("color6=", "color06=");
            }
            if (lines[i].startsWith("color7="))
            {
                lines[i] = lines[i].replaceFirst("color7=", "color07=");
            }
            if (lines[i].startsWith("color8="))
            {
                lines[i] = lines[i].replaceFirst("color8=", "color08=");
            }
            if (lines[i].startsWith("color9="))
            {
                lines[i] = lines[i].replaceFirst("color9=", "color09=");
            }
        }

        Arrays.sort(lines);

        for (int i = 0; i < lines.length; i++) {

            if (lines[i].startsWith("a1_numPoints="))
            {
                lines[i] = lines[i].replaceFirst("a1_numPoints=", "numPoints=");
            }
            if (lines[i].startsWith("a2_isLogScaled="))
            {
                lines[i] = lines[i].replaceFirst("a2_isLogScaled=", "isLogScaled=");
            }
            if (lines[i].startsWith("a3_autoDistribute="))
            {
                lines[i] = lines[i].replaceFirst("a3_autoDistribute=", "autoDistribute=");
            }


            if (lines[i].startsWith("sample00="))
            {
                lines[i] = lines[i].replaceFirst("sample00=", "sample0=");
            }
            if (lines[i].startsWith("sample01="))
            {
                lines[i] = lines[i].replaceFirst("sample01=", "sample1=");
            }
            if (lines[i].startsWith("sample02="))
            {
                lines[i] = lines[i].replaceFirst("sample02=", "sample2=");
            }
            if (lines[i].startsWith("sample03="))
            {
                lines[i] = lines[i].replaceFirst("sample03=", "sample3=");
            }
            if (lines[i].startsWith("sample04="))
            {
                lines[i] = lines[i].replaceFirst("sample04=", "sample4=");
            }
            if (lines[i].startsWith("sample05="))
            {
                lines[i] = lines[i].replaceFirst("sample05=", "sample5=");
            }
            if (lines[i].startsWith("sample06="))
            {
                lines[i] = lines[i].replaceFirst("sample06=", "sample6=");
            }
            if (lines[i].startsWith("sample07="))
            {
                lines[i] = lines[i].replaceFirst("sample07=", "sample7=");
            }
            if (lines[i].startsWith("sample08="))
            {
                lines[i] = lines[i].replaceFirst("sample08=", "sample8=");
            }
            if (lines[i].startsWith("sample09="))
            {
                lines[i] = lines[i].replaceFirst("sample09=", "sample9=");
            }

            if (lines[i].startsWith("color00="))
            {
                lines[i] = lines[i].replaceFirst("color00=", "color0=");
            }
            if (lines[i].startsWith("color01="))
            {
                lines[i] = lines[i].replaceFirst("color01=", "color1=");
            }
            if (lines[i].startsWith("color02="))
            {
                lines[i] = lines[i].replaceFirst("color02=", "color2=");
            }
            if (lines[i].startsWith("color03="))
            {
                lines[i] = lines[i].replaceFirst("color03=", "color3=");
            }
            if (lines[i].startsWith("color04="))
            {
                lines[i] = lines[i].replaceFirst("color04=", "color4=");
            }
            if (lines[i].startsWith("color05="))
            {
                lines[i] = lines[i].replaceFirst("color05=", "color5=");
            }
            if (lines[i].startsWith("color06="))
            {
                lines[i] = lines[i].replaceFirst("color06=", "color6=");
            }
            if (lines[i].startsWith("color07="))
            {
                lines[i] = lines[i].replaceFirst("color07=", "color7=");
            }
            if (lines[i].startsWith("color08="))
            {
                lines[i] = lines[i].replaceFirst("color08=", "color8=");
            }
            if (lines[i].startsWith("color09="))
            {
                lines[i] = lines[i].replaceFirst("color09=", "color9=");
            }
        }


        BufferedWriter bos = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < lines.length; i++) {
            bos.write(lines[i]);
            bos.newLine();
        }
        bos.close();
    }

    /**
     * Returns the <code>Properties</code> instance in which this property map stores its key/value pairs.
     */
    public Properties getProperties() {
        return _properties;
    }

    /**
     * Returns an enumeration of the property keys in this map.
     */
    public Enumeration getPropertyKeys() {
        return _properties.keys();
    }

    /**
     * Gets a value of type <code>boolean</code>.
     *
     * @param key the key
     *
     * @return the value for the given key, or <code>false</code> if the key is not contained in this property set.
     */
    public boolean getPropertyBool(String key) {
        return getPropertyBool(key, false);
    }

    /**
     * Gets a value of type <code>boolean</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public boolean getPropertyBool(String key, boolean defaultValue) {
        return getPropertyBool(key, defaultValue ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Gets a value of type <code>Boolean</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public Boolean getPropertyBool(String key, Boolean defaultValue) {
        String value = _properties.getProperty(key);
        if (value != null) {
            return Boolean.valueOf(value);
        }
        return defaultValue;
    }

    /**
     * Sets a value of type <code>boolean</code>.
     *
     * @param key   the key
     * @param value the value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyBool(String key, boolean value) {
        Guardian.assertNotNullOrEmpty("key", key);
        setPropertyBool(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Sets a value of type <code>Boolean</code>.
     *
     * @param key      the key
     * @param newValue the new value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyBool(String key, Boolean newValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        Boolean oldValue = getPropertyBool(key, null);
        changeInternalProperty(key, oldValue, newValue);
    }

    /**
     * Gets a value of type <code>int</code>.
     *
     * @param key the key
     *
     * @return the value for the given key, or <code>0</code> (zero) if the key is not contained in this property set.
     */
    public int getPropertyInt(String key) {
        return getPropertyInt(key, 0);
    }

    /**
     * Gets a value of type <code>int</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public int getPropertyInt(String key, int defaultValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        String value = _properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                String message = "warning: property value of type 'int' expected: "
                                 + key + "=" + value + "; using default value: "
                                 + defaultValue;
                _logger.warning(message);
                Debug.trace(message);
            }
        }
        return defaultValue;
    }


    /**
     * Gets a value of type <code>Integer</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public Integer getPropertyInt(String key, Integer defaultValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        String value = _properties.getProperty(key);
        if (value != null) {
            try {
                return new Integer(value);
            } catch (NumberFormatException e) {
                String message = "warning: property value of type 'Integer' expected: "
                                 + key + "=" + value + "; using default value: "
                                 + defaultValue;
                _logger.warning(message);
                Debug.trace(message);
            }
        }
        return defaultValue;
    }


    /**
     * Sets a value of type <code>int</code>.
     *
     * @param key      the key
     * @param newValue the new value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyInt(String key, int newValue) {
        setPropertyInt(key, new Integer(newValue));
    }

    /**
     * Sets a value of type <code>Integer</code>.
     *
     * @param key      the key
     * @param newValue the value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyInt(String key, Integer newValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        Integer oldValue = getPropertyInt(key, null);
        changeInternalProperty(key, oldValue, newValue);
    }

    /**
     * Gets a value of type <code>double</code>.
     *
     * @param key the key
     *
     * @return the value for the given key, or <code>0.0</code> (zero) if the key is not contained in this property
     *         set.
     */
    public double getPropertyDouble(String key) {
        return getPropertyDouble(key, 0.0);
    }

    /**
     * Gets a value of type <code>double</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public double getPropertyDouble(String key, double defaultValue) {
        String value = _properties.getProperty(key);
        if (value != null) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                String message = "warning: property value of type 'double' expected: "
                                 + key + "=" + value + "; using default value: "
                                 + defaultValue;
                _logger.warning(message);
                Debug.trace(message);
            }
        }
        return defaultValue;
    }

    /**
     * Gets a value of type <code>Double</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public Double getPropertyDouble(String key, Double defaultValue) {
        String value = _properties.getProperty(key);
        if (value != null) {
            try {
                return new Double(value);
            } catch (NumberFormatException e) {
                String message = "warning: property value of type 'Double' expected: "
                                 + key + "=" + value + "; using default value: "
                                 + defaultValue;
                _logger.warning(message);
                Debug.trace(message);
            }
        }
        return defaultValue;
    }

    /**
     * Sets a value of type <code>double</code>.
     *
     * @param key      the key
     * @param newValue the new value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyDouble(String key, double newValue) {
        setPropertyDouble(key, new Double(newValue));
    }

    /**
     * Sets a value of type <code>Double</code>.
     *
     * @param key      the key
     * @param newValue the value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyDouble(String key, Double newValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        Double oldValue = getPropertyDouble(key, null);
        changeInternalProperty(key, oldValue, newValue);
    }

    /**
     * Gets a value of type <code>String</code>.
     *
     * @param key the key
     *
     * @return the value for the given key, or <code>""</code> (empty string) if the key is not contained in this
     *         property set, never <code>null</code>.
     */
    public String getPropertyString(String key) {
        return getPropertyString(key, "");
    }

    /**
     * Gets a value of type <code>String</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public String getPropertyString(String key, String defaultValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        String property = _properties.getProperty(key, defaultValue);
        return property;
    }

    /**
     * Sets a value of type <code>String</code>.
     *
     * @param key      the key
     * @param newValue the new value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyString(String key, String newValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        String oldValue = getPropertyString(key, null);
        changeInternalProperty(key, oldValue, newValue);
    }

    /**
     * Gets a value of type <code>Color</code>.
     *
     * @param key the key
     *
     * @return the value for the given key, or <code>Color.black</code> if the key is not contained in this property
     *         set, never <code>null</code>.
     */
    public Color getPropertyColor(String key) {
        return getPropertyColor(key, Color.black);
    }

    /**
     * Gets a value of type <code>Color</code>.
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public Color getPropertyColor(String key, Color defaultValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        String value = _properties.getProperty(key);
        if (value != null) {
            Color color = StringUtils.parseColor(value);
            if (color != null) {
                return color;
            }
        }
        return defaultValue;
    }

    /**
     * Sets a value of type <code>Color</code>.
     *
     * @param key      the key
     * @param newValue the value
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyColor(String key, Color newValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        Color oldValue = getPropertyColor(key, null);
        if (!ObjectUtils.equalObjects(oldValue, newValue)) {
            if (newValue != null) {
                _properties.setProperty(key, StringUtils.formatColor(newValue));
            } else {
                _properties.remove(key);
            }
            firePropertyChange(key, oldValue, newValue);
        }
    }

    /**
     * Gets a value of type <code>Font</code>. The method actually looks for three keys in order to construct the font
     * instance: <ld> <li><code>&lt;key&gt;.name</code> for the font's name</li> <li><code>&lt;key&gt;.style</code> for
     * the font's style (an integer value)</li> <li><code>&lt;key&gt;.name</code> for the font's size in points (an
     * integer value)</li> </ld>
     *
     * @param key the key
     *
     * @return the value for the given key, or a plain, 12-point "SandSerif" font if the key is not contained in this
     *         property set, never <code>null</code>.
     */
    public Font getPropertyFont(String key) {
        return getPropertyFont(key, new Font("SansSerif", Font.PLAIN, 12));
    }

    /**
     * Gets a value of type <code>Font</code>. The method actually looks for three keys in order to construct the font
     * instance: <ld> <li><code>&lt;key&gt;.name</code> for the font's name</li> <li><code>&lt;key&gt;.style</code> for
     * the font's style (an integer value)</li> <li><code>&lt;key&gt;.name</code> for the font's size in points (an
     * integer value)</li> </ld>
     *
     * @param key          the key
     * @param defaultValue the default value that is returned if the key was not found in this property set.
     *
     * @return the value for the given key, or <code>defaultValue</code> if the key is not contained in this property
     *         set.
     */
    public Font getPropertyFont(String key, Font defaultValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        String fontName = getPropertyString(key + ".name", null);
        if (fontName != null) {
            int fontStyle = getPropertyInt(key + ".style", java.awt.Font.PLAIN);
            int fontSize = getPropertyInt(key + ".size", 12);
            return new Font(fontName, fontStyle, fontSize);
        }
        return defaultValue;
    }

    /**
     * Sets a font of type <code>Font</code>. The method actually puts three keys in this property set in order to store
     * the font's properties: <ld> <li><code>&lt;key&gt;.name</code> for the font's name</li>
     * <li><code>&lt;key&gt;.style</code> for the font's style (an integer font)</li> <li><code>&lt;key&gt;.name</code>
     * for the font's size in points (an integer font)</li> </ld>
     *
     * @param key      the key
     * @param newValue the font
     *
     * @throws IllegalArgumentException
     */
    public void setPropertyFont(String key, Font newValue) {
        Guardian.assertNotNullOrEmpty("key", key);
        Font oldValue = getPropertyFont(key, null);
        if (!ObjectUtils.equalObjects(oldValue, newValue)) {
            if (newValue != null) {
                _properties.setProperty(key + ".name", newValue.getName());
                _properties.setProperty(key + ".style", String.valueOf(newValue.getStyle()));
                _properties.setProperty(key + ".size", String.valueOf(newValue.getSize()));
            } else {
                _properties.remove(key + ".name");
                _properties.remove(key + ".style");
                _properties.remove(key + ".size");
            }
            firePropertyChange(key, oldValue, newValue);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(null, listener);
    }

    public void addPropertyChangeListener(String key, PropertyChangeListener listener) {
        if (listener != null) {
            if (_propertyChangeSupport == null) {
                _propertyChangeSupport = new PropertyChangeSupport(this);
            }
            if (key == null) {
                _propertyChangeSupport.addPropertyChangeListener(listener);
            } else {
                _propertyChangeSupport.addPropertyChangeListener(key, listener);
            }
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        removePropertyChangeListener(null, listener);
    }

    public void removePropertyChangeListener(String key, PropertyChangeListener listener) {
        if (listener != null && _propertyChangeSupport != null) {
            if (key == null) {
                _propertyChangeSupport.removePropertyChangeListener(listener);
            } else {
                _propertyChangeSupport.removePropertyChangeListener(key, listener);
            }
        }
    }

    protected void firePropertyChange(String key, Object oldValue, Object newValue) {
        if (_propertyChangeSupport != null) {
            _propertyChangeSupport.firePropertyChange(key, oldValue, newValue);
        }
    }

    private void changeInternalProperty(String key, Object oldValue, Object newValue) {
        if (!ObjectUtils.equalObjects(oldValue, newValue)) {
            if (newValue != null) {
                _properties.setProperty(key, newValue.toString());
            } else {
                _properties.remove(key);
            }
            firePropertyChange(key, oldValue, newValue);
        }
    }
}
