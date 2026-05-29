package com.booking.system.booking.application.port.in;

import com.booking.system.booking.domain.model.BookingId;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateBookingUseCase {
    BookingId execute(CreateBookingCommand command);

    record CreateBookingCommand(
            @NotNull(message = "Resource ID is required") UUID resourceId,
            @NotNull(message = "User ID is required") UUID userId,
            @NotNull(message = "Start time is required") LocalDateTime start,
            @NotNull(message = "End time is required") LocalDateTime end
    ) {}
}
