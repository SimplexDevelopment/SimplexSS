package io.github.simplex.api;

import java.util.function.Supplier;

public class InvalidServiceException extends RuntimeException {
    public InvalidServiceException(IService service) {
        super("The service " + service.getNamespacedKey().getKey() + " has encountered an exception.");
    }

    public InvalidServiceException(Throwable th) {
        super(th);
    }

    public static Supplier<InvalidServiceException> supplyException(IService service) {
        return () -> new InvalidServiceException(service);
    }
}
