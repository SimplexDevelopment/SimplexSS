package io.github.simplex.api;

import org.bukkit.plugin.Plugin;

public abstract class ExecutableService implements Service {
    private final int serviceID;
    private final Plugin plugin;

    public ExecutableService(Plugin plugin, int serviceID) {
        this.serviceID = serviceID;
        this.plugin = plugin;
    }

    @Override
    public int getServiceID() {
        return serviceID;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return plugin;
    }
}
