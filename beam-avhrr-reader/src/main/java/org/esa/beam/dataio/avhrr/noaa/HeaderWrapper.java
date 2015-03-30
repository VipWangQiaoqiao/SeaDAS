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

package org.esa.beam.dataio.avhrr.noaa;

import com.bc.ceres.binio.CompoundData;
import com.bc.ceres.binio.CompoundMember;
import com.bc.ceres.binio.CompoundType;
import com.bc.ceres.binio.SequenceData;
import org.esa.beam.dataio.avhrr.HeaderUtil;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;

import java.io.IOException;
import java.util.Map;

/**
 * A wrapper for a header of the NOAA AVHRR file base on a binio compound.
 */
public class HeaderWrapper {

    private final CompoundData compoundData;

    public HeaderWrapper(CompoundData compoundData) {
        this.compoundData = compoundData;
    }

    public MetadataElement getAsMetadataElement() throws IOException {
        return getAsMetadataElement(compoundData);
    }

    public static MetadataElement getAsMetadataElement(CompoundData compoundData) throws IOException {
        CompoundType type = compoundData.getType();
        final int memberCount = type.getMemberCount();
        MetadataElement metadataElement = new MetadataElement(type.getName());
        for (int i = 0; i < memberCount; i++) {
            String typeName = type.getMemberName(i);
            CompoundMember member = type.getMember(i);
            FormatMetadata formatMetadata = (FormatMetadata) member.getMetadata();
            if (typeName.equals("fill")) {
                //ignore
            } else if (formatMetadata != null && formatMetadata.getType().equals("string")) {
                String stringValue = getAsString(compoundData.getSequence(i));
                Map<Object, String> map = getMetaData(member).getItemMap();
                if (map != null) {
                    stringValue = map.get(stringValue);
                }
                ProductData data = ProductData.createInstance(stringValue);
                MetadataAttribute attribute = new MetadataAttribute(typeName, data, true);
                attribute.setDescription(getDescription(member));
                attribute.setUnit(getUnits(member));
                metadataElement.addAttribute(attribute);
            } else if (member.getType().getName().equals("DATE")) {
                CompoundData dateCompound = compoundData.getCompound(i);
                ProductData data = createDate(dateCompound);
                MetadataAttribute attribute = new MetadataAttribute(typeName, data, true);
                attribute.setDescription(getDescription(member));
                attribute.setUnit(getUnits(member));
                metadataElement.addAttribute(attribute);
            } else if (member.getType().isSequenceType()) {
                SequenceData sequence = compoundData.getSequence(i);
                if (sequence.getType().getElementType().isCompoundType()) {
                    for (int j = 0; j < sequence.getType().getElementCount(); j++) {
                        CompoundData compound = sequence.getCompound(j);
                        metadataElement.addElement(getAsMetadataElement(compound));
                    }
                }
            } else if (member.getType().isCompoundType()) {
                metadataElement.addElement(getAsMetadataElement(compoundData.getCompound(i)));
            } else if (member.getType().isSimpleType()) {
                int intValue = compoundData.getInt(i);
                Map<Object, String> map = getMetaData(member).getItemMap();
                ProductData data;
                if (map != null) {
                    String stringValue = map.get(intValue);
                    data = ProductData.createInstance(stringValue);
                } else {
                    double scalingFactor = getMetaData(member).getScalingFactor();
                    if (scalingFactor == 1.0) {
                        data = ProductData.createInstance(new int[]{intValue});
                    } else {
                        data = ProductData.createInstance(new double[]{intValue * scalingFactor});
                    }
                }
                MetadataAttribute attribute = new MetadataAttribute(typeName, data, true);
                attribute.setDescription(getDescription(member));
                attribute.setUnit(getUnits(member));
                metadataElement.addAttribute(attribute);
            } else {
                System.out.println("not handled: name=" + typeName);
                System.out.println("member = " + member.getType());
            }
        }
        return metadataElement;
    }

    static ProductData.UTC createDate(CompoundData dateCompound) throws IOException {
        int year = dateCompound.getInt("year");
        int dayOfYear = dateCompound.getInt("dayOfYear");
        int millisInDay = dateCompound.getInt("UTCmillis");
        return HeaderUtil.createUTCDate(year, dayOfYear, millisInDay);
    }

    static String getAsString(SequenceData valueSequence) throws IOException {
        byte[] data = new byte[valueSequence.getElementCount()];
        for (int i = 0; i < data.length; i++) {
            data[i] = valueSequence.getByte(i);
        }
        return new String(data).trim();
    }

    static double getValue(CompoundData compoundData, String name) throws IOException {
        CompoundType type = compoundData.getType();
        int memberIndex = type.getMemberIndex(name);
        double v = compoundData.getDouble(memberIndex);
        double scalingFactor = getScalingFactor(type.getMember(memberIndex));
        return v * scalingFactor;
    }

    private static double getScalingFactor(CompoundMember member) {
        FormatMetadata metaData = getMetaData(member);
        return metaData.getScalingFactor();
    }

    private static String getDescription(CompoundMember member) {
        FormatMetadata metaData = getMetaData(member);
        return metaData.getDescription();
    }

    private static String getUnits(CompoundMember member) {
        FormatMetadata metaData = getMetaData(member);
        return metaData.getUnits();
    }

    private static FormatMetadata getMetaData(CompoundMember member) {
        Object object = member.getMetadata();
        if (object != null && object instanceof FormatMetadata) {
            return (FormatMetadata) object;
        } else {
            return new FormatMetadata();
        }
    }

}
