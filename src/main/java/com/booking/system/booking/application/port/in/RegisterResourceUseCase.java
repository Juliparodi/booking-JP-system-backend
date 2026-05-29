package com.booking.system.booking.application.port.in;

import com.booking.system.booking.domain.model.ResourceId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface RegisterResourceUseCase {
    ResourceId execute(RegisterResourceCommand command);

    record RegisterResourceCommand(
            @NotBlank(message = "Resource name is required") String name,
            @NotBlank(message = "Resource type is required") String type
    ) {}
}
