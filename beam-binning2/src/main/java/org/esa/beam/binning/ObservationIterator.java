package org.esa.beam.binning;

import org.esa.beam.binning.support.ObservationImpl;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.ProductUtils;

import javax.media.jai.PlanarImage;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Abstract implementation of Iterator interface which iterates over {@link org.esa.beam.binning.Observation Observations}.
 * To better support a streaming processing, instances of {@link org.esa.beam.binning.Observation} can be  generated on
 * the fly each time {@link ObservationIterator#next() next()} is called.
 *
 * @author Marco Peters
 */
abstract class ObservationIterator implements Iterator<Observation> {

    private Observation next;
    private boolean nextValid;
    private SamplePointer pointer;
    private final GeoCoding gc;
    private final Product product;

    public static ObservationIterator create(PlanarImage[] sourceImages, PlanarImage maskImage, Product product,
                                             float[] superSamplingSteps, Rectangle sliceRectangle) {

        SamplePointer pointer;
        if (superSamplingSteps.length == 1) {
            pointer = SamplePointer.create(sourceImages, new Rectangle[]{sliceRectangle});
        } else {
            Point2D.Float[] superSamplingPoints = SamplePointer.createSamplingPoints(superSamplingSteps);
            pointer = SamplePointer.create(sourceImages, new Rectangle[]{sliceRectangle}, superSamplingPoints);
        }
        if (maskImage == null) {
            return new NoMaskObservationIterator(product, pointer);
        } else {
            return new FullObservationIterator(product, pointer, maskImage);
        }
    }

    protected ObservationIterator(Product product, SamplePointer pointer) {
        this.pointer = pointer;
        if (product.getStartTime() != null || product.getEndTime() != null) {
            this.product = product;
        } else {
            this.product = null;
        }
        this.gc = product.getGeoCoding();
    }

    public final SamplePointer getPointer() {
        return pointer;
    }

    @Override
    public final boolean hasNext() {
        ensureValidNext();
        return next != null;
    }

    @Override
    public final Observation next() {
        ensureValidNext();
        if (next == null) {
            throw new NoSuchElementException("EMPTY");
        }
        nextValid = false;
        return next;
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException("Removing of elements is not allowed");
    }

    private void ensureValidNext() {
        if (!nextValid) {
            next = getNextObservation();
            nextValid = true;
        }
    }

    protected abstract Observation getNextObservation();

    protected Observation createObservation(int x, int y) {
        SamplePointer pointer = getPointer();
        final float[] samples = pointer.createSamples();

        Point2D.Float superSamplingPoint = pointer.getSuperSamplingPoint();
        final PixelPos pixelPos = new PixelPos();
        pixelPos.setLocation(x + superSamplingPoint.x, y + superSamplingPoint.y);
        final GeoPos geoPos = getGeoPos(pixelPos);

        double mjd = 0.0;
        if (product != null) {
            ProductData.UTC scanLineTime = ProductUtils.getScanLineTime(product, y + 0.5);
            mjd = scanLineTime.getMJD();
        }

        return new ObservationImpl(geoPos.lat, geoPos.lon, mjd, samples);
    }

    protected GeoPos getGeoPos(PixelPos pixelPos) {
        final GeoPos geoPos = new GeoPos();
        gc.getGeoPos(pixelPos, geoPos);
        return geoPos;
    }

    static class FullObservationIterator extends ObservationIterator {

        private Raster maskTile;
        private final PlanarImage maskImage;


        FullObservationIterator(Product product, SamplePointer pointer, PlanarImage maskImage) {
            super(product, pointer);
            this.maskImage = maskImage;
        }

        @Override
        protected Observation getNextObservation() {
            SamplePointer pointer = getPointer();
            while (pointer.canMove()) {
                pointer.move();
                if (isSampleValid(pointer.getX(), pointer.getY())) {
                    return createObservation(pointer.getX(), pointer.getY());
                }
            }
            return null;
        }

        private boolean isSampleValid(int x, int y) {
            if (maskTile == null || !maskTile.getBounds().contains(x, y)) {
                int tileX = maskImage.XToTileX(x);
                int tileY = maskImage.YToTileY(y);
                maskTile = maskImage.getTile(tileX, tileY);
            }

            return maskTile.getSample(x, y, 0) != 0;
        }

    }

    static class NoMaskObservationIterator extends ObservationIterator {


        NoMaskObservationIterator(Product product, SamplePointer pointer) {
            super(product, pointer);
        }

        @Override
        protected Observation getNextObservation() {
            SamplePointer pointer = getPointer();
            if (pointer.canMove()) {
                pointer.move();
                return createObservation(pointer.getX(), pointer.getY());
            }
            return null;
        }

    }
}
