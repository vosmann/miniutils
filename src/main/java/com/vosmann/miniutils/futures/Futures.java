package com.vosmann.miniutils.futures;

import java.util.concurrent.CompletableFuture;

public class Futures {

    public static <T> CompletableFuture<T> exceptionallyCompletedFuture(final Throwable throwable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);
        return future;
    }

}