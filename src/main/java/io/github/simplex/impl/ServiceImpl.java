package io.github.simplex.impl;

import io.github.simplex.api.ExecutableService;
import io.github.simplex.api.IService;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class ServiceImpl extends ExecutableService {
    public ServiceImpl(Main plugin) {
        super(plugin, IService.getDefaultNamespacedKey(), 20L, 20L * 60L * 10L, true);
    }

    @Override
    public Mono<Void> start() {
        return null;
    }

    @Override
    public Mono<Void> stop() {
        return null;
    }

    @Override
    public void run() {
        super.run();
    }
}
