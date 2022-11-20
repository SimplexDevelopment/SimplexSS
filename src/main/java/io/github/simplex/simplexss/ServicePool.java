package io.github.simplex.simplexss;

import io.github.simplex.api.IService;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ServicePool {
    private final Set<IService> associatedServices;
    private final Scheduler scheduler;
    private final ExecutorService executor;

    public ServicePool() {
        this.associatedServices = new HashSet<>();
        this.executor = Executors.newSingleThreadExecutor();
        this.scheduler = Schedulers.fromExecutorService(executor);
    }

    void addService(IService service) {
        getAssociatedServices().add(service);
    }

    boolean isValidService(IService service) {
        return getAssociatedServices().contains(service);
    }

    @NotNull
    public Set<IService> getAssociatedServices() {
        return associatedServices;
    }

    public Flux<Disposable> startServices() {
        return Mono.just(getAssociatedServices()).flatMapMany(services -> {
            Set<Disposable> disposables = new HashSet<>();
            for (IService service : services) {
                if (service.isRepeating()) {
                    disposables.add(scheduler.schedulePeriodically(service,
                            service.getDelay() * 5,
                            service.getPeriod() * 5,
                            TimeUnit.MILLISECONDS));
                } else if (service.isDelayed()) {
                    disposables.add(scheduler.schedule(service,
                            service.getDelay() * 5,
                            TimeUnit.MILLISECONDS));
                } else {
                    disposables.add(scheduler.schedule(service));
                }
            }
            ;
            return Flux.fromIterable(disposables);
        });
    }

    public Mono<Void> stopServices(Flux<Disposable> disposableThread) {
        return Mono.just(getAssociatedServices()).doOnNext(services -> {
            for (IService service : services) {
                disposableThread.doOnNext(Disposable::dispose);
            }
        }).then();
    }

    public Mono<IService> getService(int serviceID) {
        return Flux.fromIterable(getAssociatedServices())
                .filter(service -> service.getServiceID() == serviceID)
                .next();
    }

    void removeService(IService service) {
        getAssociatedServices().remove(service);
    }

    public ServicePool recycle() {
        this.getAssociatedServices().clear();
        return this;
    }
}
