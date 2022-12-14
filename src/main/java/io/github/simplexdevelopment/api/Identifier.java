package io.github.simplexdevelopment.api;

import java.util.UUID;

public interface Identifier {
    /**
     * @return The name of the identifiable object in a readable format.
     */
    String getName();

    /**
     * @return The UUID of the identifiable object, based on the {@link Identifier#getName()} value.
     * This is calculated using {@link UUID#nameUUIDFromBytes(byte[])}.
     */
    default UUID getUniqueId() {
        return UUID.nameUUIDFromBytes(getName().getBytes());
    }

    /**
     * @return The numerical id of the identifiable object, based on the {@link Identifier#getUniqueId()} value.
     * This is calculated using {@link UUID#hashCode()}.
     */
    default int getNumericalId() {
        return getUniqueId().hashCode();
    }
}
