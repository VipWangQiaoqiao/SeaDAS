package org.esa.beam.framework.datamodel;

import java.io.File;
import java.io.IOException;

/**
 * Created by danielknowles on 6/28/14.
 */
public class ColorPaletteInfo {
    private String name;
    private String rootName;
    private String description;
    private String cpdFilenameStandard;
    private String cpdFilenameColorBlind;
    private String colorBarTitle;
    private String colorBarLabels;
    private double minValue;
    private double maxValue;
    private boolean isLogScaled;
    private boolean enabled;
    private boolean isOverRide;
    private File colorPaletteDir;


    public ColorPaletteInfo(String name, String rootName, String description, String cpdFilenameStandard, double minValue, double maxValue,
                            boolean isLogScaled, boolean isOverRide, boolean enabled, String cpdFilenameColorBlind, String colorBarTitle, String colorBarLabels,  File colorPaletteDir) {
        this.setName(name);
        if (rootName != null) {
            this.setRootName(rootName);
        } else {
            this.setRootName(name);
        }
        this.setDescription(description);
        this.setCpdFilenameStandard(cpdFilenameStandard);
        this.setMinValue(minValue);
        this.setMaxValue(maxValue);
        this.setLogScaled(isLogScaled);
        this.setEnabled(enabled);
        this.setOverRide(isOverRide);
        this.cpdFilenameColorBlind = cpdFilenameColorBlind;
        this.colorBarLabels = colorBarLabels;
        this.colorBarTitle = colorBarTitle;
        this.setColorPaletteDir(colorPaletteDir);
    }


    public ColorPaletteDef getColorPaletteDef(boolean useColorBlindPalette) {
       File cpdFile = new File(colorPaletteDir, getCpdFilename(useColorBlindPalette));
        try {
            return ColorPaletteDef.loadColorPaletteDef(cpdFile);

        } catch (IOException e) {
            return null;
        }
    }


    public String getCpdFilename(boolean isUseColorBlind) {
        if (isUseColorBlind) {
            return getCpdFilenameColorBlind();
        } else {
            return getCpdFilenameStandard();
        }
    }

    public String getCpdFilenameStandard() {
        return cpdFilenameStandard;
    }

    private void setCpdFilenameStandard(String cpdFilename) {
        this.cpdFilenameStandard = cpdFilename;
    }

    public double getMinValue() {
        return minValue;
    }

    private void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    private void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isLogScaled() {
        return isLogScaled;
    }

    private void setLogScaled(boolean isLogScaled) {
        this.isLogScaled = isLogScaled;
    }

    public String toString() {
//        if (description == null) {
//            return getName();
//        }   else {
//            return getName() + " (" + getDescription() + ")";
//        }
        return getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }


    public boolean isEnabled() {
        return enabled;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOverRide() {
        return isOverRide;
    }

    public void setOverRide(boolean overRide) {
        isOverRide = overRide;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public String getCpdFilenameColorBlind() {
        return cpdFilenameColorBlind;
    }

    public void setCpdFilenameColorBlind(String cpdFilenameColorBlind) {
        this.cpdFilenameColorBlind = cpdFilenameColorBlind;
    }

    public String getColorBarTitle() {
        return colorBarTitle;
    }

    public void setColorBarTitle(String colorBarTitle) {
        this.colorBarTitle = colorBarTitle;
    }

    public String getColorBarLabels() {
        return colorBarLabels;
    }

    public void setColorBarLabels(String colorBarLabels) {
        this.colorBarLabels = colorBarLabels;
    }



    public File getColorPaletteDir() {
        return colorPaletteDir;
    }

    public void setColorPaletteDir(File colorPaletteDir) {
        this.colorPaletteDir = colorPaletteDir;
    }
}