/*
 * MIT License
 *
 * Copyright (c) 2022 SimplexDevelopment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.simplexdevelopment.impl;

import io.github.simplexdevelopment.scheduler.SchedulingSystem;
import io.github.simplexdevelopment.scheduler.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
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

        // Creates new service pools.
        getScheduler().getServiceManager().subscribe(a -> {
            a.emptyBukkitServicePool("main_pool", this).subscribe();
            a.emptyServicePool("off_loader", true).subscribe();
        });

        // This will register all the services and set our Flux<Disposable> object above.
        registerServices("main_pool");
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

    public void registerServices(String poolName) {
        // This set will be used to set the Flux<Disposable> object
        // that will be used to stop the services when the plugin is disabled.
        Set<Disposable> dispos = new HashSet<>();

        // Find the service pool we want to register our services to.
        ServicePool pool = scheduler.getServiceManager().map(a -> a.getServicePools()
                        .filter(b -> b.getName().equalsIgnoreCase(poolName))
                        .blockFirst())
                .block();

        // Make sure the pool isn't null.
        if (pool == null) pool = new ServicePool(poolName, this);

        // Register services here
        ServiceImpl impl = new ServiceImpl(this, pool);

        // This will register the service to the service pool.
        dispos.add(scheduler.getMainScheduler().schedule(impl));
        // OR
        scheduler.queue(impl).subscribe(dispos::add);
        // OR
        scheduler.getServiceManager()
                .flatMap(manager -> manager.emptyBukkitServicePool("backup", this))
                .doOnNext(pool_a -> pool_a.getAssociatedServices().add(impl))
                .subscribe(pool_b -> pool_b.queueService(impl).subscribe(dispos::add));

        // This will set the Flux<Disposable> object on our previously made set, so that we can use it later on.
        disposables = Flux.fromIterable(dispos);
    }

    public SchedulingSystem<Main> getScheduler() {
        return this.scheduler;
    }
}
