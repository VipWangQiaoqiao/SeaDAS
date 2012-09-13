package org.esa.beam.watermask.ui;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/5/12
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResolutionInfo {


    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static enum Unit {
        METER,
        KILOMETER,
    }

    public HashMap<Unit, String> unitStrings = new HashMap<Unit, String>();

    private int resolution;
    private Unit unit;
    private String description;

    ResolutionInfo(int resolution) {
        this(Unit.METER, resolution);
    }

    ResolutionInfo(Unit unit, int resolution) {
        this.setUnit(unit);
        this.setResolution(resolution);

        initUnitStrings();
    }

    private void initUnitStrings() {
        unitStrings.put(Unit.METER, "m");
        unitStrings.put(Unit.KILOMETER, "km");
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Integer.toString(getResolution()));
        stringBuilder.append(" ");
        stringBuilder.append(unitStrings.get(getUnit()));

        return stringBuilder.toString();
    }
}
