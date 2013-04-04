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
package org.esa.beam.framework.dataop.barithm;

import com.bc.jexp.EvalEnv;
import com.bc.jexp.EvalException;
import com.bc.jexp.Symbol;
import com.bc.jexp.Term;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;

/**
 * Represents a read-only symbol. A symbol can be a named constant or variable.
 * It has a return type an can be evaluated. This class is based on RasterData.
 * <p/>
 * <p>Within an expression, a reference to a symbol is created if the parser
 * encounters a name and this name can be resolved through the parser's current namespace.
 * The resulting term in this case is an instance of <code>{@link com.bc.jexp.Term.Ref}</code>.
 *
 * @author Norman Fomferra (norman.fomferra@brockmann-consult.de)
 * @version $Revision$ $Date$
 */
public class RasterDataSymbol implements Symbol {

    public static final Source RAW = Source.RAW;
    public static final Source GEOPHYSICAL = Source.GEOPHYSICAL;

    /**
     * Lists possible source image types.
     */
    public enum Source {
        /**
         * Raw sample data (e.g. measurement counts).
         */
        RAW,
        /**
         * Geophysically interpreted data (e.g. calibration scaling applied).
         */
        GEOPHYSICAL,
    }

    private final String symbolName;
    private final int symbolType;
    private final RasterDataNode raster;
    private final Source source;
    protected ProductData data;

    public RasterDataSymbol(final String symbolName, final Mask mask) {
        this(symbolName, Term.TYPE_B, mask, RAW);
    }

    public RasterDataSymbol(final String symbolName, final RasterDataNode raster, final Source source) {
        this(symbolName, computeSymbolType(raster), raster, source);
    }

    private RasterDataSymbol(String symbolName, int symbolType, RasterDataNode raster, Source source) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
        this.raster = raster;
        this.source = source;
    }

    private static int computeSymbolType(RasterDataNode raster) {
        if (raster instanceof Mask) {
            return Term.TYPE_B;
        }
        return raster.isFloatingPointType() ? Term.TYPE_D : Term.TYPE_I;
    }

    @Override
    public String getName() {
        return symbolName;
    }

    @Override
    public int getRetType() {
        return symbolType;
    }

    /**
     * @return The source image type.
     *
     * @since BEAM 4.7
     */
    public Source getSource() {
        return source;
    }

    public RasterDataNode getRaster() {
        return raster;
    }

    public void setData(final Object data) {
        if (ProductData.class.isAssignableFrom(data.getClass())) {
            this.data = (ProductData) data;
        } else if (data instanceof float[]) {
            this.data = ProductData.createInstance((float[]) data);
        } else if (data instanceof int[]) {
            this.data = ProductData.createInstance((int[]) data);
        } else {
            throw new IllegalArgumentException("illegal data type");
        }
    }

    @Override
    public boolean evalB(final EvalEnv env) throws EvalException {
        final int elemIndex = ((RasterDataEvalEnv) env).getElemIndex();
        return Term.toB(data.getElemDoubleAt(elemIndex));
    }

    @Override
    public int evalI(final EvalEnv env) throws EvalException {
        final int elemIndex = ((RasterDataEvalEnv) env).getElemIndex();
        return data.getElemIntAt(elemIndex);
    }

    @Override
    public double evalD(final EvalEnv env) throws EvalException {
        final int elemIndex = ((RasterDataEvalEnv) env).getElemIndex();
        return data.getElemDoubleAt(elemIndex);
    }

    @Override
    public String evalS(EvalEnv env) throws EvalException {
        final double value = evalD(env);
        return Double.toString(value);
    }
}
