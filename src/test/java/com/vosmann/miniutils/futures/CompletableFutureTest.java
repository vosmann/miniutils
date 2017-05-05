package com.vosmann.miniutils.futures;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.fail;

public class CompletableFutureTest {

    @Test
    public void testHandleMustBeExecutedForFutureToComplete() {

        final AtomicBoolean wasHandleExecuted = new AtomicBoolean(false);

        final CompletableFuture<String> future = CompletableFuture.completedFuture("start")
                                                                  .handleAsync((string, throwable) -> {
                                                                      sleepMillis(10);
                                                                      wasHandleExecuted.set(true);
                                                                      return "done";
                                                                  }, Executors.newSingleThreadExecutor());

        try {
            future.get();
            assertThat("Function in handle() call should have been executed.", wasHandleExecuted.get());
        } catch (Exception e) {
            fail("Future should have been successful.");
        }

        assertThat("Should be done.", future.isDone());
        assertThat("Should be completed exceptionally.", !future.isCompletedExceptionally());
        assertThat("Should not be cancelled.", !future.isCancelled());
    }

    @Test
    public void testExceptionThrown() {
        final CompletableFuture<String> future = CompletableFuture.completedFuture("start")
                                                                  .thenApply(string -> {
                                                                      throw new IllegalArgumentException("bad future");
                                                                  });

        assertThat("Should be done.", future.isDone());
        assertThat("Should be completed exceptionally.", future.isCompletedExceptionally());
        assertThat("Should not be cancelled.", !future.isCancelled());

        assertExceptionThrownByGet(future, ExecutionException.class);
        assertExceptionThrownByJoin(future, CompletionException.class); // Join instead throws an unchecked CompletionException.
    }

    @Test
    public void testCancelledIsASpecialCaseOfCompletedExceptionally() {
        final boolean canInterruptIfRunning = false;
        final CompletableFuture<String> future = CompletableFuture.completedFuture("start")
                                                                  .thenApplyAsync(string -> {
                                                                      sleepMillis(TimeUnit.HOURS.toMillis(1));
                                                                      return "done";
                                                                  }, Executors.newSingleThreadExecutor());
        future.cancel(canInterruptIfRunning);

        assertThat("Should be done.", future.isDone());
        assertThat("Should be completed exceptionally.", future.isCompletedExceptionally()); // Even cancelled futures are completed exceptionally!
        assertThat("Should be cancelled.", future.isCancelled());
    }

    @Test
    public void testCancelledWithoutInterruptingException() {
        final boolean canInterruptIfRunning = false;
        final CompletableFuture<String> future = CompletableFuture.completedFuture("start")
                                                                  .thenApplyAsync(string -> {
                                                                      sleepMillis(TimeUnit.HOURS.toMillis(1));
                                                                      return "done";
                                                                  }, Executors.newSingleThreadExecutor());
        future.cancel(canInterruptIfRunning);

        assertExceptionThrownByGet(future, CancellationException.class); // Good.
        assertExceptionThrownByJoin(future, CancellationException.class);
    }

    @Test
    public void testCancelledWithInterruptingException() {
        final boolean canInterruptIfRunning = true;
        final CompletableFuture<String> future = CompletableFuture.completedFuture("start")
                                                                  .thenApplyAsync(string -> {
                                                                      sleepMillis(TimeUnit.HOURS.toMillis(1));
                                                                      return "done";
                                                                  }, Executors.newSingleThreadExecutor());
        future.cancel(canInterruptIfRunning);

        assertExceptionThrownByGet(future, CancellationException.class); // Even with the interrupt option, cancelling gives a CancellationException.
        assertExceptionThrownByJoin(future, CancellationException.class);
    }

    @Ignore("This test fails sporadically.")
    @Test
    public void testExceptionThrownDueToInterruptingThreadExecutingFuture() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final CompletableFuture<String> future = CompletableFuture.completedFuture("start")
                                                                  .thenApplyAsync(string -> {
                                                                      sleepMillis(TimeUnit.HOURS.toMillis(1));
                                                                      return "done";
                                                                  }, executor);
        executor.shutdownNow();
        assertThat("Executor should be stopped", executor.isShutdown());

        assertThat("Should not be done.", !future.isDone());
        assertThat("Should not be completed exceptionally.", !future.isCompletedExceptionally());
        assertThat("Should not be cancelled.", !future.isCancelled());

        // Not done, nor completed exceptionally, nor cancelled, but throws an exception. But not always.
        assertExceptionThrownByGet(future, ExecutionException.class);
        assertExceptionThrownByJoin(future, ExecutionException.class);
    }

    private void assertExceptionThrownByGet(CompletableFuture<String> future, Class<?> exceptionClass) {
        try {
            future.get();
        } catch (Exception e) {
            assertThat(e, instanceOf(exceptionClass));
        }
    }

    private void assertExceptionThrownByJoin(CompletableFuture<String> future, Class<?> exceptionClass) {
        try {
            future.join();
        } catch (Exception e) {
            assertThat(e, instanceOf(exceptionClass));
        }
    }

    private void sleepMillis(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            fail("Unwanted interruption of slow future dummy.");
        }
    }

}