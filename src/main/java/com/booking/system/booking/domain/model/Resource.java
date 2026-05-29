package com.booking.system.booking.domain.model;

import java.util.Objects;

public class Resource {
    private final ResourceId id;
    private String name;
    private String type;
    private boolean active;

    public Resource(ResourceId id, String name, String type, boolean active) {
        this.id = Objects.requireNonNull(id, "Resource ID cannot be null");
        this.name = Objects.requireNonNull(name, "Resource name cannot be null");
        this.type = Objects.requireNonNull(type, "Resource type cannot be null");
        this.active = active;
    }

    public ResourceId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void updateDetails(String name, String type) {
        this.name = Objects.requireNonNull(name, "Resource name cannot be null");
        this.type = Objects.requireNonNull(type, "Resource type cannot be null");
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
