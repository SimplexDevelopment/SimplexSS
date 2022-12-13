package io.github.simplex.simplexss;

import io.github.simplex.api.IService;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ServicePool {
    /**
     * A collection of services related to this service pool.
     */
    private final Set<IService> associatedServices;
    /**
     * The scheduler used to run the services in this pool.
     */
    private final Scheduler scheduler;
    /**
     * The key used to identify this service pool.
     */
    private final NamespacedKey name;
    /**
     * The default {@link NamespacedKey} used to identify unmarked services. This will cause errors if left unchecked.
     */
    private static final NamespacedKey DEFAULT = new NamespacedKey("simplex_ss", "default_service_pool");

    /**
     * @param name The name of this service pool.
     * @param multithreaded Whether this service pool should be multithreaded, or operate upon a single thread.
     */
    public ServicePool(NamespacedKey name, boolean multithreaded) {
        this.name = name;
        this.associatedServices = new HashSet<>();
        if (multithreaded) {
            this.scheduler = Schedulers.newBoundedElastic(4, 10, "");
        } else {
            this.scheduler = Schedulers.fromExecutorService(Executors.newSingleThreadExecutor());
        }
    }

    /**
     * @return The default namespaced key to use if one is not assigned.
     */
    static NamespacedKey getDefaultNamespacedKey() {
        return DEFAULT;
    }

    /**
     * @param service Add a service to the pool's service collection.
     */
    void addService(IService service) {
        getAssociatedServices().add(service);
    }

    /**
     * @param service The service to check against this pool.
     * @return Whether the service is registered with this pool.
     */
    boolean isValidService(IService service) {
        return getAssociatedServices().contains(service);
    }

    /**
     * @return A {@link Set} of {@link IService} objects which are registered with this pool.
     */
    @Contract(pure = true)
    @NotNull
    public Set<IService> getAssociatedServices() {
        return associatedServices;
    }

    /**
     * @param service_name The name of the service to queue. This should be a service that is located within this service pool.
     *                     If you name a service that is stored within another service pool,
     *                     this method will throw an error.
     * @return A {@link Mono} object which contains a {@link Disposable} element which can be used to destroy the registered service.
     */
    public @NotNull Mono<Disposable> queueService(NamespacedKey service_name) {
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

    /**
     * @return A {@link Flux} object which contains a collection of {@link Disposable} elements,
     * which can be used to destroy the registered services using {@link ServicePool#stopServices(Flux<Disposable>)}.
     */
    public @NotNull Flux<Disposable> startServices() {
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

    /**
     * @param disposableThread A {@link Flux<Disposable>} which contains all the services that should be disposed..
     * @return A {@link Mono<Void>} object which can be used to stop the services.
     */
    public @NotNull Mono<Void> stopServices(@NotNull Flux<Disposable> disposableThread) {
        getAssociatedServices().forEach(service -> service.stop().subscribe());
        return disposableThread.doOnNext(Disposable::dispose).then();
    }

    /**
     * @param service_name The name of the service to stop.
     * @param disposable A {@link Disposable} object which contains the service that should be disposed.
     * @return A {@link Mono<Void>} object which can be used to stop the service.
     */
    public @NotNull Mono<Void> stopService(@NotNull NamespacedKey service_name, @Nullable Mono<Disposable> disposable) {
        getService(service_name).doOnNext(IService::stop).subscribe();
        if (disposable != null) {
            disposable.doOnNext(Disposable::dispose).subscribe();
        }
        return Mono.empty();
    }

    /**
     * @param service_name The name of the service to get.
     * @return A {@link Mono} object which contains the service.
     */
    public @NotNull Mono<IService> getService(NamespacedKey service_name) {
        return Flux.fromIterable(getAssociatedServices())
                .filter(service -> service.getNamespacedKey().equals(service_name))
                .next();
    }

    /**
     * @param service The service to remove from the pool's service collection.
     */
    void removeService(IService service) {
        getAssociatedServices().remove(service);
    }

    /**
     * @return This service pool after being cleared of all services.
     * You will need to register services with this pool again before using it.
     */
    public @NotNull Mono<ServicePool> recycle() {
        this.getAssociatedServices().clear();
        return Mono.just(this);
    }

    /**
     * @return The {@link Scheduler} which hosts the threads for the service pool.
     */
    @Contract(pure = true)
    public Scheduler getScheduler() {
        return scheduler;
    }
}
