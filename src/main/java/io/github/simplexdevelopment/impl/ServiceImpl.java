package io.github.simplexdevelopment.impl;

import io.github.simplexdevelopment.api.ExecutableService;
import io.github.simplexdevelopment.api.IService;
import io.github.simplexdevelopment.scheduler.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.publisher.Mono;

public class ServiceImpl extends ExecutableService {
    private final Main plugin;

    public ServiceImpl(Main plugin) {
        super(plugin.pool, IService.getDefaultNamespacedKey(), 0L, 20 * 60 * 20L, true, false);
        this.plugin = plugin;
    }

    @Override
    public Mono<Void> start() {
        return Mono.just(plugin)
                .map(JavaPlugin::getLogger)
                .doOnNext(l -> l.info("The service has executed successfully!"))
                .then();
    }

    @Override
    public Mono<Void> stop() {
        return Mono.just(plugin)
                .map(JavaPlugin::getLogger)
                .doOnNext(l -> l.info("The service has stopped"))
                .then();
    }

    @Override
    public Main getPlugin() {
        return plugin;
    }

    @Override
    public Mono<ServicePool> getParentPool() {
        return getPlugin()
                .getScheduler()
                .getServiceManager()
                .flatMap(manager -> manager.getAssociatedServicePool(this));
    }
}
