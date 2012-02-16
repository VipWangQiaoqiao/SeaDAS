/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.framework.ui;

import com.bc.ceres.core.Assert;
import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.datamodel.Scaling;
import org.esa.beam.framework.datamodel.Stx;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


/**
 * Unstable interface. Do not use.
 *
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 * @since BEAM 4.5.1
 */
public abstract class AbstractImageInfoEditorModel implements ImageInfoEditorModel {

    private final ImageInfo imageInfo;
    private final EventListenerList listenerList;
    private Scaling scaling;
    private Stx stx;
    private String name;
    private String unit;
    private Double histogramViewGain;
    private Double minHistogramViewSample;
    private Double maxHistogramViewSample;

    protected AbstractImageInfoEditorModel(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
        this.listenerList = new EventListenerList();
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setDisplayProperties(String name, String unit, Stx stx, Scaling scaling) {
        setParameterName(name);
        setParameterUnit(unit);
        setSampleScaling(scaling);
        setSampleStx(stx);
        fireStateChanged();
    }

    public String getParameterName() {
        return name;
    }

    private void setParameterName(String name) {
        this.name = name;
    }

    public String getParameterUnit() {
        return unit;
    }

    private void setParameterUnit(String unit) {
        this.unit = unit;
    }

    public Scaling getSampleScaling() {
        return scaling;
    }

    private void setSampleScaling(Scaling scaling) {
        Assert.notNull(scaling, "scaling");
        this.scaling = scaling;
    }

    public Stx getSampleStx() {
        return stx;
    }

    private void setSampleStx(Stx stx) {
        Assert.notNull(stx, "stx");
        this.stx = stx;
    }

    public double getMinSample() {
        //return scaling == null ? 0 : (scaling.scale(stx.getMin()));
        return scaling == null ? 0 : ( scaling.isLog10Scaled() ? scaling.scale(stx.getMin()) : stx.getMin() );
    }

    public double getMinSample(boolean isLog10Scaled) {
        return scaling == null ? 0 : ( isLog10Scaled ? scaling.scale(stx.getMin()) : stx.getMin() );
    }

    public double getMaxSample(boolean isLog10Scaled) {
        return scaling == null ? 0 : ( isLog10Scaled ? scaling.scale(stx.getMax()): stx.getMax() );
    }

    public double getMaxSample() {
        //return scaling == null ? 0 : scaling.scale(stx.getMax());
        return scaling == null ? 0 : ( scaling.isLog10Scaled() ? scaling.scale(stx.getMax()) : stx.getMax() );
    }

    public boolean isHistogramAvailable() {
        return getHistogramBins() != null && getHistogramBins().length > 0;
    }

    public int[] getHistogramBins() {
        int[] histBins = stx == null ? null : stx.getHistogramBins();
        System.out.println(" histogram bin length: " + histBins == null ? null : histBins.length );
        return stx == null ? null : stx.getHistogramBins();
    }

    public double getMinHistogramViewSample() {
        if (minHistogramViewSample != null) {
            return minHistogramViewSample;
        }
        return getMinSample();
    }

    public void setMinHistogramViewSample(double minViewSample) {
        minHistogramViewSample = minViewSample;
    }

    public double getMaxHistogramViewSample() {
        if (maxHistogramViewSample != null) {
            return maxHistogramViewSample;
        }
        return getMaxSample();
    }

    public void setMaxHistogramViewSample(double maxViewSample) {
        maxHistogramViewSample = maxViewSample;
    }

    public double getHistogramViewGain() {
        if (histogramViewGain != null) {
            return histogramViewGain;
        }
        return 1.0;
    }

    public void setHistogramViewGain(double gain) {
        histogramViewGain = gain;
    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    public void fireStateChanged() {
        final ChangeEvent event = new ChangeEvent(this);
        ChangeListener[] changeListeners = listenerList.getListeners(ChangeListener.class);
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(event);
        }
    }
}