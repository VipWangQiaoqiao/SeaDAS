package org.esa.beam.dataio.aviris;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.envi.EnviProductReaderPlugIn;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductIOException;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.util.ProductUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

class AvirisProductReader extends AbstractProductReader {
//   *eph                  position data in a WGS-84/NAD83 UTM x,y,z coordinate system,
//   *lonlat_eph           position in WGS-84 longitude, latitude and elevation,
//   *obs                  parameters relating to the geometry of the observation and
//                         illumination conditions in raw spatial format,
//   *obs.hdr              OBS image header file
//   *ort_glt              parameters relating to the geometry of the observation and
//                         illumination conditions rendered using the *_ort_glt lookup
//                         table,
//   *obs_ort.hdr          OBS_ORT image header file,
//   *ort.plog             generic data processing info
//   *ortho.readme         this file,
//   *ort_glt              geometric look-up table,
//   *ort_glt.hdr          GLT image header file,
//   *ort_igm              input geometry file,
//   *ort_igm.hdr          IGM image header file,
//   *ort_img              orthocorrected, scaled radiance image file,
//   *ort_img.hdr          ORT_IMG image header file.
    private enum FileType {
        RAD, GEOMLUT, GEOM, ORT, OBS;

        static FileType fromString(String fileType) {
            if (fileType.endsWith("ort_img")) {
                return FileType.RAD;
            } else if (fileType.equalsIgnoreCase("ort_glt")) {
                return FileType.GEOMLUT;
            } else if (fileType.equalsIgnoreCase("ort_igm")) {
                return FileType.GEOM;
            } else if (fileType.equalsIgnoreCase("obs_ort")) {
                return FileType.ORT;
            }else if (fileType.equalsIgnoreCase("obs")) {
                return FileType.OBS;
            }
            throw new IllegalArgumentException("Unkown File type: " + fileType);
        }
    }

    private final EnumMap<FileType, Product> avirisProductParts = new EnumMap<>(FileType.class);

    AvirisProductReader(AvirisProductReaderPlugin avirisProductReaderPlugin) {
        super(avirisProductReaderPlugin);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        File[] hdrFiles = AvirisProductReaderPlugin.findHdrFiles(getInput());
        if (hdrFiles.length > 0) {
            return createProduct(hdrFiles);
        }
        throw new ProductIOException("Failed reading AVIRIS product");
    }

    @Override
    public void close() throws IOException {
        super.close();
        for (Product product : avirisProductParts.values()) {
            product.dispose();
        }
    }

    private Product createProduct(File[] hdrFiles) throws IOException {
        ProductReaderPlugIn enviProductReaderPlugIn = new EnviProductReaderPlugIn();
        int sceneWidth = 0;
        int sceneHeight = 0;
        AvirisFilename genericAvirisFilename = null;
        for (File hdrFile : hdrFiles) {
            ProductReader enviProductReader = enviProductReaderPlugIn.createReaderInstance();
            Product product = enviProductReader.readProductNodes(hdrFile, null);
            if (sceneHeight == 0) {
                sceneHeight = product.getSceneRasterHeight();
                sceneWidth = product.getSceneRasterWidth();
            }
            AvirisFilename avirisFilename = AvirisFilename.create(hdrFile.getName());
            if (genericAvirisFilename == null) {
                genericAvirisFilename = avirisFilename;
            }
            FileType fileType = FileType.fromString(avirisFilename.getFileType());
            avirisProductParts.put(fileType, product);
        }
        Product product = new Product(genericAvirisFilename.getProductBase(), genericAvirisFilename.getFileType(),
                                      sceneWidth, sceneHeight);
        product.setDescription("AVIRIS data product");
        handleRadianceProduct(product);
//        handleNdviProduct(product);
        handleGeomProduct(product);
        handleOrtProduct(product);
        return product;
    }

    private void handleRadianceProduct(Product product) {
        Product avirisProductPart = avirisProductParts.get(FileType.RAD);
        if (avirisProductPart != null) {
            copyMetadata(avirisProductPart, product, FileType.RAD.toString());
            String[] bandNames = avirisProductPart.getBandNames();
            for (String bandName : bandNames) {
                String[] bandNameSplit = bandName.split("_");
                String newBandname = "radiance_" + bandNameSplit[1];
                Band band = ProductUtils.copyBand(bandName, avirisProductPart, newBandname, product, true);
                band.setScalingFactor(1.0 / 50.0);
                band.setSpectralBandwidth(5.7f);
            }
            product.setAutoGrouping("radiance");
            product.setFileLocation(avirisProductPart.getFileLocation());
        }
    }

    private void handleGeomProduct(Product product) throws IOException {
        Product avirisProductPart = avirisProductParts.get(FileType.GEOM);

        if (avirisProductPart != null) {
            copyMetadata(avirisProductPart, product, FileType.GEOM.toString());
            String[] bandNames = avirisProductPart.getBandNames();
            Band latitudeBand = null;
            Band longitudeBand = null;
            for (String bandName : bandNames) {
                if (bandName.startsWith("Band_2")) {
                    latitudeBand = avirisProductPart.getBand(bandName);
                } else if (bandName.startsWith("Band_1")) {
                    longitudeBand = avirisProductPart.getBand(bandName);
                } else {
                    String newBandname = "elevation";
                    ProductUtils.copyBand(bandName, avirisProductPart, newBandname, product, true);
                }
            }
            if (latitudeBand != null && longitudeBand != null) {
//                int rasterWidth = latitudeBand.getSceneRasterWidth();
//                int rasterHeight = latitudeBand.getSceneRasterHeight();
//                Band latBand = new Band("latitude", ProductData.TYPE_FLOAT64, product.getSceneRasterWidth(), product.getSceneRasterHeight());
//                Band lonBand = new Band("longitude", ProductData.TYPE_FLOAT64, product.getSceneRasterWidth(), product.getSceneRasterHeight());
                // convert bands into tie-points
                // to create a tie-point geo-coding, because it is much faster than a pixel-geo-coding
//                float[] latData = latitudeBand.readPixels(0, 0, rasterWidth, rasterHeight, (float[]) null);
//                TiePointGrid tpLat = new TiePointGrid("latitude", rasterWidth, rasterHeight, 0.5f, 0.5f, 1f, 1f, latData);
//                product.addTiePointGrid(tpLat);
//
//                float[] lonData = longitudeBand.readPixels(0, 0, rasterWidth, rasterHeight, (float[]) null);
//                TiePointGrid tpLon = new TiePointGrid("longitude", rasterWidth, rasterHeight, 0.5f, 0.5f, 1f, 1f, lonData);
//                product.addTiePointGrid(tpLon);
                product.setGeoCoding(new PixelGeoCoding2(latitudeBand, longitudeBand,null));
//                product.setGeoCoding(new TiePointGeoCoding(tpLat, tpLon));
            }
        }
    }

    private void handleOrtProduct(Product product) {
        Product avirisProductPart = avirisProductParts.get(FileType.ORT);
        if (avirisProductPart != null) {
            copyMetadata(avirisProductPart, product, FileType.ORT.toString());
            String[] bandNames = avirisProductPart.getBandNames();
            for (String bandName : bandNames) {
                String newBandname = bandName.replace(" ", "_");
                ProductUtils.copyBand(bandName, avirisProductPart, newBandname, product, true);
            }
        }
    }

    private void copyMetadata(Product partProduct, Product targetProduct, String name) {
        MetadataElement header = partProduct.getMetadataRoot().getElement("Header");
        header.setName(name);
        targetProduct.getMetadataRoot().addElement(header);
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        throw new IllegalStateException("should be read from source images only");
    }

}
