package io.github.simplex.simplexss;

import io.github.simplex.api.Service;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ServicePool {
    private final Set<Service> associatedServices;
    private final Scheduler scheduler;
    private final ExecutorService executor;
    private boolean delayed = false;
    private boolean repeating = false;

    public ServicePool() {
        this.associatedServices = new HashSet<>();
        this.executor = Executors.newSingleThreadExecutor();
        this.scheduler = Schedulers.fromExecutorService(executor);
    }

    void addService(Service service) {
        getAssociatedServices().add(service);
    }

    boolean isValidService(Service service) {
        return getAssociatedServices().contains(service);
    }

    @NotNull
    public Set<Service> getAssociatedServices() {
        return associatedServices;
    }

    public Mono<Void> startServices() {
        return Mono.just(getAssociatedServices()).doOnNext(services -> {
            for (Service service : services) {
                if (service.isRepeating()) {
                    scheduler.schedulePeriodically(service, service.getDelay() * 5, service.getPeriod() * 5, TimeUnit.MILLISECONDS);
                } else if (service.isDelayed()) {
                    scheduler.schedule(service, service.getDelay() * 5, TimeUnit.MILLISECONDS);
                }
            }
        }).then();
    }

    public Mono<Void> stopServices() {
        return Mono.just(getAssociatedServices()).doOnNext(services -> {
            for (Service service : services) {
                service.stop();
            }
        }).then();
    }

    public Service getService(int serviceID) {
        return getAssociatedServices()
                .stream()
                .filter(s -> s.getServiceID() == serviceID)
                .findFirst()
                .orElse(null);
    }

    public void removeService(Service service) {
        getAssociatedServices().remove(service);
    }

    public ServicePool recycle() {
        this.getAssociatedServices().clear();
        this.repeating = false;
        this.delayed = false;
        return this;
    }
}
