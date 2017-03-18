package com.vosmann.miniutils.futures;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

@Immutable
public class FanInResult<T> {

    private final List<T> successful;
    private final List<Throwable> throwables;

    public static <T> FanInResult<T> success(T... successful) {
        final Builder<T> builder = new Builder<>();
        Stream.of(successful).forEach(builder::add);
        return builder.build();
    }

    public static <T> FanInResult<T> failure(Throwable... throwables) {
        final Builder<T> builder = new Builder<>();
        Stream.of(throwables).forEach(builder::add);
        return builder.build();
    }

    private FanInResult(final Builder<T> builder) {
        successful = unmodifiableList(builder.successful);
        throwables = unmodifiableList(builder.throwables);
    }

    public List<T> getSuccessful() {
        return successful;
    }

    public List<Throwable> getThrowables() {
        return throwables;
    }

    public boolean hasThrowables() {
        return !throwables.isEmpty();
    }

    public String getThrowableMessages() {
        return "[" + throwables.stream()
                               .map(Throwable::toString)
                               .collect(joining(", "))
                + "]";
    }

    @Override
    public String toString() {
        return String.format("Succeeded %d: %s Failed %d: %s",
                             successful.size(), successful, throwables.size(), throwables);
    }

    public FanInResult<T> concat(FanInResult<T> other) {
        final Builder<T> builder = new Builder<>();
        Stream.concat(this.successful.stream(), other.successful.stream()).forEach(builder::add);
        Stream.concat(this.throwables.stream(), other.throwables.stream()).forEach(builder::add);
        return builder.build();
    }

    public static final class Builder<T> {

        private final List<T> successful = synchronizedList(new ArrayList<T>());
        private final List<Throwable> throwables = synchronizedList(new ArrayList<Throwable>());

        public Builder<T> add(T element) {
            successful.add(element);
            return this;
        }

        public Builder<T> add(Throwable throwable) {
            throwables.add(throwable);
            return this;
        }

        public FanInResult<T> build() {
            return new FanInResult<>(this);
        }
    }
}
