package io.github.simplexdevelopment.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

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
