package io.github.simplexdevelopment.impl;

import io.github.simplexdevelopment.scheduler.SchedulingSystem;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Main extends JavaPlugin {
    private SchedulingSystem<Main> scheduler;
    private Flux<Disposable> disposables;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Create a new instance of the scheduling system.
        this.scheduler = new SchedulingSystem<>(this);
        // This will register all the services and set our Flux<Disposable> object above.
        registerServices(new PoolHolder(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // This will dispose of all the services we have previously registered in our Flux<Disposable> object.
        scheduler.getServiceManager().subscribe(manager -> {
            manager.getServicePools().doOnEach(signal -> Objects.requireNonNull(signal.get())
                    .stopServices(disposables)
                    .subscribe());
        });
    }

    public void registerServices(@NotNull PoolHolder poolHolder) {
        // This set will be used to set the Flux<Disposable> object
        // that will be used to stop the services when the plugin is disabled.
        Set<Disposable> dispos = new HashSet<>();

        // Register services here
        ServiceImpl impl = new ServiceImpl(this, poolHolder.getContext().block());

        // This will register the service to the service pool.
        dispos.add(scheduler.getMainScheduler().schedule(impl));
        // OR
        scheduler.queue(impl).subscribe(dispos::add);
        // OR
        scheduler.getServiceManager()
                .flatMap(manager -> manager.createServicePool("newPool", impl))
                .subscribe(p -> p.queueService(impl).subscribe(dispos::add));

        // This will set the Flux<Disposable> object on our previously made set, so that we can use it later on.
        disposables = Flux.fromIterable(dispos);
    }

    public SchedulingSystem<Main> getScheduler() {
        return this.scheduler;
    }
}
