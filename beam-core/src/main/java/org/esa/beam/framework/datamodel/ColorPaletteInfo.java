package org.esa.beam.framework.datamodel;

/**
 * Created by danielknowles on 6/28/14.
 */
public class ColorPaletteInfo {
    private String name;
    private String rootName;
    private String description;
    private String cpdFilename;
    private String cpdFilenameColorBlind;
    private String colorBarTitle;
    private String colorBarLabels;
    private double minValue;
    private double maxValue;
    private boolean isLogScaled;
    private boolean isSourceLogScaled = false;
    private ColorPaletteDef colorPaletteDef;
    private boolean enabled;
    private boolean isOverRide;




    public ColorPaletteInfo(String name, String rootName, String description, String cpdFilename, double minValue, double maxValue,
                            boolean isLogScaled, ColorPaletteDef colorPaletteDef, boolean isOverRide, boolean enabled, String cpdFilenameColorBlind, String colorBarTitle, String colorBarLabels) {
        this.setName(name);
        if (rootName != null) {
            this.setRootName(rootName);
        } else {
            this.setRootName(name);
        }
        this.setDescription(description);
        this.setCpdFilename(cpdFilename);
        this.setMinValue(minValue);
        this.setMaxValue(maxValue);
        this.setLogScaled(isLogScaled);
        this.setEnabled(enabled);
        this.setOverRide(isOverRide);
        this.colorPaletteDef = colorPaletteDef;
        this.cpdFilenameColorBlind = cpdFilenameColorBlind;
        this.colorBarLabels = colorBarLabels;
        this.colorBarTitle = colorBarTitle;
        if (colorPaletteDef != null) {
            this.setSourceLogScaled(colorPaletteDef.isLogScaled());
        } else {
            this.setSourceLogScaled(false);
        }
    }

    public ColorPaletteInfo(String name, String description) {
        // create a disabled colorPaletteInfo
        this.setName(name);
        this.setDescription(description);
//        this.setCpdFilename(cpdFilename);
//        this.setMinValue(minValue);
//        this.setMaxValue(maxValue);
//        this.setLogScaled(isLogScaled);
        this.setEnabled(false);
 //       this.colorPaletteDef = colorPaletteDef;
    }

    public String getCpdFilename() {
        return cpdFilename;
    }

    private void setCpdFilename(String cpdFilename) {
        this.cpdFilename = cpdFilename;
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

    public ColorPaletteDef getColorPaletteDef() {
        return colorPaletteDef;
    }

    private void setColorPaletteDef(ColorPaletteDef colorPaletteDef) {
        this.colorPaletteDef = colorPaletteDef;
    }

    public boolean isSourceLogScaled() {
        return isSourceLogScaled;
    }

    private void setSourceLogScaled(boolean sourceLogScaled) {
        isSourceLogScaled = sourceLogScaled;
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
}