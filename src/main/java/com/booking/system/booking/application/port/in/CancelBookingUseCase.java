package com.booking.system.booking.application.port.in;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface CancelBookingUseCase {
    void execute(CancelBookingCommand command);

    record CancelBookingCommand(
            @NotNull(message = "Booking ID is required") UUID bookingId,
            @NotNull(message = "User ID is required") UUID userId,
            boolean isAdmin
    ) {}
}
