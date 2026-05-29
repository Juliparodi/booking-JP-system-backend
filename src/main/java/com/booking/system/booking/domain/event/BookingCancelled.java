package com.booking.system.booking.domain.event;

import com.booking.system.booking.domain.model.BookingId;
import com.booking.system.booking.domain.model.ResourceId;
import java.time.LocalDateTime;

public record BookingCancelled(
        BookingId bookingId,
        ResourceId resourceId,
        LocalDateTime timestamp
) implements DomainEvent {
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
