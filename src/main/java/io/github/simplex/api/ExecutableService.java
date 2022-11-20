package io.github.simplex.api;

import org.bukkit.plugin.Plugin;

public abstract class ExecutableService implements IService {
    private final int serviceID;
    private final Plugin plugin;
    private final long delay;
    private final long period;
    private final boolean repeating;

    public ExecutableService(Plugin plugin, int serviceID, long delay, long period, boolean repeating) {
        this.plugin = plugin;
        this.serviceID = serviceID;
        this.repeating = repeating;
        this.delay = delay;
        this.period = period;
    }

    @Override
    public int getServiceID() {
        return serviceID;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return plugin;
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
    public boolean isRepeating() {
        return repeating;
    }
}
