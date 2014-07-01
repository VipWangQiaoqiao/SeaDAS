package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ColorPaletteDef;

/**
 * Created by danielknowles on 6/28/14.
 */
public class ColorPaletteInfo {
    private String name;
    private String description;
    private String cpdFilename;
    private double minValue;
    private double maxValue;
    private boolean isLogScaled;
    private ColorPaletteDef colorPaletteDef;

    public ColorPaletteInfo(String name, String description, String cpdFilename, double minValue, double maxValue,
                            boolean isLogScaled, ColorPaletteDef colorPaletteDef) {
        this.setName(name);
        this.setDescription(description);
        this.setCpdFilename(cpdFilename);
        this.setMinValue(minValue);
        this.setMaxValue(maxValue);
        this.setLogScaled(isLogScaled);
        this.colorPaletteDef = colorPaletteDef;
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
}