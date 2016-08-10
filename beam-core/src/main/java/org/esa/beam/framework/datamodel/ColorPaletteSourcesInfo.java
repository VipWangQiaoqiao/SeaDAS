package org.esa.beam.framework.datamodel;

/**
 * Created by danielknowles on 8/26/14.
 */
public class ColorPaletteSourcesInfo {

    private String schemeName = null;
    private boolean alteredScheme = false;

    private String cpdFileName = null;
    private boolean alteredCpd = false;

    private boolean schemeDefault = true;

    private boolean paletteInitialized = false;
    private boolean colorBarInitialized = false;

    private String colorBarLabels = "";
    private boolean isLogScaled;
    private double colorBarMin;
    private double colorBarMax;
    private String colorBarTitle = "";


    ColorPaletteSourcesInfo() {

    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
        setAlteredScheme(false);
    }

    public boolean isSchemeDefault() {
        return schemeDefault;
    }

    public void setSchemeDefault(boolean schemeDefault) {
        this.schemeDefault = schemeDefault;
    }

    public String getCpdFileName() {
        return cpdFileName;
    }

    public void setCpdFileName(String cpdFileName) {
        this.cpdFileName = cpdFileName;
        setAlteredCpd(false);
    }


    public String getDescriptiveCpdFileName() {
        if (getCpdFileName() == null) {
            return null;
        }

        String alteredString = (isAlteredCpd()) ? "*" : "";
        return getCpdFileName() + alteredString;
    }


    public String getDescriptiveColorSchemeName(double min, double max, boolean logScaled) {
        if (getSchemeName() == null) {
            return null;
        }


        String alteredString = (isAlteredScheme(min, max, logScaled)) ? "*" : "";
        if (isSchemeDefault()) {
            return "Default '" + getSchemeName() + "'" + alteredString;
        } else {
            return getSchemeName() + alteredString;
        }
    }




    public boolean isAlteredScheme(double min, double max, boolean logScaled) {
       return (isAlteredScheme() || logScaled != isLogScaled || min != getColorBarMin() || max != getColorBarMax() || getSchemeName() == null);
    }

    public boolean isAlteredCpd() {
        return alteredCpd;
    }


    public void setAlteredCpd(boolean alteredCpd) {
        this.alteredCpd = alteredCpd;
//        if (alteredCpd && getSchemeName() != null) {
//            setAlteredScheme(alteredCpd);
//        }
    }

    public boolean isAlteredScheme() {
        return alteredScheme;
    }

    public void setAlteredScheme(boolean alteredColorScheme) {
        this.alteredScheme = alteredColorScheme;
    }

    public boolean isPaletteInitialized() {
        return paletteInitialized;
    }

    public void setPaletteInitialized(boolean paletteInitialized) {
        this.paletteInitialized = paletteInitialized;
    }

    public String getColorBarLabels() {
        return colorBarLabels;
    }

    public void setColorBarLabels(String colorBarLabels) {
        this.colorBarLabels = colorBarLabels;
    }

    public double getColorBarMin() {
        return colorBarMin;
    }

    public void setColorBarMin(double colorBarMin) {
        this.colorBarMin = colorBarMin;
    }

    public double getColorBarMax() {
        return colorBarMax;
    }

    public void setColorBarMax(double colorBarMax) {
        this.colorBarMax = colorBarMax;
    }

    public String getColorBarTitle() {
        return colorBarTitle;
    }

    public void setColorBarTitle(String colorBarTitle) {
        this.colorBarTitle = colorBarTitle;
    }

    public boolean isLogScaled() {
        return isLogScaled;
    }

    public void setLogScaled(boolean logScaled) {
        isLogScaled = logScaled;
    }

    public boolean isColorBarInitialized() {
        return colorBarInitialized;
    }

    public void setColorBarInitialized(boolean colorBarInitialized) {
        this.colorBarInitialized = colorBarInitialized;
    }
}
