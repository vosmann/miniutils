package com.vosmann.miniutils.s3;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.joining;

public class Address {

    private static final String HTTP_PREFIX = "http://s3.amazonaws.com";
    private static final String S3_PREFIX = "s3:/"; // Second slash comes as delim.
    private static final String SLASH = "/";

    private final String bucket;
    private final String key;

    public static Address of(final String bucket, final String key) {
        return new Builder().bucket(bucket).keyPart(key).build();
    }

    private Address(Builder builder) {
        bucket = builder.bucket;
        key = builder.keyParts.stream().collect(joining(SLASH));
    }

    public static final class Builder {
        private String bucket;
        private List<String> keyParts = Lists.newArrayList();

        public Builder bucket(String bucket) {
            checkArgument(!isNullOrEmpty(bucket), "Can't set a null/blank bucket.");
            this.bucket = bucket;
            return this;
        }

        public Builder keyPart(String keyPart) {
            checkArgument(!isNullOrEmpty(keyPart), "Can't add a null/blank key part.");
            this.keyParts.add(keyPart);
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
        return Stream.of(HTTP_PREFIX, bucket, key).collect(joining(SLASH));
    }

    public String getS3Url() {
        return Stream.of(S3_PREFIX, bucket, key).collect(joining(SLASH));
    }

    @Override
    public String toString() {
        return "Address{" + "bucket='" + bucket + ", key='" + key + '}';
    }
}
