package org.esa.beam.framework.datamodel;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 7/6/16
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */


import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class MaskSchemes {
    public static final String MASKS_CONFIG_FILENAME = "l2_masks_config.txt";

    private ArrayList<MasksConfigInfo> masksConfigInfos = new ArrayList<MasksConfigInfo>();

    private File masksConfigDir = null;
    private File masksConfigFile = null;


    public MaskSchemes(File masksConfigDir) {
        this.masksConfigDir = masksConfigDir;
        masksConfigFile = new File(this.masksConfigDir, MASKS_CONFIG_FILENAME);


        if (masksConfigDir != null && masksConfigDir.exists() && masksConfigFile.exists()) {
            initMasksConfigInfos(masksConfigDir, masksConfigInfos, masksConfigFile);
        }
    }


    private void initMasksConfigInfos(File dirName, ArrayList<MasksConfigInfo> masksConfigInfos, File file) {

        ArrayList<String> lines = readFileIntoArrayList(file);


        int i = 0;
        for (String line : lines) {
            line.trim();
            if (!line.startsWith("#")) {
                String[] values = line.split(":");

                if (values != null) {
                    boolean validEntry = true;

                    String name = null;
                    String logicalExpressComponentsString = null;
                    String isUnionString = null;
                    String colorComponentsString = null;
                    String transparencyString = null;
                    String description = null;

                    boolean isUnion = true;
                    Color color = null;
                    double transparency = 0.0;


                    if (values.length == 5 || values.length == 6) {

                        name = values[0].trim();
                        logicalExpressComponentsString = values[1].trim();
                        isUnionString = values[2].trim();
                        colorComponentsString = values[3].trim();
                        transparencyString = values[4].trim();

                        if (values.length == 6) {
                            description = values[5].trim();
                        } else {
                            description = "";
                        }

                        // Determine name field
                        if (name == null || name.length() == 0) {
                            validEntry = false;
                        }


                        // Determine isUnion field
                        if (isUnionString != null && isUnionString.length() > 0) {
                            if (isUnionString.toLowerCase().equals("true")) {
                                isUnion = true;
                            } else if (isUnionString.toLowerCase().equals("false")) {
                                isUnion = false;
                            } else {
                                validEntry = false;
                            }
                        } else {
                            validEntry = false;
                        }



                        // Determine color field
                        if (colorComponentsString != null && colorComponentsString.length() > 0) {
                            String[] colorComponents = colorComponentsString.split(",");
                            if (colorComponents.length == 3) {
                                int red = Integer.parseInt(colorComponents[0]);
                                int green = Integer.parseInt(colorComponents[1]);
                                int blue = Integer.parseInt(colorComponents[2]);
                                if (testColor(red) && testColor(green) && testColor(blue)) {
                                    color = new Color(red, green, blue);
                                } else {
                                    validEntry = false;
                                }
                            } else {
                                validEntry = false;
                            }
                        } else {
                            validEntry = false;
                        }


                        // Determine transparency field
                        transparency =  Double.valueOf(transparencyString);

                        if (transparency < 0 || transparency > 1.0) {
                            validEntry = false;
                        }



                        // Determine logicalExpressComponents field

                        if (validEntry && logicalExpressComponentsString != null && logicalExpressComponentsString.length() > 0) {

                            String[] logicalExpressComponents = logicalExpressComponentsString.split(",");

                            if (logicalExpressComponents.length > 0) {
                                for (String component : logicalExpressComponents) {
                                    if (component.length() < 1) {
                                        validEntry = false;
                                    }
                                }
                            } else {
                                validEntry = false;
                            }





                            if (validEntry) {

                                MasksConfigInfo masksConfigInfo = new MasksConfigInfo(name, logicalExpressComponents, isUnion, color, transparency, description);

                                // OverRide any duplicate entries


                                // look for previous name which user may be overriding and delete it in the masksConfigInfo object
                                MasksConfigInfo colorPaletteInfoToDelete = null;
                                for (MasksConfigInfo storedMasksConfigInfo : masksConfigInfos) {
                                    if (storedMasksConfigInfo.getName().equals(name)) {
                                        colorPaletteInfoToDelete = storedMasksConfigInfo;
                                        break;
                                    }
                                }
                                if (colorPaletteInfoToDelete != null) {
                                    masksConfigInfos.remove(colorPaletteInfoToDelete);
                                }

                                masksConfigInfos.add(masksConfigInfo);

                            }
                        }
                    }


                }
            }
        }
    }


    private boolean testColor(int colorValue) {

        if (colorValue >= 0 && colorValue <= 255) {
            return true;
        } else {
            return false;
        }

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


    public ArrayList<MasksConfigInfo> getMasksConfigInfos() {
        return masksConfigInfos;
    }


}