package io.github.simplex.simplexss;

import io.github.simplex.api.Service;

import java.util.HashSet;
import java.util.Set;

public final class ServicePool {
    private final Set<Service> associatedServices;
    private boolean delayed = false;
    private boolean repeating = false;

    public ServicePool() {
        this.associatedServices = new HashSet<>();
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isPoolDelayed() {
        return delayed;
    }

    public boolean isPoolRepeating() {
        return repeating;
    }

    public void addService(Service service) {
        getAssociatedServices().add(service);
    }

    public boolean isValidService(Service service) {
        return getAssociatedServices().contains(service);
    }

    public Set<Service> getAssociatedServices() {
        return associatedServices;
    }

    public Service getService(int serviceID) {
        return getAssociatedServices()
                .stream()
                .filter(s -> s.getServiceID() == serviceID)
                .findFirst()
                .orElse(null);
    }

    public void removeService(Service service) {
        getAssociatedServices().remove(service);
    }
}
