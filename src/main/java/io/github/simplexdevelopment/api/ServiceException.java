package io.github.simplexdevelopment.api;

import java.util.function.Supplier;

public class ServiceException extends RuntimeException {
    /**
     * Constructs a new service exception which states the service is not present within any service pools.
     *
     * @param service The service that threw the exception.
     */
    public ServiceException(IService service) {
        super("The service " + service.getName() + " is not present within any service pool.");
    }

    /**
     * @param th The throwable that was thrown.
     */
    public ServiceException(Throwable th) {
        super(th);
    }

    /**
     * @param service The service that threw the exception.
     * @return A supplier that can be used in conjunction with Reactor.
     */
    public static Supplier<ServiceException> supplyException(IService service) {
        return () -> new ServiceException(service);
    }
}
