package io.github.simplex.api;

import io.github.simplex.simplexss.ServiceManager;
import io.github.simplex.simplexss.ServicePool;
import org.bukkit.plugin.Plugin;
import reactor.core.publisher.Mono;

public interface Service {
    int getServiceID();

    boolean isDelayed();

    boolean isRepeating();

    Mono<Void> start();

    Mono<Void> stop();

    Plugin getProvidingPlugin();

    ServicePool getServicePool();
}
