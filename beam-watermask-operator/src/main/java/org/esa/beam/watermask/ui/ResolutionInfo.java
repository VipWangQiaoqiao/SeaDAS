package org.esa.beam.watermask.ui;

import org.esa.beam.watermask.operator.WatermaskClassifier;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 9/5/12
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResolutionInfo {


    public enum Unit {
        METER("m"),
        KILOMETER("km");

        private Unit(String name) {
            this.name = name;
        }

        private final String name;

        public String toString() {
            return name;
        }
    }


    private int resolution;
    private Unit unit;
    private String description;
    private WatermaskClassifier.Mode mode;
    private boolean enabled;


    public ResolutionInfo(int resolution, Unit unit, WatermaskClassifier.Mode mode) {
        setUnit(unit);
        setResolution(resolution);
        setMode(mode);
        setDescription();
        setEnabled(false);
    }


    public int getResolution() {
        return resolution;
    }

    public int getResolution(Unit unit) {
        if (unit == getUnit()) {
            return resolution;
        } else if (unit == Unit.METER && getUnit() == Unit.KILOMETER) {
            return resolution * 1000;
        } else if (unit == Unit.KILOMETER && getUnit() == Unit.METER) {
            float x = resolution / 1000;
            return Math.round(x);
        }

        return resolution;
    }

    private void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public Unit getUnit() {
        return unit;
    }

    private void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription() {

        String core = "Uses the " + Integer.toString(getResolution()) + " " + getUnit().toString() +
                " dataset obtained from the<br> "
                + getMode().getDescription();

        if (isEnabled()) {
            this.description = "<html>" + core + "</html>";
        } else {
            this.description = "<html>" + core + "<br> NOTE: this file is not currently installed -- see help</html>";
        }
    }


    public WatermaskClassifier.Mode getMode() {
        return mode;
    }

    private void setMode(WatermaskClassifier.Mode mode) {
        this.mode = mode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setDescription();
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

//        StringBuilder resolutionStringBuilder = new StringBuilder(Integer.toString(getResolution()));
//
//        while (resolutionStringBuilder.length() < 5) {
//            resolutionStringBuilder.insert(0, " ");
//        }
//
//        stringBuilder.append(resolutionStringBuilder.toString());

        stringBuilder.append(Integer.toString(getResolution()));
        stringBuilder.append(" ");
        stringBuilder.append(getUnit().toString());
        stringBuilder.append(" (");
        stringBuilder.append(getMode().toString());
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
