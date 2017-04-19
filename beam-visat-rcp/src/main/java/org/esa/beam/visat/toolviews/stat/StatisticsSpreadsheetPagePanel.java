package org.esa.beam.visat.toolviews.stat;

import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.Stx;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.application.ToolView;
import org.esa.beam.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * Created by knowles on 4/19/17.
 */
class StatisticsSpreadsheetPagePanel extends PagePanel {

    private static final String TITLE_PREFIX = "Statistics Spreadsheet";

    private final StatisticsSpreadsheetPagePanel.PopupHandler popupHandler;
    private final StringBuilder resultText;
    private boolean init;

    private Object[][] statsSpreadsheet;
    private StatisticsCriteriaPanel statisticsCriteriaPanel;


    public StatisticsSpreadsheetPagePanel(final ToolView parentDialog, String helpID, StatisticsCriteriaPanel statisticsCriteriaPanel, Object[][] statsSpreadsheet) {
        super(parentDialog, helpID, TITLE_PREFIX);

        this.statsSpreadsheet = statsSpreadsheet;
        this.statisticsCriteriaPanel = statisticsCriteriaPanel;

//        setMinimumSize(new Dimension(1000, 390));
        resultText = new StringBuilder("");
        popupHandler = new StatisticsSpreadsheetPagePanel.PopupHandler();
//        if (visatApp != null) {
//            this.configuration = visatApp.getPreferences();
//        }

    }

    @Override
    protected void initComponents() {
        init = true;

        JPanel statsSpreadsheetPane = statsSpreadsheetPanel();

        add(statsSpreadsheetPane);
        setPreferredSize(statsSpreadsheetPane.getPreferredSize());

        resultText.setLength(0);
        resultText.append(createText());

    }


    @Override
    protected void updateComponents() {
        if (!init) {
            initComponents();
        }

    }

        @Override
    protected String getDataAsText() {
        return resultText.toString();
    }


    private class PopupHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == 2 || e.isPopupTrigger()) {
                final JPopupMenu menu = new JPopupMenu();
                menu.add(createCopyDataToClipboardMenuItem());
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }



    private JPanel statsSpreadsheetPanel() {

        JPanel pane = GridBagUtils.createPanel();

        if (statsSpreadsheet == null) {
            return pane;
        }
        //     pane.setBorder(UIUtils.createGroupBorder("Statistics Spreadsheet")); /*I18N*/
        GridBagConstraints gbcMain = GridBagUtils.createConstraints("");
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weighty = 1.0;
        gbcMain.anchor = GridBagConstraints.NORTHWEST;




        TableModel tableModel = new DefaultTableModel(statsSpreadsheet, statsSpreadsheet[0]) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex <= 1 ? String.class : Number.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        final JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Number.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Float || value instanceof Double) {
                    setHorizontalTextPosition(RIGHT);
                    setText(getFormattedValue((Number) value));
                }
                return label;
            }

            private String getFormattedValue(Number value) {
                if (value.doubleValue() < 0.001 && value.doubleValue() > -0.001 && value.doubleValue() != 0.0) {
                    return new DecimalFormat("0.####E0").format(value.doubleValue());
                }
                String format = "%." + Integer.toString(statisticsCriteriaPanel.decimalPlaces()) + "f";

                return String.format(format, value.doubleValue());
            }
        });
        table.addMouseListener(popupHandler);


        FontMetrics fm = table.getFontMetrics(table.getFont());
        TableColumn column = null;

        // int colPreferredWidthData = fm.stringWidth("StandardDeviation(LogBinned):") + 10;

        StringBuilder sampleEntry = new StringBuilder("");
        for (int i = 1; i < statisticsCriteriaPanel.colCharWidth(); i++) {
            sampleEntry.append("n");
        }

        int colPreferredWidthData = fm.stringWidth(sampleEntry.toString());
        int tableWidth = 0;
        int bufferWidth = fm.stringWidth("nn");


        for (int i = 0; i < statsSpreadsheet[0].length; i++) {
            column = table.getColumnModel().getColumn(i);

            if (statisticsCriteriaPanel.colCharWidth() < 8) {
                String header = statsSpreadsheet[0][i].toString();
                int headerWidth = fm.stringWidth(header) + bufferWidth;

                column.setPreferredWidth(headerWidth);
                column.setMaxWidth(headerWidth);
                column.setMinWidth(headerWidth);
                tableWidth += headerWidth;
            } else {
                column.setPreferredWidth(colPreferredWidthData);
                column.setMaxWidth(colPreferredWidthData);
                column.setMinWidth(colPreferredWidthData);
                tableWidth += colPreferredWidthData;
            }
        }


        //  table.setPreferredSize(new Dimension(tableWidth, table.getRowCount() * table.getRowHeight()));

        pane.add(table, gbcMain);
        //  pane.setMinimumSize(new Dimension(tableWidth, table.getRowCount() * table.getRowHeight()));


        return pane;
    }


    private String createText() {

        if (statsSpreadsheet == null || statsSpreadsheet.length == 0 || statsSpreadsheet[0].length == 0) {
            return "No Statistics Processed";
        }

        final StringBuilder sb = new StringBuilder();


        int numRows = statsSpreadsheet.length;
        int numCols = statsSpreadsheet[0].length;

        for (int rowIdx = 0; rowIdx < statsSpreadsheet.length; rowIdx++) {

            for (int colIdx = 0; colIdx < statsSpreadsheet[0].length; colIdx++) {
                Object valueObject = statsSpreadsheet[rowIdx][colIdx];

                if (valueObject == null) {
                    sb.append("");
                } else if (valueObject instanceof Float || valueObject instanceof Double) {
                        String valueFormatted = getFormattedValue((Number) valueObject);
                        sb.append(valueFormatted);
                } else {
                    sb.append(valueObject.toString());
                }

                if (colIdx < statsSpreadsheet[0].length -1) {
                    sb.append("\t");
                }

            }

            sb.append("\n");
        }


        return sb.toString();
    }


    private String getFormattedValue(Number value) {
        if (value.doubleValue() < 0.001 && value.doubleValue() > -0.001 && value.doubleValue() != 0.0) {
            return new DecimalFormat("0.####E0").format(value.doubleValue());
        }
        String format = "%." + Integer.toString(statisticsCriteriaPanel.decimalPlaces()) + "f";

        return String.format(format, value.doubleValue());
    }

}
