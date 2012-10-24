package org.esa.beam.watermask.ui;

import org.esa.beam.watermask.operator.WatermaskClassifier;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/4/12
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
class LandMasksData {

    private boolean createMasks = false;
    private boolean deleteMasks = false;

    private int superSampling = 1;


    private double landMaskTransparency = 0.0;
    private double waterMaskTransparency = 0.5;
    private double coastlineMaskTransparency = 0.0;

    private boolean showLandMaskAllBands = false;
    private boolean showWaterMaskAllBands = false;
    private boolean showCoastlineMaskAllBands = true;

    private Color landMaskColor = new Color(100, 49, 12);
    private Color waterMaskColor = new Color(0, 0, 255);
    private Color coastlineMaskColor = new Color(192, 192, 192);


    private String waterFractionBandName = "mask_data_water_fraction";
    private String waterFractionSmoothedName = "mask_data_water_fraction_smoothed";

    private String landMaskName = "LandMask";
    private String landMaskMath = getWaterFractionBandName() + " == 0";
    private String landMaskDescription = "Land pixels";


    private String coastlineMaskName = "CoastLine";
    private String coastlineMath = getWaterFractionSmoothedName() + " > 25 and " + getWaterFractionSmoothedName() + " < 75";
    private String coastlineMaskDescription = "Coastline pixels";


    private String waterMaskName = "WaterMask";
    private String waterMaskMath = getWaterFractionBandName() + " > 0";
    private String waterMaskDescription = "Water pixels";


    private ArrayList<SourceFileInfo> sourceFileInfos = new ArrayList<SourceFileInfo>();
    private SourceFileInfo sourceFileInfo;

    public LandMasksData() {

        SourceFileInfo sourceFileInfo;

        sourceFileInfo = new SourceFileInfo(WatermaskClassifier.RESOLUTION_50m,
                SourceFileInfo.Unit.METER,
                WatermaskClassifier.Mode.SRTM_GC,
                WatermaskClassifier.FILENAME_SRTM_GC_50m);
        getSourceFileInfos().add(sourceFileInfo);

        sourceFileInfo = new SourceFileInfo(WatermaskClassifier.RESOLUTION_150m,
                SourceFileInfo.Unit.METER,
                WatermaskClassifier.Mode.SRTM_GC,
                WatermaskClassifier.FILENAME_SRTM_GC_150m);
        getSourceFileInfos().add(sourceFileInfo);

        sourceFileInfo = new SourceFileInfo(WatermaskClassifier.RESOLUTION_250m,
                SourceFileInfo.Unit.METER,
                WatermaskClassifier.Mode.GSHHS,
                WatermaskClassifier.FILENAME_GSHHS_250m);
        getSourceFileInfos().add(sourceFileInfo);

        sourceFileInfo = new SourceFileInfo(WatermaskClassifier.RESOLUTION_500m,
                SourceFileInfo.Unit.METER,
                WatermaskClassifier.Mode.GSHHS,
                WatermaskClassifier.FILENAME_GSHHS_500m);
        getSourceFileInfos().add(sourceFileInfo);

        sourceFileInfo = new SourceFileInfo(WatermaskClassifier.RESOLUTION_1km,
                SourceFileInfo.Unit.METER,
                WatermaskClassifier.Mode.GSHHS,
                WatermaskClassifier.FILENAME_GSHHS_1km);
        getSourceFileInfos().add(sourceFileInfo);
        // set the default
        this.sourceFileInfo = sourceFileInfo;

        sourceFileInfo = new SourceFileInfo(WatermaskClassifier.RESOLUTION_10km,
                SourceFileInfo.Unit.METER,
                WatermaskClassifier.Mode.GSHHS,
                WatermaskClassifier.FILENAME_GSHHS_10km);
        getSourceFileInfos().add(sourceFileInfo);

    }


    public boolean isCreateMasks() {
        return createMasks;
    }

    public void setCreateMasks(boolean closeClicked) {
        this.createMasks = closeClicked;
    }

    public double getLandMaskTransparency() {
        return landMaskTransparency;
    }

    public void setLandMaskTransparency(double landMaskTransparency) {
        this.landMaskTransparency = landMaskTransparency;
    }

    public double getWaterMaskTransparency() {
        return waterMaskTransparency;
    }

    public void setWaterMaskTransparency(double waterMaskTransparency) {
        this.waterMaskTransparency = waterMaskTransparency;
    }

    public double getCoastlineMaskTransparency() {
        return coastlineMaskTransparency;
    }

    public void setCoastlineMaskTransparency(double coastlineMaskTransparency) {
        this.coastlineMaskTransparency = coastlineMaskTransparency;
    }

    public boolean isShowLandMaskAllBands() {
        return showLandMaskAllBands;
    }

    public void setShowLandMaskAllBands(boolean showLandMaskAllBands) {
        this.showLandMaskAllBands = showLandMaskAllBands;
    }

    public boolean isShowWaterMaskAllBands() {
        return showWaterMaskAllBands;
    }

    public void setShowWaterMaskAllBands(boolean showWaterMaskAllBands) {
        this.showWaterMaskAllBands = showWaterMaskAllBands;
    }

    public boolean isShowCoastlineMaskAllBands() {
        return showCoastlineMaskAllBands;
    }

    public void setShowCoastlineMaskAllBands(boolean showCoastlineMaskAllBands) {
        this.showCoastlineMaskAllBands = showCoastlineMaskAllBands;
    }

    public Color getLandMaskColor() {
        return landMaskColor;
    }

    public void setLandMaskColor(Color landMaskColor) {
        this.landMaskColor = landMaskColor;
    }

    public Color getWaterMaskColor() {
        return waterMaskColor;
    }

    public void setWaterMaskColor(Color waterMaskColor) {
        this.waterMaskColor = waterMaskColor;
    }

    public Color getCoastlineMaskColor() {
        return coastlineMaskColor;
    }

    public void setCoastlineMaskColor(Color coastlineMaskColor) {
        this.coastlineMaskColor = coastlineMaskColor;
    }

    public int getSuperSampling() {
        return superSampling;
    }

    public void setSuperSampling(int superSampling) {
        this.superSampling = superSampling;
    }

    public SourceFileInfo getSourceFileInfo() {
        return sourceFileInfo;
    }

    public void setSourceFileInfo(SourceFileInfo resolution) {
        this.sourceFileInfo = resolution;
    }

    public String getWaterFractionBandName() {
        return waterFractionBandName;
    }

    public void setWaterFractionBandName(String waterFractionBandName) {
        this.waterFractionBandName = waterFractionBandName;
    }

    public String getWaterFractionSmoothedName() {
        return waterFractionSmoothedName;
    }

    public void setWaterFractionSmoothedName(String waterFractionSmoothedName) {
        this.waterFractionSmoothedName = waterFractionSmoothedName;
    }

    public String getLandMaskName() {
        return landMaskName;
    }

    public void setLandMaskName(String landMaskName) {
        this.landMaskName = landMaskName;
    }

    public String getLandMaskMath() {
        return landMaskMath;
    }

    public void setLandMaskMath(String landMaskMath) {
        this.landMaskMath = landMaskMath;
    }

    public String getLandMaskDescription() {
        return landMaskDescription;
    }

    public void setLandMaskDescription(String landMaskDescription) {
        this.landMaskDescription = landMaskDescription;
    }

    public String getCoastlineMaskName() {
        return coastlineMaskName;
    }

    public void setCoastlineMaskName(String coastlineMaskName) {
        this.coastlineMaskName = coastlineMaskName;
    }

    public String getCoastlineMath() {
        return coastlineMath;
    }

    public void setCoastlineMath(String coastlineMath) {
        this.coastlineMath = coastlineMath;
    }

    public String getCoastlineMaskDescription() {
        return coastlineMaskDescription;
    }

    public void setCoastlineMaskDescription(String coastlineMaskDescription) {
        this.coastlineMaskDescription = coastlineMaskDescription;
    }

    public String getWaterMaskName() {
        return waterMaskName;
    }

    public void setWaterMaskName(String waterMaskName) {
        this.waterMaskName = waterMaskName;
    }

    public String getWaterMaskMath() {
        return waterMaskMath;
    }

    public void setWaterMaskMath(String waterMaskMath) {
        this.waterMaskMath = waterMaskMath;
    }

    public String getWaterMaskDescription() {
        return waterMaskDescription;
    }

    public void setWaterMaskDescription(String waterMaskDescription) {
        this.waterMaskDescription = waterMaskDescription;
    }

    public boolean isDeleteMasks() {
        return deleteMasks;
    }

    public void setDeleteMasks(boolean deleteMasks) {
        this.deleteMasks = deleteMasks;
    }

    public ArrayList<SourceFileInfo> getSourceFileInfos() {
        return sourceFileInfos;
    }

    public void setSourceFileInfos(ArrayList<SourceFileInfo> sourceFileInfos) {
        this.sourceFileInfos = sourceFileInfos;
    }
}


