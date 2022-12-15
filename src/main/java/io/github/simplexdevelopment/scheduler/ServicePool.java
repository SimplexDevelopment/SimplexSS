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
import io.github.simplexdevelopment.api.Identifier;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
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
import java.util.concurrent.TimeUnit;

/**
 * A service pool is a collection of services which are managed by a single scheduler.
 * The scheduler can either be an instance of {@link Scheduler} or {@link ReactorBukkitScheduler}.
 * Using {@link Scheduler} allows for more flexibility, but doesn't communicate with the Main thread.
 * Using {@link ReactorBukkitScheduler} allows for communication with the Main thread, but is less flexible.
 *
 * @author SimplexDevelopment
 * @see ReactorBukkitScheduler
 * @see Scheduler
 */
public final class ServicePool implements Identifier {
    /**
     * The default {@link String} used to identify unmarked services. This will cause errors if left unchecked.
     */
    private static final String DEFAULT = "default_service_pool";
    /**
     * A collection of services related to this service pool.
     */
    private final Set<IService> associatedServices;
    /**
     * The scheduler used to run the services in this pool.
     */
    private final Scheduler scheduler;
    /**
     * The name used to identify this service pool.
     */
    private final String name;

    /**
     * This will create a new instance of a Service Pool with a {@link Scheduler} as its main scheduler.
     * This should be used if you'd like to execute tasks without communicating on the main server thread.
     *
     * @param name          The name of this service pool.
     * @param multithreaded Whether this service pool should be multithreaded, or operate upon a single thread.
     */
    public ServicePool(String name, boolean multithreaded) {
        this.name = name;
        this.associatedServices = new HashSet<>();
        if (multithreaded) {
            this.scheduler = Schedulers.boundedElastic();
        } else {
            this.scheduler = Schedulers.single();
        }
    }

    /**
     * This will create a new instance of a Service Pool with the {@link ReactorBukkitScheduler} as its main scheduler.
     * This should be used if you'd like to execute tasks while communicating on the main server thread.
     *
     * @param name The name of this service pool.
     */
    public ServicePool(String name, JavaPlugin plugin) {
        this.name = name;
        this.associatedServices = new HashSet<>();
        this.scheduler = new ReactorBukkitScheduler(plugin);
    }

    /**
     * @param service Add a service to the pool's service collection.
     */
    void addService(IService service) {
        getAssociatedServices().add(service);
    }

    /**
     * Checks to see if the defined service is present within this pool.
     *
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
     * This method is the actual method used to schedule a service.
     * This will register the service with the scheduler, and then return a {@link Disposable} object
     * encapsulated within a {@link Mono}. If {@link IService#isPeriodic()} returns true, then the service
     * will be scheduled to run periodically. Otherwise, it will be scheduled to run once.
     *
     * @param service The name of the service to queue. This should be a service that is located within this service pool.
     *                If you name a service that is stored within another service pool,
     *                this method will throw an error.
     * @return A {@link Mono} object which contains a {@link Disposable} element which can be used to destroy the registered service.
     */
    public @NotNull Mono<Disposable> queueService(IService service) {
        return Mono.just(service).map(s -> {
            if (s.isPeriodic()) {
                return scheduler.schedulePeriodically(s,
                        s.getDelay() * 50,
                        s.getPeriod() * 50,
                        TimeUnit.MILLISECONDS);
            } else {
                return scheduler.schedule(s,
                        s.getDelay() * 50,
                        TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * This method can be used to start all the services registered with this pool.
     * If there are no services, this will do nothing.
     *
     * @return A {@link Flux} object which contains a collection of {@link Disposable} elements,
     * which can be used to destroy the registered services using {@link ServicePool#stopServices(Flux)}.
     */
    public @NotNull Flux<Disposable> queueServices() {
        Set<Disposable> disposables = new HashSet<>();
        return Flux.fromIterable(getAssociatedServices())
                .doOnEach(service -> disposables.add(queueService(service.get()).block()))
                .flatMap(service -> Flux.fromIterable(disposables));
    }

    /**
     * This method can be used to stop all the services registered with this pool.
     * If there are no services, this will do nothing.
     *
     * @param disposableThread A {@link Flux<Disposable>} which contains all the services that should be disposed..
     * @return A {@link Mono<Void>} object which can be used to stop the services.
     */
    public @NotNull Mono<Void> stopServices(@NotNull Flux<Disposable> disposableThread) {
        getAssociatedServices().forEach(service -> service.stop().subscribe());
        return disposableThread.doOnNext(Disposable::dispose).then();
    }

    /**
     * This is the method used to stop a service. This will call the relative {@link Disposable#dispose} method
     * to the {@link Scheduler} supplied for this pool. If you are using the {@link ReactorBukkitScheduler},
     * this will cancel the task upstream on the {@link BukkitScheduler}.
     *
     * @param service_name The name of the service to stop.
     * @param disposable   A {@link Disposable} object which contains the service that should be disposed.
     * @return A {@link Mono<Void>} object which can be used to stop the service.
     */
    public @NotNull Mono<Void> stopService(@NotNull String service_name, @Nullable Mono<Disposable> disposable) {
        getService(service_name).doOnNext(IService::stop).subscribe();
        if (disposable != null) {
            disposable.doOnNext(Disposable::dispose).subscribe();
        }
        return Mono.empty();
    }

    /**
     * Gets a service based on the name of the service defined by {@link Identifier#getName()}.
     * This will search the service pool for a service with the same name, and return it.
     *
     * @param service_name The name of the service to get.
     * @return A {@link Mono} object which contains the service.
     */
    public @NotNull Mono<IService> getService(String service_name) {
        return Flux.fromIterable(getAssociatedServices())
                .filter(service -> service.getName().equals(service_name))
                .next();
    }

    /**
     * This method removes a service from the service pool set.
     *
     * @param service The service to remove from the pool's service collection.
     */
    void removeService(IService service) {
        getAssociatedServices().remove(service);
    }

    /**
     * This will clear the ServicePool of all services and return an empty pool.
     *
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

    @Override
    public String getName() {
        return name;
    }
}
