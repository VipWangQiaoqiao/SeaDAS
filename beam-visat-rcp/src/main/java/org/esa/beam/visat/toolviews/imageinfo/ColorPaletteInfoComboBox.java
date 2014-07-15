package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ColorPaletteDef;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by danielknowles on 6/28/14.
 */
public class ColorPaletteInfoComboBox {

    private JComboBox otherJCComboBox = null;
    private JComboBox standardJComboBox = null;
    private JComboBox userJComboBox = null;


    ArrayList<ColorPaletteInfo> otherColorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    ArrayList<ColorPaletteInfo> standardColorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    ArrayList<ColorPaletteInfo> userColorPaletteInfos = new ArrayList<ColorPaletteInfo>();

    private ColorPaletteInfo defaultOtherColorPaletteInfo = null;
    private ColorPaletteInfo defaultStandardColorPaletteInfo = null;
    private ColorPaletteInfo defaultUserColorPaletteInfo = null;


    private File colorPaletteDir = null;

    private boolean shouldFire = true;

    public ColorPaletteInfoComboBox(File dirName) {
        colorPaletteDir = dirName;

        initOtherSchemeComboBox();
        initStandardSchemeComboBox();
        initUserSchemeComboBox();

        reset();
    }


    private void initOtherSchemeComboBox() {

        File file = new File(colorPaletteDir, "other_color_palette_schemas.txt");
        defaultOtherColorPaletteInfo = new ColorPaletteInfo("Other Products    ", null, null, 0, 0, false, null);
        otherColorPaletteInfos.add(defaultOtherColorPaletteInfo);
        initColorPaletteInfos(colorPaletteDir, otherColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = otherColorPaletteInfos.toArray();

        final String[] toolTipsArray = new String[otherColorPaletteInfos.size()];

        int j = 0;
        for (ColorPaletteInfo colorPaletteInfo : otherColorPaletteInfos) {
            toolTipsArray[j] = colorPaletteInfo.getDescription();
            j++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);

        otherJCComboBox = new JComboBox(colorPaletteInfosArray);
        otherJCComboBox.setRenderer(myComboBoxRenderer);
        otherJCComboBox.setEditable(false);
    }


    private void initStandardSchemeComboBox() {

        defaultStandardColorPaletteInfo = new ColorPaletteInfo("Standard Products    ", null, null, 0, 0, false, null);
        standardColorPaletteInfos.add(defaultStandardColorPaletteInfo);

        File file = new File(colorPaletteDir, "standard_color_palette_schemas.txt");
        initColorPaletteInfos(colorPaletteDir, standardColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = standardColorPaletteInfos.toArray();


        final String[] toolTipsArray = new String[standardColorPaletteInfos.size()];

        int j = 0;
        for (ColorPaletteInfo colorPaletteInfo : standardColorPaletteInfos) {
            toolTipsArray[j] = colorPaletteInfo.getDescription();
            j++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);

        standardJComboBox = new JComboBox(colorPaletteInfosArray);
        standardJComboBox.setRenderer(myComboBoxRenderer);
        standardJComboBox.setEditable(false);

    }


    private void initUserSchemeComboBox() {

        defaultUserColorPaletteInfo = new ColorPaletteInfo("User Schemes    ", null, null, 0, 0, false, null);
        userColorPaletteInfos.add(defaultUserColorPaletteInfo);

        File file = new File(colorPaletteDir, "user_color_palette_schemas.txt");
        initColorPaletteInfos(colorPaletteDir, userColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = userColorPaletteInfos.toArray();

        final String[] toolTipsArray = new String[userColorPaletteInfos.size()];

        int j = 0;
        for (ColorPaletteInfo colorPaletteInfo : userColorPaletteInfos) {
            toolTipsArray[j] = colorPaletteInfo.getDescription();
            j++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);

        userJComboBox = new JComboBox(colorPaletteInfosArray);
        userJComboBox.setRenderer(myComboBoxRenderer);
        userJComboBox.setEditable(false);
        userJComboBox.setToolTipText("To modify see file: ~/.seadas/beam-ui/auxdata/color-palettes/user_color_palette_schemas.txt");
    }


    private void initColorPaletteInfos(File dirName, ArrayList<ColorPaletteInfo> colorPaletteInfos, File file) {

        ArrayList<String> lines = readFileIntoArrayList(file);


        int i = 0;
        for (String line : lines) {
            line.trim();
            if (!line.startsWith("#")) {
                String[] values = line.split(":");

                if (values != null && (values.length == 5 || values.length == 6)) {

                    String name = values[0].trim();
                    String minValStr = values[1].trim();
                    String maxValStr = values[2].trim();
                    String isLogScaledStr = values[3].trim();
                    String cpdFileName = values[4].trim();

                    String description;
                    if (values.length == 6) {
                        description = values[5].trim();
                    } else {
                        description = "";
                    }

                    Double minVal = Double.valueOf(minValStr);
                    Double maxVal = Double.valueOf(maxValStr);
                    boolean isLogScaled = false;
                    if (isLogScaledStr != null && isLogScaledStr.toLowerCase().equals("true")) {
                        isLogScaled = true;
                    }

                    File cpdFile = new File(dirName, cpdFileName);

                    if (cpdFile.exists()) {
                        ColorPaletteDef colorPaletteDef;
                        try {
                            colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(cpdFile);

                            ColorPaletteInfo colorPaletteInfo = new ColorPaletteInfo(name, description, cpdFileName, minVal, maxVal, isLogScaled, colorPaletteDef);
                            colorPaletteInfos.add(colorPaletteInfo);
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            }
        }
    }

    public void reset() {
        otherJCComboBox.setSelectedItem(defaultOtherColorPaletteInfo);
        standardJComboBox.setSelectedItem(defaultStandardColorPaletteInfo);
        userJComboBox.setSelectedItem(defaultUserColorPaletteInfo);
    }

    public JComboBox getOtherJCComboBox() {
        return otherJCComboBox;
    }


    public ArrayList<String> readFileIntoArrayList(File file) {
        String lineData;
        ArrayList<String> fileContents = new ArrayList<String>();
        BufferedReader moFile = null;
        try {
            moFile = new BufferedReader(new FileReader(file));
            while ((lineData = moFile.readLine()) != null) {

                fileContents.add(lineData);
            }
        } catch (IOException e) {
            ;
        } finally {
            try {
                moFile.close();
            } catch (Exception e) {
                //Ignore
            }
        }
        return fileContents;
    }

    public boolean isShouldFire() {
        return shouldFire;
    }

    public void setShouldFire(boolean shouldFire) {
        this.shouldFire = shouldFire;
    }

    public JComboBox getStandardJComboBox() {
        return standardJComboBox;
    }


    public JComboBox getUserJComboBox() {
        return userJComboBox;
    }


    class MyComboBoxRenderer extends BasicComboBoxRenderer {

        private String[] tooltips;

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            if (isSelected) {
                if (-1 < index && index < tooltips.length) {
                    list.setToolTipText(tooltips[index]);
                }

                setBackground(Color.blue);
                setForeground(Color.white);
            } else {
                setBackground(Color.white);
                setForeground(Color.black);
            }

            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());

            return this;
        }

        public void setTooltipList(String[] tooltipList) {
            this.tooltips = tooltipList;
        }
    }

}