package com.booking.system.booking.domain.event;

import com.booking.system.booking.domain.model.BookingId;
import com.booking.system.booking.domain.model.ResourceId;
import com.booking.system.booking.domain.model.TimeRange;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCreated(
        BookingId bookingId,
        ResourceId resourceId,
        UUID userId,
        TimeRange timeRange,
        LocalDateTime timestamp
) implements DomainEvent {
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
