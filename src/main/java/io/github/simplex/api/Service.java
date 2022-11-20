package io.github.simplex.api;

import io.github.simplex.simplexss.ServicePool;
import org.bukkit.plugin.Plugin;
import reactor.core.publisher.Mono;

import java.util.concurrent.RunnableScheduledFuture;

public interface Service extends RunnableScheduledFuture<Service> {
    int getServiceID();

    boolean isDelayed();

    boolean isRepeating();

    long getPeriod();

    long getDelay();

    Mono<Void> start();

    Mono<Void> stop();

    Plugin getProvidingPlugin();

    @Override
    default void run() {
        start().subscribe();
    }
}
