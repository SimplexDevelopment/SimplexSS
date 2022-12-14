package io.github.simplexdevelopment.impl;

import io.github.simplexdevelopment.api.IService;
import io.github.simplexdevelopment.scheduler.SchedulingSystem;
import io.github.simplexdevelopment.scheduler.ServiceManager;
import io.github.simplexdevelopment.scheduler.ServicePool;
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
        this.scheduler = new SchedulingSystem<>(this);
        IService service = new ServiceImpl(this);
        service.getParentPool().subscribe(element -> disposables = element.startServices());
    }

    @Override
    public void onDisable() {
        scheduler.getServiceManager().subscribe(manager -> {
            manager.getServicePools().doOnEach(signal -> Objects.requireNonNull(signal.get())
                    .stopServices(disposables)
                    .subscribe());
        });
    }

    public SchedulingSystem<Main> getScheduler() {
        return this.scheduler;
    }
}
