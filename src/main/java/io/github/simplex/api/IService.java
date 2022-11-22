package io.github.simplex.api;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface IService extends Runnable, Cancellable {
    @Contract(" -> new")
    static @NotNull NamespacedKey getDefaultNamespacedKey() {
        return new NamespacedKey("simplex_ss", "default_service_name");
    }

    NamespacedKey getNamespacedKey();

    boolean isPeriodic();

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
