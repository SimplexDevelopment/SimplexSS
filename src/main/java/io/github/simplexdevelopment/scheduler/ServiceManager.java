/*
 * MIT License
 *
 * Copyright (c) 2022 SimplexDevelopment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.simplexdevelopment.scheduler;

import io.github.simplexdevelopment.api.IService;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The ServiceManager is a factory class for managing {@link ServicePool}s.
 * You can use this class for easy creation of {@link ServicePool}s,
 * as well as adding and removing {@link IService}s from the pool. You can create an
 * {@link #emptyServicePool(String, boolean)}, {@link #createServicePool(String, IService...)},
 * and even create a {@link #multithreadedServicePool(String, IService...)}.
 *
 * @author SimplexDevelopment
 */
public final class ServiceManager {
    /**
     * A set of {@link ServicePool}s which are currently active.
     */
    private final Set<ServicePool> servicePools;

    /**
     * Creates a new instance of the Service Manager class.
     * This class acts as a Service Pool factory, and can be used to create
     * both single and multithreaded Service Pools, empty service pools, as well as
     * retrieve existing Service Pools. It also provides methods for you to add and remove
     * {@link IService}s from the {ServicePool} parameter.
     */
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
    public @NotNull Mono<ServicePool> createServicePool(String poolName, IService... services) {
        ServicePool pool = new ServicePool(poolName, false);
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> pool.addService(s.get()));
        servicePools.add(pool);
        return Mono.just(pool);
    }

    /**
     * @param poolName The name of the service pool.
     * @param plugin   The plugin which will be used to register the service pool.
     * @return A {@link Mono} object which contains a {@link ServicePool} element.
     * This Service Pool will execute each service within the main server thread.
     */
    @Contract(pure = true, value = "_, _ -> new")
    public @NotNull Mono<ServicePool> emptyBukkitServicePool(String poolName, JavaPlugin plugin) {
        ServicePool pool = new ServicePool(poolName, plugin);
        servicePools.add(pool);
        return Mono.just(pool);

    }

    /**
     * @param poolName The name of the service pool.
     * @param plugin   The plugin which will be used to register the service pool.
     * @param services The services to register within the service pool.
     * @return A {@link Mono} object which contains a {@link ServicePool} element.
     * This Service Pool will execute each service within the main server thread.
     */
    @Contract(pure = true, value = "_, _, _ -> new")
    public @NotNull Mono<ServicePool> bukkitServicePool(String poolName, JavaPlugin plugin, IService... services) {
        ServicePool pool = new ServicePool(poolName, plugin);
        Flux.fromIterable(Arrays.asList(services)).doOnEach(s -> pool.addService(s.get()));
        servicePools.add(pool);
        return Mono.just(pool);
    }

    /**
     * @param name     The name of the service pool.
     * @param services The services to register within the service pool.
     * @return A {@link Mono} object which contains a {@link ServicePool} element.
     * This service pool will execute each service across a set of non-blocking threads.
     */
    @Contract(pure = true, value = "_, _ -> new")
    public @NotNull Mono<ServicePool> multithreadedServicePool(String name, IService... services) {
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
    public @NotNull Mono<ServicePool> emptyServicePool(String poolName, boolean multithreaded) {
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
