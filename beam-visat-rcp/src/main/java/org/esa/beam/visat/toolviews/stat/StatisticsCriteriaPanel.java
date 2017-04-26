package org.esa.beam.visat.toolviews.stat;

import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.TextFieldContainer;
import org.esa.beam.util.PropertyMap;
import org.esa.beam.visat.VisatApp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

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


    // "Fields" Tab Variables and Components

    private boolean handlersEnabled = true; //todo this may be temporary and not needed
    private String percentThresholds = StatisticsToolView.PARAM_DEFVAL_PERCENT_THRESHOLDS;
    private java.util.List<Integer> percentThresholdsList = null;
    private JTextField percentThresholdsTextField = null;
    private JLabel percentThresholdsLabel = null;

    private boolean includeMedian = true;
    private JCheckBox includeMedianCheckBox = null;

    private boolean includeHistogramStats = false;
    private JCheckBox includeHistogramStatsCheckBox = null;

    private boolean includeFileName = true;
    private boolean includeBandName = true;
    private boolean includeBandUnits = true;
    private boolean includeMaskName = true;
    private boolean includeDateTime = true;
    private boolean includeValidPixExp = true;
    private boolean includeDescription = true;
    private boolean includeProductFormat = true;
    private boolean includeTimeSeriesFields = true;



    // "Text" Tab Variables and Components

    private static final int COL_WIDTH_DEFAULT = 10;
    private int colCharWidth = COL_WIDTH_DEFAULT;
    private TextFieldContainer spreadsheetColWidthTextfieldContainer = null;

    private int decimalPlaces = StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_DECIMAL_PLACES;
    private TextFieldContainer decimalPlacesTextfieldContainer = null;





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
        percentThresholds = getPreferencesPercentThresholds();

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


        // "Fields" Tab Variables and Components


        percentThresholdsLabel = new JLabel(StatisticsToolView.PARAM_LABEL_PERCENT_THRESHOLDS);
        percentThresholdsTextField = new JTextField(14);
        percentThresholdsTextField.setName(StatisticsToolView.PARAM_LABEL_PERCENT_THRESHOLDS);
        percentThresholdsTextField.setText(percentThresholds);

        includeMedianCheckBox = new JCheckBox("Show Median");
        includeMedianCheckBox.setSelected(includeMedian);

        includeHistogramStatsCheckBox = new JCheckBox("Show Histogram Statistics");
        includeHistogramStatsCheckBox.setSelected(includeHistogramStats);



        // "Text" Tab Variables and Components

        spreadsheetColWidthTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_LABEL_SPREADSHEET_COL_WIDTH,
                StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_COL_WIDTH,
                StatisticsToolView.PARAM_MINVAL_SPREADSHEET_COL_WIDTH,
                StatisticsToolView.PARAM_MAXVAL_SPREADSHEET_COL_WIDTH,
                TextFieldContainer.NumType.INT,
                2,
                getParentDialogContentPane);


        decimalPlacesTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_LABEL_SPREADSHEET_DECIMAL_PLACES,
                StatisticsToolView.PARAM_DEFVAL_SPREADSHEET_DECIMAL_PLACES,
                StatisticsToolView.PARAM_MINVAL_SPREADSHEET_DECIMAL_PLACES,
                StatisticsToolView.PARAM_MAXVAL_SPREADSHEET_DECIMAL_PLACES,
                TextFieldContainer.NumType.INT,
                2,
                getParentDialogContentPane);






        // "Plots" Tab Variables and Components


        plotsThreshDomainLowTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_THRESH_DOMAIN_LOW,
                StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_LOW,
                StatisticsToolView.PARAM_MINVAL_PLOTS_THRESH_DOMAIN_LOW,
                StatisticsToolView.PARAM_MAXVAL_PLOTS_THRESH_DOMAIN_LOW,
                TextFieldContainer.NumType.DOUBLE,
                4,
                getParentDialogContentPane);


        plotsThreshDomainHighTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_THRESH_DOMAIN_HIGH,
                StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_HIGH,
                StatisticsToolView.PARAM_MINVAL_PLOTS_THRESH_DOMAIN_HIGH,
                StatisticsToolView.PARAM_MAXVAL_PLOTS_THRESH_DOMAIN_HIGH,
                TextFieldContainer.NumType.DOUBLE,
                4,
                getParentDialogContentPane);


        plotsDomainLowTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_LABEL_PLOTS_DOMAIN_LOW,
                StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_LOW,
                TextFieldContainer.NumType.DOUBLE,
                4,
                getParentDialogContentPane);


        plotsDomainHighTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_LABEL_PLOTS_DOMAIN_HIGH,
                StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_HIGH,
                TextFieldContainer.NumType.DOUBLE,
                4,
                getParentDialogContentPane);


        plotsSizeHeightTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_SIZE_HEIGHT,
                StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE_HEIGHT,
                StatisticsToolView.PARAM_MINVAL_PLOTS_SIZE_HEIGHT,
                StatisticsToolView.PARAM_MAXVAL_PLOTS_SIZE_HEIGHT,
                TextFieldContainer.NumType.INT,
                4,
                getParentDialogContentPane);


        plotsSizeWidthTextfieldContainer = new TextFieldContainer(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_SIZE_WIDTH,
                StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE_WIDTH,
                StatisticsToolView.PARAM_MINVAL_PLOTS_SIZE_WIDTH,
                StatisticsToolView.PARAM_MAXVAL_PLOTS_SIZE_WIDTH,
                TextFieldContainer.NumType.INT,
                4,
                getParentDialogContentPane);



        plotsThreshDomainSpanCheckBox = new JCheckBox(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_THRESH_DOMAIN_SPAN);
        plotsThreshDomainSpanCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_SPAN);

        plotsDomainSpanCheckBox = new JCheckBox(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_DOMAIN_SPAN);
        plotsDomainSpanCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_SPAN);

        plotsSizeCheckBox = new JCheckBox(StatisticsToolView.PARAM_SHORTLABEL_PLOTS_SIZE);
        plotsSizeCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE);




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

        // toggle each checkbox to force event change in listeners and hence established all proper initial enablement
        plotsSizeCheckBox.setSelected(!StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE);
        plotsSizeCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE);
        plotsDomainSpanCheckBox.setSelected(!StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_SPAN);
        plotsDomainSpanCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_SPAN);
        plotsThreshDomainSpanCheckBox.setSelected(!StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_SPAN);
        plotsThreshDomainSpanCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_SPAN);
        binWidthEnabledCheckBox.setSelected(!StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH_ENABLED);
        binWidthEnabledCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_BIN_WIDTH_ENABLED);
        binMinMaxCheckBox.setSelected(!StatisticsToolView.PARAM_DEFVAL_BIN_MIN_MAX_ENABLED);
        binMinMaxCheckBox.setSelected(StatisticsToolView.PARAM_DEFVAL_BIN_MIN_MAX_ENABLED);


    }


    //
    //------------------------------- LISTENERS / HANDLERS / -------------------------------------
    //





    private void addListeners() {

        // "Bins" Tab Variables and Components

        binWidthEnabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                binWidthTextfieldContainer.setEnabled(binWidthEnabledCheckBox.isSelected());
                numBinsTextfieldContainer.setEnabled(!binWidthEnabledCheckBox.isSelected());            }
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


        // "Fields" Tab Variables and Components

        textfieldHandler(percentThresholdsTextField);

        includeMedianCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                includeMedian = includeMedianCheckBox.isSelected();

            }
        });

        includeHistogramStatsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                includeHistogramStats = includeHistogramStatsCheckBox.isSelected();

            }
        });


        // "Text" Tab Variables and Components



        // "Plots" Tab Variables and Components


        plotsSizeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                plotsSizeHeightTextfieldContainer.setEnabled(plotsSizeCheckBox.isSelected());
                plotsSizeWidthTextfieldContainer.setEnabled(plotsSizeCheckBox.isSelected());
            }
        });


        plotsThreshDomainSpanCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                plotsThreshDomainHighTextfieldContainer.setEnabled(plotsThreshDomainSpanCheckBox.isSelected());
                plotsThreshDomainLowTextfieldContainer.setEnabled(plotsThreshDomainSpanCheckBox.isSelected());

                if (plotsThreshDomainSpanCheckBox.isSelected()) {
                    plotsDomainSpanCheckBox.setSelected(false);
                }
            }
        });

        plotsDomainSpanCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                plotsDomainHighTextfieldContainer.setEnabled(plotsDomainSpanCheckBox.isSelected());
                plotsDomainLowTextfieldContainer.setEnabled(plotsDomainSpanCheckBox.isSelected());

                if (plotsDomainSpanCheckBox.isSelected()) {
                    plotsThreshDomainSpanCheckBox.setSelected(false);
                }
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


    private void textfieldHandler(final JTextField textField) {

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                textfieldHandlerAction(textField);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                textfieldHandlerAction(textField);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                textfieldHandlerAction(textField);
            }
        });
    }

    private void textfieldHandlerAction(final JTextField textField) {

        if (handlersEnabled) {
            if (StatisticsToolView.PARAM_LABEL_PERCENT_THRESHOLDS.equals(textField.getName())) {
                percentThresholds = textField.getText().toString();
            }
        }

    }

    public void reset() {
        binWidthTextfieldContainer.reset();
        binMinTextfieldContainer.reset();
        binMaxTextfieldContainer.reset();
        plotsDomainLowTextfieldContainer.reset();
        plotsDomainHighTextfieldContainer.reset();
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

    public int plotSizeHeight() {
        return plotSizeHeight;
    }

    public int plotSizeWidth() {
        return plotSizeWidth;
    }





    // "Fields" Tab Variables and Components


    public boolean includeMedian() {
        return includeMedian;
    }

    public boolean includeHistogramStats() {
        return includeHistogramStats;
    }

    public List<Integer> getPercentThresholdsList() {
        List<Integer> percentThresholdsList = new ArrayList<Integer>();

        String[] thresholds = percentThresholds.split(",");


        for (String threshold : thresholds) {
            if (threshold != null) {
                threshold.trim();
                if (threshold.length() > 0) {
                    int value;
                    try {
                        value = Integer.parseInt(threshold);
                        if (value < 0 || value > 100) {
                            JOptionPane.showMessageDialog(getParentDialogContentPane,
                                    "ERROR: Valid " + StatisticsToolView.PARAM_LABEL_PERCENT_THRESHOLDS + " range is (0 to 100)",
                                    "Invalid Input",
                                    JOptionPane.ERROR_MESSAGE);
                            return null;

                        } else {
                            percentThresholdsList.add(value);
                        }
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(getParentDialogContentPane,
                                StatisticsToolView.PARAM_LABEL_PERCENT_THRESHOLDS + "field " + exception.toString(),
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                }
            }
        }

        return percentThresholdsList;
    }



    public boolean isIncludeFileName() {
        return includeFileName;
    }

    public boolean isIncludeBandName() {
        return includeBandName;
    }

    public boolean isIncludeBandUnits() {
        return includeBandUnits;
    }

    public boolean isIncludeMaskName() {
        return includeMaskName;
    }

    public boolean isIncludeDateTime() {
        return includeDateTime;
    }

    public boolean isIncludeValidPixExp() {
        return includeValidPixExp;
    }

    public boolean isIncludeDescription() {
        return includeDescription;
    }

    public boolean isIncludeProductFormat() {
        return includeProductFormat;
    }


    public boolean isIncludeTimeSeriesFields() {
        return includeTimeSeriesFields;
    }



    // "Text" Tab Variables and Components

    public int colCharWidth() {
        return colCharWidth;
    }

    public int decimalPlaces() {
        return decimalPlaces;
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
    //------------------------------- PUBLIC TABBED PANEL -------------------------------------
    //

    // "Bins" Tab Variables and Components

    public JTabbedPane getCriteriaFormattingTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Bins", getBinningCriteriaPanel());
        tabbedPane.setToolTipTextAt(0, "Histogram statistics binning criteria");

        tabbedPane.addTab("Fields", getFieldOptionsPanel());
        tabbedPane.setToolTipTextAt(1, "Statistic fields to display within text and spreadsheet");

        tabbedPane.addTab("Text", getTextOptionsPanel());
        tabbedPane.setToolTipTextAt(2, "Text and spreadsheet formatting");

        tabbedPane.addTab("Plots", getPlotsOptionsPanel());
        tabbedPane.setToolTipTextAt(3, "Plot formatting");

        tabbedPane.addTab("View", getViewPanel());
        tabbedPane.setToolTipTextAt(4, "View options");

        return  tabbedPane;

    }


    //
    //------------------------------- PRIVATE TABBED PANELS -------------------------------------
    //

    // "Bins" Tab Variables and Components

    private JPanel getBinningCriteriaPanel() {

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


    // "Fields" Tab Variables and Components


    private JPanel getFieldOptionsPanel() {

        JPanel thresholdsPanel = getThresholdsPanel();


        final JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        gbc.weighty = 0;
        gbc.insets.top = 5;
        panel.add(includeMedianCheckBox, gbc);


        gbc.insets.top = 0;
        gbc.gridy += 1;
        panel.add(includeHistogramStatsCheckBox, gbc);


        gbc.gridy += 1;
        panel.add(thresholdsPanel, gbc);


        // Add filler panel at bottom which expands as needed to force all components within this panel to the top
        gbc = GridBagUtils.restoreConstraints(gbc);
        gbc.weighty = 1;
        gbc.gridy += 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }


    // "Text" Tab Variables and Components

    private JPanel getTextOptionsPanel() {

        JPanel decimalPlacesPanel = getDecimalPlacesPanel();


        final JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        gbc.weighty = 0;
        gbc.insets.top = 5;
        panel.add(decimalPlacesPanel, gbc);


        gbc.insets.top = 0;
        gbc.gridy += 1;
        panel.add(getColWidthPanel(), gbc);


        // Add filler panel at bottom which expands as needed to force all components within this panel to the top
        gbc = GridBagUtils.restoreConstraints(gbc);
        gbc.weighty = 1;
        gbc.gridy += 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }


    // "Plots" Tab Variables and Components


    private JPanel getPlotsOptionsPanel() {

        final JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();

        gbc.weighty = 0;
        gbc.insets.top = 5;
        panel.add(getPlotsThreshDomainSpanPanel(), gbc);

        gbc.insets.top = 0;
        gbc.gridy += 1;
        panel.add(getPlotsDomainSpanPanel(), gbc);

        // todo PlotSize
        gbc.gridy += 1;
        panel.add(getPlotsSizePanel(), gbc);


        // Add filler panel at bottom which expands as needed to force all components within this panel to the top
        gbc = GridBagUtils.restoreConstraints(gbc);
        gbc.weighty = 1;
        gbc.gridy += 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }




    // "View" Tab Variables and Components

    private JPanel getViewPanel() {

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


    // "Fields" Tab Variables and Components

    private JPanel getThresholdsPanel() {

        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 3;
        panel.add(percentThresholdsLabel, gbc);
        gbc.insets.right = 0;
        gbc.gridx++;
        panel.add(percentThresholdsTextField, gbc);

        return panel;
    }



    // "Text" Tab Variables and Components


    private JPanel getColWidthPanel() {


        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 3;
        panel.add(spreadsheetColWidthTextfieldContainer.getLabel(), gbc);
        gbc.insets.right = 0;
        gbc.gridx++;
        panel.add(spreadsheetColWidthTextfieldContainer.getTextfield(), gbc);

        return panel;
    }


    private JPanel getDecimalPlacesPanel() {

        JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 3;
        panel.add(decimalPlacesTextfieldContainer.getLabel(), gbc);
        gbc.insets.right = 0;
        gbc.gridx++;
        panel.add(decimalPlacesTextfieldContainer.getTextfield(), gbc);

        return panel;
    }

    // "Plots" Tab Variables and Components

    private JPanel getPlotsThreshDomainSpanPanel() {

        return getCheckboxTextFieldGroupPanel(plotsThreshDomainSpanCheckBox, plotsThreshDomainLowTextfieldContainer, plotsThreshDomainHighTextfieldContainer);

    }


    private JPanel getPlotsDomainSpanPanel() {


        return getCheckboxTextFieldGroupPanel(plotsDomainSpanCheckBox, plotsDomainLowTextfieldContainer, plotsDomainHighTextfieldContainer);

    }



    private JPanel getPlotsSizePanel() {

        return getCheckboxTextFieldGroupPanel(plotsSizeCheckBox, plotsSizeWidthTextfieldContainer, plotsSizeHeightTextfieldContainer);

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

        exactPlotSize = plotsSizeCheckBox.isSelected();
        plotsDomainSpan = plotsDomainSpanCheckBox.isSelected();
        plotsThreshDomainSpan = plotsThreshDomainSpanCheckBox.isSelected();

        if (plotsSizeCheckBox.isSelected()) {
            if (plotsSizeHeightTextfieldContainer != null && plotsSizeHeightTextfieldContainer.isValid(true) && plotsSizeHeightTextfieldContainer.getValue() != null) {
                plotSizeHeight = plotsSizeHeightTextfieldContainer.getValue().intValue();
            } else {
                return false;
            }

            if (plotsSizeWidthTextfieldContainer != null && plotsSizeWidthTextfieldContainer.isValid(true) && plotsSizeWidthTextfieldContainer.getValue() != null) {
                plotSizeWidth = plotsSizeWidthTextfieldContainer.getValue().intValue();
            } else {
                return false;
            }
        } else {
            plotSizeHeight = StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE_HEIGHT;
            plotSizeWidth = StatisticsToolView.PARAM_DEFVAL_PLOTS_SIZE_WIDTH;
        }


        if (plotsDomainSpanCheckBox.isSelected()) {
            if (plotsDomainLowTextfieldContainer != null && plotsDomainLowTextfieldContainer.isValid(true) && plotsDomainLowTextfieldContainer.getValue() != null) {
                plotsDomainLow = plotsDomainLowTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }


            if (plotsDomainHighTextfieldContainer != null && plotsDomainHighTextfieldContainer.isValid(true) && plotsDomainHighTextfieldContainer.getValue() != null) {
                plotsDomainHigh = plotsDomainHighTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }
        } else {
            plotsDomainLow = StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_LOW;
            plotsDomainHigh = StatisticsToolView.PARAM_DEFVAL_PLOTS_DOMAIN_HIGH;
        }


        if (plotsThreshDomainSpanCheckBox.isSelected()) {
            if (plotsThreshDomainLowTextfieldContainer != null && plotsThreshDomainLowTextfieldContainer.isValid(true) && plotsThreshDomainLowTextfieldContainer.getValue() != null) {
                plotsThreshDomainLow = plotsThreshDomainLowTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }


            if (plotsThreshDomainHighTextfieldContainer != null && plotsThreshDomainHighTextfieldContainer.isValid(true) && plotsThreshDomainHighTextfieldContainer.getValue() != null) {
                plotsThreshDomainHigh = plotsThreshDomainHighTextfieldContainer.getValue().doubleValue();
            } else {
                return false;
            }
        } else {
            plotsThreshDomainLow = StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_LOW;
            plotsThreshDomainHigh = StatisticsToolView.PARAM_DEFVAL_PLOTS_THRESH_DOMAIN_HIGH;
        }


        {
            String lowName = plotsThreshDomainLowTextfieldContainer.getName();
            String highName = plotsThreshDomainHighTextfieldContainer.getName();
            double lowVal = plotsThreshDomainLowTextfieldContainer.getValue().doubleValue();
            double highVal = plotsThreshDomainHighTextfieldContainer.getValue().doubleValue();
            if (!compareFields(lowVal, highVal, lowName, highName, true)) {
                return false;
            }
        }

        {
            String lowName = plotsDomainLowTextfieldContainer.getName();
            String highName = plotsDomainHighTextfieldContainer.getName();
            double lowVal = plotsDomainLowTextfieldContainer.getValue().doubleValue();
            double highVal = plotsDomainHighTextfieldContainer.getValue().doubleValue();
            if (!compareFields(lowVal, highVal, lowName, highName, true)) {
                return false;
            }
        }


        if (spreadsheetColWidthTextfieldContainer != null && spreadsheetColWidthTextfieldContainer.isValid(true) && spreadsheetColWidthTextfieldContainer.getValue() != null) {
            colCharWidth = spreadsheetColWidthTextfieldContainer.getValue().intValue();
        } else {
            return false;
        }

        if (decimalPlacesTextfieldContainer != null && decimalPlacesTextfieldContainer.isValid(true) && decimalPlacesTextfieldContainer.getValue() != null) {
            decimalPlaces = decimalPlacesTextfieldContainer.getValue().intValue();
        } else {
            return false;
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

    public String getPreferencesPercentThresholds() {

        if (configuration != null) {
            return configuration.getPropertyString(StatisticsToolView.PARAM_KEY_PERCENT_THRESHOLDS, StatisticsToolView.PARAM_DEFVAL_PERCENT_THRESHOLDS);
        } else {
            return StatisticsToolView.PARAM_DEFVAL_PERCENT_THRESHOLDS;
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
