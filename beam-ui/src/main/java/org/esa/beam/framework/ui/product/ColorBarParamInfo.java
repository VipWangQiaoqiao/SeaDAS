package org.esa.beam.framework.ui.product;

import org.esa.beam.framework.datamodel.ImageLegend;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 8/8/14
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColorBarParamInfo {

    public static final String HORIZONTAL_STR = "Horizontal";
    public static final String VERTICAL_STR = "Vertical";


    public static final int DEFAULT_LABELS_FONT_SIZE = 28;
    public static final Boolean DEFAULT_BACKGROUND_TRANSPARENCY_ENABLED = Boolean.TRUE;
    public static final Boolean DEFAULT_SHOW_TITLE_ENABLED = Boolean.TRUE;
    public static final String DEFAULT_TITLE = "";
    public static final String DEFAULT_TITLE_UNITS = "";
    public static final int DEFAULT_TITLE_FONT_SIZE = 36;
    public static final int DEFAULT_TITLE_UNITS_FONT_SIZE = 28;
    public static final double DEFAULT_SCALING_FACTOR = 1.0;
    public static final int DEFAULT_COLOR_BAR_LENGTH = 1200;
    public static final int DEFAULT_COLOR_BAR_THICKNESS = 48;
    public static final double DEFAULT_LAYER_SCALING = 75;
    public static final Boolean DEFAULT_CENTER_ON_LAYER = Boolean.TRUE;
    public static final String DEFAULT_MANUAL_POINTS = "";
    public static final String DEFAULT_ORIENTATION = HORIZONTAL_STR;
    public static final String DEFAULT_DISTRIBUTION_TYPE = ImageLegend.DISTRIB_EVEN_STR;
    public static final int DEFAULT_NUM_TICK_MARKS = 8;
    public static final int DEFAULT_DECIMAL_PLACES = 2;
    public static final Color DEFAULT_FOREGROUND_COLOR = Color.black;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.white;





    private int labelsFontSize = DEFAULT_LABELS_FONT_SIZE;
    private Boolean backgroundTransparencyEnabled = DEFAULT_BACKGROUND_TRANSPARENCY_ENABLED;
    private Boolean showTitle = DEFAULT_SHOW_TITLE_ENABLED;
    private String title = DEFAULT_TITLE;
    private String titleUnits = DEFAULT_TITLE_UNITS;
    private int titleFontSize = DEFAULT_TITLE_FONT_SIZE;
    private int titleUnitsFontSize = DEFAULT_TITLE_UNITS_FONT_SIZE;
    private double scalingFactor = DEFAULT_SCALING_FACTOR;
    private int colorBarLength = DEFAULT_COLOR_BAR_LENGTH;
    private int colorBarThickness = DEFAULT_COLOR_BAR_THICKNESS;
    private double layerScaling = DEFAULT_LAYER_SCALING;
    private Boolean centerOnLayer = DEFAULT_CENTER_ON_LAYER;
    private String manualPoints = DEFAULT_MANUAL_POINTS;
    private String orientation = DEFAULT_ORIENTATION;
    private String distributionType = DEFAULT_DISTRIBUTION_TYPE;
    private int numTickMarks = DEFAULT_NUM_TICK_MARKS;
    private int decimalPlaces = DEFAULT_DECIMAL_PLACES;
    private Color foregroundColor = DEFAULT_FOREGROUND_COLOR;
    private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;



    private boolean titleModified = false;



    public ColorBarParamInfo() {

    }

    public int getLabelsFontSize() {
        return labelsFontSize;
    }

    public void setLabelsFontSize(int labelsFontSize) {
        this.labelsFontSize = labelsFontSize;
    }

    public boolean isTitleModified() {
        return titleModified;
    }

    public void setTitleModified(boolean titleModified) {
        this.titleModified = titleModified;
    }

    public Boolean getBackgroundTransparencyEnabled() {
        return backgroundTransparencyEnabled;
    }

    public void setBackgroundTransparencyEnabled(Boolean backgroundTransparencyEnabled) {
        this.backgroundTransparencyEnabled = backgroundTransparencyEnabled;
    }

    public Boolean getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(Boolean showTitle) {
        this.showTitle = showTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleUnits() {
        return titleUnits;
    }

    public void setTitleUnits(String titleUnits) {
        this.titleUnits = titleUnits;
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public int getTitleUnitsFontSize() {
        return titleUnitsFontSize;
    }

    public void setTitleUnitsFontSize(int titleUnitsFontSize) {
        this.titleUnitsFontSize = titleUnitsFontSize;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    public int getColorBarLength() {
        return colorBarLength;
    }

    public void setColorBarLength(int colorBarLength) {
        this.colorBarLength = colorBarLength;
    }

    public int getColorBarThickness() {
        return colorBarThickness;
    }

    public void setColorBarThickness(int colorBarThickness) {
        this.colorBarThickness = colorBarThickness;
    }

    public double getLayerScaling() {
        return layerScaling;
    }

    public void setLayerScaling(double layerScaling) {
        this.layerScaling = layerScaling;
    }

    public Boolean getCenterOnLayer() {
        return centerOnLayer;
    }

    public void setCenterOnLayer(Boolean centerOnLayer) {
        this.centerOnLayer = centerOnLayer;
    }

    public String getManualPoints() {
        return manualPoints;
    }

    public void setManualPoints(String manualPoints) {
        this.manualPoints = manualPoints;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(String distributionType) {
        this.distributionType = distributionType;
    }

    public int getNumTickMarks() {
        return numTickMarks;
    }

    public void setNumTickMarks(int numTickMarks) {
        this.numTickMarks = numTickMarks;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
