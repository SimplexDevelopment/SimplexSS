package io.github.simplexdevelopment.scheduler;

import io.github.simplexdevelopment.api.ISchedule;
import io.github.simplexdevelopment.api.IService;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class SchedulingSystem<T extends JavaPlugin> implements ISchedule {
    private final ServiceManager serviceManager;
    private final T plugin;
    private final Set<ServicePool> repeatingPools;
    private final ReactorBukkitScheduler mainScheduler;

    /**
     * Creates a new instance of the scheduling system. This is used to manage the scheduling of services.
     *
     * @param plugin         The plugin to use for this scheduling system. This should be an instance of your plugin.
     */
    public SchedulingSystem(T plugin) {
        this.serviceManager = new ServiceManager();
        this.plugin = plugin;
        this.repeatingPools = new HashSet<>();
        this.mainScheduler = new ReactorBukkitScheduler(plugin, plugin.getServer().getScheduler());
    }

    /**
     * @return A set of {@link ServicePool}s which contain repeating services.
     */
    @Contract(pure = true)
    public Set<ServicePool> getRepeatingPools() {
        return repeatingPools;
    }

    @Override
    public @NotNull Mono<ServiceManager> getServiceManager() {
        return Mono.just(serviceManager);
    }

    @Override
    @NotNull
    public Mono<ServicePool> queue(@NotNull IService service) {
        return getServiceManager().flatMap(serviceManager -> {
            Mono<ServicePool> pool = serviceManager.getAssociatedServicePool(service);
            return pool.defaultIfEmpty(Objects.requireNonNull(serviceManager
                    .createServicePool(ServicePool.getDefaultNamespacedKey(), service)
                    .block()));
        });
    }

    @Override
    public @NotNull Mono<Void> runOnce(IService service) {
        return Mono.just(service).doOnNext(s -> {
            s.start().then(s.stop()).subscribe();
        }).then();
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
