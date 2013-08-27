package org.esa.beam.binning.support;

import org.esa.beam.util.StringUtils;
import org.esa.beam.util.io.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marco Peters
 */
public class GaussianGridConfig {

    private static final int[] ALLOWED_ROW_COUNTS = new int[]{32, 48, 80, 128, 160, 200, 256, 320, 400, 512, 640};

    private int[] reducedColumnCount;
    private int[] reducedFirstBinIndexes;
    private double[] regularLongitudePoints;
    private List<double[]> reducedLongitudePoints;
    private int regularColumnCount;
    private double[] latitudePoints;

    public static GaussianGridConfig load(int rowCount) throws IOException {
        if (Arrays.binarySearch(ALLOWED_ROW_COUNTS, rowCount) < 0) {
            String msg = String.format("Invalid rowCount. Must be one of {%s}, but is %d",
                                       StringUtils.arrayToCsv(ALLOWED_ROW_COUNTS), rowCount);
            throw new IllegalArgumentException(msg);
        }
        int numRecords = rowCount * 2;
        int regularColumnCount = rowCount * 4;
        int[] reducedColumnCount = new int[numRecords];
        double[] latitudePoints = new double[numRecords];
        int[] reducedFirstBinIndexes = new int[numRecords];
        readGridConfig(rowCount, numRecords, reducedColumnCount, latitudePoints, reducedFirstBinIndexes);

        GaussianGridConfig config = new GaussianGridConfig();
        config.regularColumnCount = regularColumnCount;
        config.regularLongitudePoints = computeLongitudePoints(regularColumnCount);
        config.reducedLongitudePoints = new ArrayList<double[]>(numRecords);
        for (int i = 0; i < numRecords; i++) {
            double[] longitudePointsInRow = computeLongitudePoints(reducedColumnCount[i]);
            config.reducedLongitudePoints.add(i, longitudePointsInRow);
        }
        config.reducedColumnCount = reducedColumnCount;
        config.reducedFirstBinIndexes = reducedFirstBinIndexes;
        config.latitudePoints = latitudePoints;


        return config;
    }

    private GaussianGridConfig() {
    }

    public int getRegularColumnCount() {
        return regularColumnCount;
    }

    public double[] getRegularLongitudePoints() {
        return regularLongitudePoints;
    }

    public int getReducedColumnCount(int rowIndex) {
        return reducedColumnCount[rowIndex];
    }

    public double[] getReducedLongitudePoints(int rowIndex) {
        return reducedLongitudePoints.get(rowIndex);
    }

    public int getReducedFirstBinIndex(int rowIndex) {
        return reducedFirstBinIndexes[rowIndex];
    }

    public double getLatitude(int row) {
        return latitudePoints[row];
    }

    public double[] getLatitudePoints() {
        return latitudePoints;
    }

    static double[] computeLongitudePoints(int columnCount) {
        double[] longitudePoints = new double[columnCount];
        for (int i = 0; i < longitudePoints.length; i++) {
            longitudePoints[i] = 360.0 * ((i + 0.5) / columnCount) - 180.0;
        }
        return longitudePoints;
    }

    private static void readGridConfig(int rowCount, int numRecords, int[] reducedColumnCount, double[] latitudePoints,
                                       int[] reducedFirstBinIndexes) throws IOException {
        InputStream is = GaussianGridConfig.class.getResourceAsStream(String.format("N%d.txt", rowCount));
        CsvReader csvReader = new CsvReader(new InputStreamReader(is), new char[]{'\t'}, true, "#");
        reducedFirstBinIndexes[0] = 0;
        try {
            for (int i = 0; i < numRecords; i++) {
                String[] record = csvReader.readRecord();
                reducedColumnCount[i] = Integer.parseInt(record[0]);
                latitudePoints[i] = Double.parseDouble(record[2]);
                if (i > 0) {
                    reducedFirstBinIndexes[i] = reducedFirstBinIndexes[i - 1] + reducedColumnCount[i - 1];
                }
            }
        } finally {
            csvReader.close();
        }
    }

}
