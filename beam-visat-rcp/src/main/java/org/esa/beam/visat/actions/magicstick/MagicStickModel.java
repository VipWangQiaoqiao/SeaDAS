package org.esa.beam.visat.actions.magicstick;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.dataop.barithm.BandArithmetic;
import org.esa.beam.util.ObjectUtils;
import org.esa.beam.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for the magic stick tool (interactor).
 *
 * @author Norman Fomferra
 * @since BEAM 4.10
 */
public class MagicStickModel implements Cloneable {

    public enum Mode {
        SINGLE,
        PLUS,
        MINUS,
    }

    public enum Operator {
        INTEGRAL,
        IDENTITY,
        DERIVATIVE,
    }

    public enum Method {
        DISTANCE,
        AVERAGE,
        LIMITS,
    }

    static final String MAGIC_STICK_MASK_NAME = "magic_stick";

    private double tolerance;
    private double minTolerance;
    private double maxTolerance;
    private Operator operator;
    private Method method;
    private boolean normalize;
    private Mode mode;
    private ArrayList<double[]> plusSpectra;
    private ArrayList<double[]> minusSpectra;

    public MagicStickModel() {
        mode = Mode.SINGLE;
        method = Method.DISTANCE;
        operator = Operator.IDENTITY;
        plusSpectra = new ArrayList<double[]>();
        minusSpectra = new ArrayList<double[]>();
        tolerance = 0.1;
        minTolerance = 0.0;
        maxTolerance = 1.0;
    }

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    @Override
    public MagicStickModel clone() {
        try {
            MagicStickModel clone = (MagicStickModel) super.clone();
            clone.plusSpectra = new ArrayList<double[]>(plusSpectra);
            clone.minusSpectra = new ArrayList<double[]>(minusSpectra);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public void set(MagicStickModel other) {
        method = other.method;
        operator = other.operator;
        mode = other.mode;
        tolerance = other.tolerance;
        minTolerance = other.minTolerance;
        maxTolerance = other.maxTolerance;
        plusSpectra = new ArrayList<double[]>(other.plusSpectra);
        minusSpectra = new ArrayList<double[]>(other.minusSpectra);
    }

    void addSpectrum(double... spectrum) {
        if (mode == Mode.SINGLE) {
            plusSpectra.clear();
            minusSpectra.clear();
            plusSpectra.add(spectrum);
        } else if (mode == Mode.PLUS) {
            plusSpectra.add(spectrum);
        } else if (mode == Mode.MINUS) {
            minusSpectra.add(spectrum);
        }
    }

    void clearSpectra() {
        plusSpectra.clear();
        minusSpectra.clear();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Method getMethod() {
        return method;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public double getMinTolerance() {
        return minTolerance;
    }

    public void setMinTolerance(double minTolerance) {
        this.minTolerance = minTolerance;
    }

    public double getMaxTolerance() {
        return maxTolerance;
    }

    public void setMaxTolerance(double maxTolerance) {
        this.maxTolerance = maxTolerance;
    }


    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public List<double[]> getPlusSpectra() {
        return plusSpectra;
    }

    void setPlusSpectra(List<double[]> plusSpectra) {
        this.plusSpectra = new ArrayList<double[]>(plusSpectra);
    }

    public List<double[]> getMinusSpectra() {
        return minusSpectra;
    }

    void setMinusSpectra(List<double[]> minusSpectra) {
        this.minusSpectra = new ArrayList<double[]>(minusSpectra);
    }

    static Band[] getSpectralBands(Product product) {
        final Band[] bands = product.getBands();
        final ArrayList<Band> spectralBands = new ArrayList<Band>(bands.length);
        for (Band band : bands) {
            if (band.getSpectralWavelength() > 0.0) {
                spectralBands.add(band);
            }
        }
        return spectralBands.toArray(new Band[spectralBands.size()]);
    }

    static void setMagicStickMask(Product product, String expression) {
        final Mask magicStickMask = product.getMaskGroup().get(MAGIC_STICK_MASK_NAME);
        if (magicStickMask != null) {
            magicStickMask.getImageConfig().setValue("expression", expression);
        } else {
            final int width = product.getSceneRasterWidth();
            final int height = product.getSceneRasterHeight();
            product.getMaskGroup().add(Mask.BandMathsType.create(MAGIC_STICK_MASK_NAME,
                                                                 "Magic stick mask",
                                                                 width, height,
                                                                 expression,
                                                                 Color.RED, 0.5));
        }
    }

    String createExpression(Band... spectralBands) {
        final String plusPart;
        final String minusPart;
        if (getMethod() == Method.DISTANCE) {
            plusPart = getDistancePart(spectralBands, operator, plusSpectra, tolerance, normalize);
            minusPart = getDistancePart(spectralBands, operator, minusSpectra, tolerance, normalize);
        } else if (getMethod() == Method.AVERAGE) {
            plusPart = getAveragePart(spectralBands, operator, plusSpectra, tolerance, normalize);
            minusPart = getAveragePart(spectralBands, operator, minusSpectra, tolerance, normalize);
        } else if (getMethod() == Method.LIMITS) {
            plusPart = getLimitsPart(spectralBands, operator, plusSpectra, tolerance, normalize);
            minusPart = getLimitsPart(spectralBands, operator, minusSpectra, tolerance, normalize);
        } else {
            throw new IllegalStateException("Unhandled method " + getMethod());
        }
        if (plusPart != null && minusPart != null) {
            return String.format("(%s) && !(%s)", plusPart, minusPart);
        } else if (plusPart != null) {
            return plusPart;
        } else if (minusPart != null) {
            return String.format("!(%s)", minusPart);
        } else {
            return "0";
        }
    }

    private static String getDistancePart(Band[] bands, Operator operator, List<double[]> spectra, double tolerance, boolean normalize) {
        if (spectra.isEmpty()) {
            return null;
        }
        final StringBuilder part = new StringBuilder();
        for (int i = 0; i < spectra.size(); i++) {
            double[] spectrum = getSpectrum(spectra.get(i), normalize);
            if (i > 0) {
                part.append(" || ");
            }
            part.append(getDistanceSubPart(bands, operator, spectrum, tolerance, normalize));
        }
        return part.toString();
    }

    private static String getAveragePart(Band[] spectralBands, Operator operator, List<double[]> spectra, double tolerance, boolean normalize) {
        if (spectra.isEmpty()) {
            return null;
        }
        double[] avgSpectrum = getAvgSpectrum(spectralBands.length, spectra, normalize);
        return getDistanceSubPart(spectralBands, operator, avgSpectrum, tolerance, normalize);
    }

    private static String getLimitsPart(Band[] spectralBands, Operator operator, List<double[]> spectra, double tolerance, boolean normalize) {
        if (spectra.isEmpty()) {
            return null;
        }
        double[] minSpectrum = getMinSpectrum(spectralBands.length, spectra, tolerance, normalize);
        double[] maxSpectrum = getMaxSpectrum(spectralBands.length, spectra, tolerance, normalize);
        return getLimitsSubPart(spectralBands, operator, minSpectrum, maxSpectrum, normalize);
    }

    private static double[] getSpectrum(double[] spectrum, boolean normalize) {
        if (normalize) {
            double[] normSpectrum = new double[spectrum.length];
            for (int i = 0; i < spectrum.length; i++) {
                normSpectrum[i] = getSpectrumValue(spectrum, i, normalize);
            }
            return normSpectrum;
        } else {
            return spectrum;
        }
    }

    private static double[] getAvgSpectrum(int numBands, List<double[]> spectra, boolean normalize) {
        double[] avgSpectrum = new double[numBands];
        for (double[] spectrum : spectra) {
            for (int i = 0; i < spectrum.length; i++) {
                double value = getSpectrumValue(spectrum, i, normalize);
                avgSpectrum[i] += value;
            }
        }
        for (int i = 0; i < avgSpectrum.length; i++) {
            avgSpectrum[i] /= spectra.size();
        }
        return avgSpectrum;
    }

    private static double[] getMinSpectrum(int numBands, List<double[]> spectra, double tolerance, boolean normalize) {
        double[] minSpectrum = new double[numBands];
        Arrays.fill(minSpectrum, +Double.MAX_VALUE);
        for (double[] spectrum : spectra) {
            for (int i = 0; i < spectrum.length; i++) {
                double value = getSpectrumValue(spectrum, i, normalize);
                minSpectrum[i] = Math.min(minSpectrum[i], value - tolerance);
            }
        }
        return minSpectrum;
    }

    private static double[] getMaxSpectrum(int numBands, List<double[]> spectra, double tolerance, boolean normalize) {
        double[] maxSpectrum = new double[numBands];
        Arrays.fill(maxSpectrum, -Double.MAX_VALUE);
        for (double[] spectrum : spectra) {
            for (int i = 0; i < spectrum.length; i++) {
                double value = getSpectrumValue(spectrum, i, normalize);
                maxSpectrum[i] = Math.max(maxSpectrum[i], value + tolerance);
            }
        }
        return maxSpectrum;
    }

    private static double getSpectrumValue(double[] spectrum, int i, boolean normalize) {
        return normalize ? spectrum[i] / spectrum[0] : spectrum[i];
    }

    private static String getDistanceSubPart(Band[] bands, Operator operator, double[] spectrum, double tolerance, boolean normalize) {
        if (bands.length == 0) {
            return "0";
        }
        final StringBuilder arguments = new StringBuilder();
        appendSpectrumBandNames(bands, normalize, arguments);
        appendSpectrumBandValues(spectrum, arguments);
        String functionName;
        if (operator == Operator.IDENTITY) {
            functionName = "distance";
        } else if (operator == Operator.DERIVATIVE) {
            functionName = "distance_deriv";
        } else if (operator == Operator.INTEGRAL) {
            functionName = "distance_integ";
        } else {
            throw new IllegalStateException("unhandled operator " + operator);
        }
        if (bands.length == 1) {
            return String.format("%s(%s) < %s", functionName, arguments, tolerance);
        } else {
            return String.format("%s(%s)/%s < %s", functionName, arguments, bands.length, tolerance);
        }
    }

    private static String getLimitsSubPart(Band[] bands, Operator operator, double[] minSpectrum, double[] maxSpectrum, boolean normalize) {
        if (bands.length == 0) {
            return "0";
        }
        final StringBuilder arguments = new StringBuilder();
        appendSpectrumBandNames(bands, normalize, arguments);
        appendSpectrumBandValues(minSpectrum, arguments);
        appendSpectrumBandValues(maxSpectrum, arguments);
        String functionName;
        if (operator == Operator.IDENTITY) {
            functionName = "inrange";
        } else if (operator == Operator.DERIVATIVE) {
            functionName = "inrange_deriv";
        } else if (operator == Operator.INTEGRAL) {
            functionName = "inrange_integ";
        } else {
            throw new IllegalStateException("unhandled operator " + operator);
        }
        return String.format("%s(%s)", functionName, arguments);
    }

    private static void appendSpectrumBandNames(Band[] bands, boolean normalize, StringBuilder arguments) {
        String firstName = BandArithmetic.createExternalName(bands[0].getName());
        for (int i = 0; i < bands.length; i++) {
            if (i > 0) {
                arguments.append(",");
            }
            String name = BandArithmetic.createExternalName(bands[i].getName());
            if (normalize) {
                arguments.append(name + "/" + firstName);
            } else {
                arguments.append(name);
            }
        }
    }

    private static void appendSpectrumBandValues(double[] spectrum, StringBuilder arguments) {
        for (double value : spectrum) {
            arguments.append(",");
            arguments.append(value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MagicStickModel that = (MagicStickModel) o;

        if (Double.compare(that.maxTolerance, maxTolerance) != 0) return false;
        if (Double.compare(that.minTolerance, minTolerance) != 0) return false;
        if (normalize != that.normalize) return false;
        if (Double.compare(that.tolerance, tolerance) != 0) return false;
        if (method != that.method) return false;
        if (mode != that.mode) return false;
        if (operator != that.operator) return false;
        if (!ObjectUtils.equalObjects(plusSpectra.toArray(), that.plusSpectra.toArray())) return false;
        if (!ObjectUtils.equalObjects(minusSpectra.toArray(), that.minusSpectra.toArray())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mode.hashCode();
        result = 31 * result + operator.hashCode();
        result = 31 * result + method.hashCode();
        result = 31 * result + plusSpectra.hashCode();
        result = 31 * result + minusSpectra.hashCode();
        temp = tolerance != +0.0d ? Double.doubleToLongBits(tolerance) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (normalize ? 1 : 0);
        temp = minTolerance != +0.0d ? Double.doubleToLongBits(minTolerance) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxTolerance != +0.0d ? Double.doubleToLongBits(maxTolerance) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static MagicStickModel fromXml(String xml) {
        return (MagicStickModel) createXStream().fromXML(xml);
    }

    public String toXml() {
        return createXStream().toXML(this);
    }

    private static XStream createXStream() {
        final XStream xStream = new XStream();
        xStream.alias("magicStickModel", MagicStickModel.class);
        xStream.registerConverter(new SingleValueConverter() {
            @Override
            public String toString(Object obj) {
                return StringUtils.arrayToString(obj, ",");
            }

            @Override
            public Object fromString(String str) {
                return StringUtils.toDoubleArray(str, ",");
            }

            @Override
            public boolean canConvert(Class type) {
                return type.equals(double[].class);
            }
        });
        return xStream;
    }
}
