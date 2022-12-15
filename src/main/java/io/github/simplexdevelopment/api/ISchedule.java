package io.github.simplexdevelopment.api;

import io.github.simplexdevelopment.scheduler.ServiceManager;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public interface ISchedule {

    /**
     * @return The service manager which exerts control over the service pools and their associated services.
     */
    Mono<ServiceManager> getServiceManager();

    /**
     * Queues a service to be executed in a service pool.
     *
     * @param service The service to use to locate the associated service pool and queue the service for execution.
     * @return A Mono object that can be used to cancel the service.
     */
    @NotNull Mono<Disposable> queue(@NotNull IService service);

    /**
     * @param service The service to run once.
     * @return A Mono object which can be used to run the service one time using {@link Mono#subscribe()}.
     */
    Mono<Void> runOnce(IService service);

    /**
     * @param service The service to forcefully stop.
     * @return A Mono object which can be used to forcefully stop the service with {@link Mono#subscribe()}.
     */
    Mono<Void> forceStop(IService service);

    /**
     * @param service The service to forcefully start.
     * @return A Mono object which can be used to forcefully start the service with {@link Mono#subscribe()}.
     */
    Mono<Void> forceStart(IService service);
}