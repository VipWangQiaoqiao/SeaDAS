package org.esa.beam.dataio.aviris;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the information encoded in a AVIRIS filename.
 */
class AvirisFilename {

//    fmmyyddtnnpnnrnnrdn_v_
//    f140507t01p00r14rdn_e.
//f:     Airborne Flight.
//yy:    The year of the airborne flight run (i.e. 10 represents 2010).
//mm:    The month of the airborne flight run (i.e. 05 represents May).
//dd:    The day of the airborne flight run (i.e. 22 represents 22nd day of the month).
//tnn:   Legacy naming convention when AVIRIS used to record data onto VLDS tapes.
//pnn:   Power cycle number (static number of 00).
//rnn:   Airborne flight run number.
//v:     Radiance software version number
    private static final Pattern PARTS_PATTERN = Pattern.compile(
            "f(\\d{6})" +
            "(t\\d{2})" +
            "(p\\d{2})" +
            "(r\\d{2})" +
            "rdn_(.)_" +
            "(.*)\\.hdr");

    private final String processingLevel;
    private final String flightDate;
    private final String legacyName;
    private final String powerCycle;
    private final String flightRun;
    private final String radianceSoftwareVersion;
    private final String fileType;
    private final String productBase;

    AvirisFilename(String processingLevel, String flightDate, String legacyName,
                 String powerCycle, String flightRun, 
                 String radianceSoftwareVersion, String fileType, String productBase) {

        this.processingLevel = processingLevel;
        this.flightDate = flightDate;
        this.legacyName = legacyName;
        this.powerCycle = powerCycle;
        this.flightRun = flightRun;
        this.radianceSoftwareVersion = radianceSoftwareVersion;
        this.productBase = productBase;
        this.fileType = fileType;
    }

    public String getProcessingLevel() {
        return processingLevel;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public String getLegacyName() {
        return legacyName;
    }

    public String getPowerCycle() {
        return powerCycle;
    }

    public String getFlightRun() {
        return flightRun;
    }

    public String getRadianceSoftwareVersion() {
        return radianceSoftwareVersion;
    }

    public String getFileType() {
        return fileType;
    }

    public String getProductBase() {
        return productBase;
    }


    @Override
    public String toString() {
        return "AvirisFilename{" +
                "flightDate=" + flightDate  +
                ", legacyName=" + legacyName+ '\'' +
                ", powerCycle=" + powerCycle+ '\'' +
                ", flightRun=" + flightRun+ '\'' +
                ", radianceSoftwareVersion =" + radianceSoftwareVersion+ '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }


    static AvirisFilename create(String filename) {
        Matcher partMatcher = PARTS_PATTERN.matcher(filename);
        if (partMatcher.matches()) {
            String flightDate = partMatcher.group(1);
            String legacyName = partMatcher.group(2);
            String powerCycle = partMatcher.group(3);
            String flightRun = partMatcher.group(4);
            String radianceSoftwareVersion = partMatcher.group(5);
            String fileType = partMatcher.group(6);
            String processingLevel = "L1B";

            String productBase = "f" + flightDate + legacyName + powerCycle
                    + flightRun + "rdn_" + radianceSoftwareVersion + "_";

            return new AvirisFilename(processingLevel, flightDate, legacyName,
                    powerCycle, flightRun, radianceSoftwareVersion, fileType, productBase);

        }
        return null;
    }

    public File[] findHdrs(File parentFile) {
        File[] hdrFiles = parentFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                boolean hdrExist = name.startsWith(getProductBase()) && name.endsWith(".hdr");
                boolean dataExist = false;
                if (hdrExist) {
                    int hdrIndex = name.lastIndexOf(".hdr");
                    String bandBaseName = name.substring(0, hdrIndex);
                    File dataFile = new File(dir,bandBaseName);
                    dataExist = dataFile.exists();
                }
                return hdrExist && dataExist;
            }
        });
        if (hdrFiles == null) {
            hdrFiles = new File[0];
        }
        return hdrFiles;
    }
}
