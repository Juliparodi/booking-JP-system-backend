package com.booking.system.booking.infrastructure.adapter.out.event;

import com.booking.system.booking.application.port.out.DomainEventPublisherPort;
import com.booking.system.booking.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisherAdapter implements DomainEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(SpringEventPublisherAdapter.class);
    private final ApplicationEventPublisher eventPublisher;

    public SpringEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing domain event: {} created at {}", event.getClass().getSimpleName(), event.getTimestamp());
        eventPublisher.publishEvent(event);
    }
}
