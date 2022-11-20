package io.github.simplex.simplexss;

import io.github.simplex.api.IService;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ServiceManager {
    private final Set<ServicePool> servicePools;
    private final Plugin plugin;

    public ServiceManager(Plugin plugin) {
        this.plugin = plugin;
        servicePools = new HashSet<>();
    }

    @Contract(pure = true, value = "_ -> new")
    public @NotNull Mono<ServicePool> createServicePool(IService... services) {
        ServicePool pool = new ServicePool(false);
        Stream.of(services).forEach(pool::addService);
        return Mono.just(pool);
    }

    public @NotNull Mono<ServicePool> multithreadedServicePool(IService... services) {
        ServicePool pool = new ServicePool(true);
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> {
            pool.addService(s.get());
        });
        return Mono.just(pool);
    }

    @Contract("_, _ -> param1")
    public @NotNull Mono<ServicePool> addToExistingPool(@NotNull ServicePool pool, IService... services) {
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> {
            pool.addService(s.get());
        });
        return Mono.just(pool);
    }

    @Contract("_, _ -> param1")
    public @NotNull Mono<ServicePool> takeFromExistingPool(@NotNull ServicePool pool, IService... services) {
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> {
            pool.removeService(s.get());
        });
        return Mono.just(pool);
    }

    public @NotNull Flux<ServicePool> getServicePools() {
        return Flux.fromIterable(servicePools);
    }

    public boolean locateServiceWithinPools(IService service) {
        return servicePools.stream().map(p -> p.isValidService(service)).findFirst().orElseGet(() -> false);
    }

    public @NotNull Mono<ServicePool> getAssociatedServicePool(IService service) {
        if (!locateServiceWithinPools(service)) return Mono.empty();
        return getServicePools()
                .filter(p -> p.getAssociatedServices().contains(service))
                .next();
    }

    public Plugin getProvidingPlugin() {
        return plugin;
    }
}
