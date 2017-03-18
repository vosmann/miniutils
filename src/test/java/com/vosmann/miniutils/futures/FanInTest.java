package com.vosmann.miniutils.futures;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.vosmann.miniutils.futures.Futures.exceptionallyCompletedFuture;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class FanInTest {

    @Test
    public void testWaitOneSuccessful() {
        List<CompletableFuture<String>> futures = singletonList(completedFuture("hello"));

        FanIn<String> multiFuture = new FanIn<>(futures);
        FanInResult<String> result = multiFuture.waitForAll();

        assertThat(result.getSuccessful(), is(singletonList("hello")));

        assertThat(result.getThrowables(), empty());
    }

    @Test
    public void testWaitOneFailed() {
        List<CompletableFuture<String>> futures =
                singletonList(exceptionallyCompletedFuture(new NullPointerException("ah")));

        FanIn<String> multiFuture = new FanIn<>(futures);
        FanInResult<String> result = multiFuture.waitForAll();

        assertThat(result.getSuccessful(), empty());

        assertThat(result.getThrowables(), hasSize(1));
        assertThat(result.getThrowables().get(0), instanceOf(NullPointerException.class));
        assertThat(result.getThrowables().get(0).getMessage(), is("ah"));
    }

    @Test
    public void testWaitOneFailedWithThrow() {
        List<CompletableFuture<String>> futures = singletonList(completedFuture("ok")
                                                                        .thenApply(s -> {
                                                                            throw new IllegalArgumentException("ah");
                                                                        }));
        FanIn<String> multiFuture = new FanIn<>
                (futures);
        FanInResult<String> result = multiFuture
                .waitForAll();

        assertThat(result.getSuccessful(), empty());

        assertThat(result.getThrowables(), hasSize(1));
        assertThat(result.getThrowables().get(0), instanceOf(CompletionException.class));
    }

    @Test
    public void testWaitOneSucceededOneFailed() {
        List<CompletableFuture<String>> futures = asList(completedFuture("ok"),
                                                         exceptionallyCompletedFuture(new NullPointerException("ah")));

        FanIn<String> multiFuture = new FanIn<>(futures);
        FanInResult<String> result = multiFuture.waitForAll();

        assertThat(result.getSuccessful(), hasSize(1));
        assertThat(result.getSuccessful(), is(singletonList("ok")));

        assertThat(result.getThrowables(), hasSize(1));
        assertThat(result.getThrowables().get(0), instanceOf(NullPointerException.class));
        assertThat(result.getThrowables().get(0).getMessage(), is("ah"));
    }

    @Test
    public void testWhenCompleteAllSucceed() throws InterruptedException {

        CompletableFuture<String> okFuture = new CompletableFuture<>();
        CompletableFuture<String> goodFuture = new CompletableFuture<>();
        CompletableFuture<String> badFuture = new CompletableFuture<>();

        List<CompletableFuture<String>> futures = asList(okFuture, goodFuture, badFuture);
        StringBuilder resultString = new StringBuilder();

        new FanIn<>(futures).whenComplete(multiFutureResult -> toString(multiFutureResult, resultString));

        okFuture.complete("ok");
        goodFuture.complete("good");
        badFuture.completeExceptionally(new NullPointerException("bad"));

        Thread.sleep(2);

        assertThat(resultString.toString(), is("successful: okgood throwables: bad"));
    }

    // Write succeeded and failed futures into string.
    private void toString(FanInResult<String> multiFutureResult, StringBuilder result) {

        result.append("successful: ");
        for (String successful : multiFutureResult.getSuccessful()) {
            result.append(successful);
        }

        result.append(" throwables: ");
        for (Throwable throwable : multiFutureResult.getThrowables()) {
            result.append(throwable.getMessage());
        }
    }

}