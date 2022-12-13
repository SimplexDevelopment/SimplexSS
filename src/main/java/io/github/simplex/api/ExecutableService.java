package io.github.simplex.api;

import io.github.simplex.simplexss.ServicePool;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.Objects;

public abstract class ExecutableService implements IService {
    private final NamespacedKey service_name;
    private final long delay;
    private final long period;
    private final boolean repeating;
    private final boolean mayInterruptWhenRunning;

    private boolean cancelled = false;
    private ServicePool parentPool;

    /**
     * Creates a new instance of an executable service.
     * Each service is registered with a {@link NamespacedKey},
     * to allow for easy identification within the associated {@link ServicePool}.
     *
     * @param service_name A namespaced key which can be used to identify the service.
     */
    public ExecutableService(@NotNull NamespacedKey service_name) {
        this((new ServicePool(IService.newNamespacedKey("", ""), false)), service_name, 0L, 0L, false, false);
    }

    /**
     * Creates a new instance of an executable service.
     * Each service is registered with a {@link NamespacedKey},
     * to allow for easy identification within the associated {@link ServicePool}.
     *
     * @param parentPool   The {@link ServicePool} which this service is executing on.
     * @param service_name A namespaced key which can be used to identify the service.
     */
    public ExecutableService(@Nullable ServicePool parentPool, @NotNull NamespacedKey service_name) {
        this(parentPool, service_name, 0L, 0L, false, false);
    }

    /**
     * Creates a new instance of an executable service.
     * The timings are measured in ticks (20 ticks per second).
     * You do not need to explicitly define a delay.
     * Each service is registered with a {@link NamespacedKey},
     * to allow for easy identification within the associated {@link ServicePool}.
     *
     * @param parentPool   The {@link ServicePool} which this service is executing on.
     * @param service_name A namespaced key which can be used to identify the service.
     * @param delay        A specified amount of time (in ticks) to wait before the service runs.
     */
    public ExecutableService(
            @Nullable ServicePool parentPool,
            @NotNull NamespacedKey service_name,
            @Nullable Long delay) {
        this(parentPool, service_name, delay, 0L, false, false);
    }

    /**
     * Creates a new instance of an executable service.
     * The timings are measured in ticks (20 ticks per second).
     * You do not need to explicitly define a delay or a period,
     * however if you have flagged {@link #repeating} as true, and the period is null,
     * then the period will automatically be set to 20 minutes.
     * Each service is registered with a {@link NamespacedKey},
     * to allow for easy identification within the associated {@link ServicePool}.
     *
     * @param parentPool   The {@link ServicePool} which this service is executing on.
     * @param service_name A namespaced key which can be used to identify the service.
     * @param delay        A specified amount of time (in ticks) to wait before the service runs.
     * @param period       How long the service should wait between service executions (in ticks).
     * @param repeating    If the service should be scheduled for repeated executions or not.
     */
    public ExecutableService(
            @Nullable ServicePool parentPool,
            @NotNull NamespacedKey service_name,
            @NotNull Long delay,
            @NotNull Long period,
            @NotNull Boolean repeating) {
        this(parentPool, service_name, delay, period, repeating, false);
    }

    /**
     * Creates a new instance of an executable service.
     * The timings are measured in ticks (20 ticks per second).
     * You do not need to explicitly define a delay or a period,
     * however if you have flagged {@link #repeating} as true, and the period is null,
     * then the period will automatically be set to 20 minutes.
     * Each service is registered with a {@link NamespacedKey},
     * to allow for easy identification within the associated {@link ServicePool}.
     *
     * @param parentPool              The {@link ServicePool} which this service is executing on.
     * @param service_name            A namespaced key which can be used to identify the service.
     * @param delay                   A specified amount of time (in ticks) to wait before the service runs.
     * @param period                  How long the service should wait between service executions (in ticks).
     * @param repeating               If the service should be scheduled for repeated executions or not.
     * @param mayInterruptWhenRunning If the service can be cancelled during execution.
     */
    public ExecutableService(
            @Nullable ServicePool parentPool,
            @NotNull NamespacedKey service_name,
            @Nullable Long delay,
            @Nullable Long period,
            @NotNull Boolean repeating,
            @NotNull Boolean mayInterruptWhenRunning) {
        this.service_name = service_name;
        this.repeating = repeating;
        this.delay = Objects.requireNonNullElse(delay, 0L);
        this.period = Objects.requireNonNullElse(period, (20L * 60L) * 20L);
        this.mayInterruptWhenRunning = mayInterruptWhenRunning;
        this.parentPool = parentPool;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return service_name;
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public boolean isPeriodic() {
        return repeating;
    }

    /**
     * Cancels the execution of this service.
     *
     * @return true if the service was cancelled, false if not.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Cancels the execution of this service.
     *
     * @param cancel Whether the service should be cancelled or not.
     */
    public Mono<Void> setCancelled(boolean cancel) {
        if (!mayInterruptWhenRunning) {
            return Mono.empty();
        }

        cancelled = cancel;
        return cancel();
    }

    /**
     * Actual stop call, to ensure that the service actually #isCancelled().
     */
    @Contract(pure = true)
    Mono<Void> cancel() {
        if (isCancelled()) {
            return stop().then();
        }
        return Mono.empty();
    }

    @Override
    public Mono<ServicePool> getParentPool() {
        return Mono.just(parentPool);
    }
}
