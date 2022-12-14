package io.github.simplexdevelopment.api;

import io.github.simplexdevelopment.scheduler.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface IService extends Runnable, Identifier {

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

    /**
     * Sets the parent pool for this service.
     *
     * @param servicePool The service pool to attach this service to.
     * @return An encapsulated Mono object representing the set operation.
     */
    Mono<Void> setParentPool(ServicePool servicePool);

    @Override
    default void run() {
        start().subscribe();
    }
}
