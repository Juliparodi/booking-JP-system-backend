package com.booking.system.booking.application.port.out;

import com.booking.system.booking.domain.event.DomainEvent;
import java.util.List;

public interface DomainEventPublisherPort {
    void publish(DomainEvent event);

    default void publishAll(List<DomainEvent> events) {
        if (events != null) {
            events.forEach(this::publish);
        }
    }
}
