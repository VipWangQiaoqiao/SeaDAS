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
    public static final String CPD_DEFAULTS_FILENAME = "cpd_defaults.txt";
    public static final String CPD_SCHEMES_FILENAME = "cpd_schemes.txt";
    public static final String USER_CPD_DEFAULTS_FILENAME = "user_cpd_defaults.txt";
    public static final String USER_CPD_SCHEMES_FILENAME = "user_cpd_schemes.txt";

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


    private final String STANDARD_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME = "Standard Schemes";


    private JComboBox jComboBox = null;
    private boolean jComboBoxShouldFire = true;

    private ArrayList<ColorPaletteInfo> colorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    private ColorPaletteInfo jComboBoxFirstEntryColorPaletteInfo = null;

    private File colorPaletteDir = null;
    private File schemesFile = null;
    private File userSchemesFile = null;
    private String jComboBoxFirstEntryName = null;


    public ColorPaletteSchemes(File colorPaletteDir, Id id, boolean userInterfaceMode) {
        this.colorPaletteDir = colorPaletteDir;

        switch (id) {
            case STANDARD:
                schemesFile = new File(this.colorPaletteDir, CPD_SCHEMES_FILENAME);
                userSchemesFile = new File(this.colorPaletteDir, USER_CPD_SCHEMES_FILENAME);
                setjComboBoxFirstEntryName(STANDARD_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME);
                break;
            case DEFAULTS:
                schemesFile = new File(this.colorPaletteDir, CPD_DEFAULTS_FILENAME);
                userSchemesFile = new File(this.colorPaletteDir, USER_CPD_DEFAULTS_FILENAME);
                //          setjComboBoxFirstEntryName(DEFAULTS_SCHEME_COMBO_BOX_FIRST_ENTRY_NAME);
                break;

        }


        if (colorPaletteDir != null && colorPaletteDir.exists()) {

            if (userInterfaceMode) {
                initComboBox();
            } else {
                // this mode is used for setting the default color scheme for an image when first opened
                // it doesn't need comboBoxes, only the colorPaletteInfos is needed
                initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, schemesFile, false);
                if (userSchemesFile.exists()) {
                    initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, userSchemesFile, false);
                }

            }

            reset();
        }
    }


    private void initComboBox() {

        jComboBoxFirstEntryColorPaletteInfo = new ColorPaletteInfo(getjComboBoxFirstEntryName(), null, null, 0, 0, false, null, true, true);
        colorPaletteInfos.add(jComboBoxFirstEntryColorPaletteInfo);

        initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, schemesFile, true);
        if (userSchemesFile.exists()) {
            initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, userSchemesFile, true);
        }

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
            jComboBox.setToolTipText("To modify see file: " + colorPaletteDir + "/" + userSchemesFile.getName());
        }


    }


    private void initColorPaletteInfos(File dirName, ArrayList<ColorPaletteInfo> colorPaletteInfos, File file, boolean schemeSelectorMode) {

        ArrayList<String> lines = readFileIntoArrayList(file);


        int i = 0;
        for (String line : lines) {
            line.trim();
            if (!line.startsWith("#")) {
                String[] values = line.split(":");

                if (values != null && (values.length == 6 || values.length == 7)) {

                    String name = values[0].trim();
                    String minValStr = values[1].trim();
                    String maxValStr = values[2].trim();
                    String logScaledStr = values[3].trim();
                    String cpdFileName = values[4].trim();


                    if (name != null && name.length() > 0 &&
                            minValStr != null && minValStr.length() > 0 &&
                            maxValStr != null && maxValStr.length() > 0 &&
                            logScaledStr != null && logScaledStr.length() > 0 &&
                            cpdFileName != null && cpdFileName.length() > 0) {
                        String description;
                        if (values.length >= 7) {
                            description = values[6].trim();
                        } else {
                            description = "";
                        }

                        String overRideStr;
                        if (values.length >= 6) {
                         overRideStr = values[5].trim();
                        } else {
                            overRideStr = "false";
                        }

                        Double minVal = Double.valueOf(minValStr);
                        Double maxVal = Double.valueOf(maxValStr);
                        boolean logScaled = false;
                        if (logScaledStr != null && logScaledStr.toLowerCase().equals("true")) {
                            logScaled = true;
                        }

                        boolean overRide = false;
                        if (overRideStr != null && overRideStr.toLowerCase().equals("true")) {
                            overRide = true;
                        }

                        ColorPaletteInfo colorPaletteInfo;

                        if (testMinMax(minVal, maxVal, logScaled)) {

                            File cpdFile = new File(dirName, cpdFileName);
                            if (cpdFile.exists()) {
                                ColorPaletteDef colorPaletteDef;
                                try {
                                    colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(cpdFile);
                                    colorPaletteInfo = new ColorPaletteInfo(name, description, cpdFileName, minVal, maxVal, logScaled, colorPaletteDef, overRide, true);

                                } catch (IOException e) {
                                    colorPaletteInfo = new ColorPaletteInfo(name, description);
                                }
                            } else {
                                colorPaletteInfo = new ColorPaletteInfo(name, description);
                            }
                        } else {
                            colorPaletteInfo = new ColorPaletteInfo(name, description);
                        }

                        if (colorPaletteInfo != null) {
                            if (schemeSelectorMode && overRide) {
                                // look for previous name which user may be overriding and delete it in the colorPaletteInfo object
                                ColorPaletteInfo colorPaletteInfoToDelete = null;
                                for (ColorPaletteInfo storedColorPaletteInfo : colorPaletteInfos) {
                                    if (storedColorPaletteInfo.getName().equals(name)) {
                                        colorPaletteInfoToDelete = storedColorPaletteInfo;
                                        break;
                                    }
                                }
                                if (colorPaletteInfoToDelete != null) {
                                    colorPaletteInfos.remove(colorPaletteInfoToDelete);
                                }
                            }
                            colorPaletteInfos.add(colorPaletteInfo);
                        }
                    }
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