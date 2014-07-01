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
    private JComboBox standardJComboBox = null;
    private JComboBox userJComboBox = null;


    ArrayList<ColorPaletteInfo> colorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    ArrayList<ColorPaletteInfo> standardColorPaletteInfos = new ArrayList<ColorPaletteInfo>();
    ArrayList<ColorPaletteInfo> userColorPaletteInfos = new ArrayList<ColorPaletteInfo>();

    private ColorPaletteInfo defaultColorPaletteInfo = null;
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
        defaultColorPaletteInfo = new ColorPaletteInfo("Other Products    ", null, null, 0, 0, false, null);
        colorPaletteInfos.add(defaultColorPaletteInfo);
        initColorPaletteInfos(colorPaletteDir, colorPaletteInfos, file);

        Object[] colorPaletteInfosArray = colorPaletteInfos.toArray();

        jComboBox = new JComboBox(colorPaletteInfosArray);
        getjComboBox().setEditable(false);
    }


    private void initStandardSchemeComboBox() {

        defaultStandardColorPaletteInfo = new ColorPaletteInfo("Standard Products    ", null, null, 0, 0, false, null);
        standardColorPaletteInfos.add(defaultStandardColorPaletteInfo);

        File file = new File(colorPaletteDir, "standard_color_palette_schemas.txt");
        initColorPaletteInfos(colorPaletteDir, standardColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = standardColorPaletteInfos.toArray();

        standardJComboBox = new JComboBox(colorPaletteInfosArray);
        getStandardJComboBox().setEditable(false);
    }


    private void initUserSchemeComboBox() {

        defaultUserColorPaletteInfo = new ColorPaletteInfo("User Schemes    ", null, null, 0, 0, false, null);
        userColorPaletteInfos.add(defaultUserColorPaletteInfo);

        File file = new File(colorPaletteDir, "user_color_palette_schemas.txt");
        initColorPaletteInfos(colorPaletteDir, userColorPaletteInfos, file);

        Object[] colorPaletteInfosArray = userColorPaletteInfos.toArray();

        userJComboBox = new JComboBox(colorPaletteInfosArray);
        getStandardJComboBox().setEditable(false);
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


    private void initColorPaletteInfos(File dirName, ArrayList<ColorPaletteInfo> colorPaletteInfos, File file) {

        ArrayList<String> lines = readFileIntoArrayList(file);



        int i = 0;
        for (String line : lines) {
            line.trim();
            if (!line.startsWith("#")) {
                String[] values = line.split(":");

                if (values != null && values.length == 8) {
                    String description = values[0].trim();
                    String name = values[1].trim();
                    String cpdFileName = values[7].trim() + ".cpd";
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
        standardJComboBox.setSelectedItem(defaultStandardColorPaletteInfo);
        userJComboBox.setSelectedItem(defaultUserColorPaletteInfo);
    }

    public JComboBox getjComboBox() {
        return jComboBox;
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

}