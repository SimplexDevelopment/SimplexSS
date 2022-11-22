package io.github.simplex.api;

import io.github.simplex.simplexss.ServicePool;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ExecutableService implements IService {
    private final NamespacedKey service_name;
    private final Plugin plugin;
    private final long delay;
    private final long period;
    private final boolean repeating;

    private boolean cancelled = false;

    /**
     * Creates a new instance of an executable service.
     * The timings are measured in ticks (20 ticks per second).
     * You do not need to explicitly define a delay or a period,
     * however if you have flagged {@link #repeating} as true, and the period is null,
     * then the period will automatically be set to 20 minutes.
     * Each service is registered with a {@link NamespacedKey},
     * to allow for easy identification within the associated {@link ServicePool}.
     * Each service also has a plugin parameter to allow for easy dependency injection.
     *
     * @param plugin       Your plugin
     * @param service_name A namespaced key which can be used to identify the service.
     * @param delay        A specified amount of time (in ticks) to wait before the service runs.
     * @param period       How long the service should wait between service executions (in ticks).
     * @param repeating    If the service should be scheduled for repeated executions or not.
     */
    public ExecutableService(@NotNull Plugin plugin, @NotNull NamespacedKey service_name, @Nullable Long delay, @Nullable Long period, @NotNull Boolean repeating) {
        this.plugin = plugin;
        this.service_name = service_name;
        this.repeating = repeating;
        this.delay = Objects.requireNonNullElse(delay, 0L);
        this.period = Objects.requireNonNullElse(period, (20L * 60L) * 20L);
    }

    /**
     * @return The NamespacedKey of this service.
     */
    @Override
    public NamespacedKey getNamespacedKey() {
        return service_name;
    }

    /**
     * @return The plugin which was defined in the constructor.
     * This should be an instance of your main plugin class.
     */
    @Override
    public Plugin getProvidingPlugin() {
        return plugin;
    }

    /**
     * @return
     */
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

    @Override
    public void setCancelled(boolean mayInterruptIfRunning) {
        if (!mayInterruptIfRunning) {
            cancelled = false;
        }

        stop();
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
