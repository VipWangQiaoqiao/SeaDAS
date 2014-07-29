/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.glevel;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelModel;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.SystemUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;

/**
 * A multi-level source (= level-image source for image pyramids) for visual RGB images derived from
 * {@code RasterDataNode}s.
 *
 * @author Norman Fomferra
 * @since Since BEAM 4.0
 */
public class BandImageMultiLevelSource extends AbstractMultiLevelSource {

    private final RasterDataNode[] rasterDataNodes;
    private ImageInfo imageInfo;

    public static BandImageMultiLevelSource create(RasterDataNode rasterDataNode, ProgressMonitor pm) {
        return create(new RasterDataNode[]{rasterDataNode}, pm);
    }

    public static BandImageMultiLevelSource create(RasterDataNode[] rasterDataNodes, ProgressMonitor pm) {
        RasterDataNode rdn = rasterDataNodes[0];
        MultiLevelModel model = ImageManager.getMultiLevelModel(rdn);
        return create(rasterDataNodes, model, pm);
    }

    public static BandImageMultiLevelSource create(RasterDataNode[] rasterDataNodes, MultiLevelModel model, ProgressMonitor pm) {
        ImageManager.getInstance().prepareImageInfos(rasterDataNodes, pm);
        return new BandImageMultiLevelSource(model, rasterDataNodes);
    }

    /**
     * @deprecated since BEAM 5 because it creates a {@code DefaultMultiLevelModel}, which is not the desired behaviour in many cases
     */
    @Deprecated
    public static BandImageMultiLevelSource create(RasterDataNode rasterDataNode,
                                                   AffineTransform i2mTransform, ProgressMonitor pm) {
        return create(new RasterDataNode[]{rasterDataNode}, i2mTransform, pm);
    }

    /**
     * @deprecated since BEAM 5 because it creates a {@code DefaultMultiLevelModel}, which is not the desired behaviour in many cases
     */
    @Deprecated
    public static BandImageMultiLevelSource create(RasterDataNode[] rasterDataNodes,
                                                   AffineTransform i2mTransform, ProgressMonitor pm) {
        return create(rasterDataNodes, i2mTransform,
                DefaultMultiLevelModel.getLevelCount(rasterDataNodes[0].getSceneRasterWidth(),
                        rasterDataNodes[0].getSceneRasterHeight()), pm);
    }

    /**
     * @deprecated since BEAM 5 because it creates a {@code DefaultMultiLevelModel}, which is not the desired behaviour in many cases
     */
    @Deprecated
    private static BandImageMultiLevelSource create(RasterDataNode[] rasterDataNodes,
                                                    AffineTransform i2mTransform,
                                                    int levelCount,
                                                    ProgressMonitor pm) {
        Assert.notNull(rasterDataNodes);
        Assert.argument(rasterDataNodes.length > 0);
        final int w = rasterDataNodes[0].getSceneRasterWidth();
        final int h = rasterDataNodes[0].getSceneRasterHeight();
        MultiLevelModel model = new DefaultMultiLevelModel(levelCount, i2mTransform, w, h);
        return create(rasterDataNodes, model, pm);
    }

    private BandImageMultiLevelSource(MultiLevelModel model, RasterDataNode[] rasterDataNodes) {
        super(model);
        this.rasterDataNodes = rasterDataNodes.clone();
        imageInfo = ImageManager.getInstance().getImageInfo(rasterDataNodes);
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    @Override
    public Shape getImageShape(int level) {
        return rasterDataNodes[0].getSourceImage().getImageShape(level);
    }

    @Override
    public RenderedImage createImage(int level) {
//        if (imageInfo.getColorPaletteDef().getNumPoints() <= 3) {
//            ColorPaletteSchemes colorPaletteSchemes = new ColorPaletteSchemes(getSystemAuxdataDir(), false);
//            if (colorPaletteSchemes != null) {
//                ArrayList<ColorPaletteInfo> defaultSchemes = colorPaletteSchemes.getDefaultsColorPaletteInfos();
//                for (ColorPaletteInfo cpdInfo : defaultSchemes) {
//
//                    if (cpdInfo.getName() != null & cpdInfo.getName().trim().equals(rasterDataNodes[0].getName().trim())) {
//                        ColorPaletteDef colorPaletteDef = cpdInfo.getColorPaletteDef();
//                        imageInfo.setColorPaletteDef(colorPaletteDef,
//                                cpdInfo.getMinValue(),
//                                cpdInfo.getMaxValue(),
//                                true, //colorPaletteDef.isAutoDistribute(),
//                                cpdInfo.isSourceLogScaled(),
//                                cpdInfo.isLogScaled());
//                        imageInfo.setLogScaled(cpdInfo.isLogScaled());
//                        break;
//                    }
//                }
//            }
//        }
        return ImageManager.getInstance().createColoredBandImage(rasterDataNodes, imageInfo, level);
    }

    private File getSystemAuxdataDir() {
        return new File(SystemUtils.getApplicationDataDir(), "beam-ui/auxdata/color-palettes");
    }
}
