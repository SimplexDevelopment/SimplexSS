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

import io.github.simplexdevelopment.scheduler.ServicePool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * This class is used to represent a service pool exception.
 * This exception is thrown when a {@link ServicePool} is not found,
 * when {@link ServicePool#queueService(IService)} is called but the service is not registered with this pool,
 * or if the service pool is empty and {@link ServicePool#queueServices()} is called.
 *
 * @author SimplexDevelopment
 */
public class ServicePoolException extends RuntimeException {
    /**
     * Constructs a new generic service pool exception.
     * This will be thrown when a service attempts to execute without an associated service pool.
     */
    public ServicePoolException() {
        super("There is no service pool associated with this service. The service will be automatically recycled.");
    }

    /**
     * @param string The message to be displayed when the exception is thrown.
     */
    public ServicePoolException(@NotNull String string) {
        super(string);
    }

    /**
     * @param ex The exception to be thrown.
     */
    public ServicePoolException(Throwable ex) {
        super(ex);
    }

    /**
     * @return A supplier which can be used in conjunction with Reactor.
     */
    @Contract(pure = true)
    public static @NotNull Supplier<ServicePoolException> supplyException() {
        return ServicePoolException::new;
    }
}
