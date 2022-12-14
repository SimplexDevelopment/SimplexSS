package io.github.simplexdevelopment.impl;

import io.github.simplexdevelopment.api.Context;
import io.github.simplexdevelopment.scheduler.ServicePool;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

public class PoolHolder implements Context<ServicePool> {
    private final Collection<ServicePool> servicePoolCollection = new ArrayList<>();
    private ServicePool selectedPool;

    public PoolHolder(Main plugin) {
        this.servicePoolCollection.add(new ServicePool("Pool-One", plugin));
        this.servicePoolCollection.add(new ServicePool("Pool-Two", true));
        this.servicePoolCollection.add(new ServicePool("Pool-Three", false));
        this.selectedPool = servicePoolCollection.stream().findFirst().orElseGet(() -> new ServicePool("Default", plugin));
    }

    @Override
    public @NotNull Mono<ServicePool> getContext() {
        return Mono.just(selectedPool);
    }

    @Override
    public @NotNull Mono<Void> setContext(ServicePool context) {
        return Mono.just(context).doOnNext(pool -> this.selectedPool = pool).then();
    }

    @Override
    public Collection<ServicePool> contextCollection() {
        return servicePoolCollection;
    }
}
