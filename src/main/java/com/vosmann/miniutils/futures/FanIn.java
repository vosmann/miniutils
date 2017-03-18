package com.vosmann.miniutils.futures;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;

/**
 * Convenience class for getting results from many CompletableFutures, be they successful or completed
 * exceptionally.
 * The constructor converts all the futures into Void ones because we will only want to know when they are done and
 * will use FanInResultFuture for keeping track of the actual results.
 */
@Immutable
public class FanIn<T> {

    private final FanInResult.Builder<T> multiFutureResult;
    private final List<CompletableFuture<T>> futures;

    public static <T> FanIn<T> of(final CompletableFuture<T> future) {
        return new FanIn<>(singletonList(future));
    }

    /**
     * Intended to be called by FanInFutureCollector.
     */
    FanIn(final Collection<CompletableFuture<T>> futures) {
        this.multiFutureResult = new FanInResult.Builder<>();
        this.futures = futures.stream()
                              .map(future -> future.<T>handle((result, throwable) -> {
                                  if (throwable == null) {
                                      multiFutureResult.add(result);
                                  } else {
                                      multiFutureResult.add(throwable);
                                  }
                                  return null; // Unused; type is Void.
                              }))
                              .collect(toList());

    }

    /**
     * Blocks to wait for all of the futures to finish.
     *
     * @return FanInResult containing results of successfully completed futures and Throwables for failed
     * ones.
     */
    public FanInResult<T> waitForAll() {
        allOf().join();
        return multiFutureResult.build();
    }

    public FanInResult<T> waitForAll(final long timeout, final TimeUnit unit) {
        try {
            allOf().get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
        }
        return multiFutureResult.build();
    }

    /**
     * A simplified version of CompletableFuture's whenComplete. This one does not support handling throwables,
     * as this
     * is already being done for each future separately.
     *
     * @param consumer A consumer that receives a FanInResult and defines actions on both its successfully and
     *                 exceptionally completed futures.
     */
    public void whenComplete(final Consumer<FanInResult<T>> consumer) {
        allOf().whenComplete((aVoid, throwable) -> consumer.accept(multiFutureResult.build()));
    }

    private CompletableFuture<Void> allOf() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
    }

    public static class FanInFutureCollector<T>
            implements Collector<CompletableFuture<T>, ImmutableList.Builder<CompletableFuture<T>>, FanIn<T>> {

        public static <T> FanInFutureCollector<T> toFanInFuture() {
            return new FanInFutureCollector<>();
        }

        @Override
        public Supplier<ImmutableList.Builder<CompletableFuture<T>>> supplier() {
            return ImmutableList.Builder<CompletableFuture<T>>::new;
        }

        @Override
        public BiConsumer<ImmutableList.Builder<CompletableFuture<T>>, CompletableFuture<T>> accumulator() {
            return (builder, element) -> builder.add(element);
        }

        @Override
        public BinaryOperator<ImmutableList.Builder<CompletableFuture<T>>> combiner() {
            return (builder1, builder2) -> builder1.addAll(builder2.build());
        }

        @Override
        public Function<ImmutableList.Builder<CompletableFuture<T>>, FanIn<T>> finisher() {
            return builder -> new FanIn<>(builder.build());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return unmodifiableSet(emptySet());
        }

    }

}
