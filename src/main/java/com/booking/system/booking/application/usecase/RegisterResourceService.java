package com.booking.system.booking.application.usecase;

import com.booking.system.booking.application.port.in.RegisterResourceUseCase;
import com.booking.system.booking.application.port.out.ResourceRepositoryPort;
import com.booking.system.booking.domain.model.Resource;
import com.booking.system.booking.domain.model.ResourceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterResourceService implements RegisterResourceUseCase {

    private final ResourceRepositoryPort resourceRepositoryPort;

    public RegisterResourceService(ResourceRepositoryPort resourceRepositoryPort) {
        this.resourceRepositoryPort = resourceRepositoryPort;
    }

    @Override
    @Transactional
    public ResourceId execute(RegisterResourceCommand command) {
        ResourceId resourceId = ResourceId.generate();
        Resource resource = new Resource(
                resourceId,
                command.name(),
                command.type(),
                true // Registered active by default
        );

        Resource savedResource = resourceRepositoryPort.save(resource);
        return savedResource.getId();
    }
}
