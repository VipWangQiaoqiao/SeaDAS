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
    public final static String PREFERENCES_KEY_RGB_MIN_RED = "visat.color_palettes.rgb.minRed";
    public final static String PREFERENCES_KEY_RGB_MIN_GREEN = "visat.color_palettes.rgb.minGreen";
    public final static String PREFERENCES_KEY_RGB_MIN_BLUE = "visat.color_palettes.rgb.minBlue";
    public final static String PREFERENCES_KEY_RGB_MAX_RED = "visat.color_palettes.rgb.maxRed";
    public final static String PREFERENCES_KEY_RGB_MAX_GREEN = "visat.color_palettes.rgb.maxGreen";
    public final static String PREFERENCES_KEY_RGB_MAX_BLUE = "visat.color_palettes.rgb.maxBlue";
    public final static boolean PREFERENCES_DEFAULT_RGB_MANUAL_MINMAX = false;
    public final static double PREFERENCES_DEFAULT_RGB_MIN_RED = 0.0;
    public final static double PREFERENCES_DEFAULT_RGB_MIN_GREEN = 0.0;
    public final static double PREFERENCES_DEFAULT_RGB_MIN_BLUE = 0.0;
    public final static double PREFERENCES_DEFAULT_RGB_MAX_RED = 1.0;
    public final static double PREFERENCES_DEFAULT_RGB_MAX_GREEN = 1.0;
    public final static double PREFERENCES_DEFAULT_RGB_MAX_BLUE = 1.0;

    public final static String PREFERENCES_KEY_RGB_SET_GAMMA = "visat.color_palettes.rgb.setGamma";
    public final static String PREFERENCES_KEY_RGB_GAMMA_RED = "visat.color_palettes.rgb.gammaRed";
    public final static String PREFERENCES_KEY_RGB_GAMMA_GREEN = "visat.color_palettes.rgb.gammaGreen";
    public final static String PREFERENCES_KEY_RGB_GAMMA_BLUE = "visat.color_palettes.rgb.gammaBlue";
    public final static boolean PREFERENCES_DEFAULT_RGB_SET_GAMMA = false;
    public final static double PREFERENCES_DEFAULT_RGB_GAMMA_RED = 1.0;
    public final static double PREFERENCES_DEFAULT_RGB_GAMMA_GREEN = 1.0;
    public final static double PREFERENCES_DEFAULT_RGB_GAMMA_BLUE = 1.0;

    public static final String ID = ColorManipulationToolView.class.getName();

    public ColorManipulationToolView() {
    }

    @Override
    protected JComponent createControl() {
        ColorManipulationForm cmf = new ColorManipulationForm(this);
        return cmf.getContentPanel();
    }
}