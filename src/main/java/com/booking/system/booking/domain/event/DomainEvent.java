package com.booking.system.booking.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getTimestamp();
}
