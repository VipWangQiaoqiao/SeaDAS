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
    public static final int PARAM_MAXVAL_NUM_BINS = 1000000;

    public static final String PARAM_LABEL_PLOTS_THRESH_DOMAIN_LOW = "Plot Domain Low Threshold";
    public static final String PARAM_SHORTLABEL_PLOTS_THRESH_DOMAIN_LOW = "Low";
    public static final String PARAM_KEY_PLOTS_THRESH_DOMAIN_LOW = "statistics.plotsThreshDomainLow";
    public static final double PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_LOW = 5;
    public static final double PARAM_MINVAL_PLOTS_THRESH_DOMAIN_LOW = 0;
    public static final double PARAM_MAXVAL_PLOTS_THRESH_DOMAIN_LOW = 100;

    public static final String PARAM_LABEL_PLOTS_THRESH_DOMAIN_HIGH = "Plot Domain High Threshold";
    public static final String PARAM_SHORTLABEL_PLOTS_THRESH_DOMAIN_HIGH = "High";
    public static final String PARAM_KEY_PLOTS_THRESH_DOMAIN_HIGH = "statistics.plotsThreshDomainHigh";
    public static final double PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_HIGH = 95;
    public static final double PARAM_MINVAL_PLOTS_THRESH_DOMAIN_HIGH = 0;
    public static final double PARAM_MAXVAL_PLOTS_THRESH_DOMAIN_HIGH = 100;

    public static final String PARAM_LABEL_PLOTS_DOMAIN_LOW = "Low";
    public static final double PARAM_DEFVAL_PLOTS_DOMAIN_LOW = Double.NaN;

    public static final String PARAM_LABEL_PLOTS_DOMAIN_HIGH = "High";
    public static final double PARAM_DEFVAL_PLOTS_DOMAIN_HIGH = Double.NaN;


    public static final String PARAM_LABEL_PLOTS_DOMAIN_SPAN = "Set Plot Domain: (by Value)";
    public static final String PARAM_SHORTLABEL_PLOTS_DOMAIN_SPAN = "Set Domain: (by Value)";
    public static final boolean PARAM_DEFVAL_PLOTS_DOMAIN_SPAN = false;

    public static final String PARAM_LABEL_PLOTS_THRESH_DOMAIN_SPAN = "Set Plot Domain: (by Threshold)";
    public static final String PARAM_SHORTLABEL_PLOTS_THRESH_DOMAIN_SPAN = "Set Domain: (by Threshold)";
    public static final boolean PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_SPAN = true;


    public static final String PARAM_LABEL_PLOTS_SIZE = "Set Plot Size";
    public static final String PARAM_SHORTLABEL_PLOTS_SIZE = "Set Size (includes labels)";
    public static final boolean PARAM_DEFVAL_PLOTS_SIZE = false;

    public static final String PARAM_LABEL_PLOTS_SIZE_HEIGHT = "Plot Size (Height)";
    public static final String PARAM_SHORTLABEL_PLOTS_SIZE_HEIGHT = "Height";
    public static final String PARAM_KEY_PLOTS_SIZE_HEIGHT = "statistics.plotsSizeHeight";
    public static final int PARAM_DEFVAL_PLOTS_SIZE_HEIGHT = 300;
    public static final int PARAM_MINVAL_PLOTS_SIZE_HEIGHT = 50;
    public static final int PARAM_MAXVAL_PLOTS_SIZE_HEIGHT = 2000;

    public static final String PARAM_LABEL_PLOTS_SIZE_WIDTH = "Plot Size (Width)";
    public static final String PARAM_SHORTLABEL_PLOTS_SIZE_WIDTH = "Width";
    public static final String PARAM_KEY_PLOTS_SIZE_WIDTH = "statistics.plotsSizeWidth";
    public static final int PARAM_DEFVAL_PLOTS_SIZE_WIDTH = 300;
    public static final int PARAM_MINVAL_PLOTS_SIZE_WIDTH = 50;
    public static final int PARAM_MAXVAL_PLOTS_SIZE_WIDTH = 2000;



    public static final String PARAM_KEY_SPREADSHEET_COL_WIDTH_ = "statistics.spreadsheetColWidth";
    public static final String PARAM_LABEL_SPREADSHEET_COL_WIDTH = "Column Width (Spreadsheet)";
    public static final int PARAM_DEFVAL_SPREADSHEET_COL_WIDTH = 0;
    public static final int PARAM_MINVAL_SPREADSHEET_COL_WIDTH = 0;
    public static final int PARAM_MAXVAL_SPREADSHEET_COL_WIDTH = 50;


    public static final String PARAM_KEY_SPREADSHEET_DECIMAL_PLACES = "statistics.textDecimalPlaces";
    public static final String PARAM_LABEL_SPREADSHEET_DECIMAL_PLACES = "Decimal Places";
    public static final int PARAM_DEFVAL_SPREADSHEET_DECIMAL_PLACES = 4;
    public static final int PARAM_MINVAL_SPREADSHEET_DECIMAL_PLACES = 0;
    public static final int PARAM_MAXVAL_SPREADSHEET_DECIMAL_PLACES = 20;



    public static final String PARAM_LABEL_BIN_MIN_MAX_ENABLED = "Bin Span";
    public static final String PARAM_KEY_BIN_MIN_MAX_ENABLED = "statistics.binMinMaxEnabled";
    public static final boolean PARAM_DEFVAL_BIN_MIN_MAX_ENABLED = false;

    public static final String PARAM_LABEL_BIN_MIN = "Bin Min";
    public static final String PARAM_SHORTLABEL_BIN_MIN = "Min";
    public static final String PARAM_KEY_BIN_MIN = "statistics.binMin";
    public static final double PARAM_DEFVAL_BIN_MIN = Double.NaN;

    public static final String PARAM_LABEL_BIN_MAX = "Bin Max";
    public static final String PARAM_SHORTLABEL_BIN_MAX = "Max";
    public static final String PARAM_KEY_BIN_MAX = "statistics.binMax";
    public static final double PARAM_DEFVAL_BIN_MAX = Double.NaN;


    public static final String PARAM_LABEL_BIN_WIDTH = "Bin Width";
    public static final String PARAM_SHORTLABEL_BIN_WIDTH = "Width";
    public static final String PARAM_KEY_BIN_WIDTH = "statistics.binWidthMax";
    public static final double PARAM_DEFVAL_BIN_WIDTH = Double.NaN;

    public static final String PARAM_LABEL_BIN_WIDTH_ENABLED = "Set #Bins from Bin Width";
    public static final String PARAM_KEY_BIN_WIDTH_ENABLED = "statistics.binWidthEnabled";
    public static final boolean PARAM_DEFVAL_BIN_WIDTH_ENABLED = false;


    public static final String PARAM_KEY_RESET_TO_DEFAULTS = "statistics.resetToDefaults.enabled";


    @Override
    protected PagePanel createPagePanel() {
        return new StatisticsPanel(this, getDescriptor().getHelpId());
    }
}
