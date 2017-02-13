package com.vosmann.miniutils.futures;

import com.google.common.collect.ImmutableList;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

public class MultiFutureCollector<T>
        implements Collector<CompletableFuture<T>, ImmutableList.Builder<CompletableFuture<T>>, MultiFuture<T>> {

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
    public Function<ImmutableList.Builder<CompletableFuture<T>>, MultiFuture<T>> finisher() {
        return builder -> new MultiFuture<>(builder.build());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return unmodifiableSet(emptySet());
    }

}
