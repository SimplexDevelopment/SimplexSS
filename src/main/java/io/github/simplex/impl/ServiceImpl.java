package io.github.simplex.impl;

import io.github.simplex.api.ExecutableService;
import io.github.simplex.api.IService;
import io.github.simplex.simplexss.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.publisher.Mono;

public class ServiceImpl extends ExecutableService {
    private final Main plugin;

    public ServiceImpl(Main plugin) {
        super(IService.getDefaultNamespacedKey(), 20L, 20L * 60L * 10L, true, true);
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
