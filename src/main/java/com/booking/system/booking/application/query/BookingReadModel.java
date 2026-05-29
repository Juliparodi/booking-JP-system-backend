package com.booking.system.booking.application.query;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingReadModel(
        UUID bookingId,
        UUID resourceId,
        String resourceName,
        UUID userId,
        LocalDateTime start,
        LocalDateTime end,
        String status,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt
) {}
