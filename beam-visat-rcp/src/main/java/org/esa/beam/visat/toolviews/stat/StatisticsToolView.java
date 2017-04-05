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

    public static final String PARAM_LABEL_HISTOGRAM_PLOT_ENABLED = "Show Histogram Plot";
    public static final String PARAM_KEY_HISTOGRAM_PLOT_ENABLED = "statistics.histogramPlot.enabled";
    public static final boolean PARAM_DEFVAL_HISTOGRAM_PLOT_ENABLED = true;

    public static final String PARAM_LABEL_PERCENT_PLOT_ENABLED = "Show Percentile Plot";
    public static final String PARAM_KEY_PERCENT_PLOT_ENABLED = "statistics.percentPlot.enabled";
    public static final boolean PARAM_DEFVAL_PERCENT_PLOT_ENABLED = true;

    public static final String PARAM_LABEL_STATS_LIST_ENABLED = "Show Statistics List";
    public static final String PARAM_KEY_STATS_LIST_ENABLED = "statistics.statsList.enabled";
    public static final boolean PARAM_DEFVAL_STATS_LIST_ENABLED = true;

    public static final String PARAM_LABEL_STATS_SPREADSHEET_ENABLED = "Show Statistics Spreadsheet";
    public static final String PARAM_KEY_STATS_SPREADSHEET_ENABLED = "statistics.statsSpreadSheet.enabled";
    public static final boolean PARAM_DEFVAL_STATS_SPREADSHEET_ENABLED = true;

    public static final String PARAM_LABEL_PERCENT_THRESHOLDS = "Thresholds";
    public static final String PARAM_KEY_PERCENT_THRESHOLDS = "statistics.percentThresholds";
    public static final String PARAM_DEFVAL_PERCENT_THRESHOLDS = "80,85,90,95,98";

    public static final String PARAM_LABEL_NUM_BINS = "#Bins";
    public static final String PARAM_KEY_NUM_BINS = "statistics.numBins";
    public static final int PARAM_DEFVAL_NUM_BINS = 1000;
    public static final int PARAM_MINVAL_NUM_BINS = 1;
    public static final int PARAM_MAXVAL_NUM_BINS = 10000000;

    public static final String PARAM_LABEL_HIST_DISPLAY_LOW_THRESH = "Thresh Range: Low";
    public static final String PARAM_KEY_HIST_DISPLAY_LOW_THRESH = "statistics.histDisplayLowThresh";
    public static final int PARAM_DEFVAL_HIST_DISPLAY_LOW_THRESH = 5;
    public static final int PARAM_MINVAL_HIST_DISPLAY_LOW_THRESH = 0;
    public static final int PARAM_MAXVAL_HIST_DISPLAY_LOW_THRESH = 100;

    public static final String PARAM_LABEL_HIST_DISPLAY_HIGH_THRESH = "High";
    public static final String PARAM_KEY_HIST_DISPLAY_HIGH_THRESH = "statistics.histDisplayHighThresh";
    public static final int PARAM_DEFVAL_HIST_DISPLAY_HIGH_THRESH = 95;
    public static final int PARAM_MINVAL_HIST_DISPLAY_HIGH_THRESH = 0;
    public static final int PARAM_MAXVAL_HIST_DISPLAY_HIGH_THRESH = 100;

    public static final String PARAM_KEY_RESET_TO_DEFAULTS = "statistics.resetToDefaults.enabled";


    @Override
    protected PagePanel createPagePanel() {
        return new StatisticsPanel(this, getDescriptor().getHelpId());
    }
}
