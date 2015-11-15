package com.vosmann.miniutils.s3;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.joining;

public class Address {

    private static final String HTTP_PREFIX = "http://s3";
    private static final String HTTP_SUFFIX = ".amazonaws.com";
    private static final String SLASH = "/";
    private static final String DASH = "-";

    private final String bucket;
    private final String key;
    private final Optional<String> region;

    public static Address of(final String bucket, final String key) {
        return new Builder().bucket(bucket).keyPart(key).build();
    }

    public static Address of(final String bucket, final String key, final String region) {
        return new Builder().bucket(bucket).keyPart(key).region(region).build();
    }

    private Address(Builder builder) {
        bucket = builder.bucket;
        key = builder.keyParts.stream().collect(joining(SLASH));
        region = builder.region;
        checkArgument(!isNullOrEmpty(bucket), "Can't have a null/blank bucket.");
        checkArgument(!isNullOrEmpty(key), "Can't have a null/blank key.");
        checkArgument(!region.isPresent() || !isNullOrEmpty(region.get()), "Can't have a null/blank region.");
    }

    public static final class Builder {
        private String bucket;
        private List<String> keyParts = Lists.newArrayList();
        private Optional<String> region = Optional.empty();

        public Builder bucket(final String bucket) {
            checkArgument(!isNullOrEmpty(bucket), "Can't set a null/blank bucket.");
            this.bucket = bucket;
            return this;
        }

        public Builder keyPart(final String keyPart) {
            checkArgument(!isNullOrEmpty(keyPart), "Can't add a null/blank key part.");
            this.keyParts.add(keyPart);
            return this;
        }

        public Builder region(final String region) {
            checkArgument(!isNullOrEmpty(region), "Can't add a null/blank region.");
            this.region = Optional.of(region);
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }

    public String getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }

    public String getHttpUrl() {
        if (region.isPresent()) {
            return HTTP_PREFIX + DASH + region.get() + HTTP_SUFFIX + SLASH + bucket + SLASH + key;
        } else {
            return HTTP_PREFIX + HTTP_SUFFIX + SLASH + bucket + SLASH + key;
        }
    }

    @Override
    public String toString() {
        return "Address{" + "bucket='" + bucket + '\'' + ", key='" + key + '\'' + ", region=" + region + '}';
    }

}
