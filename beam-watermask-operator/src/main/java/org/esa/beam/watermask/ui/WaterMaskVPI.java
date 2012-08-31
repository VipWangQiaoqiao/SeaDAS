package org.esa.beam.watermask.ui;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import com.jidesoft.action.CommandBar;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.ConvolutionFilterBand;
import org.esa.beam.framework.datamodel.Kernel;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeGroup;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.command.CommandAdapter;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.command.ExecCommand;
import org.esa.beam.visat.AbstractVisatPlugIn;
import org.esa.beam.visat.VisatApp;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.operator.FormatDescriptor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * This VISAT PlugIn registers an action  which calls the "LandWaterMask" Operator and based on its generated "water_fraction"
 * band, defines 3 masks in the currently selected product:
 * <ol>
 * <li>Water: water_fraction > 90</li>
 * <li>Land: water_fraction < 10</li>
 * <li>Coastline: water_fraction > 0.1 && water_fraction < 99.9 (meaning if water_fraction is not 0 or 100 --> it is a coastline)</li>
 * </ol>
 * <p/>
 * <p/>
 * <i>IMPORTANT Note:
 * This VISAT PlugIn is a workaround.
 * Ideally, users would register an action in BEAM's  {@code module.xml} and specify a target toolbar for it.
 * Actions specified in BEAM's  {@code module.xml} currently only appear in menus, and not in tool bars
 * (because they are hard-coded in VisatApp).
 * Since this feature is still missing in BEAM, so we have to place the action in its target tool bar
 * ("layersToolBar") manually.</i>
 *
 * @author Tonio Fincke
 * @author Danny Knowles
 * @author Marco Peters
 */
public class WaterMaskVPI extends AbstractVisatPlugIn {

    public static final String COMMAND_ID = "createLandWaterCoastMasks";

    public static final String LAND_WATER_MASK_OP_ALIAS = "LandWaterMask";
    public static final String TARGET_TOOL_BAR_NAME = "layersToolBar";
    public static final String WATER_FRACTION_BAND_NAME = "mask_data_water_fraction";
    public static final String WATER_FRACTION_SMOOTHED_NAME = "mask_data_water_fraction_smoothed";


    private static final String LAND_MASK_NAME = "LandMask";
    private static final String LAND_MASK_MATH = WATER_FRACTION_BAND_NAME + " == 0";
    private static final String LAND_MASK_DESCRIPTION = "Land pixels";
    private static final Color LAND_MASK_COLOR = new Color(100, 49, 12);
    private static final double LAND_MASK_TRANSPARENCY = 0.0;
    private static final boolean SHOW_LAND_MASK_ALL_BANDS = false;


    private static final String COASTLINE_MASK_NAME = "CoastLine";
    private static final String COASTLINE_MATH = WATER_FRACTION_SMOOTHED_NAME + " > 25 and " + WATER_FRACTION_SMOOTHED_NAME + " < 75";
    private static final String COASTLINE_MASK_DESCRIPTION = "Coastline pixels";
    private static final Color COASTLINE_MASK_COLOR = new Color(192, 192, 192);
    private static final double COASTLINE_MASK_TRANSPARENCY = 0.0;
    private static final boolean SHOW_COASTLINE_MASK_ALL_BANDS = true;


    private static final String WATER_MASK_NAME = "WaterMask";
    private static final String WATER_MASK_MATH = WATER_FRACTION_BAND_NAME + " > 0";
    private static final String WATER_MASK_DESCRIPTION = "Water pixels";
    private static final Color WATER_MASK_COLOR = new Color(0, 0, 255);
    private static final double WATER_MASK_TRANSPARENCY = 0.5;
    private static final boolean SHOW_WATER_MASK_ALL_BANDS = false;


    @Override
    public void start(final VisatApp visatApp) {
        final ExecCommand action = visatApp.getCommandManager().createExecCommand(COMMAND_ID,
                new ToolbarCommand(visatApp));
        action.setLargeIcon(UIUtils.loadImageIcon("/org/esa/beam/watermask/ui/icons/dock.gif"));

        final AbstractButton lwcButton = visatApp.createToolButton(COMMAND_ID);

        visatApp.getMainFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                CommandBar layersBar = visatApp.getToolBar(TARGET_TOOL_BAR_NAME);
                layersBar.add(lwcButton);
            }

        });
    }

    class WaterMaskData {

        private boolean testBoolean = true;

        public WaterMaskData() {

        }

        public boolean isTestBoolean() {
            return testBoolean;
        }

        public void setTestBoolean(boolean testBoolean) {
            this.testBoolean = testBoolean;
        }
    }


    public class WaterMaskSampleCheckbox {

        private WaterMaskData waterMaskData;

        private JLabel jLabel;
        private JCheckBox jCheckBox = new JCheckBox();

        public WaterMaskSampleCheckbox(WaterMaskData waterMaskData) {

            this.waterMaskData = waterMaskData;

            jLabel = new JLabel("test Checkbox");
            jLabel.setToolTipText("test toolTip");
            jCheckBox.setName("test Checkbox");
            jCheckBox.setSelected(waterMaskData.isTestBoolean());

            addControlListeners();
        }

        private void addControlListeners() {
            jCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    waterMaskData.setTestBoolean(jCheckBox.isSelected());
                }
            });
        }


        public JLabel getjLabel() {
            return jLabel;
        }

        public JCheckBox getjCheckBox() {
            return jCheckBox;
        }
    }


    class WaterMaskDialog extends JDialog {

        public WaterMaskData waterMaskData = null;


        public WaterMaskDialog(WaterMaskData waterMaskData) {
            this.waterMaskData = waterMaskData;

            initUI();
        }

        public final void initUI() {

            JPanel jPanel = GridBagUtils.createPanel();
            GridBagConstraints coastlineConstraints = new GridBagConstraints();

            final WaterMaskSampleCheckbox waterMaskSampleCheckbox = new WaterMaskSampleCheckbox(waterMaskData);


            GridBagUtils.addToPanel(jPanel, waterMaskSampleCheckbox.getjCheckBox(), coastlineConstraints,
                    "anchor=WEST, gridx=0, gridy=0");


            JButton close = new JButton("Close");
            close.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    waterMaskData.setTestBoolean(waterMaskSampleCheckbox.getjCheckBox().isSelected());
                    dispose();
                }
            });

            close.setAlignmentX(0.5f);

            GridBagUtils.addToPanel(jPanel, close, coastlineConstraints,
                    "anchor=WEST, gridx=0, gridy=0");


            add(jPanel);

            setModalityType(ModalityType.APPLICATION_MODAL);

            setTitle("About Notes");
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setSize(300, 200);
        }
    }


    private void showLandWaterCoastMasks(final VisatApp visatApp) {

        WaterMaskData waterMaskData = new WaterMaskData();
        waterMaskData.setTestBoolean(true);

        System.out.println("bool=" + waterMaskData.isTestBoolean());
        WaterMaskDialog waterMaskDialog = new WaterMaskDialog(waterMaskData);

        System.out.println("bool=" + waterMaskData.isTestBoolean());


//        //1. Create the frame.
//        JFrame frame = new JFrame("FrameDemo");
////
//////2. Optional: What happens when the frame closes?
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////
//////3. Create components and put them in the frame.
//////...create emptyLabel...
////        JLabel jLabel = new JLabel("test");
////        frame.getContentPane().add(jLabel, BorderLayout.CENTER);
////
//////4. Size the frame.
//        frame.pack();
////
//////5. Show it.
//        frame.setVisible(true);
//
//
//        JOptionPane.showMessageDialog(frame,
//                "Eggs are not supposed to be green.");


//        JFrame frame = new JFrame();
//
//        JDialog landWaterCoastDialog = new JDialog(frame, "My Dialog", true);
//
//        //     landWaterCoastDialog.setLayout(new GridBagLayout());
//        landWaterCoastDialog.setVisible(true);
//        landWaterCoastDialog.add(new JLabel("test"));
//        landWaterCoastDialog.pack();
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        frame.pack();
//        frame.setVisible(true);


        // BEAM COMMENTS
//
//        JPanel lwcPanel = GridBagUtils.createPanel();
//        JPanel coastlinePanel = GridBagUtils.createPanel();
//        GridBagConstraints coastlineConstraints = new GridBagConstraints();
//        int rightInset = 10;
//
//        SpinnerModel transparencyModel = new SpinnerNumberModel(0.4, 0.0, 1.0, 0.1);
//        JSpinner transparencySpinner = new JSpinner(transparencyModel);
//
//        SpinnerModel samplingModel = new SpinnerNumberModel(1, 1, 10, 1);
//        JSpinner xSamplingSpinner = new JSpinner(samplingModel);
//        JSpinner ySamplingSpinner = new JSpinner(samplingModel);
//
//        Integer[] resolutions = {50, 150};
//        JComboBox resolutionComboBox = new JComboBox(resolutions);
//
//        GridBagUtils.addToPanel(coastlinePanel, new JCheckBox("Coastline"), coastlineConstraints, "anchor=WEST, gridx=0, gridy=0");
//        GridBagUtils.addToPanel(coastlinePanel, new JLabel("Mask name: "), coastlineConstraints, "gridy=1, insets.right="+ rightInset);
//        GridBagUtils.addToPanel(coastlinePanel, new JTextField("Coastline"), coastlineConstraints, "gridx=1, insets.right=0");
//        GridBagUtils.addToPanel(coastlinePanel, new JLabel("Line color: "), coastlineConstraints, "gridx=0, gridy=2, insets.right=" + rightInset);
//        GridBagUtils.addToPanel(coastlinePanel, new ColorExComboBox(), coastlineConstraints, "gridx=1, insets.right=0");
//        GridBagUtils.addToPanel(coastlinePanel, new JLabel("Transparency: "), coastlineConstraints, "gridy=2, insets.right="+ rightInset);
//        GridBagUtils.addToPanel(coastlinePanel, transparencySpinner, coastlineConstraints, "gridx=1, insets.right=0");
//        GridBagUtils.addToPanel(coastlinePanel, new JLabel("Resolution: "), coastlineConstraints, "gridy=3, insets.right="+ rightInset);
//        GridBagUtils.addToPanel(coastlinePanel, resolutionComboBox, coastlineConstraints, "gridx=1, insets.right=0");
//        GridBagUtils.addToPanel(coastlinePanel, new JLabel("Supersampling factor x: "), coastlineConstraints, "gridy=4, insets.right="+ rightInset);
//        GridBagUtils.addToPanel(coastlinePanel, xSamplingSpinner, coastlineConstraints, "gridx=1, insets.right=0");
//        GridBagUtils.addToPanel(coastlinePanel, new JLabel("Supersampling factor y: "), coastlineConstraints, "gridy=5, insets.right="+ rightInset);
//        GridBagUtils.addToPanel(coastlinePanel, ySamplingSpinner, coastlineConstraints, "gridx=1, insets.right=0");


        ProgressMonitorSwingWorker pmSwingWorker = new ProgressMonitorSwingWorker(visatApp.getMainFrame(),
                "Computing Masks") {

            @Override
            protected Void doInBackground(ProgressMonitor pm) throws Exception {
                Product product = visatApp.getSelectedProduct();
                pm.beginTask("Creating land, water, coastline masks", 2);

                try {
//        Product landWaterProduct = GPF.createProduct("LandWaterMask", GPF.NO_PARAMS, product);
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("subSamplingFactorX", 3);
                    parameters.put("subSamplingFactorY", 3);
                    Product landWaterProduct = GPF.createProduct(LAND_WATER_MASK_OP_ALIAS, parameters, product);
                    Band waterFractionBand = landWaterProduct.getBand("land_water_fraction");
                    Band coastBand = landWaterProduct.getBand("coast");

                    // PROBLEM WITH TILE SIZES
                    // Example: product has tileWidth=498 and tileHeight=611
                    // resulting image has tileWidth=408 and tileHeight=612
                    // Why is this happening and where?
                    // For now we change the image layout here.
                    reformatSourceImage(waterFractionBand, new ImageLayout(product.getBandAt(0).getSourceImage()));
                    reformatSourceImage(coastBand, new ImageLayout(product.getBandAt(0).getSourceImage()));

                    pm.worked(1);
                    waterFractionBand.setName(WATER_FRACTION_BAND_NAME);
                    product.addBand(waterFractionBand);
                    //todo BEAM folks left this as a placeholder
//                    product.addBand(coastBand);

                    //todo replace with JAI operator "GeneralFilter" which uses a GeneralFilterFunction
                    final Kernel arithmeticMean3x3Kernel = new Kernel(3, 3, 1.0 / 9.0,
                            new double[]{
                                    +1, +1, +1,
                                    +1, +1, +1,
                                    +1, +1, +1,
                            });
                    final ConvolutionFilterBand filteredCoastlineBand = new ConvolutionFilterBand(WATER_FRACTION_SMOOTHED_NAME,
                            waterFractionBand,
                            arithmeticMean3x3Kernel);
                    product.addBand(filteredCoastlineBand);

                    ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();


                    Mask landMask = Mask.BandMathsType.create(LAND_MASK_NAME,
                            LAND_MASK_DESCRIPTION,
                            product.getSceneRasterWidth(),
                            product.getSceneRasterHeight(),
                            LAND_MASK_MATH,
                            LAND_MASK_COLOR,
                            LAND_MASK_TRANSPARENCY);
//                    Color.GREEN.darker(),

                    maskGroup.add(landMask);


                    Mask coastlineMask = Mask.BandMathsType.create(COASTLINE_MASK_NAME,
                            COASTLINE_MASK_DESCRIPTION,
                            product.getSceneRasterWidth(),
                            product.getSceneRasterHeight(),
                            COASTLINE_MATH,
                            COASTLINE_MASK_COLOR,
                            COASTLINE_MASK_TRANSPARENCY);
                    maskGroup.add(coastlineMask);


                    Mask waterMask = Mask.BandMathsType.create(WATER_MASK_NAME,
                            WATER_MASK_DESCRIPTION,
                            product.getSceneRasterWidth(),
                            product.getSceneRasterHeight(),
                            WATER_MASK_MATH,
                            WATER_MASK_COLOR,
                            WATER_MASK_TRANSPARENCY);
                    maskGroup.add(waterMask);
                    pm.worked(1);

                    String[] bandNames = product.getBandNames();
                    for (String bandName : bandNames) {
                        RasterDataNode raster = product.getRasterDataNode(bandName);
                        if (SHOW_COASTLINE_MASK_ALL_BANDS) {
                            raster.getOverlayMaskGroup().add(coastlineMask);
                        }
                        if (SHOW_LAND_MASK_ALL_BANDS) {
                            raster.getOverlayMaskGroup().add(landMask);
                        }
                        if (SHOW_WATER_MASK_ALL_BANDS) {
                            raster.getOverlayMaskGroup().add(waterMask);
                        }
                    }

//                    visatApp.setSelectedProductNode(waterFractionBand);

//        ProductSceneView selectedProductSceneView = visatApp.getSelectedProductSceneView();
//        if (selectedProductSceneView != null) {
//            RasterDataNode raster = selectedProductSceneView.getRaster();
//            raster.getOverlayMaskGroup().add(landMask);
//            raster.getOverlayMaskGroup().add(coastlineMask);
//            raster.getOverlayMaskGroup().add(waterMask);
//        }
                } finally {
                    pm.done();
                }
                return null;
            }


        };

        pmSwingWorker.executeWithBlocking();


    }


    private void reformatSourceImage(Band band, ImageLayout imageLayout) {
        RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
        MultiLevelImage waterFractionSourceImage = band.getSourceImage();
        int waterFractionDataType = waterFractionSourceImage.getData().getDataBuffer().getDataType();
        RenderedImage newImage = FormatDescriptor.create(waterFractionSourceImage, waterFractionDataType,
                renderingHints);
        band.setSourceImage(newImage);
    }

    private class ToolbarCommand extends CommandAdapter {
        private final VisatApp visatApp;

        public ToolbarCommand(VisatApp visatApp) {
            this.visatApp = visatApp;
        }

        @Override
        public void actionPerformed(
                CommandEvent event) {
            showLandWaterCoastMasks(
                    visatApp);

        }

        @Override
        public void updateState(
                CommandEvent event) {
            Product selectedProduct = visatApp.getSelectedProduct();
            boolean productSelected = selectedProduct != null;
            boolean hasBands = false;
            boolean hasGeoCoding = false;
            if (productSelected) {
                hasBands = selectedProduct.getNumBands() > 0;
                hasGeoCoding = selectedProduct.getGeoCoding() != null;
            }
            event.getCommand().setEnabled(
                    productSelected && hasBands && hasGeoCoding);
        }
    }
}
