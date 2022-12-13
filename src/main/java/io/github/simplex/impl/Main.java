package io.github.simplex.impl;

import io.github.simplex.api.IService;
import io.github.simplex.simplexss.SchedulingSystem;
import io.github.simplex.simplexss.ServiceManager;
import io.github.simplex.simplexss.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class Main extends JavaPlugin {
    public ServicePool pool = new ServicePool(IService.newNamespacedKey("pool", "one"), true);
    private SchedulingSystem<Main> scheduler;
    private Flux<Disposable> disposables;

    @Override
    public void onEnable() {
        ServiceManager serviceManager = new ServiceManager();
        this.scheduler = new SchedulingSystem<>(serviceManager, this);
        IService service = new ServiceImpl(this);
        service.getParentPool().subscribe(element -> disposables = element.startServices());
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
