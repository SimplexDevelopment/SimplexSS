package io.github.simplex.api;

import io.github.simplex.simplexss.ServiceManager;
import io.github.simplex.simplexss.ServicePool;
import reactor.core.publisher.Mono;

public interface ISchedule {
    Mono<ServiceManager> getServiceManager();

    Mono<ServicePool> queue(IService service);

    Mono<Void> runOnce(IService service);

    Mono<Void> forceStop(IService service);

    Mono<Void> forceStart(IService service);
}
