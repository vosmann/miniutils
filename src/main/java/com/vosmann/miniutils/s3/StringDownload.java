package com.vosmann.miniutils.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.vosmann.miniutils.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class StringDownload implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(StringDownload.class);

    private final Address address;
    private final int maxSize;
    private final AmazonS3Client client;

    private Optional<String> result = Optional.empty();

    public StringDownload(Address address, int maxSize, AmazonS3Client client) {
        checkNotNull(address, "S3Address is null.");
        checkArgument(maxSize > 0, "Size must be positive.");
        checkNotNull(client, "S3 client is null.");
        this.address = address;
        this.maxSize = maxSize;
        this.client = client;
    }

    public Optional<String> getResult() {
        return result;
    }

    @Override
    public void run() {
        LOG.info("Downloading max {} bytes from {}.", address);
        try {
            final S3Object obj = client.getObject(address.getBucket(), address.getKey());
            warnSize(obj);

            final Data data = Data.from(maxSize, obj.getObjectContent());
            final String string = data.toString();

            if (!isNullOrEmpty(string)) {
                result = Optional.empty().of(string);
            } else {
                result = Optional.empty();
            }
        } catch (final RuntimeException e) {
            LOG.error("Could not download String from {}. Returning empty.", address, e);
            result = Optional.empty();
        }
    }

    private void warnSize(final S3Object obj) {
        final long objSize = obj.getObjectMetadata().getContentLength();
        if (objSize > (long) maxSize) {
            LOG.warn("The S3 object is bigger than expected: {}B. Will read only {}B.", objSize, maxSize);
        }
    }

}
