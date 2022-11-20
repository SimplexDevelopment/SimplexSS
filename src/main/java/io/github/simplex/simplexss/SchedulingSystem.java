package io.github.simplex.simplexss;

import io.github.simplex.api.ISchedule;
import io.github.simplex.api.IService;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class SchedulingSystem implements ISchedule {
    private final ServiceManager serviceManager;
    private final Plugin plugin;
    private final Set<ServicePool> repeatingPools;
    private final Set<ServicePool> delayedPools;

    public SchedulingSystem(@NotNull ServiceManager serviceManager, Plugin plugin) {
        this.serviceManager = serviceManager;
        this.plugin = plugin;
        this.repeatingPools = new HashSet<>();
        this.delayedPools = new HashSet<>();
    }

    public Set<ServicePool> getRepeatingPools() {
        return repeatingPools;
    }

    public Set<ServicePool> getDelayedPools() {
        return delayedPools;
    }

    @Override
    public Mono<ServiceManager> getServiceManager() {
        return Mono.just(serviceManager);
    }

    @Override
    @NotNull
    public Mono<ServicePool> queue(@NotNull IService service) {
        return getServiceManager().flatMap(serviceManager -> {
            Mono<ServicePool> pool = serviceManager.getAssociatedServicePool(service);
            return pool.defaultIfEmpty(Objects.requireNonNull(serviceManager.createServicePool(service).block()));
        });
    }

    @Override
    public Mono<Void> runOnce(IService service) {
        return Mono.just(service).doOnNext(s -> {
            s.start();
            s.stop();
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

    public Plugin getProvidingPlugin() {
        return plugin;
    }
}
