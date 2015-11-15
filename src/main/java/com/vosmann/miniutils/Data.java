package com.vosmann.miniutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Loads from a stream or string into an in-memory byte array in one go.
 */
public class Data {

    private static final Logger LOG = LoggerFactory.getLogger(Data.class);
    private static final int WARN_SIZE = 10 * 1024 * 1024;  // 10 MiB.

    private byte[] bytes;

    private Data(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Data empty() {
        return new Data(new byte[0]);
    }

    public static Data from(final String string) {
        warnSize(string.length());
        return new Data(string.getBytes());
    }

    public static Data from(final long size, final InputStream stream) {
        checkArgument(size <= Integer.MAX_VALUE, "Size doesn't fit into an int.");
        return from((int) size, stream);
    }

    public static Data from(final int maxSize, final InputStream stream /* inputStream */) {
        checkArgument(maxSize > 0, "Max size must be positive.");
        warnSize(maxSize);
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];
            while(true) {
                final int nrRead = stream.read(buffer);
                if( nrRead < 0) {
                    break;
                }
                final int nrToWrite = Math.min(nrRead, maxSize - baos.size());
                if (nrRead > nrToWrite) {
                    LOG.warn("InputStream contained more data than was read.");
                }
                baos.write(buffer, 0, nrToWrite);
                if (nrToWrite < nrRead) {
                    break;
                }
            }
            byte[] bytes = baos.toByteArray(); // Another copy.

            LOG.info("Read {}B from stream. Returning array of that length.", bytes.length);
            return new Data(bytes);
        } catch (final IOException | RuntimeException e) {
            LOG.error("Could not load data from input stream. Returning empty.", e);
            return Data.empty();
        } finally {
            close(stream); // Doesn't get called only if System.exit(), thread interrupt or JVM crash.
        }

    }

    public int getSize() {
        return bytes.length;
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public String toString() {
        return new String(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        if (bytes.length != data.bytes.length) return false;
        return Arrays.equals(bytes, data.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    private static void warnSize(final int size) {
        if (size > WARN_SIZE) {
            LOG.warn("Loading data bigger than {} B.");
        }
    }

    private static void close(final InputStream stream) {
        try {
            stream.close();
        } catch (final IOException e) {
            LOG.warn("Could not close input stream.");
        }
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
