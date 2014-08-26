package org.esa.beam.framework.datamodel;

/**
 * Created by danielknowles on 8/26/14.
 */
public class ColorPaletteSourcesInfo {

    private String colorPaletteSchemeName = null;
    private boolean colorPaletteSchemeDefaultList = true;
    private String cpdFileName = null;

    private static final String ALTERED_STRING = "* (altered)";

    ColorPaletteSourcesInfo() {

    }

    public String getColorPaletteSchemeName() {
        return colorPaletteSchemeName;
    }

    public void setColorPaletteSchemeName(String colorPaletteSchemeName) {
        this.colorPaletteSchemeName = colorPaletteSchemeName;
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
    }

    public void setCpdFileNameAsAltered() {
        if (getCpdFileName() != null && !getCpdFileName().endsWith(ALTERED_STRING)) {
            setCpdFileName(getCpdFileName() + ALTERED_STRING);
        }

    }
}
