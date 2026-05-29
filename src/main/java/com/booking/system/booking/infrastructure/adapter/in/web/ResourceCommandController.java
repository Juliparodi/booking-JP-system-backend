package com.booking.system.booking.infrastructure.adapter.in.web;

import com.booking.system.booking.application.port.in.RegisterResourceUseCase;
import com.booking.system.booking.application.port.in.RegisterResourceUseCase.RegisterResourceCommand;
import com.booking.system.booking.domain.model.ResourceId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceCommandController {

    private final RegisterResourceUseCase registerResourceUseCase;

    public ResourceCommandController(RegisterResourceUseCase registerResourceUseCase) {
        this.registerResourceUseCase = registerResourceUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> registerResource(@Valid @RequestBody RegisterResourceRequest request) {
        RegisterResourceCommand command = new RegisterResourceCommand(request.name(), request.type());
        ResourceId resourceId = registerResourceUseCase.execute(command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resourceId.value())
                .toUri();

        return ResponseEntity.created(location)
                .body(Map.of("resourceId", resourceId.value().toString()));
    }

    public record RegisterResourceRequest(
            @NotBlank(message = "Resource name cannot be blank") String name,
            @NotBlank(message = "Resource type cannot be blank") String type
    ) {}
}
