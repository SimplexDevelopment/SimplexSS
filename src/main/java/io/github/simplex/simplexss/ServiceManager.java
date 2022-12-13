package io.github.simplex.simplexss;

import io.github.simplex.api.IService;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ServiceManager {
    private final Set<ServicePool> servicePools;

    public ServiceManager() {
        servicePools = new HashSet<>();
    }

    /**
     * @param poolName The name of the service pool.
     * @param services The services to register within the service pool.
     * @return A {@link Mono} object which contains a {@link ServicePool} element.
     * This service pool will execute each service consecutively within a singular non-blocking thread.
     */
    @Contract(pure = true, value = "_, _ -> new")
    public @NotNull Mono<ServicePool> createServicePool(NamespacedKey poolName, IService... services) {
        ServicePool pool = new ServicePool(poolName, false);
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> pool.addService(s.get()));
        return Mono.just(pool);
    }

    /**
     * @param name     The name of the service pool.
     * @param services The services to register within the service pool.
     * @return A {@link Mono} object which contains a {@link ServicePool} element.
     * This service pool will execute each service across a set of non-blocking threads.
     */
    @Contract(pure = true, value = "_, _ -> new")
    public @NotNull Mono<ServicePool> multithreadedServicePool(NamespacedKey name, IService... services) {
        ServicePool pool = new ServicePool(name, true);
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> pool.addService(s.get()));
        return Mono.just(pool);
    }

    /**
     * @param poolName      The name of the service pool.
     * @param multithreaded Whether the service pool should be multithreaded, or operate upon a single thread.
     * @return A {@link Mono} object which contains a {@link ServicePool} element.
     * This pool is empty, meaning it contains no services. Any attempt to run services on this pool while it remains empty will either fail or error.
     * You can add services to this pool by using {@link ServiceManager#addToExistingPool(ServicePool, IService...)},
     * or by using {@link ServicePool#addService(IService)}.
     */
    @Contract(pure = true, value = "_, _ -> new")
    public @NotNull Mono<ServicePool> emptyServicePool(NamespacedKey poolName, boolean multithreaded) {
        ServicePool pool = new ServicePool(poolName, multithreaded);
        return Mono.just(pool);
    }

    /**
     * @param pool     The service pool to add to.
     * @param services The services to register within the service pool.
     * @return A {@link Mono} object which contains the {@link ServicePool} element that now contains the registered services.
     */
    @Contract("_, _ -> new")
    public @NotNull Mono<ServicePool> addToExistingPool(@NotNull ServicePool pool, IService... services) {
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> {
            pool.addService(s.get());
        });
        return Mono.just(pool);
    }

    /**
     * @param pool     The service pool to take from.
     * @param services The services to remove from the pool.
     * @return A {@link Mono} object which contains the {@link ServicePool} that no longer contains the removed services.
     */
    @Contract("_, _ -> new")
    public @NotNull Mono<ServicePool> takeFromExistingPool(@NotNull ServicePool pool, IService... services) {
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> {
            pool.removeService(s.get());
        });
        return Mono.just(pool);
    }

    /**
     * @return A {@link Flux} object which contains all the service pools currently available.
     */
    @Contract(" -> new")
    public @NotNull Flux<ServicePool> getServicePools() {
        return Flux.fromIterable(servicePools);
    }

    /**
     * @param service The service to locate.
     * @return True if the service is somewhere within a service pool, false otherwise.
     */
    @Contract(pure = true)
    public boolean locateServiceWithinPools(IService service) {
        return servicePools.stream().map(p -> p.isValidService(service)).findFirst().orElseGet(() -> false);
    }

    /**
     * @param service The service pool to call from.
     * @return A {@link Mono} object which contains a {@link ServicePool} element which contains the specified service.
     * If no service pool can be found, an empty Mono is returned.
     */
    @Contract("_ -> new")
    public @NotNull Mono<ServicePool> getAssociatedServicePool(IService service) {
        if (!locateServiceWithinPools(service)) return Mono.empty();
        return getServicePools()
                .filter(p -> p.getAssociatedServices().contains(service))
                .next();
    }
}
