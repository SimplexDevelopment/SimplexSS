package io.github.simplex.simplexss;

import io.github.simplex.api.IService;
import io.github.simplex.api.InvalidServiceException;
import org.bukkit.NamespacedKey;
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
    private final NamespacedKey name;
    private static final NamespacedKey DEFAULT = new NamespacedKey("simplex_ss", "default_service_pool");

    public ServicePool(NamespacedKey name, boolean multithreaded) {
        this.name = name;
        this.associatedServices = new HashSet<>();
        if (multithreaded) {
            this.scheduler = Schedulers.newBoundedElastic(4, 10, "");
        } else {
            this.scheduler = Schedulers.fromExecutorService(Executors.newSingleThreadExecutor());
        }
    }

    static NamespacedKey getDefaultNamespacedKey() {
        return DEFAULT;
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

    public Mono<Disposable> startService(NamespacedKey service_name) {
        Mono<IService> service = getService(service_name);
        return service.map(s -> {
            if (s.isPeriodic()) {
                return scheduler.schedulePeriodically(s,
                        s.getDelay() * 50,
                        s.getPeriod() * 50,
                        TimeUnit.MILLISECONDS);
            }
            return scheduler.schedule(s,
                    s.getDelay() * 50,
                    TimeUnit.MILLISECONDS);

        });
    }

    public Flux<Disposable> startServices() {
        return Mono.just(getAssociatedServices()).flatMapMany(services -> {
            Set<Disposable> disposables = new HashSet<>();
            for (IService service : services) {
                if (service.isPeriodic()) {
                    disposables.add(scheduler.schedulePeriodically(service,
                            service.getDelay() * 50,
                            service.getPeriod() * 50,
                            TimeUnit.MILLISECONDS));
                } else {
                    disposables.add(scheduler.schedule(service,
                            service.getDelay() * 50,
                            TimeUnit.MILLISECONDS));
                }
            }
            return Flux.fromIterable(disposables);
        });
    }

    public Mono<Void> stopServices(Flux<Disposable> disposableThread) {
        getAssociatedServices().forEach(service -> service.stop().subscribe());
        return disposableThread.doOnNext(Disposable::dispose).then();
    }

    public Mono<Void> stopService(NamespacedKey service_name) {
        return getService(service_name).doOnNext(IService::stop).then();
    }

    public Mono<IService> getService(NamespacedKey service_name) {
        return Flux.fromIterable(getAssociatedServices())
                .filter(service -> service.getNamespacedKey().equals(service_name))
                .next();
    }

    void removeService(IService service) {
        getAssociatedServices().remove(service);
    }

    public Mono<ServicePool> recycle() {
        this.getAssociatedServices().clear();
        return Mono.just(this);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
