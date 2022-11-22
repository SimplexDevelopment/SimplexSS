package io.github.simplex.impl;

import io.github.simplex.simplexss.SchedulingSystem;
import io.github.simplex.simplexss.ServiceManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private SchedulingSystem scheduler;
    private ServiceManager serviceManager;

    @Override
    public void onEnable() {
        this.serviceManager = new ServiceManager(this);
        this.scheduler = new SchedulingSystem(serviceManager, this);
    }

    @Override
    public void onDisable() {
    }
}
