package io.github.simplex.api;

import io.github.simplex.simplexss.ServiceManager;
import reactor.core.publisher.Mono;

public interface Scheduler {
    Mono<ServiceManager> getServiceManager();

    Mono<Void> queue(Service service);

    Mono<Void> runOnce(Service service);

    Mono<Void> forceStop(Service service);

    Mono<Void> forceStart(Service service);
}
