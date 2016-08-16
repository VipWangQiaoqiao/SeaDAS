package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ColorPaletteDef;
import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.math.Range;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class ColorPaletteChooser extends JComboBox<ColorPaletteChooser.ColorPaletteWrapper> {

    private final String DERIVED_FROM = "Current: ";
    private final String UNNAMED = "unnamed";
    private boolean discreteDisplay;
    private boolean log10Display;
    private String selectedName = "";

    public ColorPaletteChooser() {
        super(getPalettes());
        ListCellRenderer<ColorPaletteWrapper> renderer = createPaletteRenderer();
        setRenderer(renderer);
        setEditable(false);
    }

    public void resetRenderer() {
        ListCellRenderer<ColorPaletteWrapper> renderer = createPaletteRenderer();
        setRenderer(renderer);
    }
    public void removeUserDefinedPalette() {
        final String name = getItemAt(0).name;
        if (UNNAMED.equals(name) || name.startsWith(DERIVED_FROM)) {
            removeItemAt(0);
        }
    }

    public ColorPaletteDef getSelectedColorPaletteDefinition() {
        final int selectedIndex = getSelectedIndex();
        final ComboBoxModel<ColorPaletteWrapper> model = getModel();
        final ColorPaletteWrapper colorPaletteWrapper = model.getElementAt(selectedIndex);
        final ColorPaletteDef cpd = colorPaletteWrapper.cpd;
        cpd.getFirstPoint().setLabel(colorPaletteWrapper.name);
        return cpd;
    }

    public void setSelectedColorPaletteDefinition(ColorPaletteDef cpd) {
        removeUserDefinedPalette();

        // DANNY removed this because it caused a tooltip bug since the list size changes if the following is invoked
        // by adding/removed the userDefinedPalette at index 0
//        final ComboBoxModel<ColorPaletteWrapper> model = getModel();
//        for (int i = 0; i < model.getSize(); i++) {
//            if (model.getElementAt(i).cpd.equals(cpd)) {
//                setSelectedIndex(i);
//                return;
//            }
//        }

        setUserDefinedPalette(cpd);
    }

    public void reloadPalettes() {
        setModel(new DefaultComboBoxModel<>(getPalettes()));
        repaint();
    }

    private void setUserDefinedPalette(ColorPaletteDef userPalette) {
        final String suffix = userPalette.getFirstPoint().getLabel();
        final String name;
        if (suffix != null && suffix.trim().length() > 0) {
            name = DERIVED_FROM + " " + suffix.trim();
        } else {
            name = UNNAMED;
        }
        final ColorPaletteWrapper item = new ColorPaletteWrapper(name, userPalette);
        insertItemAt(item, 0);
        setSelectedIndex(0);
        setSelectedName(name);
    }



    private static Vector<ColorPaletteWrapper> getPalettes() {

        final List<ColorPaletteDef> defList = ColorPalettesManager.getColorPaletteDefList();
        final Vector<ColorPaletteWrapper> paletteWrappers = new Vector<>();
        for (ColorPaletteDef colorPaletteDef : defList) {

            // todo Danny removed this so .cpd would show in name
            // makes it a little more obvious that you are loading a file
            //     final String nameFor = getNameForWithoutExtension(colorPaletteDef);
            final String nameFor = ColorPalettesManager.getNameFor(colorPaletteDef);
            paletteWrappers.add(new ColorPaletteWrapper(nameFor, colorPaletteDef));
        }

        return paletteWrappers;
    }




    private static String getNameForWithoutExtension(ColorPaletteDef colorPaletteDef) {
        final String nameFor = ColorPalettesManager.getNameFor(colorPaletteDef);
        if (nameFor.toLowerCase().endsWith(".cpd")) {
            return nameFor.substring(0, nameFor.length() - 4);
        } else {
            return nameFor;
        }
    }


    private ListCellRenderer<ColorPaletteWrapper> createPaletteRenderer() {


        return new ListCellRenderer<ColorPaletteWrapper>() {


            @Override
            public Component getListCellRendererComponent(JList<? extends ColorPaletteWrapper> list, ColorPaletteWrapper value, final int index, final boolean isSelected, boolean cellHasFocus) {

                final ColorPaletteDef cpd = value.cpd;
                final JLabel rampComp = new JLabel(" ") {
                    @Override
                    public void paint(Graphics g) {
                        super.paint(g);
                        drawPalette((Graphics2D) g, cpd, g.getClipBounds().getSize(), isSelected, index);
                    }
                };


                if (isSelected) {
                    StringBuilder toolTipStringBuilder = new StringBuilder("");

                    if (cpd != null) {
                        String fileName = ColorPalettesManager.getNameFor(cpd);
                        if (fileName != null && fileName.length() > 0) {
                            toolTipStringBuilder.append(ColorPalettesManager.getNameFor(cpd));

                            String shortDescription = cpd.getShortDescription();
                            if (shortDescription != null && shortDescription.length() > 0) {
                                toolTipStringBuilder.append(" (" +shortDescription+")");
                            }
                        }
                        list.setToolTipText(toolTipStringBuilder.toString());
                    }


                }

                //    setToolTipText("Note that the color palette definition data has been stored within the current band and any subsequent alterations to the cpd file will not show up unless the file is reloaded");



                JPanel palettePanel = GridBagUtils.createPanel();
                GridBagConstraints gbc = new GridBagConstraints();

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.WEST;

                gbc.gridy = 0;
                gbc.gridx = 0;
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.insets = new Insets(0, 0, 0, 0);
                palettePanel.add(rampComp, gbc);

                return palettePanel;
            }

        };

    }

    private void drawPalette(Graphics2D g2, ColorPaletteDef colorPaletteDef, Dimension paletteDim, boolean isSelected, int index) {
        final int width = paletteDim.width;
        final int height = paletteDim.height;

        final ColorPaletteDef cpdCopy = colorPaletteDef.createDeepCopy();
        cpdCopy.setDiscrete(discreteDisplay);
        cpdCopy.setNumColors(width);
        final ImageInfo imageInfo = new ImageInfo(cpdCopy);
        imageInfo.setLogScaled(log10Display);
        imageInfo.setLogScaled(colorPaletteDef.isLogScaled());

        Color[] colorPalette = ImageManager.createColorPalette(imageInfo);

        g2.setStroke(new BasicStroke(2.0f));

        for (int x = 0; x < width; x = x + 2) {

            if (index == 0) {
                g2.setColor(UIManager.getColor("Panel.background"));
                g2.drawLine(x, 0, x, height);
            } else if (isSelected) {
                if (x == 0 || x >= width - 1) {
                    g2.setColor(Color.blue);
                    g2.drawLine(x, 0, x, height);
                } else {
                    int edgeThickness = 1;
                    g2.setColor(colorPalette[x]);
                    g2.drawLine(x, (edgeThickness + 1), x, height - (edgeThickness + 1));
                    g2.setColor(Color.blue);
                    g2.drawLine(x, 0, x, edgeThickness);
                    g2.drawLine(x, height - edgeThickness, x, height);
                }
            } else {
                g2.setColor(colorPalette[x]);
                g2.drawLine(x, 1, x, height);
                g2.setColor(UIManager.getColor("Panel.background"));
                g2.drawLine(x, 0, x, 1);
            }
        }
    }

    public void setLog10Display(boolean log10Display) {
        this.log10Display = log10Display;
        repaint();
    }

    public void setDiscreteDisplay(boolean discreteDisplay) {
        this.discreteDisplay = discreteDisplay;
        repaint();
    }

    public Range getRangeFromFile() {
        final ComboBoxModel<ColorPaletteWrapper> model = getModel();
        final int selectedIndex = getSelectedIndex();
        final ColorPaletteWrapper paletteWrapper = model.getElementAt(selectedIndex);
        String name = paletteWrapper.name;
        final ColorPaletteDef cpd;
        if (name.startsWith(DERIVED_FROM)) {
            name = name.substring(DERIVED_FROM.length()).trim();
            if (name.toLowerCase().endsWith(".cpd")) {
                name = FileUtils.getFilenameWithoutExtension(name);
            }
            cpd = findColorPalette(name);
        } else {
            cpd = paletteWrapper.cpd;
        }
        return new Range(cpd.getMinDisplaySample(), cpd.getMaxDisplaySample());
    }

    private ColorPaletteDef findColorPalette(String name) {
        final ComboBoxModel<ColorPaletteWrapper> model = getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final ColorPaletteWrapper paletteWrapper = model.getElementAt(i);
            if (paletteWrapper.name.equals(name)) {
                return paletteWrapper.cpd;
            }
        }
        return null;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public static final class ColorPaletteWrapper {

        public final String name;

        public final ColorPaletteDef cpd;

        private ColorPaletteWrapper(String name, ColorPaletteDef cpd) {
            this.name = name;
            this.cpd = cpd;
        }
    }
}
