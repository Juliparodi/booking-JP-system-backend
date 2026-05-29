package com.booking.system.booking.infrastructure.adapter.out.persistence;

import com.booking.system.booking.application.port.out.ResourceRepositoryPort;
import com.booking.system.booking.application.query.ResourceQueryService;
import com.booking.system.booking.application.query.ResourceReadModel;
import com.booking.system.booking.domain.model.Resource;
import com.booking.system.booking.domain.model.ResourceId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResourcePersistenceAdapter implements ResourceRepositoryPort, ResourceQueryService {

    private final SpringDataResourceRepository repository;

    public ResourcePersistenceAdapter(SpringDataResourceRepository repository) {
        this.repository = repository;
    }

    // --- Outbound Port Implementations (Command / Write path) ---

    @Override
    public Optional<Resource> findById(ResourceId id) {
        return repository.findById(id.value())
                .map(ResourceJpaEntity::toDomain);
    }

    @Override
    public Resource save(Resource resource) {
        ResourceJpaEntity entity = ResourceJpaEntity.fromDomain(resource);
        ResourceJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public boolean existsById(ResourceId id) {
        return repository.existsById(id.value());
    }

    // --- Query Service Implementations (Read / CQRS path) ---

    @Override
    public List<ResourceReadModel> execute(GetAvailableResourcesQuery query) {
        return repository.findAvailableResources(query.start(), query.end());
    }
}
