package org.esa.beam.framework.datamodel;

/**
 * Created by danielknowles on 8/26/14.
 */
public class ColorPaletteSourcesInfo {

    private String colorPaletteSchemeName = null;
    private boolean alteredColorScheme = false;

    private String cpdFileName = null;
    private boolean alteredCpd = false;

    private boolean colorPaletteSchemeDefaultList = true;



    private static final String ALTERED_STRING = "(Altered) ";

    ColorPaletteSourcesInfo() {

    }

    public String getColorPaletteSchemeName() {
        return colorPaletteSchemeName;
    }

    public void setColorPaletteSchemeName(String colorPaletteSchemeName) {
        this.colorPaletteSchemeName = colorPaletteSchemeName;
        setAlteredColorScheme(false);
    }

    public boolean isColorPaletteSchemeDefaultList() {
        return colorPaletteSchemeDefaultList;
    }

    public void setColorPaletteSchemeDefaultList(boolean colorPaletteSchemeDefaultList) {
        this.colorPaletteSchemeDefaultList = colorPaletteSchemeDefaultList;
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

        String alteredString = (isAlteredCpd()) ? ALTERED_STRING : "";
        return alteredString + getCpdFileName();
    }


    public String getDescriptiveColorSchemeName() {
        if (getColorPaletteSchemeName() == null) {
            return null;
        }

        String alteredString = (isAlteredColorScheme()) ? ALTERED_STRING : "";
        if (isColorPaletteSchemeDefaultList()) {
            return alteredString + "'" + getColorPaletteSchemeName() + "' Default";
        } else {
            return alteredString + getColorPaletteSchemeName();
        }
    }


    public boolean isAlteredCpd() {
        return alteredCpd;
    }

    public void setAlteredCpd(boolean alteredCpd) {
        this.alteredCpd = alteredCpd;
        if (alteredCpd && getColorPaletteSchemeName() != null) {
            setAlteredColorScheme(alteredCpd);
        }
    }

    public boolean isAlteredColorScheme() {
        return alteredColorScheme;
    }

    public void setAlteredColorScheme(boolean alteredColorScheme) {
        this.alteredColorScheme = alteredColorScheme;
    }
}
