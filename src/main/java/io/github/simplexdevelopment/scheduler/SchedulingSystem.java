package io.github.simplexdevelopment.scheduler;

import io.github.simplexdevelopment.api.ISchedule;
import io.github.simplexdevelopment.api.IService;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

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

    @Override
    public @NotNull Mono<Void> runOnce(IService service) {
        return Mono.just(service)
                .doOnNext(s -> s.start()
                        .then(s.stop())
                        .subscribe())
                .then();
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
