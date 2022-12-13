package io.github.simplex.api;

import io.github.simplex.simplexss.ServiceManager;
import io.github.simplex.simplexss.ServicePool;
import reactor.core.publisher.Mono;

public interface ISchedule {

    /**
     * @return The service manager which exerts control over the service pools and their associated services.
     */
    Mono<ServiceManager> getServiceManager();

    /**
     * @param service The service to use to locate the associated service pool and queue the service for execution.
     * @return A Mono<ServicePool> that can be used to prepare the service for execution within it's associated service pool.
     * If the service has no associated pool, a new pool will be created.
     */
    Mono<ServicePool> queue(IService service);

    /**
     * @param service The service to run once.
     * @return A Mono<Void> object which can be used to run the service one time using {@link Mono#subscribe()}.
     */
    Mono<Void> runOnce(IService service);

    /**
     * @param service The service to forcefully stop.
     * @return A Mono<Void> object which can be used to forcefully stop the service with {@link Mono#subscribe()}.
     */
    Mono<Void> forceStop(IService service);

    /**
     * @param service The service to forcefully start.
     * @return A Mono<Void> object which can be used to forcefully start the service with {@link Mono#subscribe()}.
     */
    Mono<Void> forceStart(IService service);
}
