package com.booking.system.booking.application.usecase;

import com.booking.system.booking.application.port.in.CreateBookingUseCase;
import com.booking.system.booking.application.port.out.BookingRepositoryPort;
import com.booking.system.booking.application.port.out.DomainEventPublisherPort;
import com.booking.system.booking.application.port.out.ResourceRepositoryPort;
import com.booking.system.booking.domain.exception.BookingOverlappingException;
import com.booking.system.booking.domain.exception.ResourceNotFoundException;
import com.booking.system.booking.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreateBookingService implements CreateBookingUseCase {

    private final BookingRepositoryPort bookingRepositoryPort;
    private final ResourceRepositoryPort resourceRepositoryPort;
    private final DomainEventPublisherPort domainEventPublisherPort;

    public CreateBookingService(
            BookingRepositoryPort bookingRepositoryPort,
            ResourceRepositoryPort resourceRepositoryPort,
            DomainEventPublisherPort domainEventPublisherPort) {
        this.bookingRepositoryPort = bookingRepositoryPort;
        this.resourceRepositoryPort = resourceRepositoryPort;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    @Transactional
    public BookingId execute(CreateBookingCommand command) {
        ResourceId resourceId = new ResourceId(command.resourceId());
        
        // 1. Validate resource existence and activity
        Resource resource = resourceRepositoryPort.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + resourceId + " does not exist."));
        
        if (!resource.isActive()) {
            throw new ResourceNotFoundException("Resource with ID " + resourceId + " is currently inactive.");
        }

        // 2. Validate time slot overlap
        TimeRange timeRange = new TimeRange(command.start(), command.end());
        List<Booking> overlappingBookings = bookingRepositoryPort.findActiveOverlappingBookings(resourceId, timeRange);
        if (!overlappingBookings.isEmpty()) {
            throw new BookingOverlappingException(
                    "Resource " + resource.getName() + " is already booked for an overlapping time slot during " + timeRange
            );
        }

        // 3. Create the booking aggregate root
        BookingId bookingId = BookingId.generate();
        Booking booking = Booking.create(
                bookingId,
                resourceId,
                command.userId(),
                timeRange,
                LocalDateTime.now()
        );

        // 4. Save and publish events
        Booking savedBooking = bookingRepositoryPort.save(booking);
        domainEventPublisherPort.publishAll(savedBooking.getDomainEvents());
        savedBooking.clearDomainEvents();

        return savedBooking.getId();
    }
}
