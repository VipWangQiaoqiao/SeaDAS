package org.esa.beam.dataio.aviris;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import org.esa.beam.dataio.envi.EnviProductReaderPlugIn;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductIOException;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.util.ProductUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.EnumMap;
import org.esa.beam.dataio.envi.EnviConstants;
import org.esa.beam.dataio.envi.EnviCrsFactory;
import org.esa.beam.dataio.envi.EnviMapInfo;
import org.esa.beam.dataio.envi.EnviProjectionInfo;
import org.esa.beam.dataio.envi.Header;
import org.esa.beam.util.Debug;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

class AvirisProductReader extends AbstractProductReader {
    private Header header;
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
            
            AvirisFilename avirisFilename = AvirisFilename.create(hdrFile.getName());
            if (genericAvirisFilename == null) {
                genericAvirisFilename = avirisFilename;
            }
            FileType fileType = FileType.fromString(avirisFilename.getFileType());
            if (fileType == FileType.RAD || fileType == FileType.ORT){
                ProductReader enviProductReader = enviProductReaderPlugIn.createReaderInstance();
                Product product = enviProductReader.readProductNodes(hdrFile, null);
                if (sceneHeight == 0) {
                    sceneHeight = product.getSceneRasterHeight();
                    sceneWidth = product.getSceneRasterWidth();
                }
                avirisProductParts.put(fileType, product);
                if (fileType == FileType.RAD){
                    final BufferedReader headerReader =  new BufferedReader(new FileReader(hdrFile));
                    header = new Header(headerReader);
                }
            }
        }
        Product product = new Product(genericAvirisFilename.getProductBase(), genericAvirisFilename.getFileType(),
                                      sceneWidth, sceneHeight);
        product.setDescription("AVIRIS data product");
        handleRadianceProduct(product);
//        handleGeomProduct(product);
        handleObsOrtProduct(product);
        initGeoCoding(product);
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
    
    private void initGeoCoding(final Product product) {
        final EnviMapInfo enviMapInfo = header.getMapInfo();
        if (enviMapInfo == null) {
            return;
        }
        final EnviProjectionInfo projectionInfo = header.getProjectionInfo();
        CoordinateReferenceSystem crs = null;
        if (projectionInfo != null) {
            try {
                crs = EnviCrsFactory.createCrs(projectionInfo.getProjectionNumber(), projectionInfo.getParameter(),
                                               enviMapInfo.getDatum(), enviMapInfo.getUnit());
            } catch (IllegalArgumentException ignore) {
            }
        }
        if (EnviConstants.PROJECTION_NAME_WGS84.equalsIgnoreCase(enviMapInfo.getProjectionName())) {
            crs = DefaultGeographicCRS.WGS84;
        } else if ("UTM".equalsIgnoreCase(enviMapInfo.getProjectionName())) {
            try {
                final int zone = enviMapInfo.getUtmZone();
                final String hemisphere = enviMapInfo.getUtmHemisphere();

                if (zone >= 1 && zone <= 60) {
                    final CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null);
                    if ("North".equalsIgnoreCase(hemisphere)) {
                        final int WGS84_UTM_zone_N_BASE = 32600;
                        crs = factory.createProjectedCRS("EPSG:" + (WGS84_UTM_zone_N_BASE + zone));
                    } else {
                        final int WGS84_UTM_zone_S_BASE = 32700;
                        crs = factory.createProjectedCRS("EPSG:" + (WGS84_UTM_zone_S_BASE + zone));
                    }
                }
            } catch (FactoryException ignore) {
            }
        }


        if (crs != null) {
            try {
                AffineTransform i2m = new AffineTransform();
                i2m.translate(enviMapInfo.getEasting(), enviMapInfo.getNorthing());
                i2m.scale(enviMapInfo.getPixelSizeX(), -enviMapInfo.getPixelSizeY());
                i2m.rotate(Math.toRadians(-enviMapInfo.getOrientation()));
                i2m.translate(-(enviMapInfo.getReferencePixelX() - 1), -(enviMapInfo.getReferencePixelY() - 1));
                Rectangle rect = new Rectangle(product.getSceneRasterWidth(), product.getSceneRasterHeight());
                GeoCoding geoCoding = new CrsGeoCoding(crs, rect, i2m);

                product.setGeoCoding(geoCoding);
            } catch (FactoryException | TransformException fe) {
                Debug.trace(fe);
            }
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
                    ProductUtils.copyBand(bandName, avirisProductPart, "latitude", product, true);
                } else if (bandName.startsWith("Band_1")) {
                    longitudeBand = avirisProductPart.getBand(bandName);
                    ProductUtils.copyBand(bandName, avirisProductPart, "longitude", product, true);
                } else {
                    ProductUtils.copyBand(bandName, avirisProductPart, "elevation", product, true);
                }
            }
            if (latitudeBand != null && longitudeBand != null) {
                // convert bands into tie-points
                // to create a tie-point geo-coding, because it is much faster than a pixel-geo-coding
//                int rasterWidth = latitudeBand.getSceneRasterWidth();
//                int rasterHeight = latitudeBand.getSceneRasterHeight();
//                float[] latData = latitudeBand.readPixels(0, 0, rasterWidth, rasterHeight, (float[]) null);
//                TiePointGrid tpLat = new TiePointGrid("latitude", rasterWidth, rasterHeight, 0.5f, 0.5f, 1f, 1f, latData);
//                product.addTiePointGrid(tpLat);
//
//                float[] lonData = longitudeBand.readPixels(0, 0, rasterWidth, rasterHeight, (float[]) null);
//                TiePointGrid tpLon = new TiePointGrid("longitude", rasterWidth, rasterHeight, 0.5f, 0.5f, 1f, 1f, lonData);
//                product.addTiePointGrid(tpLon);
//                product.setGeoCoding(new TiePointGeoCoding(tpLat, tpLon));
//                product.setGeoCoding(new PixelGeoCoding(latitudeBand, longitudeBand,null,4));
            }
        }
    }

    private void handleObsOrtProduct(Product product) throws IOException {
        Product avirisProductPart = avirisProductParts.get(FileType.ORT);
        if (avirisProductPart == null) {
            avirisProductPart = avirisProductParts.get(FileType.OBS);
        }
        if (avirisProductPart != null) {
            copyMetadata(avirisProductPart, product, FileType.ORT.toString());
            String[] bandNames = avirisProductPart.getBandNames();
            for (String bandName : bandNames) {
                String newBandname = bandName.replace(" ", "_");
                ProductUtils.copyBand(bandName, avirisProductPart, newBandname, product, true);
                if (bandName.equals("UTC time")) {
                    Band utcTime = avirisProductPart.getBand(bandName);
                    if (utcTime != null) {
                        int rasterWidth = utcTime.getSceneRasterWidth();
                        int rasterHeight = utcTime.getSceneRasterHeight();
                        float[] timeData = utcTime.readPixels(0, 0, rasterWidth, rasterHeight, (float[]) null);
                        float[] minmaxTime = minMaxTime(timeData);
                        final DateFormat dateFormat = ProductData.UTC.createDateFormat("yyMMdd HH:mm:ss");
                        AvirisFilename avirisFilename = AvirisFilename.create(product.getFileLocation().getName());
                        try {
                            String startTime = decimalHour2HHMMSS(minmaxTime[0]);
                            String endTime = decimalHour2HHMMSS(minmaxTime[1]);
                            Date startDate = dateFormat.parse(avirisFilename.getFlightDate() + " " + startTime);
                            Date endDate = dateFormat.parse(avirisFilename.getFlightDate() + " " + endTime);

                            product.setStartTime(ProductData.UTC.create(startDate, 0));
                            product.setEndTime(ProductData.UTC.create(endDate, 0));
                        } catch (ParseException ignored) {
            }
        }
    }
            }
        }
    }
    private String decimalHour2HHMMSS(double decimalHour){
        double hour = Math.floor(decimalHour);
        double minute = Math.floor((decimalHour - hour)*60.);
        double second = Math.floor((decimalHour - hour)*3600.) - minute*60;
        String HHMMSS = String.format("%2d:%2d:%2d", (int)hour, (int)minute, (int)second);

        return(HHMMSS);
    }
    private float[] minMaxTime (float[] timeArray){
        float[] minMax = new float[2];
        for(int i = 0; i < timeArray.length; i++) {
            if (timeArray[i] > 0){
                minMax[0] = timeArray[i];
                break;
            }
        }
        for(int i = timeArray.length-1; i >0 ; i--) {
            if (timeArray[i] > 0){
                minMax[1] = timeArray[i];
                break;
            }  
        }
        return(minMax);
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
