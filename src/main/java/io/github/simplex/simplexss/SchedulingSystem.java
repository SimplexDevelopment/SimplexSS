package io.github.simplex.simplexss;

import io.github.simplex.api.Scheduler;
import io.github.simplex.api.Service;
import org.bukkit.plugin.Plugin;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public final class SchedulingSystem implements Scheduler {
    private final ServiceManager serviceManager;
    private final Plugin plugin;
    private final Set<ServicePool> repeatingPools;
    private final Set<ServicePool> delayedPools;

    public SchedulingSystem(ServiceManager serviceManager, Plugin plugin) {
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
    public Mono<ServicePool> queue(Service service) {
        return getServiceManager().flatMap(serviceManager -> {
            Mono<ServicePool> pool = Mono.justOrEmpty(serviceManager.getAssociatedServicePool(service));
            return pool.defaultIfEmpty(serviceManager.createServicePool(service));
        });
    }

    @Override
    public Mono<Void> runOnce(Service service) {
        service.start().block();
        service.stop().block();
        return Mono.empty();
    }

    @Override
    public Mono<Void> forceStop(Service service) {
        return service.stop();
    }

    @Override
    public Mono<Void> forceStart(Service service) {
        return service.start();
    }

    public Plugin getProvidingPlugin() {
        return plugin;
    }
}
