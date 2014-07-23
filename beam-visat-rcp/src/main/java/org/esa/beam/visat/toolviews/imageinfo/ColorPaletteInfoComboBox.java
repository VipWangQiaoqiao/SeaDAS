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

    private JComboBox standardJComboBox = null;
    private JComboBox userJComboBox = null;
    private JComboBox everythingJComboBox = null;

    ArrayList<ColorPaletteInfo> standardColorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    ArrayList<ColorPaletteInfo> userColorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    ArrayList<ColorPaletteInfo> everythingColorPaletteInfos = new ArrayList<ColorPaletteInfo>();

    private ColorPaletteInfo defaultStandardColorPaletteInfo = null;
    private ColorPaletteInfo defaultUserColorPaletteInfo = null;
    private ColorPaletteInfo defaultEverythingColorPaletteInfo = null;


    private final String USER_SCHEMA_FILENAME = "user_color_palette_schemas.txt";
    private final String STANDARD_SCHEMA_FILENAME = "standard_color_palette_schemas.txt";
    private final String EVERYTHING_SCHEMA_FILENAME = "everything_color_palette_schemas.txt";


    private final String USER_SCHEMA_COMBO_BOX_NAME = "User";
    private final String STANDARD_SCHEMA_COMBO_BOX_NAME = "Standard";
    private final String EVERYTHING_SCHEMA_COMBO_BOX_NAME = "Everything";


    private File colorPaletteDir = null;

    private boolean shouldFire = true;

    public ColorPaletteInfoComboBox(File dirName) {
        colorPaletteDir = dirName;

        initStandardSchemeComboBox();
        initUserSchemeComboBox();
        initEverythingSchemeComboBox();

        reset();
    }




    private void initStandardSchemeComboBox() {

        defaultStandardColorPaletteInfo = new ColorPaletteInfo(STANDARD_SCHEMA_COMBO_BOX_NAME, null, null, 0, 0, false, null, true);
        standardColorPaletteInfos.add(defaultStandardColorPaletteInfo);

        File file = new File(colorPaletteDir, STANDARD_SCHEMA_FILENAME);
        initColorPaletteInfos(colorPaletteDir, standardColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = standardColorPaletteInfos.toArray();


        final String[] toolTipsArray = new String[standardColorPaletteInfos.size()];

        int i = 0;
        for (ColorPaletteInfo colorPaletteInfo : standardColorPaletteInfos) {
            toolTipsArray[i] = colorPaletteInfo.getDescription();
            i++;
        }


        final Boolean[] enabledArray = new Boolean[standardColorPaletteInfos.size()];

        i = 0;
        for (ColorPaletteInfo colorPaletteInfo : standardColorPaletteInfos) {
            enabledArray[i] = colorPaletteInfo.isEnabled();
            i++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);
        myComboBoxRenderer.setEnabledList(enabledArray);

        standardJComboBox = new JComboBox(colorPaletteInfosArray);
        standardJComboBox.setRenderer(myComboBoxRenderer);
        standardJComboBox.setEditable(false);
        standardJComboBox.setMaximumRowCount(20);
        standardJComboBox.setToolTipText("To modify see file: " + colorPaletteDir + "/" + STANDARD_SCHEMA_FILENAME);


    }


    private void initUserSchemeComboBox() {

        defaultUserColorPaletteInfo = new ColorPaletteInfo(USER_SCHEMA_COMBO_BOX_NAME, null, null, 0, 0, false, null, true);
        userColorPaletteInfos.add(defaultUserColorPaletteInfo);

        File file = new File(colorPaletteDir, USER_SCHEMA_FILENAME);
        initColorPaletteInfos(colorPaletteDir, userColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = userColorPaletteInfos.toArray();

        final String[] toolTipsArray = new String[userColorPaletteInfos.size()];

        int i = 0;
        for (ColorPaletteInfo colorPaletteInfo : userColorPaletteInfos) {
            toolTipsArray[i] = colorPaletteInfo.getDescription();
            i++;
        }

        final Boolean[] enabledArray = new Boolean[userColorPaletteInfos.size()];

        i = 0;
        for (ColorPaletteInfo colorPaletteInfo : userColorPaletteInfos) {
            enabledArray[i] = colorPaletteInfo.isEnabled();
            i++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);
        myComboBoxRenderer.setEnabledList(enabledArray);

        userJComboBox = new JComboBox(colorPaletteInfosArray);
        userJComboBox.setRenderer(myComboBoxRenderer);
        userJComboBox.setEditable(false);
        userJComboBox.setMaximumRowCount(20);
        userJComboBox.setToolTipText("To modify see file: " + colorPaletteDir + "/" + USER_SCHEMA_FILENAME);


    }



    private void initEverythingSchemeComboBox() {

        defaultEverythingColorPaletteInfo = new ColorPaletteInfo(EVERYTHING_SCHEMA_COMBO_BOX_NAME, null, null, 0, 0, false, null, true);
        everythingColorPaletteInfos.add(defaultEverythingColorPaletteInfo);

        File file = new File(colorPaletteDir, EVERYTHING_SCHEMA_FILENAME);
        initColorPaletteInfos(colorPaletteDir, everythingColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = everythingColorPaletteInfos.toArray();


        final String[] toolTipsArray = new String[everythingColorPaletteInfos.size()];

        int i = 0;
        for (ColorPaletteInfo colorPaletteInfo : everythingColorPaletteInfos) {
            toolTipsArray[i] = colorPaletteInfo.getDescription();
            i++;
        }


        final Boolean[] enabledArray = new Boolean[everythingColorPaletteInfos.size()];

        i = 0;
        for (ColorPaletteInfo colorPaletteInfo : everythingColorPaletteInfos) {
            enabledArray[i] = colorPaletteInfo.isEnabled();
            i++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);
        myComboBoxRenderer.setEnabledList(enabledArray);

        everythingJComboBox = new JComboBox(colorPaletteInfosArray);
        everythingJComboBox.setRenderer(myComboBoxRenderer);
        everythingJComboBox.setEditable(false);
        everythingJComboBox.setMaximumRowCount(20);
        everythingJComboBox.setToolTipText("To modify see file: " + colorPaletteDir + "/" + EVERYTHING_SCHEMA_FILENAME);


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

                    ColorPaletteInfo colorPaletteInfo;

                    if (testMinMax(minVal, maxVal, isLogScaled)) {

                        File cpdFile = new File(dirName, cpdFileName);
                        if (cpdFile.exists()) {
                            ColorPaletteDef colorPaletteDef;
                            try {
                                colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(cpdFile);
                                colorPaletteInfo = new ColorPaletteInfo(name, description, cpdFileName, minVal, maxVal, isLogScaled, colorPaletteDef, true);

                            } catch (IOException e) {
                                colorPaletteInfo = new ColorPaletteInfo(name, description);
                            }
                        } else {
                            colorPaletteInfo = new ColorPaletteInfo(name, description);
                        }
                    } else {
                        colorPaletteInfo = new ColorPaletteInfo(name, description);
                    }

                    colorPaletteInfos.add(colorPaletteInfo);
                }
            }
        }
    }


    private boolean testMinMax(double min, double max, boolean isLogScaled) {
        boolean checksOut = true;

        if (min == max) {
            checksOut = false;
        }

        if (isLogScaled && min == 0) {
            checksOut = false;
        }

        return checksOut;
    }


    public void reset() {
        standardJComboBox.setSelectedItem(defaultStandardColorPaletteInfo);
        userJComboBox.setSelectedItem(defaultUserColorPaletteInfo);
        everythingJComboBox.setSelectedItem(defaultEverythingColorPaletteInfo);
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

    public JComboBox getEverythingJComboBox() {
        return everythingJComboBox;
    }


    class MyComboBoxRenderer extends BasicComboBoxRenderer {

        private String[] tooltips;
        private Boolean[] enabledList;

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            if (index >= 0 && index < enabledList.length) {
                setEnabled(enabledList[index]);
                setFocusable(enabledList[index]);
            }


            if (isSelected) {
                setBackground(Color.blue);

                if (index >= 0  && index < tooltips.length) {
                    list.setToolTipText(tooltips[index]);
                }

                if (index >= 0 && index < enabledList.length) {

                    if (enabledList[index]) {
                        setForeground(Color.white);
                    } else {
                        setForeground(Color.gray);
                    }
                }

            } else {
                setBackground(Color.white);

                if (index >= 0 && index < enabledList.length) {
                    if (enabledList[index] == true) {
                        setForeground(Color.black);
                    } else {
                        setForeground(Color.gray);
                    }

                }
            }


            if (index == 0) {
                setForeground(Color.black);
            }

            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());




            return this;
        }

        public void setTooltipList(String[] tooltipList) {
            this.tooltips = tooltipList;
        }


        public void setEnabledList(Boolean[] enabledList) {
            this.enabledList = enabledList;
        }
    }

}