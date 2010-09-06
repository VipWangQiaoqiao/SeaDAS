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

package org.esa.beam.pet;

import org.esa.beam.framework.datamodel.GeoPos;

public class Coordinate {

    private String name;
    private GeoPos geoPos;
    private int id;

    public Coordinate(int id, GeoPos geoPos) {
        this(id, " ", geoPos);
    }

    public Coordinate(int id, String name, GeoPos geoPos) {
        this.id = id;
        this.name = name;
        this.geoPos = geoPos;
    }

    public String getName() {
        return name;
    }

    public GeoPos getGeoPos() {
        return geoPos;
    }

    public int getId() {
        return id;
    }
}