package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ColorPaletteDef;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by danielknowles on 6/28/14.
 */
public class ColorPaletteInfoComboBox {

    private JComboBox jComboBox = null;
    private JLabel jLabel = null;
    private JLabel jLabel2 = new JLabel("");
    ArrayList<ColorPaletteInfo> colorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    private File colorPaletteDir = null;
    private ColorPaletteInfo defaultColorPaletteInfo = null;
    private boolean shouldFire = true;

    public ColorPaletteInfoComboBox(File dirName) {
        colorPaletteDir = dirName;
        initColorPaletteInfos(dirName);

        Object[] colorPaletteInfosArray = colorPaletteInfos.toArray();

        jComboBox = new JComboBox(colorPaletteInfosArray);
        getjComboBox().setEditable(false);


        jLabel = new JLabel("");
        getjLabel().setToolTipText("Set color ramp, min, max, and logScaled based on some pre-defined settings");

        reset();
    }

//    public boolean setSelectedByValues(ColorPaletteDef cpd, double min, double max, boolean isLogScaled) {
//        System.out.println("hello");
//
//
//        double rndMin = Math.round((min * 100000000)) / 100000000;
//        double rndMax = Math.round((max * 100000000)) / 100000000;
//        System.out.println("min=" + rndMin);
//        System.out.println("max=" + rndMax);
//        if (isLogScaled) {
//            System.out.println("isLogScaled=true");
//        } else {
//            System.out.println("isLogScaled=false");
//        }
//        for (ColorPaletteInfo colorPaletteInfo : colorPaletteInfos) {
//            if (colorPaletteInfo.getColorPaletteDef() != null) {
//                System.out.println("we are a go");
//                if (
//
//                    //colorPaletteInfo.getColorPaletteDef() == cpd &&
//                        colorPaletteInfo.getMinValue() == rndMin &&
//                                colorPaletteInfo.getMaxValue() == rndMax
//                                // &&
//                       //         colorPaletteInfo.isLogScaled() == isLogScaled
//                        ) {
//                    System.out.println("we are a go");
//                    System.out.println(colorPaletteInfo.getName());
//                    setShouldFire(false);
//                    jComboBox.setSelectedItem(colorPaletteInfo);
//                    setShouldFire(true);
//                    return true;
//                }
//            }
//        }
//
//        setShouldFire(false);
//        jComboBox.setSelectedItem(defaultColorPaletteInfo);
//        setShouldFire(true);
//        System.out.println("we are NOT a go");
//        return false;
//    }


    private void initColorPaletteInfos(File dirName) {
        File file = new File(colorPaletteDir, "color_palette_schemas.txt");
        ArrayList<String> lines = readFileIntoArrayList(file);

        defaultColorPaletteInfo = new ColorPaletteInfo("Stored Schemas    ", null, null, 0, 0, false, null);
        colorPaletteInfos.add(defaultColorPaletteInfo);

        int i = 0;
        for (String line : lines) {
            line.trim();
            if (!line.startsWith("#")) {
                String[] values = line.split(":");

                if (values != null && values.length == 8) {
                    String description = values[0].trim();
                    String name = values[1].trim();
                    String cpdFileName = "OC_" + values[7].trim() + ".cpd";
                    String minValStr = values[4].trim();
                    String maxValStr = values[5].trim();
                    String isLogScaledStr = values[3].trim();

                    Double minVal = Double.valueOf(minValStr);
                    Double maxVal = Double.valueOf(maxValStr);
                    boolean isLogScaled = false;
                    if ("logarithmic".equals(isLogScaledStr)) {
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
        jComboBox.setSelectedItem(defaultColorPaletteInfo);
//        getjLabel2().setText("");
    }

    public JComboBox getjComboBox() {
        return jComboBox;
    }

    public JLabel getjLabel() {
        return jLabel;
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

    public JLabel getjLabel2() {
        return jLabel2;
    }

    public void setjLabel2(String text) {
        this.jLabel2.setText("Settings updated with schema: '" + text + "'");
    }

    public boolean isShouldFire() {
        return shouldFire;
    }

    public void setShouldFire(boolean shouldFire) {
        this.shouldFire = shouldFire;
    }
}