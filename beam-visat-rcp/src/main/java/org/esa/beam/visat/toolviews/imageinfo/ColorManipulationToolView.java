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
package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.ui.application.support.AbstractToolView;

import javax.swing.JComponent;


/**
 * The color manipulation tool window.
 */
public class ColorManipulationToolView extends AbstractToolView {

    public final static String PREFERENCES_KEY_RGB_MANUAL_MINMAX = "visat.color_palettes.rgb.manualMinMax";
    public final static String PREFERENCES_KEY_RGB_MIN = "visat.color_palettes.rgb.min";
    public final static String PREFERENCES_KEY_RGB_MAX = "visat.color_palettes.rgb.max";
    public final static boolean PREFERENCES_DEFAULT_RGB_MANUAL_MINMAX = false;
    public final static double PREFERENCES_DEFAULT_RGB_MIN = 0.0;
    public final static double PREFERENCES_DEFAULT_RGB_MAX = 1.0;

    public static final String ID = ColorManipulationToolView.class.getName();

    public ColorManipulationToolView() {
    }

    @Override
    protected JComponent createControl() {
        ColorManipulationForm cmf = new ColorManipulationForm(this);
        return cmf.getContentPanel();
    }
}