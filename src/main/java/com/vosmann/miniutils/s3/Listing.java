package com.vosmann.miniutils.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

// T ... ObjectSummary || Instant
public class Listing {

    private final List<S3ObjectSummary> summaries;
    private final Optional<String> errorMessage;

    public static Listing of(final S3Address address) {
        return null;
    }

    public static Listing between(final Instant begin, final Instant end) {
        return null;
    }

    public static Listing after(final Instant begin) {
        return between(begin, Instant.MAX);
    }

    public static Listing before(final Instant end) {
        return between(Instant.MIN, end);
    }

    private Listing(final List<S3ObjectSummary> summaries, final String error) {
        checkNotNull(summaries);
        this.summaries = summaries;
        this.errorMessage = Optional.ofNullable(error);
    }

    public List<S3ObjectSummary> get() {
        return summaries;
    }

    public Optional<String> getErrorMessage() {
        return errorMessage;
    }
}
