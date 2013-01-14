package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ColorPaletteDef;
import org.esa.beam.framework.datamodel.Scaling;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: dshea
 * User: aabduraz
 * Date: 4/10/12
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ColorPaletteChooser extends JComboBox {
    private final int COLORBAR_HEIGHT = 15;
    private final int COLORBAR_WIDTH = 204;
    public final String DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME = "defaultGrayColor.cpd";

    private File colorPaletteDir;
    private Dimension colorBarDimension;
    private ComboBoxModel colorModel;
    private ArrayList<ImageIcon> icons;

    //private ColorHashMap colorBarMap;
    private HashMap<String, ColorRamp> colorBarMap;
    private double colorBarMin;
    private double colorBarMax;
    private boolean isDiscrete;
    private int currentColorBarIndex;
    private ColorPaletteDef currentCPD;

    public ColorPaletteChooser(File colorPaletteDir) {
        super();
        colorBarDimension = new Dimension(COLORBAR_WIDTH, COLORBAR_HEIGHT);
        this.colorPaletteDir = colorPaletteDir;
        colorBarMap = new HashMap();
        updateDisplay();
        setColorPaletteDir(colorPaletteDir);
        setEditable(false);
        setRenderer(new ComboBoxRenderer());
    }

    public ColorPaletteChooser(File colorPaletteDir, ColorPaletteDef defaultColorPaletteDef) {
        super();
        colorBarDimension = new Dimension(COLORBAR_WIDTH, COLORBAR_HEIGHT);
        this.colorPaletteDir = colorPaletteDir;
        colorBarMap = new HashMap();
        createDefaultGrayColorPaletteFile(defaultColorPaletteDef);
        updateDisplay();
        setColorPaletteDir(colorPaletteDir);
        setEditable(false);
        setRenderer(new ComboBoxRenderer());
    }

    public void distributeSlidersEvenly(ColorPaletteDef colorPaletteDef) {
        final double pos1 = colorPaletteDef.getMinDisplaySample();
        final double pos2 = colorPaletteDef.getMaxDisplaySample();
        final double delta = pos2 - pos1;
        final double evenSpace = delta / (colorPaletteDef.getNumPoints() - 1);
        for (int i = 1; i < colorPaletteDef.getNumPoints() - 1; i++) {
            final double value = pos1 + evenSpace * i;
            colorPaletteDef.getPointAt(i).setSample(value);
        }
    }

    private void drawPalette(Graphics2D g2, ColorPaletteDef colorPaletteDef, Rectangle paletteRect) {
        int paletteX1 = paletteRect.x;
        int paletteX2 = paletteRect.x + paletteRect.width;

        g2.setStroke(new BasicStroke(1.0f));
        Color[] colorPalette = colorPaletteDef.createColorPalette(Scaling.IDENTITY);
        int divisor = paletteX2 - paletteX1;
        for (int x = paletteX1; x < paletteX2; x++) {

            int palIndex = ((colorPalette.length * (x - paletteX1)) / divisor);

            if (palIndex < 0) {
                palIndex = 0;
            }
            if (palIndex >= colorPalette.length) {
                palIndex = colorPalette.length - 1;
            }
            g2.setColor(colorPalette[palIndex]);
            g2.drawLine(x, paletteRect.y, x, paletteRect.y + paletteRect.height);
        }
    }

    private ImageIcon createGrayColorBarIcon(ColorPaletteDef colorPaletteDef, Dimension dimension) {
        BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        drawPalette(g2, colorPaletteDef, new Rectangle(dimension));
        ImageIcon icon = new ImageIcon(bufferedImage);
        colorPaletteDef.setCpdFileName(DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME);
        icon.setDescription(DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME);
        colorBarMap.put(DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME, new ColorRamp(colorPaletteDef.getCpdFileName(), colorPaletteDef, colorPaletteDef.getMinDisplaySample(), colorPaletteDef.getMaxDisplaySample()));
        return icon;
    }

    private String colorBarMinMaxDescription() {
        return "  cpd file min: " + colorBarMin + "   cpd file max: " + colorBarMax + "discrete: " + isDiscrete;
    }

    private void createDefaultGrayColorPaletteFile(ColorPaletteDef defaultGrayColorPaletteDef) {

        File grayColorFile = new File(colorPaletteDir, DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME);
        try {
            defaultGrayColorPaletteDef.setAutoDistribute(true);
            ColorPaletteDef.storeColorPaletteDef(defaultGrayColorPaletteDef, grayColorFile);
        } catch (IOException ioe) {
            System.err.println("Default Gray Color File is not created!");
        }
    }

    private void drawPalette(Graphics2D g2, File paletteFile, Rectangle paletteRect) throws IOException {
        ColorPaletteDef colorPaletteDef = ColorPaletteDef.loadColorPaletteDefForColorBar(paletteFile);
        updateColorBarMinMax(colorPaletteDef);
        distributeSlidersEvenly(colorPaletteDef);
        drawPalette(g2, colorPaletteDef, paletteRect);
    }

    private ImageIcon createColorBarIcon(ColorPaletteDef colorPaletteDef, Dimension dimension) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        distributeSlidersEvenly(colorPaletteDef);
        drawPalette(g2, colorPaletteDef, new Rectangle(dimension));
        return new ImageIcon(bufferedImage);
    }

    private ImageIcon createColorBarIcon(File cpdFile, Dimension dimension) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        drawPalette(g2, cpdFile, new Rectangle(dimension));
        ImageIcon icon = new ImageIcon(bufferedImage);
        icon.setDescription(cpdFile.getName());
        ColorPaletteDef colorPaletteDef = ColorPaletteDef.loadColorPaletteDefForColorBar(cpdFile);
        colorBarMap.put(cpdFile.getName(), new ColorRamp(colorPaletteDef.getCpdFileName(), colorPaletteDef, colorPaletteDef.getMinDisplaySample(), colorPaletteDef.getMaxDisplaySample()));
        updateColorBarMinMax(colorPaletteDef);
        return icon;
    }

    private void updateColorBarMinMax(ColorPaletteDef colorPaletteDef) {
        colorBarMin = colorPaletteDef.getMinDisplaySample();
        colorBarMax = colorPaletteDef.getMaxDisplaySample();
        isDiscrete = colorPaletteDef.isDiscrete();
    }

    ComboBoxModel createColorBarModel() {
        ArrayList<ImageIcon> icons = new ArrayList<ImageIcon>();
        File[] files = colorPaletteDir.listFiles();
        ImageIcon defaultIcon = null;
        for (File file : files) {
            try {
                ImageIcon icon = createColorBarIcon(file, colorBarDimension);
                icons.add(icon);
                if (file.getName().indexOf(DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME) != -1) {
                    defaultIcon = icon;
                }
            } catch (IOException e) {
            }
        }
        Collections.sort(icons, new Comparator<ImageIcon>() {
            @Override
            public int compare(ImageIcon o1, ImageIcon o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });
        this.icons = icons;
        this.icons.remove(defaultIcon);
        this.icons.add(0, defaultIcon);
        DefaultComboBoxModel colorBarModel = new DefaultComboBoxModel(icons.toArray(new ImageIcon[icons.size()]));
        colorBarModel.setSelectedItem(defaultIcon);
        return colorBarModel;
    }

    private void updateDisplay() {
        colorModel = createColorBarModel();
        setModel(colorModel);
    }

    public void updateColorPalette(ColorPaletteDef colorPaletteDef) {
        currentCPD = colorPaletteDef;

        ImageIcon currentColorBarIcon, newIcon = null;

        if (colorPaletteDef.getNumPoints() == 3) {
            createDefaultGrayColorPaletteFile(colorPaletteDef);
            currentColorBarIcon = createGrayColorBarIcon(colorPaletteDef, colorBarDimension);
            colorPaletteDef.setCpdFileName(DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME);
            icons.add(currentColorBarIcon);
            colorModel.setSelectedItem(currentColorBarIcon);
            setModel(colorModel);
        } else {
            currentColorBarIcon = (ImageIcon) colorModel.getSelectedItem();

            try {
                newIcon = createColorBarIcon(colorPaletteDef, colorBarDimension);
                newIcon.setDescription(colorPaletteDef.getCpdFileName());
            } catch (IOException ioe) {

            }
            ColorRamp cr = colorBarMap.get(colorPaletteDef.getCpdFileName());
            cr.setColorPaletteDef(colorPaletteDef);
            colorBarMap.put(colorPaletteDef.getCpdFileName(), cr);
            icons.remove(currentColorBarIcon);
            icons.add(newIcon);

            if (newIcon != null) {
                colorModel.setSelectedItem(newIcon);
                setModel(colorModel);
            }
        }
    }

    protected void updateColorBar(ColorPaletteDef colorPaletteDef) {

        currentCPD = colorPaletteDef;

        System.out.println("isDiscrete Changed!");

        ImageIcon newIcon = new ImageIcon();
        try {

            newIcon = createColorBarIcon(colorPaletteDef, colorBarDimension);
        } catch (IOException ioe) {

        }


        validate();
        repaint();

    }

    public File getColorPaletteDir() {
        return colorPaletteDir;
    }

    public void setColorPaletteDir(File colorPaletteDir) {
        this.colorPaletteDir = colorPaletteDir;
    }

    public Dimension getColorBarDimension() {
        return colorBarDimension;
    }

    public void setColorBarDimension(Dimension colorBarDimension) {
        this.colorBarDimension = colorBarDimension;
    }

    protected void setColorPaletteDef(ColorPaletteDef colorPaletteDef) {
        currentCPD = colorPaletteDef;
    }

    private ColorRamp getCurrentColorRamp(){
        String cpdFileName = ((ImageIcon) colorModel.getSelectedItem()).getDescription();

        if (cpdFileName == null) {
            cpdFileName = DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME;
        }
        ColorRamp cr =  colorBarMap.get(cpdFileName);
       return cr;
    }

    public ColorPaletteDef getSelectedColorPaletteDef() {

        return getCurrentColorRamp().getColorPaletteDef();
    }

    protected File getSelectedColorPaletteDefCPDFile(){
        String cpdFileName = ((ImageIcon) colorModel.getSelectedItem()).getDescription();

        if (cpdFileName == null) {
            cpdFileName = DEFAULT_GRAY_COLOR_PALETTE_FILE_NAME;
        }

        return new File(colorPaletteDir, cpdFileName);
        //return null;
    }

    public double getColorBarMin() {
        return getCurrentColorRamp().getCpdFileMin();
    }

    public double getColorBarMax() {
        return getCurrentColorRamp().getCpdFileMax();
    }

    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        public ComboBoxRenderer() {
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        /*
        * This method finds the image and text corresponding
        * to the selected value and returns the label, set up
        * to display the text and image.
        */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            ImageIcon icon = (ImageIcon) value;
            BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth() + 2, icon.getIconHeight() + 2,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            if (isSelected) {
                g2.setColor(Color.darkGray);
            } else {
                g2.setColor(list.getParent().getBackground());
            }
            g2.fillRect(0, 0, icon.getIconWidth() + 2, icon.getIconHeight() + 2);
            g2.drawImage(icon.getImage(), 1, 1, null);

            setIcon(new ImageIcon(bufferedImage));
            setToolTipText(icon.getDescription());

            return this;
        }

    }

    private class ColorHashMap extends HashMap<Color[], ImageIcon> {
        ColorHashMap() {
            super();
        }

        protected ImageIcon getImageIcon(Color[] currentColors) {
            Set<Color[]> keys = keySet();
            Iterator itr = keys.iterator();
            Color[] colors;
            while (itr.hasNext()) {
                colors = (Color[]) itr.next();
                if (Arrays.equals(colors, currentColors)) {
                    return get(colors);
                }

            }
            return null;
        }


    }

    private class ColorRamp {
        private ColorPaletteDef colorPaletteDef;
        private String cpdFileName;
        private double cpdFileMax;
        private double cpdFileMin;

        public ColorRamp() {
            this(null, null, 0, 0);
        }

        public ColorRamp(String cpdFileName, ColorPaletteDef colorPaletteDef, double cpdFileMin, double cpdFileMax) {
            this.cpdFileName = cpdFileName;
            this.colorPaletteDef = colorPaletteDef;
            this.cpdFileMin = colorBarMin;
            this.cpdFileMax = cpdFileMax;
        }

        public ColorPaletteDef getColorPaletteDef() {
            return colorPaletteDef;
        }

        public void setColorPaletteDef(ColorPaletteDef colorPaletteDef) {
            this.colorPaletteDef = colorPaletteDef;
        }

        public String getCpdFileName() {
            return cpdFileName;
        }

        public void setCpdFileName(String cpdFileName) {
            this.cpdFileName = cpdFileName;
        }

        public double getCpdFileMax() {
            return cpdFileMax;
        }

        public void setCpdFileMax(double cpdFileMax) {
            this.cpdFileMax = cpdFileMax;
        }

        public double getCpdFileMin() {
            return cpdFileMin;
        }

        public void setCpdFileMin(double cpdFileMin) {
            this.cpdFileMin = cpdFileMin;
        }
    }
//    private class ColorHashMap extends HashMap<String, ImageIcon> {
//        ColorHashMap() {
//            super();
//        }
//
//        protected ImageIcon getImageIcon(String cpdFileName) {
//            Set<String> keys = keySet();
//            Iterator itr = keys.iterator();
//            String fileName;
//            while (itr.hasNext()) {
//                fileName = (String) itr.next();
//                if (fileName.equals(cpdFileName)) {
//                    return get(fileName);
//                }
//            }
//            return null;
//        }
//
//
//    }


}
