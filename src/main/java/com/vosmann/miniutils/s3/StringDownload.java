package com.vosmann.miniutils.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringDownload implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(StringDownload.class);

    private Optional<String>

    @Override
    public void run() {}

    private String downloadString(final long size, final InputStream stream) {
        try(final InputStream input = new BufferedInputStream(stream)) {
            final byte[] bytes = new byte[toSafeSize(size)];
            input.read(bytes);
            final String string = new String(bytes).trim();
            return string;
        } catch (final IOException | RuntimeException e) {
            // LOG.error("Couldn't read stream into a String in memory.", e);
            return "";
        }
    }

}
