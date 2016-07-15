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

        //todo look into this but it works badly for decimal places of 1 or 0 (default behavior is dynamic anyway)
        if (getDecimalPlaces() < 2) {
            setDecimalPlaces(2);
        }

        if (getDecimalPlaces() > 0) {

            double actualDecimalPlaces = getDecimalPlaces();
            double minValue = 1;
            for (int j = 0; j < getDecimalPlaces(); j++) {
                if (j > 0) {
                    minValue = minValue / 10.0;
                }
            }

            // handle smaller numbers
            while ((getValue()) < minValue) {
                minValue = minValue / 10.0;
                actualDecimalPlaces++;
            }

            //set max decimal places
            for (int j = 0; j < actualDecimalPlaces; j++) {
                if (j == 0) {
                    decimalFormatStringBuilder.append(".");
                }
                decimalFormatStringBuilder.append("0");

            }

            String formattedDecimalValue = new DecimalFormat(decimalFormatStringBuilder.toString()).format(getValue()).toString();


            // trim off trailing zeros
            while (formattedDecimalValue.length() > 0 && formattedDecimalValue.endsWith("0")) {
                formattedDecimalValue = formattedDecimalValue.substring(0, formattedDecimalValue.length() - 1);
            }

            // trim of period in the case of an integer
            if (formattedDecimalValue.length() > 0 && formattedDecimalValue.endsWith(".")) {
                formattedDecimalValue = formattedDecimalValue.substring(0, formattedDecimalValue.length() - 1);
            }


            this.formattedValue = formattedDecimalValue;
        } else {
            String formattedDecimalValue = new DecimalFormat(decimalFormatStringBuilder.toString()).format(getValue()).toString();
            this.formattedValue = formattedDecimalValue;
        }
    }


    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    private void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
}