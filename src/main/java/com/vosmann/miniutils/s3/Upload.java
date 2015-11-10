package com.vosmann.miniutils.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Upload implements Runnable {

    private final S3Address address;
    private final ObjectMetadata metadata;
    private final InputStream stream;
    private final AmazonS3Client client;

    public Upload(S3Address address, int size, final InputStream stream, final AmazonS3Client client) {
        checkNotNull(address, "S3Address is null.");
        checkArgument(size > 0, "Size must be positive.");
        checkNotNull(stream, "InputStream is null.");
        checkNotNull(client, "S3 client is null.");
        this.address = address;
        this.metadata = new ObjectMetadata();
        this.metadata.setContentLength(size);
        this.stream = stream;
        this.client = client;
    }

    @Override
    public void run() {
        // LOG.info("Uploading {} bytes from input stream to {}.", address);
        try {
            client.putObject(address.getBucket(), address.getKey(), stream, metadata);
            // LOG.info("Finished uploading to {}.", address);
        } catch (final RuntimeException e) {
            // LOG.error("Failed uploading to {}.", address, e);
        } finally {
            close(stream);
        }
    }

    private void close(final InputStream stream) {
        try {
            stream.close();
        } catch (IOException e) {
            // LOG.error("Could not close input stream.");
        }
    }

}
