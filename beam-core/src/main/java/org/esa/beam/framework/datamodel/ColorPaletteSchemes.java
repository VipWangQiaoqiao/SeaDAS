package org.esa.beam.framework.datamodel;

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
public class ColorPaletteSchemes {


    public boolean isjComboBoxShouldFire() {
        return jComboBoxShouldFire;
    }

    public void setjComboBoxShouldFire(boolean jComboBoxShouldFire) {
        this.jComboBoxShouldFire = jComboBoxShouldFire;
    }

    public static enum Id {
        STANDARD,
        DEFAULTS
    }

    private final String STANDARD_SCHEMA_FILENAME = "standard_color_palette_schemas.txt";
    private final String DEFAULTS_SCHEMA_FILENAME = "defaults_color_palette_schemas.txt";

//    private final String STANDARD_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME = "General Schemes";
//    private final String DEFAULTS_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME = "Default Schemes";

    private final String STANDARD_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME = "................... select ...................";
    private final String DEFAULTS_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME = "................................";


    private JComboBox jComboBox = null;
    private boolean jComboBoxShouldFire = true;

    private ArrayList<ColorPaletteInfo> colorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    private ColorPaletteInfo jComboBoxFirstEntryColorPaletteInfo = null;

    private File colorPaletteDir = null;
    private File schemesFile = null;
    private String jComboBoxFirstEntryName = null;


    public ColorPaletteSchemes(File colorPaletteDir, Id id, boolean userInterfaceMode) {
        this.colorPaletteDir = colorPaletteDir;

        switch (id) {
            case STANDARD:
                schemesFile = new File(this.colorPaletteDir, STANDARD_SCHEMA_FILENAME);
                setjComboBoxFirstEntryName(STANDARD_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME);
                break;
            case DEFAULTS:
                schemesFile = new File(this.colorPaletteDir, DEFAULTS_SCHEMA_FILENAME);
      //          setjComboBoxFirstEntryName(DEFAULTS_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME);
                break;

        }


        if (colorPaletteDir != null && colorPaletteDir.exists()) {

            if (userInterfaceMode) {
                initComboBox();
            } else {
                // this mode is used for setting the default color scheme for an image when first opened
                // it doesn't need comboBoxes, only the colorPaletteInfos is needed
                initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, schemesFile);
            }

            reset();
        }
    }


    private void initComboBox() {

        jComboBoxFirstEntryColorPaletteInfo = new ColorPaletteInfo(getjComboBoxFirstEntryName(), null, null, 0, 0, false, null, true);
        colorPaletteInfos.add(jComboBoxFirstEntryColorPaletteInfo);

        initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, schemesFile);

        Object[] colorPaletteInfosArray = colorPaletteInfos.toArray();

        final String[] toolTipsArray = new String[colorPaletteInfos.size()];

        int i = 0;
        for (ColorPaletteInfo colorPaletteInfo : colorPaletteInfos) {
            toolTipsArray[i] = colorPaletteInfo.getDescription();
            i++;
        }

        final Boolean[] enabledArray = new Boolean[colorPaletteInfos.size()];

        i = 0;
        for (ColorPaletteInfo colorPaletteInfo : colorPaletteInfos) {
            enabledArray[i] = colorPaletteInfo.isEnabled();
            i++;
        }

        final MyComboBoxRenderer myComboBoxRenderer = new MyComboBoxRenderer();
        myComboBoxRenderer.setTooltipList(toolTipsArray);
        myComboBoxRenderer.setEnabledList(enabledArray);

        jComboBox = new JComboBox(colorPaletteInfosArray);
        jComboBox.setRenderer(myComboBoxRenderer);
        jComboBox.setEditable(false);
        jComboBox.setMaximumRowCount(20);
        if (schemesFile != null) {
            jComboBox.setToolTipText("To modify see file: " + colorPaletteDir + "/" + schemesFile.getName());
        }


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
        if (jComboBox != null) {
            jComboBox.setSelectedItem(jComboBoxFirstEntryColorPaletteInfo);
        }
    }

    public ColorPaletteInfo setSchemeName(String schemeName) {

        if (schemeName != null) {
            for (ColorPaletteInfo colorPaletteInfo : colorPaletteInfos) {
                if (schemeName.trim().equals(colorPaletteInfo.getName().trim())) {
                    jComboBox.setSelectedItem(colorPaletteInfo);
                    return colorPaletteInfo;
                }
            }
        }

        return null;
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


    public JComboBox getjComboBox() {
        return jComboBox;
    }

    public ArrayList<ColorPaletteInfo> getColorPaletteInfos() {
        return colorPaletteInfos;
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

                if (index >= 0 && index < tooltips.length) {
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


    public String getjComboBoxFirstEntryName() {
        return jComboBoxFirstEntryName;
    }

    private void setjComboBoxFirstEntryName(String jComboBoxFirstEntryName) {
        this.jComboBoxFirstEntryName = jComboBoxFirstEntryName;
    }


}