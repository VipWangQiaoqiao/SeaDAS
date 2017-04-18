package org.esa.beam.visat.toolviews.stat;

import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.TextFieldContainer;
import org.esa.beam.util.PropertyMap;
import org.esa.beam.visat.VisatApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by knowles on 4/18/17.
 */
public class StatisticsCriteriaPanel {

    private Container getParentDialogContentPane;
    final VisatApp visatApp = VisatApp.getApp();
    private PropertyMap configuration = null;


    // "Bins" Tab Variables and Components

    private int numBins = StatisticsToolView.PARAM_DEFVAL_NUM_BINS;
    private TextFieldContainer numBinsTextfieldContainer = null;

    private double binWidth = StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH;
    private TextFieldContainer binWidthTextfieldContainer = null;

    private JCheckBox binWidthEnabledCheckBox = null;
    private JCheckBox logModeCheckBox = null;

    private boolean logMode = false;


    private JCheckBox binMinMaxCheckBox = null;
    double binMin = StatisticsToolView.PARAM_DEFVAL_BIN_MIN;
    double binMax = StatisticsToolView.PARAM_DEFVAL_BIN_MAX;
    private TextFieldContainer binMinTextfieldContainer = null;
    private TextFieldContainer binMaxTextfieldContainer = null;


    // "Plots" Tab Variables and Components

    private boolean plotsThreshDomainSpan = StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_SPAN;
    private double plotsThreshDomainLow = StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_LOW;
    private double plotsThreshDomainHigh = StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_HIGH;

    private JCheckBox plotsThreshDomainSpanCheckBox = null;
    private TextFieldContainer plotsThreshDomainLowTextfieldContainer = null;
    private TextFieldContainer plotsThreshDomainHighTextfieldContainer = null;

    private boolean plotsDomainSpan = StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_SPAN;
    private double plotsDomainLow = StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_LOW;
    private double plotsDomainHigh = StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_HIGH;

    private JCheckBox plotsDomainSpanCheckBox = null;
    private TextFieldContainer plotsDomainLowTextfieldContainer = null;
    private TextFieldContainer plotsDomainHighTextfieldContainer = null;

    private boolean exactPlotSize = StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE;
    private int plotSizeHeight = StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE_HEIGHT;
    private int plotSizeWidth = StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE_WIDTH;

    private JCheckBox plotsSizeCheckBox = null;
    private TextFieldContainer plotsSizeHeightTextfieldContainer = null;
    private TextFieldContainer plotsSizeWidthTextfieldContainer = null;


    // "View" Tab Variables and Components

    private boolean showPercentPlots = StatisticsToolView.PARAM_DEFVAL_PERCENT_PLOT_ENABLED;
    private boolean showHistogramPlots = StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_PLOT_ENABLED;
    private boolean showStatsList = StatisticsToolView.PARAM_DEFVAL_STATS_LIST_ENABLED;
    private boolean showStatsSpreadSheet = StatisticsToolView.PARAM_DEFVAL_STATS_SPREADSHEET_ENABLED;

    private JCheckBox showPercentPlotCheckBox = null;
    private JCheckBox showHistogramPlotCheckBox = null;
    private JCheckBox showStatsListCheckBox = null;
    private JCheckBox showStatsSpreadSheetCheckBox = null;


    public StatisticsCriteriaPanel(Container getParentDialogContentPane) {
        this.getParentDialogContentPane = getParentDialogContentPane;

        if (visatApp != null) {
            this.configuration = visatApp.getPreferences();
        }

        initPreferences();
        initComponents();
    }


    //
    //------------------------------- INIT/RESET -------------------------------------
    //

    private void initPreferences() {
        numBins = getPreferencesNumBins();
        showHistogramPlots = getPreferencesHistogramPlotEnabled();
        showPercentPlots = getPreferencesPercentPlotEnabled();
        showStatsList = getPreferencesStatsListEnabled();
        showStatsSpreadSheet = getPreferencesStatsSpreadSheetEnabled();

    }

    private void initComponents() {

        // "Bins" Tab Variables and Components

        numBinsTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_LABEL_NUM_BINS,
                numBins,
                StatisticsToolView.PARAM_MINVAL_NUM_BINS,
                StatisticsToolView.PARAM_MAXVAL_NUM_BINS,
                TextFieldContainer.NumType.INT,
                7,
                getParentDialogContentPane);


        binWidthTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_LABEL_BIN_WIDTH,
                StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH,
                TextFieldContainer.NumType.DOUBLE,
                5,
                getParentDialogContentPane);


        binWidthEnabledCheckBox = new JCheckBox(StatisticsToolView.PARAM_LABEL_BIN_WIDTH_ENABLED);
        binWidthEnabledCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH_ENABLED);


        binMinMaxCheckBox = new JCheckBox(StatisticsToolView.PARAM_LABEL_BIN_MIN_MAX_ENABLED);
        binMinMaxCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_BIN_MIN_MAX_ENABLED);


        binMinTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_SHORTLABEL_BIN_MIN,
                StatisticsToolView.PARAM_DEFVAL_BIN_MIN,
                TextFieldContainer.NumType.DOUBLE,
                5,
                getParentDialogContentPane);


        binMaxTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_SHORTLABEL_BIN_MAX,
                StatisticsToolView.PARAM_DEFVAL_BIN_MAX,
                TextFieldContainer.NumType.DOUBLE,
                5,
                getParentDialogContentPane);


        logModeCheckBox = new JCheckBox("Log Scaled Bins");
        logModeCheckBox.setSelected(logMode);


        // "View" Tab Variables and Components

        showStatsListCheckBox = new JCheckBox(StatisticsToolView.PARAM_LABEL_STATS_LIST_ENABLED);
        showStatsListCheckBox.setSelected(showStatsList);

        showStatsSpreadSheetCheckBox = new JCheckBox(StatisticsToolView.PARAM_LABEL_STATS_SPREADSHEET_ENABLED);
        showStatsSpreadSheetCheckBox.setSelected(showStatsSpreadSheet);

        showPercentPlotCheckBox = new JCheckBox(StatisticsToolView.PARAM_LABEL_PERCENT_PLOT_ENABLED);
        showPercentPlotCheckBox.setSelected(showPercentPlots);

        showHistogramPlotCheckBox = new JCheckBox(StatisticsToolView.PARAM_LABEL_HISTOGRAM_PLOT_ENABLED);
        showHistogramPlotCheckBox.setSelected(showHistogramPlots);


        addListeners();

        updateEnablement();
    }


    private void updateEnablement() {
        binWidthTextfieldContainer.setEnabled(binWidthEnabledCheckBox.isSelected());
        numBinsTextfieldContainer.setEnabled(!binWidthEnabledCheckBox.isSelected());
        binMinTextfieldContainer.setEnabled(binMinMaxCheckBox.isSelected());
        binMaxTextfieldContainer.setEnabled(binMinMaxCheckBox.isSelected());
    }


    private void addListeners() {

        // "Bins" Tab Variables and Components

        binWidthEnabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                binWidthTextfieldContainer.setEnabled(binWidthEnabledCheckBox.isSelected());
                numBinsTextfieldContainer.setEnabled(!binWidthEnabledCheckBox.isSelected());
            }
        });

        binMinMaxCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                binMinTextfieldContainer.setEnabled(binMinMaxCheckBox.isSelected());
                binMaxTextfieldContainer.setEnabled(binMinMaxCheckBox.isSelected());
            }
        });

        logModeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                logMode = logModeCheckBox.isSelected();

            }
        });


        // "View" Tab Variables and Components

        showStatsListCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                showStatsList = showStatsListCheckBox.isSelected();
            }
        });

        showStatsSpreadSheetCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                showStatsSpreadSheet = showStatsSpreadSheetCheckBox.isSelected();
                // todo Danny this listener needs to be outside in the calling program
                //            updateLeftPanel();
            }
        });

        showPercentPlotCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                showPercentPlots = showPercentPlotCheckBox.isSelected();
            }
        });

        showHistogramPlotCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                showHistogramPlots = showHistogramPlotCheckBox.isSelected();
            }
        });

    }


    public void reset() {
        binWidthTextfieldContainer.reset();
        binMinTextfieldContainer.reset();
        binMaxTextfieldContainer.reset();
    }


    //
    //------------------------------- FIELD RETRIEVALS -------------------------------------
    //

    // "Bins" Tab Variables and Components

    public int getNumBins() {
        return numBins;
    }

    public double getBinWidth() {
        return binWidth;
    }

    public double getBinMin() {
        return binMin;
    }

    public double getBinMax() {
        return binMax;
    }

    public boolean isLogMode() {
        return logMode;
    }


    // "Plots" Tab Variables and Components

    public boolean plotsThreshDomainSpan() {
        return plotsThreshDomainSpan;
    }

    public double plotsThreshDomainLow() {
        return plotsThreshDomainLow;
    }

    public double plotsThreshDomainHigh() {
        return plotsThreshDomainHigh;
    }

    public boolean plotsDomainSpan() {
        return plotsDomainSpan;
    }

    public double plotsDomainLow() {
        return plotsDomainLow;
    }

    public double plotsDomainHigh() {
        return plotsDomainHigh;
    }

    public boolean exactPlotSize() {
        return exactPlotSize;
    }

    public double plotSizeHeight() {
        return plotSizeHeight;
    }

    public double plotSizeWidth() {
        return plotSizeWidth;
    }


    // "View" Tab Variables and Components

    public boolean showPercentPlots() {
        return showPercentPlots;
    }

    public boolean showHistogramPlots() {
        return showHistogramPlots;
    }

    public boolean showStatsList() {
        return showStatsList;
    }

    public boolean showStatsSpreadSheet() {
        return showStatsSpreadSheet;
    }


    //
    //------------------------------- PUBLIC PANELS -------------------------------------
    //

    // "Bins" Tab Variables and Components

    public JPanel getBinningCriteriaPanel() {

        JPanel numBinsPanel = getNumBinsPanel();


        final JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        gbc.insets.top = 5;
        gbc.weighty = 0;
        panel.add(numBinsPanel, gbc);

        gbc.insets.top = 0;
        gbc.gridy += 1;

        panel.add(getBinWidthPanel(), gbc);


        gbc.gridy += 1;
        panel.add(getBinMinMaxPanel(), gbc);

        gbc.gridy += 1;
        panel.add(logModeCheckBox, gbc);

        // Add filler panel at bottom which expands as needed to force all components within this panel to the top
        gbc = GridBagUtils.restoreConstraints(gbc);
        gbc.weighty = 1;
        gbc.gridy += 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }


    // "View" Tab Variables and Components

    public JPanel getViewPanel() {

        final JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();


        gbc.weighty = 0;
        gbc.insets.top = 5;
        panel.add(showPercentPlotCheckBox, gbc);

        gbc.insets.top = 0;
        gbc.gridy += 1;
        panel.add(showHistogramPlotCheckBox, gbc);

        gbc.gridy += 1;
        panel.add(showStatsListCheckBox, gbc);

        gbc.gridy += 1;
        panel.add(showStatsSpreadSheetCheckBox, gbc);

        // Add filler panel at bottom which expands as needed to force all components within this panel to the top
        gbc = GridBagUtils.restoreConstraints(gbc);
        gbc.weighty = 1;
        gbc.gridy += 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }


    //
    //------------------------------- PRIVATE CHILDREN PANELS -------------------------------------
    //


    // "Bins" Tab Variables and Components

    private JPanel getNumBinsPanel() {

        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 3;
        panel.add(numBinsTextfieldContainer.getLabel(), gbc);
        gbc.insets.right = 0;
        gbc.gridx++;
        panel.add(numBinsTextfieldContainer.getTextfield(), gbc);

        return panel;
    }


    private JPanel getBinWidthPanel() {


        JPanel childPanel = GridBagUtils.createPanel();
        GridBagConstraints gbcChild = GridBagUtils.createConstraints();

        gbcChild.gridx += 1;
        gbcChild.insets.left = new JCheckBox(" ").getPreferredSize().width;
        gbcChild.insets.right = 3;
        childPanel.add(binWidthTextfieldContainer.getLabel(), gbcChild);
        gbcChild = GridBagUtils.restoreConstraints(gbcChild);

        gbcChild.gridx += 1;
        childPanel.add(binWidthTextfieldContainer.getTextfield(), gbcChild);


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        panel.add(binWidthEnabledCheckBox, gbc);

        gbc.gridy += 1;
        panel.add(childPanel, gbc);


        return panel;
    }


    private JPanel getBinMinMaxPanel() {

        return getCheckboxTextFieldGroupPanel(binMinMaxCheckBox, binMinTextfieldContainer, binMaxTextfieldContainer);

    }


    //
    //------------------------------- VALIDATION -------------------------------------
    //


    public boolean validatePrepare() {
        if (numBinsTextfieldContainer != null && numBinsTextfieldContainer.isValid(true) && numBinsTextfieldContainer.getValue() != null) {
            numBins = numBinsTextfieldContainer.getValue().intValue();
            if (!validNumBins()) {
                return false;
            }
        } else {
            return false;
        }

        if (binWidthEnabledCheckBox.isSelected()) {
            if (binWidthTextfieldContainer != null && binWidthTextfieldContainer.isValid(true) && binWidthTextfieldContainer.getValue() != null) {
                binWidth = binWidthTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }

        } else {
            binWidth = StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH;
            binWidth = StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH;
        }


        if (binMinMaxCheckBox.isSelected()) {
            if (binMinTextfieldContainer != null && binMinTextfieldContainer.isValid(true) && binMinTextfieldContainer.getValue() != null) {
                binMin = binMinTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }


            if (binMaxTextfieldContainer != null && binMaxTextfieldContainer.isValid(true) && binMaxTextfieldContainer.getValue() != null) {
                binMax = binMaxTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }
        } else {
            binMin = StatisticsToolView.PARAM_DEFVAL_BIN_MIN;
            binMax = StatisticsToolView.PARAM_DEFVAL_BIN_MAX;
        }

        {
            String lowName = binMinTextfieldContainer.getName();
            String highName = binMaxTextfieldContainer.getName();
            double lowVal = binMinTextfieldContainer.getValue().doubleValue();
            double highVal = binMaxTextfieldContainer.getValue().doubleValue();
            if (!compareFields(lowVal, highVal, lowName, highName, true)) {
                return false;
            }
        }


        return true;
    }

    private boolean validNumBins() {
        if (numBins < StatisticsToolView.PARAM_MINVAL_NUM_BINS || numBins > StatisticsToolView.PARAM_MAXVAL_NUM_BINS) {
            return false;
        }

        return true;
    }


    //
    //------------------------------- PREFERENCES -------------------------------------
    //


    public int getPreferencesNumBins() {

        if (configuration != null) {
            return configuration.getPropertyInt(StatisticsToolView.PARAM_KEY_NUM_BINS, StatisticsToolView.PARAM_DEFVAL_NUM_BINS);
        } else {
            return StatisticsToolView.PARAM_DEFVAL_NUM_BINS;
        }
    }

    public boolean getPreferencesHistogramPlotEnabled() {

        if (configuration != null) {
            return configuration.getPropertyBool(StatisticsToolView.PARAM_KEY_HISTOGRAM_PLOT_ENABLED, StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_PLOT_ENABLED);
        } else {
            return StatisticsToolView.PARAM_DEFVAL_HISTOGRAM_PLOT_ENABLED;
        }
    }

    public boolean getPreferencesPercentPlotEnabled() {

        if (configuration != null) {
            return configuration.getPropertyBool(StatisticsToolView.PARAM_KEY_PERCENT_PLOT_ENABLED, StatisticsToolView.PARAM_DEFVAL_PERCENT_PLOT_ENABLED);
        } else {
            return StatisticsToolView.PARAM_DEFVAL_PERCENT_PLOT_ENABLED;
        }
    }

    public boolean getPreferencesStatsListEnabled() {

        if (configuration != null) {
            return configuration.getPropertyBool(StatisticsToolView.PARAM_KEY_STATS_LIST_ENABLED, StatisticsToolView.PARAM_DEFVAL_STATS_LIST_ENABLED);
        } else {
            return StatisticsToolView.PARAM_DEFVAL_STATS_LIST_ENABLED;
        }
    }

    public boolean getPreferencesStatsSpreadSheetEnabled() {

        if (configuration != null) {
            return configuration.getPropertyBool(StatisticsToolView.PARAM_KEY_STATS_SPREADSHEET_ENABLED, StatisticsToolView.PARAM_DEFVAL_STATS_SPREADSHEET_ENABLED);
        } else {
            return StatisticsToolView.PARAM_DEFVAL_STATS_SPREADSHEET_ENABLED;
        }
    }


    //
    //------------------------------- GENERAL PURPOSE -------------------------------------
    //


    private boolean compareFields(double lowVal, double highVal, String lowName, String HighName, boolean showDialog) {
        if (lowVal >= highVal) {
            if (showDialog) {
                JOptionPane.showMessageDialog(getParentDialogContentPane,
                        "ERROR: Value of " + lowName + " must be greater than value of " + HighName,
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }

            return false;
        }

        return true;

    }

    public JPanel getCheckboxTextFieldGroupPanel(JCheckBox checkbox, TextFieldContainer textFieldContainer) {

        return getCheckboxTextFieldGroupPanel(checkbox, textFieldContainer, null);
    }

    public JPanel getCheckboxTextFieldGroupPanel(JCheckBox checkbox, TextFieldContainer textFieldContainer1, TextFieldContainer textFieldContainer2) {


        JPanel childPanel = GridBagUtils.createPanel();
        GridBagConstraints gbcChild = GridBagUtils.createConstraints();
        gbcChild.insets.left = new JCheckBox(" ").getPreferredSize().width;

        gbcChild.insets.right = 3;
        childPanel.add(textFieldContainer1.getLabel(), gbcChild);
        gbcChild = GridBagUtils.restoreConstraints(gbcChild);

        gbcChild.gridx += 1;
        childPanel.add(textFieldContainer1.getTextfield(), gbcChild);

        if (textFieldContainer2 != null) {
            gbcChild.gridx += 1;
            gbcChild.insets.right = 3;
            childPanel.add(textFieldContainer2.getLabel(), gbcChild);
            gbcChild = GridBagUtils.restoreConstraints(gbcChild);

            gbcChild.gridx += 1;
            childPanel.add(textFieldContainer2.getTextfield(), gbcChild);
        }

        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        panel.add(checkbox, gbc);

        gbc.gridy += 1;
        panel.add(childPanel, gbc);


        return panel;
    }
}
