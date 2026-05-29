package com.booking.system.booking.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ResourceId(UUID value) {
    public ResourceId {
        Objects.requireNonNull(value, "Resource ID value cannot be null");
    }

    public static ResourceId generate() {
        return new ResourceId(UUID.randomUUID());
    }

    public static ResourceId fromString(String uuid) {
        return new ResourceId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
