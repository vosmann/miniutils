package com.vosmann.miniutils.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Lister {

    private static final Logger LOG = LoggerFactory.getLogger(Lister.class);

    private final AmazonS3Client client;

    public Lister(AmazonS3Client client) {
        this.client = client;
    }

    public Listing at(final Address address) {
        try {
            final ObjectListing listing = getObjectListing(address);
            return new Listing(address, listing.getObjectSummaries());
        } catch (final RuntimeException e) {
            LOG.warn("Could not list objects at {}. Returning empty.", address);
            return new Listing(address, "Listing failed: " + e.getMessage());
        }
    }

    private ObjectListing getObjectListing(final Address address) {
        return client.listObjects(
                new ListObjectsRequest().withBucketName(address.getBucket()).withPrefix(address.getKey()));
    }

}
