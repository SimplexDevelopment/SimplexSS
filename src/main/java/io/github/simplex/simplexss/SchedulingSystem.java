package io.github.simplex.simplexss;

import io.github.simplex.api.ISchedule;
import io.github.simplex.api.IService;
import org.bukkit.plugin.java.JavaPlugin;
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
    private final Scheduler mainScheduler;

    public SchedulingSystem(@NotNull ServiceManager serviceManager, T plugin) {
        this.serviceManager = serviceManager;
        this.plugin = plugin;
        this.repeatingPools = new HashSet<>();
        this.mainScheduler = Schedulers.boundedElastic();
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
            s.start().subscribe();
            s.stop().subscribe();
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
    public Scheduler getMainSchedulerThread() {
        return mainScheduler;
    }
}
