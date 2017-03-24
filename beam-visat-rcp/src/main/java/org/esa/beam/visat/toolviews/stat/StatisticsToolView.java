/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.visat.toolviews.stat;

/**
 * The tool view containing the product / band information
 *
 * @author Marco Zuehlke
 */
public class StatisticsToolView extends AbstractStatisticsToolView {

    public static final String ID = StatisticsToolView.class.getName();

    public static final String PARAMETER_NAME_HISTOGRAM_PLOT_ENABLED = "statistics.histogramPlot.enabled";
    public static final boolean DEFAULT_HISTOGRAM_PLOT_ENABLED = true;

    public static final String PARAMETER_NAME_PERCENT_PLOT_ENABLED = "statistics.percentPlot.enabled";
    public static final boolean DEFAULT_PERCENT_PLOT_ENABLED = true;

    public static final String PARAMETER_NAME_STATS_LIST_ENABLED = "statistics.statsList.enabled";
    public static final boolean DEFAULT_STATS_LIST_ENABLED = true;

    public static final String PARAMETER_NAME_STATS_SPREADSHEET_ENABLED = "statistics.statsSpreadSheet.enabled";
    public static final boolean DEFAULT_STATS_SPREADSHEET_ENABLED = true;






    public static final String RESET_TO_DEFAULTS_PARAM_STR = "statistics.resetToDefaults.enabled";


    @Override
    protected PagePanel createPagePanel() {
        return new StatisticsPanel(this, getDescriptor().getHelpId());
    }
}
