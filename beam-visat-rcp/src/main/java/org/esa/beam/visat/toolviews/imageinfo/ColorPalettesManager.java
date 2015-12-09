package org.esa.beam.visat.toolviews.imageinfo;

import org.esa.beam.framework.datamodel.ColorPaletteDef;
import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class ColorPalettesManager {

    private static ArrayList<ColorPaletteDef> cpdList;
    private static ArrayList<String> cpdNames;
    private static Map<ColorPaletteDef, List<RasterDataNode>> cpdRasterList;


    public static void loadAvailableColorPalettes(File palettesDir) {

        cpdRasterList = new HashMap<>();
        final ArrayList<ColorPaletteDef> newCpdList = new ArrayList<>();
        final ArrayList<String> newCpdNames = new ArrayList<>();

        final File[] file_oceancolor_default = palettesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().equals("oceancolor_standard.cpd"));

            }
        });

        final File[] file_seadas_universal = palettesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().equals("standard_uni.cpd"));

            }
        });
//        final File[] files_oceancolor = palettesDir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return (name.toLowerCase().endsWith(".cpd") && name.toLowerCase().startsWith("oceancolor_") && !name.toLowerCase().equals("oceancolor_standard.cpd") );
//
//            }
//        });

//        final File[] files_other = palettesDir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return (name.toLowerCase().endsWith(".cpd") && !name.toLowerCase().startsWith("oceancolor_"));
//
//            }
//        });

        final File[] files_other = palettesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".cpd") && !name.toLowerCase().equals("oceancolor_standard.cpd")
                        && !name.toLowerCase().equals("standard_uni.cpd"));

            }
        });

//        Arrays.sort(files_oceancolor);
        Arrays.sort(files_other);
        File[] tmpfiles1 = concat(file_oceancolor_default, file_seadas_universal);
//        File[] tmpfiles2 = concat(tmpfiles1, files_oceancolor);
        File[] files = concat(tmpfiles1, files_other);

        for (File file : files) {
            try {
                final ColorPaletteDef newCpd = ColorPaletteDef.loadColorPaletteDef(file);
                newCpdList.add(newCpd);
                newCpdNames.add(file.getName());
            } catch (IOException e) {
                final Logger logger = BeamLogManager.getSystemLogger();
                logger.warning("Unable to load color palette definition from file '" + file.getAbsolutePath() + "'");
                logger.log(Level.INFO, e.getMessage(), e);
            }
        }
        if (cpdList != null) {
            for (ColorPaletteDef oldCpd : cpdList) {
                if (newCpdList.contains(oldCpd)) {
                    final int i = newCpdList.indexOf(oldCpd);
                    final ColorPaletteDef newCpd = newCpdList.get(i);
                    cpdRasterList.put(newCpd, cpdRasterList.remove(oldCpd));
                } else {
                    final List<RasterDataNode> nodes = cpdRasterList.remove(oldCpd);
                    if (nodes != null) {
                        nodes.clear();
                    }
                }
            }
            cpdNames.clear();
            cpdList.clear();
        }
        cpdNames = newCpdNames;
        cpdList = newCpdList;
    }

    public static File[] concat(File[] A, File[] B) {
        int aLen = A.length;
        int bLen = B.length;
        File[] C = new File[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    public static List<ColorPaletteDef> getColorPaletteDefList() {
        return Collections.unmodifiableList(cpdList);
    }

    public static void applyPaletteToRaster(ColorPaletteDef cpd, RasterDataNode raster) {
        removeRasterFromMapping(raster);
        final ImageInfo imageInfo = raster.getImageInfo();
        if (imageInfo == null) {
            return;
        }

        final double minSample;
        final double maxSample;
        final boolean autoDistribute;

        final ColorPaletteDef oldDef = imageInfo.getColorPaletteDef();
        if (oldDef != null) {
            minSample = oldDef.getMinDisplaySample();
            maxSample = oldDef.getMaxDisplaySample();
            autoDistribute = oldDef.isAutoDistribute();
        } else {
            minSample = cpd.getMinDisplaySample();
            maxSample = cpd.getMaxDisplaySample();
            autoDistribute = cpd.isAutoDistribute();
        }
        imageInfo.setColorPaletteDef(cpd.createDeepCopy(), minSample, maxSample, autoDistribute);
        appendRasterToMapping(cpd, raster);
    }

    public static ColorPaletteDef findCpdFor(RasterDataNode raster) {
        for (Map.Entry<ColorPaletteDef, List<RasterDataNode>> entry : cpdRasterList.entrySet()) {
            final List<RasterDataNode> nodes = entry.getValue();
            if (nodes != null && nodes.contains(raster)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void appendRasterToMapping(ColorPaletteDef cpd, RasterDataNode raster) {
        if (!cpdList.contains(cpd)) {
            return;
        }
        List<RasterDataNode> rasterDataNodes = cpdRasterList.get(cpd);
        if (rasterDataNodes == null) {
            rasterDataNodes = new ArrayList<>();
            cpd = cpdList.get(cpdList.indexOf(cpd));
            cpdRasterList.put(cpd, rasterDataNodes);
        }
        rasterDataNodes.add(raster);
    }

    public static void removeRasterFromMapping(RasterDataNode raster) {
        for (Map.Entry<ColorPaletteDef, List<RasterDataNode>> entry : cpdRasterList.entrySet()) {
            final List<RasterDataNode> value = entry.getValue();
            if (value.contains(raster)) {
                value.remove(raster);
            }
        }
    }

    public static String getNameFor(ColorPaletteDef cpdForRaster) {
        for (int i = 0; i < cpdList.size(); i++) {
            ColorPaletteDef colorPaletteDef = cpdList.get(i);
            if (colorPaletteDef == cpdForRaster)
                return cpdNames.get(i);
        }
        return null;
    }
}
