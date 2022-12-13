package io.github.simplex.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class InvalidServicePoolException extends RuntimeException {
    public InvalidServicePoolException() {
        super("There is no service pool associated with this service. The service will be automatically recycled.");
    }

    public InvalidServicePoolException(Throwable ex) {
        super(ex);
    }

    @Contract(pure = true)
    public static @NotNull Supplier<InvalidServicePoolException> supplyException() {
        return InvalidServicePoolException::new;
    }
}
