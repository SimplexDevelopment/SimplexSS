/*
 * MIT License
 *
 * Copyright (c) 2022 SimplexDevelopment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.simplexdevelopment.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;

/**
 * A glorified wrapper class which collects an element and abstracts it behind the Reactor API.
 *
 * @param <S> Any object type to wrap with this class.
 * @author SimplexDevelopment
 */
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
