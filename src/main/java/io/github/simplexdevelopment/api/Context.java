package io.github.simplexdevelopment.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;

public interface Context<S> {
    /**
     * @return A Mono object which contains a single element represented by the definer of this Context class.
     */
    @NotNull Mono<S> getContext();

    /**
     * @param context A separate (or identical) object identified by the definer of this Context class.
     * @return A Mono object which can be used to set the element of this Context class in a non-blocking manner.
     */
    @NotNull Mono<Void> setContext(S context);

    /**
     * @return A collection of objects related to the definer of this Context class.
     */
    @Nullable Collection<S> contextCollection();

    /**
     * @return A Flux object which contains the values of the {@link Context#contextCollection()}, for non-blocking interpretation.
     */
    default @Nullable Flux<S> fluxFromCollection() {
        return contextCollection() == null ? null : Flux.fromIterable(Objects.requireNonNull(contextCollection()));
    }
}
