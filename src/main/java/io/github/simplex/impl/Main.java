package io.github.simplex.impl;

import io.github.simplex.simplexss.SchedulingSystem;
import io.github.simplex.simplexss.ServiceManager;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class Main extends JavaPlugin {
    private SchedulingSystem<Main> scheduler;
    private Flux<Disposable> disposables;

    @Override
    public void onEnable() {
        ServiceManager serviceManager = new ServiceManager();
        this.scheduler = new SchedulingSystem<>(serviceManager, this);
        scheduler.getServiceManager().subscribe(manager -> manager.getServicePools()
                .doOnEach(signal -> disposables = Objects.requireNonNull(signal.get()).startServices())
                .subscribeOn(scheduler.getMainSchedulerThread(), false)
                .subscribe());
    }

    @Override
    public void onDisable() {
        scheduler.getServiceManager().subscribe(manager -> {
            manager.getServicePools().doOnEach(signal -> Objects.requireNonNull(signal.get())
                    .stopServices(disposables)
                    .subscribeOn(scheduler.getMainSchedulerThread())
                    .subscribe());
        });
    }

    public SchedulingSystem<Main> getScheduler() {
        return this.scheduler;
    }
}
