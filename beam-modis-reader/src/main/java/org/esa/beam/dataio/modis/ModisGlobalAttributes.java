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

package org.esa.beam.dataio.modis;

import org.esa.beam.dataio.modis.hdf.HdfDataField;
import org.esa.beam.framework.dataio.ProductIOException;
import org.esa.beam.framework.datamodel.GeoCoding;

import java.awt.Dimension;
import java.util.Date;


public interface ModisGlobalAttributes {

    String getProductName();

    String getProductType();

    Dimension getProductDimensions();

    HdfDataField getDatafield(String name) throws ProductIOException;

    Date getSensingStart();

    Date getSensingStop();

    int[] getSubsamplingAndOffset(String dimensionName);

    boolean isImappFormat();

    String getEosType();

    GeoCoding createGeocoding();
}
