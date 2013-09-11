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

package org.esa.beam.framework.gpf;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.SourceProducts;
import org.esa.beam.framework.gpf.internal.OperatorSpiRegistryImpl;
import org.esa.beam.gpf.operators.standard.WriteOp;
import org.esa.beam.util.Guardian;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.esa.beam.util.SystemUtils.getApplicationContextId;
/**
 * <p>The facade for the Graph Processing Framework.</p>
 * <p>The Graph Processing Framework makes extensive use of Java Advanced Imaging (JAI).
 * Therefore, configuring the JAI {@link javax.media.jai.TileCache TileCache} and
 * {@link javax.media.jai.TileScheduler TileScheduler} will also affect the overall performance of
 * the Graph Processing Framework.</p>
 * <p>This class may be overridden in order to alter product creation behaviour of the static
 * {@code createProduct} methods of the GPF instance.
 * The current instance can be set by {@link #setDefaultInstance(GPF)}.</p>
 *
 * @author Norman Fomferra
 * @since 4.1
 */
public class GPF {

    public static final String DISABLE_TILE_CACHE_PROPERTY = getApplicationContextId()+".gpf.disableTileCache";
    public static final String USE_FILE_TILE_CACHE_PROPERTY = getApplicationContextId()+".gpf.useFileTileCache";
    public static final String TILE_COMPUTATION_OBSERVER_PROPERTY = getApplicationContextId()+".gpf.tileComputationObserver";

    public static final String SOURCE_PRODUCT_FIELD_NAME = "sourceProduct";
    public static final String TARGET_PRODUCT_FIELD_NAME = "targetProduct";

    /**
     * Key for GPF tile size {@link RenderingHints}.
     * <p/>
     * The value for this key must be an instance of {@link Dimension} with
     * both width and height positive.
     */
    public static final RenderingHints.Key KEY_TILE_SIZE =
            new RenderingKey<Dimension>(1, Dimension.class, new RenderingKey.Validator<Dimension>() {
                @Override
                public boolean isValid(Dimension val) {
                    return val.width > 0 && val.height > 0;
                }
            });

    /**
     * An unmodifiable empty {@link Map Map}.
     * <p/>
     * Can be used for convenience as a parameter for {@code createProduct()} if no
     * parameters are needed for the operator.
     *
     * @see #createProduct(String, Map)
     * @see #createProduct(String, Map, Product ...)
     * @see #createProduct(String, Map, Map)
     */
    public static final Map<String, Object> NO_PARAMS = Collections.unmodifiableMap(new TreeMap<String, Object>());

    /**
     * An unmodifiable empty {@link Map Map}.
     * <p/>
     * Can be used for convenience as a parameter for {@code createProduct(String, Map, Map)} if no
     * source products are needed for the operator.
     *
     * @see #createProduct(String, Map, Map)
     */
    public static final Map<String, Product> NO_SOURCES = Collections.unmodifiableMap(new TreeMap<String, Product>());

    private static GPF defaultInstance = new GPF();

    private OperatorSpiRegistry spiRegistry;

    /**
     * Constructor.
     */
    protected GPF() {
        spiRegistry = new OperatorSpiRegistryImpl();
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName the name of the operator to use.
     * @param parameters   the named parameters needed by the operator.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(String operatorName,
                                        Map<String, Object> parameters) throws OperatorException {
        return createProduct(operatorName, parameters, NO_SOURCES);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param renderingHints the rendering hints may be {@code null}.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(String operatorName,
                                        Map<String, Object> parameters,
                                        RenderingHints renderingHints) throws OperatorException {
        return createProduct(operatorName, parameters, NO_SOURCES, renderingHints);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName  the name of the operator to use.
     * @param parameters    the named parameters needed by the operator.
     * @param sourceProduct a source product.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(final String operatorName,
                                        final Map<String, Object> parameters,
                                        final Product sourceProduct) throws OperatorException {
        return createProduct(operatorName, parameters, sourceProduct, null);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProduct  the source product.
     * @param renderingHints the rendering hints may be {@code null}.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(final String operatorName,
                                        final Map<String, Object> parameters,
                                        final Product sourceProduct,
                                        RenderingHints renderingHints) throws OperatorException {
        return createProduct(operatorName, parameters, new Product[]{sourceProduct}, renderingHints);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProducts the source products.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(final String operatorName,
                                        final Map<String, Object> parameters,
                                        final Product... sourceProducts) throws OperatorException {
        return createProduct(operatorName, parameters, sourceProducts, null);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProducts the source products.
     * @param renderingHints the rendering hints may be {@code null}.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(String operatorName,
                                        Map<String, Object> parameters,
                                        Product[] sourceProducts,
                                        RenderingHints renderingHints) throws OperatorException {
        Map<String, Product> sourceProductMap = NO_SOURCES;
        if (sourceProducts.length > 0) {
            sourceProductMap = new HashMap<String, Product>(sourceProducts.length);
            OperatorSpi operatorSpi = GPF.getDefaultInstance().spiRegistry.getOperatorSpi(operatorName);
            if (operatorSpi == null) {
                throw new OperatorException(
                        String.format("Unknown operator '%s'. Note that operator aliases are case sensitive.",
                                      operatorName));
            }
            Field[] declaredFields = operatorSpi.getOperatorClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                SourceProduct sourceProductAnnotation = declaredField.getAnnotation(SourceProduct.class);
                if (sourceProductAnnotation != null) {
                    sourceProductMap.put(SOURCE_PRODUCT_FIELD_NAME, sourceProducts[0]);
                }
                SourceProducts sourceProductsAnnotation = declaredField.getAnnotation(SourceProducts.class);
                if (sourceProductsAnnotation != null) {
                    for (int i = 0; i < sourceProducts.length; i++) {
                        Product sourceProduct = sourceProducts[i];
                        sourceProductMap.put(SOURCE_PRODUCT_FIELD_NAME + "." + (i + 1), sourceProduct);
                        sourceProductMap.put(SOURCE_PRODUCT_FIELD_NAME + (i + 1), sourceProduct);
                    }
                }
            }
        }
        return defaultInstance.createProductNS(operatorName, parameters, sourceProductMap, renderingHints);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProducts the map of named source products.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(String operatorName,
                                        Map<String, Object> parameters,
                                        Map<String, Product> sourceProducts) throws OperatorException {
        return createProduct(operatorName, parameters, sourceProducts, null);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProducts the map of named source products.
     * @param renderingHints the rendering hints, may be {@code null}.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public static Product createProduct(String operatorName,
                                        Map<String, Object> parameters,
                                        Map<String, Product> sourceProducts,
                                        RenderingHints renderingHints) throws OperatorException {
        return defaultInstance.createProductNS(operatorName, parameters, sourceProducts, renderingHints);
    }

    /**
     * Creates a product by using the operator specified by the given name.
     * The resulting product can be used as input product for a further call to {@code createProduct()}.
     * By concatenating multiple calls it is possible to set up a processing graph.
     * <p>All static {@code createProduct} methods delegate to this non-static (= NS) version.
     * It can be overriden by clients in order to alter product creation behaviour of the static
     * {@code createProduct} methods of the current GPF instance.</p>
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProducts the map of named source products.
     * @param renderingHints the rendering hints, may be {@code null}.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     */
    public Product createProductNS(String operatorName,
                                   Map<String, Object> parameters,
                                   Map<String, Product> sourceProducts,
                                   RenderingHints renderingHints) {
        Operator operator = createOperator(operatorName, parameters, sourceProducts, renderingHints);
        return operator.getTargetProduct();
    }

    /**
     * Creates an operator instance by using the given operator (alias) name.
     *
     * @param operatorName   the name of the operator to use.
     * @param parameters     the named parameters needed by the operator.
     * @param sourceProducts the map of named source products.
     * @param renderingHints the rendering hints, may be {@code null}.
     *
     * @return the product created by the operator.
     *
     * @throws OperatorException if the product could not be created.
     * @since BEAM 4.9
     */
    public Operator createOperator(String operatorName, Map<String, Object> parameters, Map<String, Product> sourceProducts,
                                   RenderingHints renderingHints) {
        OperatorSpi operatorSpi = spiRegistry.getOperatorSpi(operatorName);
        if (operatorSpi == null) {
            throw new OperatorException("No SPI found for operator '" + operatorName + "'");
        }
        return operatorSpi.createOperator(parameters, sourceProducts, renderingHints);
    }

    /**
     * Gets the registry for operator SPIs.
     *
     * @return the registry for operator SPIs.
     */
    public OperatorSpiRegistry getOperatorSpiRegistry() {
        return spiRegistry;
    }

    /**
     * Sets the registry for operator SPIs.
     *
     * @param spiRegistry the registry for operator SPIs.
     */
    public void setOperatorSpiRegistry(OperatorSpiRegistry spiRegistry) {
        Guardian.assertNotNull("spiRegistry", spiRegistry);
        this.spiRegistry = spiRegistry;
    }

    /**
     * Gets the default GPF instance.
     *
     * @return the singelton instance.
     */
    public static GPF getDefaultInstance() {
        return defaultInstance;
    }

    /**
     * Sets the default GPF instance.
     *
     * @param defaultInstance the GPF default instance.
     */
    public static void setDefaultInstance(GPF defaultInstance) {
        GPF.defaultInstance = defaultInstance;
    }

    /**
     * Writes a product with the specified format to the given file.
     *
     * @param product     the product
     * @param file        the product file
     * @param formatName  the name of a supported product format, e.g. "HDF5". If <code>null</code>, the default format
     *                    "BEAM-DIMAP" will be used
     * @param incremental switch the product writer in incremental mode or not.
     * @param pm          a monitor to inform the user about progress
     */
    public static void writeProduct(Product product, File file, String formatName, boolean incremental, ProgressMonitor pm) {
        WriteOp writeOp = new WriteOp(product, file, formatName);
        writeOp.setDeleteOutputOnFailure(true);
        writeOp.setWriteEntireTileRows(true);
        writeOp.setClearCacheAfterRowWrite(true);
        writeOp.setIncremental(incremental);
        writeOp.writeProduct(pm);
    }

    static class RenderingKey<T> extends RenderingHints.Key {

        private final Class<T> objectClass;
        private final Validator<T> validator;

        RenderingKey(int privateKey, Class<T> objectClass) {
            this(privateKey, objectClass, new Validator<T>() {
                @Override
                public boolean isValid(T val) {
                    return true;
                }
            });
        }

        RenderingKey(int privateKey, Class<T> objectClass, Validator<T> validator) {
            super(privateKey);
            this.objectClass = objectClass;
            this.validator = validator;
        }

        @Override
        public final boolean isCompatibleValue(Object val) {
            //noinspection unchecked
            return val != null && objectClass.isAssignableFrom(val.getClass()) && validator.isValid((T) val);
        }

        interface Validator<T> {

            boolean isValid(T val);
        }
    }

}
