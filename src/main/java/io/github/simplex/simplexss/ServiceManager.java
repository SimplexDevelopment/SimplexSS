package io.github.simplex.simplexss;

import io.github.simplex.api.Service;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ServiceManager {
    private final Set<ServicePool> servicePools;
    private final Plugin plugin;
    
    public ServiceManager(Plugin plugin) {
        this.plugin = plugin;
        servicePools = new HashSet<>();
    }
    
    @Contract(pure = true, value = "_ -> new")
    public @NotNull ServicePool createServicePool(Service... services) {
        ServicePool pool = new ServicePool();
        Stream.of(services).forEach(pool::addService);
        return pool;
    }
    
    @Contract("_, _ -> param1")
    public ServicePool addToExistingPool(@NotNull ServicePool pool, Service... services) {
        Stream.of(services).forEach(pool::addService);
        return pool;
    }
    
    @Contract("_, _ -> param1")
    public ServicePool takeFromExistingPool(@NotNull ServicePool pool, Service... services) {
        Stream.of(services).forEach(pool::removeService);
        return pool;
    }
    
    public Set<ServicePool> getServicePools() {
        return servicePools;
    }

    public boolean locateServiceWithinPools(Service service) {
        return servicePools.stream().map(p -> p.isValidService(service)).findFirst().orElseGet(() -> false);
    }

    public @Nullable ServicePool getAssociatedServicePool(Service service) {
        if (!locateServiceWithinPools(service)) return null;

        return servicePools
                .stream()
                .filter(p -> p.getAssociatedServices().contains(service))
                .findFirst()
                .orElseGet(() -> null);
    }
    
    public Plugin getProvidingPlugin() {
        return plugin;
    }
}
