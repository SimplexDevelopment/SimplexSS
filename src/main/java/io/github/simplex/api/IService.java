package io.github.simplex.api;

import io.github.simplex.simplexss.ServicePool;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface IService extends Runnable {
    @Contract(" -> new")
    static @NotNull NamespacedKey getDefaultNamespacedKey() {
        return new NamespacedKey("simplex_ss", "default_service_name");
    }

    /**
     * @return The NamespacedKey of this service.
     */
    NamespacedKey getNamespacedKey();

    /**
     * @return If the service should be scheduled for repeated executions or not.
     */
    boolean isPeriodic();

    /**
     * @return How long the service should wait between subsequent executions.
     */
    long getPeriod();

    /**
     * @return How long the service should wait before executing the first time.
     */
    long getDelay();

    /**
     * The actual start method for the service. This should be overridden by subclasses,
     * and should include all the required code necessary to execute when the service is queued.
     *
     * @return An encapsulated Mono object representing the start method for the service.
     */
    Mono<Void> start();

    /**
     * The actual end method for the service. This should be overridden by subclasses,
     * and should include all the required code necessary to execute when the service is stopped.
     *
     * @return An encapsulated Mono object representing the end method for the service.
     */
    Mono<Void> stop();

    /**
     * @return The plugin which was defined in the constructor.
     * This should be an instance of your main plugin class.
     */
    JavaPlugin getPlugin();

    /**
     * @return The {@link ServicePool} which this service is executing on.
     */
    Mono<ServicePool> getParentPool();

    @Override
    default void run() {
        start().subscribeOn(getParentPool()
                    .map(ServicePool::getScheduler)
                    .blockOptional()
                    .orElseThrow(InvalidServicePoolException.supplyException()))
                .subscribe();
    }

    /**
     * This is an easy static call for creating a new namespaced key for services and service pools.
     *
     * @param space The namespace of the service.
     * @param key The key name of the service.
     * @return A NamespacedKey object representing the service.
     */
    @Contract("_, _ -> new")
    static @NotNull NamespacedKey newNamespacedKey(String space, String key) {
        return new NamespacedKey(space, key);
    }
}
