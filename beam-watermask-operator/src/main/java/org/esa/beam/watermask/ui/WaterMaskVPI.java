package org.esa.beam.watermask.ui;

import com.bc.ceres.core.*;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import com.jidesoft.action.CommandBar;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.gpf.GPF;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;


/**
 * This VISAT PlugIn registers an action which calls the "LandWaterMask" Operator and based on its generated "water_fraction"
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
 * Ideally, users would register an action in BEAM's {@code module.xml} and specify a target toolbar for it.
 * Actions specified in BEAM's {@code module.xml} currently only appear in menus, and not in tool bars
 * (because they are hard-coded in VisatApp).
 * Since this feature is still missing in BEAM, so we have to place the action in its target tool bar
 * ("layersToolBar") manually.</i>
 *
 * @author Tonio Fincke
 * @author Danny Knowles
 * @author Marco Peters
 */
public class WaterMaskVPI extends AbstractVisatPlugIn {

    public static final String COMMAND_ID = "newCreateLandWaterCoastMasks";

    public static final String LAND_WATER_MASK_OP_ALIAS = "LandWaterMask";
    public static final String TARGET_TOOL_BAR_NAME = "layersToolBar";


    @Override
    public void start(final VisatApp visatApp) {
        final ExecCommand action = visatApp.getCommandManager().createExecCommand(COMMAND_ID,
                new ToolbarCommand(visatApp));

        action.setLargeIcon(UIUtils.loadImageIcon("/org/esa/beam/watermask/ui/icons/coastline2_24.png"));

        final AbstractButton lwcButton = visatApp.createToolButton(COMMAND_ID);

        visatApp.getMainFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                CommandBar layersBar = visatApp.getToolBar(TARGET_TOOL_BAR_NAME);
                layersBar.add(lwcButton);
            }

        });
    }


    private void showLandWaterCoastMasks(final VisatApp visatApp) {

        final Product product = visatApp.getSelectedProduct();
        if (product != null) {

            boolean useDialogs = false;

            final boolean[] masksCreated = {false};

            final AuxilliaryMasksData auxilliaryMasksData = new AuxilliaryMasksData();
            auxilliaryMasksData.setCreateMasks(false);
            auxilliaryMasksData.setDeleteMasks(false);

            if (!useDialogs) {
                auxilliaryMasksData.setCreateMasks(true);
            }

            final ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
            final ProductNodeGroup<Band> bandGroup = product.getBandGroup();


            for (String name : maskGroup.getNodeNames()) {
                if (name.equals(auxilliaryMasksData.getCoastlineMaskName()) ||
                        name.equals(auxilliaryMasksData.getLandMaskName()) ||
                        name.equals(auxilliaryMasksData.getWaterMaskName())) {
                    masksCreated[0] = true;
                }
            }


            for (String name : bandGroup.getNodeNames()) {
                if (name.equals(auxilliaryMasksData.getWaterFractionBandName()) ||
                        name.equals(auxilliaryMasksData.getWaterFractionSmoothedName())) {
                    masksCreated[0] = true;
                }
            }


            if (masksCreated[0]) {
                if (useDialogs) {
                    AuxilliaryMasksDialog auxilliaryMasksDialog = new AuxilliaryMasksDialog(auxilliaryMasksData, masksCreated[0]);
                    auxilliaryMasksDialog.setVisible(true);
                }

                if (auxilliaryMasksData.isDeleteMasks() || !useDialogs) {
                    masksCreated[0] = false;


                    for (String name : maskGroup.getNodeNames()) {
                        if (name.equals(auxilliaryMasksData.getCoastlineMaskName()) ||
                                name.equals(auxilliaryMasksData.getLandMaskName()) ||
                                name.equals(auxilliaryMasksData.getWaterMaskName())) {
                            maskGroup.remove(maskGroup.get(name));
                        }
                    }

                    for (String name : bandGroup.getNodeNames()) {
                        if (name.equals(auxilliaryMasksData.getWaterFractionBandName()) ||
                                name.equals(auxilliaryMasksData.getWaterFractionSmoothedName())) {
                            bandGroup.remove(bandGroup.get(name));
                        }
                    }


                }
            }


            if (!masksCreated[0]) {
                if (useDialogs) {
                    AuxilliaryMasksDialog auxilliaryMasksDialog = new AuxilliaryMasksDialog(auxilliaryMasksData, masksCreated[0]);
                    auxilliaryMasksDialog.setVisible(true);
                }

                if (auxilliaryMasksData.isCreateMasks()) {
                    ProgressMonitorSwingWorker pmSwingWorker = new ProgressMonitorSwingWorker(visatApp.getMainFrame(),
                            "Computing Masks") {

                        @Override
                        protected Void doInBackground(com.bc.ceres.core.ProgressMonitor pm) throws Exception {

                            pm.beginTask("Creating land, water, coastline masks", 2);

                            try {
//        Product landWaterProduct = GPF.createProduct("LandWaterMask", GPF.NO_PARAMS, product);


                                Map<String, Object> parameters = new HashMap<String, Object>();

                                parameters.put("subSamplingFactorX", new Integer(auxilliaryMasksData.getSuperSampling()));
                                parameters.put("subSamplingFactorY", new Integer(auxilliaryMasksData.getSuperSampling()));

                                ResolutionInfo resolutionInfo = auxilliaryMasksData.getResolutionInfo();

                                if ((resolutionInfo.getResolution() == 50 ||
                                        resolutionInfo.getResolution() == 150) &&
                                        resolutionInfo.getUnit() == ResolutionInfo.Unit.METER) {
                                    parameters.put("resolution", new Integer(resolutionInfo.getResolution()));
                                } else {

                                }


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
                                waterFractionBand.setName(auxilliaryMasksData.getWaterFractionBandName());
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

                                final ConvolutionFilterBand filteredCoastlineBand = new ConvolutionFilterBand(
                                        auxilliaryMasksData.getWaterFractionSmoothedName(),
                                        waterFractionBand,
                                        arithmeticMean3x3Kernel);

                                product.addBand(filteredCoastlineBand);


                                Mask coastlineMask = Mask.BandMathsType.create(
                                        auxilliaryMasksData.getCoastlineMaskName(),
                                        auxilliaryMasksData.getCoastlineMaskDescription(),
                                        product.getSceneRasterWidth(),
                                        product.getSceneRasterHeight(),
                                        auxilliaryMasksData.getCoastlineMath(),
                                        auxilliaryMasksData.getCoastlineMaskColor(),
                                        auxilliaryMasksData.getCoastlineMaskTransparency());
                                maskGroup.add(coastlineMask);


                                Mask waterMask = Mask.BandMathsType.create(
                                        auxilliaryMasksData.getWaterMaskName(),
                                        auxilliaryMasksData.getWaterMaskDescription(),
                                        product.getSceneRasterWidth(),
                                        product.getSceneRasterHeight(),
                                        auxilliaryMasksData.getWaterMaskMath(),
                                        auxilliaryMasksData.getWaterMaskColor(),
                                        auxilliaryMasksData.getWaterMaskTransparency());
                                maskGroup.add(waterMask);


                                Mask landMask = Mask.BandMathsType.create(
                                        auxilliaryMasksData.getLandMaskName(),
                                        auxilliaryMasksData.getLandMaskDescription(),
                                        product.getSceneRasterWidth(),
                                        product.getSceneRasterHeight(),
                                        auxilliaryMasksData.getLandMaskMath(),
                                        auxilliaryMasksData.getLandMaskColor(),
                                        auxilliaryMasksData.getLandMaskTransparency());

                                maskGroup.add(landMask);


                                pm.worked(1);

                                String[] bandNames = product.getBandNames();
                                for (String bandName : bandNames) {
                                    RasterDataNode raster = product.getRasterDataNode(bandName);
                                    if (auxilliaryMasksData.isShowCoastlineMaskAllBands()) {
                                        raster.getOverlayMaskGroup().add(coastlineMask);
                                    }
                                    if (auxilliaryMasksData.isShowLandMaskAllBands()) {
                                        raster.getOverlayMaskGroup().add(landMask);
                                    }
                                    if (auxilliaryMasksData.isShowWaterMaskAllBands()) {
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
            }
        }

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

