package com.booking.system.booking.application.port.out;

import com.booking.system.booking.domain.model.Resource;
import com.booking.system.booking.domain.model.ResourceId;
import java.util.Optional;

public interface ResourceRepositoryPort {
    Optional<Resource> findById(ResourceId id);
    Resource save(Resource resource);
    boolean existsById(ResourceId id);
}
