package com.vosmann.miniutils.futures;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MultiFuture<T> {

    private final Collection<CompletableFuture<T>> futures;

    MultiFuture(final Collection<CompletableFuture<T>> futures) {
        this.futures = futures;
    }

    public void waitForAll() {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                             .get();
        } catch (final Exception e) {
        }
    }

    public Collection<T> waitForAllOf() {
        waitForAll();
        return futures.stream()
                      .map(CompletableFuture::join)
                      .collect(toList());
    }

    public Stream<CompletableFuture<T>> getSuccessfullyCompleted() {
        return futures.stream()
                      .filter(CompletableFuture::isDone)
                      .filter(future -> !future.isCompletedExceptionally());
    }

    public Stream<CompletableFuture<T>> getExceptionallyCompleted() {
        return futures.stream()
                      .filter(CompletableFuture::isDone)
                      .filter(CompletableFuture::isCompletedExceptionally);
    }

}
