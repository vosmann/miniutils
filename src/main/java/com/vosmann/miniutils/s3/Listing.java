package com.vosmann.miniutils.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.joining;

public class Listing {

    private static final Logger LOG = LoggerFactory.getLogger(Listing.class);

    private final Address address;
    private final List<S3ObjectSummary> summaries;
    private final Optional<String> errorMessage;

    Listing(final Address address, final String message) {
        checkNotNull(address, "Null address.");
        checkArgument(!isNullOrEmpty(message), "Error message null/empty.");
        this.address = address;
        this.summaries = ImmutableList.of();
        this.errorMessage = Optional.of(message);
    }

    Listing(final Address address, final List<S3ObjectSummary> summaries) {
        checkNotNull(address, "Null address.");
        checkNotNull(summaries, "Null summaries.");
        this.address = address;
        this.summaries = summaries;
        this.errorMessage = Optional.empty();
    }

    public List<S3ObjectSummary> get() {
        return summaries;
    }

    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    public Listing between(final Instant begin, final Instant end) {
        final List<S3ObjectSummary> filtered = summaries.stream()
            .filter(summary -> isBetween(summary, begin, end))
            .collect(toList());
        return new Listing(address, filtered);
    }

    public Listing after(final Instant begin) {
        return between(begin, Instant.MAX);
    }

    public Listing before(final Instant end) {
        return between(Instant.MIN, end);
    }

    private boolean isBetween(final S3ObjectSummary summary, final Instant begin, final Instant end) {
        final Instant instant = summary.getLastModified().toInstant();
        return begin.isBefore(instant) && end.isAfter(instant);
    }

    @Override
    public String toString() {
        if (errorMessage.isPresent()) {
            return "Listing{address=" + address + ", errorMessage=" + errorMessage + '}';
        } else {
            return "Listing{" + "address=" + address + ", keys="
                    + summaries.stream().map(s -> s.getKey()).collect(joining(",")) + '}';
        }
    }

}
