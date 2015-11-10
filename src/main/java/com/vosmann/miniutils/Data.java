package com.vosmann.miniutils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;

public class Data {

    // private static final Logger LOG = LoggerFactory.getLogger(Data.class);

    private final int maxSize;
    private byte[] bytes;

    public Data(final int maxSize) {
        this.maxSize = maxSize;
    }

    public void load(final String string) {
        checkArgument(bytes == null, "Can't load more than once.");
        checkArgument(string.length() <= maxSize, "String too long.");
        bytes = string.getBytes();
    }

    public void load(final long size, final InputStream stream) {
        checkArgument(size > 0, "Size must be positive.");
        checkArgument(size <= Integer.MAX_VALUE, "Size doesn't fit into an int.");
        load((int) size, stream);
    }

    public void load(final int size, final InputStream stream) {
        checkArgument(size <= maxSize, "String too long.");
        checkArgument(bytes == null, "Can't load more than once.");
        bytes = new byte[size];
        try (final InputStream input = new BufferedInputStream(stream)) {
            input.read(bytes);
        } catch (final IOException e) {
            // LOG.error("Could not load data from input stream.", e);
        }
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public String toString() {
        return new String(bytes);
    }

}
/*
plus ono nešto tipa obriši sve starije od ovog datuma

plus neka datumska klasa koja samo wrapa Instante
treba imat factory metode: from(string, "yyyy-MM-dd") i onda sve što fali postavi na nulu.
također treba imat i metodu to("yyyy-MM-dd-hh-mm");
vjerojatno bi sve mogla vraćat kao UTC, ali čuvati u sebi i zonski offset lokalnog vremena ili tako nešto

plus onaj generator intervala datuma s određenom preciznošću:
npr.
2015-12-21-05-01,
2015-12-21-05-02,
2015-12-21-05-03
ili pak
2015-12-21,
2015-12-22,
2015-12-23
*/
