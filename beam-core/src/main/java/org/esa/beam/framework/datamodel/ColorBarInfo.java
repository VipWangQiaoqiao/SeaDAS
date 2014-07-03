package org.esa.beam.framework.datamodel;

import java.text.DecimalFormat;

/**
 * Created by danielknowles on 6/27/14.
 */
public class ColorBarInfo {

    private double value;
    private double locationWeight;
    private String formattedValue;
    private int decimalPlaces;


    public ColorBarInfo(double value, double locationWeight, int decimalPlaces) {
        setValue(value);
        setLocationWeight(locationWeight);
        setDecimalPlaces(decimalPlaces);
        setFormattedValue();
    }

    public ColorBarInfo(double value, double locationWeight, String formattedValue) {
        setValue(value);
        setLocationWeight(locationWeight);
        setFormattedValue(formattedValue);
    }
    public double getValue() {
        return value;
    }

    private void setValue(double value) {
        this.value = value;
    }

    public double getLocationWeight() {
        return locationWeight;
    }

    private void setLocationWeight(double locationWeight) {
        this.locationWeight = locationWeight;
    }

    public String getFormattedValue() {
        return formattedValue;
    }


    private void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    private void setFormattedValue() {
        StringBuilder decimalFormatStringBuilder = new StringBuilder("0");
        for (int j = 0; j < getDecimalPlaces(); j++) {
            if (j == 0) {
                decimalFormatStringBuilder.append(".");
            }
            decimalFormatStringBuilder.append("0");

        }

        this.formattedValue = new DecimalFormat(decimalFormatStringBuilder.toString()).format(getValue()).toString();
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    private void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
}