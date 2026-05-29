package com.booking.system.booking.infrastructure.adapter.out.persistence;

import com.booking.system.booking.domain.model.Resource;
import com.booking.system.booking.domain.model.ResourceId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "resources")
public class ResourceJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private boolean active;

    public ResourceJpaEntity() {
    }

    public ResourceJpaEntity(UUID id, String name, String type, boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.active = active;
    }

    public static ResourceJpaEntity fromDomain(Resource resource) {
        return new ResourceJpaEntity(
                resource.getId().value(),
                resource.getName(),
                resource.getType(),
                resource.isActive()
        );
    }

    public Resource toDomain() {
        return new Resource(
                new ResourceId(id),
                name,
                type,
                active
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
