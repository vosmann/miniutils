package com.vosmann.miniutils.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class Deletion implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Deletion.class);

    private final Listing listing;
    private final AmazonS3Client client;

    public Deletion(final Listing listing, final AmazonS3Client client) {
        checkNotNull(listing, "Listing is null.");
        checkNotNull(client, "S3 client is null.");
        this.listing = listing;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            for (final S3ObjectSummary summary : listing.get()) {
                client.deleteObject(summary.getBucketName(), summary.getKey());
            }
        } catch (final RuntimeException e) {
            LOG.error("Could not delete {}.", listing);
        }
    }

}
