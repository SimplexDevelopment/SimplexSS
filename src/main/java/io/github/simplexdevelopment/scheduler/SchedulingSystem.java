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
 *
 */

package io.github.simplexdevelopment.scheduler;

import io.github.simplexdevelopment.api.ISchedule;
import io.github.simplexdevelopment.api.IService;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class is used to manage the scheduling of {@link IService}s,
 * and the creation of {@link ServicePool}s through the {@link ServiceManager}.
 * The {@link ServiceManager} should be used to create new {@link ServicePool}s on
 * initialization of your plugin, and your {@link IService}s should be registered in the
 * {@link JavaPlugin#onEnable()} method. You can then use the {@link #queue(IService)} method
 * to then queue up your services, or call {@link #queueAll()} to queue up all services in every pool.
 * {@link #forceStart(IService)} and {@link #forceStop(IService)} will forcefully start and stop the services, respectively.
 * {@link #getMainScheduler()} gets the main scheduler for the scheduling system, which is a {@link ReactorBukkitScheduler} object.
 *
 * @param <T> Your plugin class, which extends {@link JavaPlugin}.
 * @author SimplexDevelopment
 * @see ServiceManager
 * @see ServicePool
 * @see ReactorBukkitScheduler
 */
public final class SchedulingSystem<T extends JavaPlugin> implements ISchedule {
    /**
     * A denominator to use when registering default service pool names.
     */
    static int denom = 0;
    /**
     * The service manager to use for controlling service pools.
     */
    private final ServiceManager serviceManager;
    /**
     * The plugin to use for registering tasks. This should be an instance of your plugin.
     */
    private final T plugin;
    /**
     * The main scheduler which this system runs on. This is an abstraction of the {@link BukkitScheduler},
     * and as a result runs on the Main server thread.
     */
    private final ReactorBukkitScheduler mainScheduler;

    /**
     * Creates a new instance of the scheduling system. This is used to manage the scheduling of services.
     *
     * @param plugin The plugin to use for this scheduling system. This should be an instance of your plugin.
     */
    public SchedulingSystem(T plugin) {
        this.serviceManager = new ServiceManager();
        this.plugin = plugin;
        this.mainScheduler = new ReactorBukkitScheduler(plugin);
    }

    @Override
    public @NotNull Mono<ServiceManager> getServiceManager() {
        return Mono.just(serviceManager);
    }

    @Override
    @NotNull
    public Mono<Disposable> queue(@NotNull IService service) {
        return getServiceManager()
                .flatMap(manager -> manager.getAssociatedServicePool(service))
                .flatMap(pool -> pool.queueService(service));
    }

    public Flux<Disposable> queueAll() {
        return getServiceManager()
                .flatMapMany(ServiceManager::getServicePools)
                .flatMap(ServicePool::queueServices);
    }

    @Override
    public @NotNull Mono<Void> runOnce(IService service) {
        return Mono.create(sink -> service.start().then(service.stop()).subscribe(sink::success));
    }

    @Override
    public Mono<Void> forceStop(IService service) {
        return service.stop();
    }

    @Override
    public Mono<Void> forceStart(IService service) {
        return service.start();
    }

    /**
     * @return A Mono object containing your plugin, for non-blocking communication.
     */
    public @NotNull Mono<T> getProvidingPlugin() {
        return Mono.just(plugin);
    }

    /**
     * @return The main thread which the scheduling system operates on.
     */
    @Contract(pure = true)
    public ReactorBukkitScheduler getMainScheduler() {
        return mainScheduler;
    }
}
