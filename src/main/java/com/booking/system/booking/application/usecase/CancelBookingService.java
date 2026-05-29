package com.booking.system.booking.application.usecase;

import com.booking.system.booking.application.port.in.CancelBookingUseCase;
import com.booking.system.booking.application.port.out.BookingRepositoryPort;
import com.booking.system.booking.application.port.out.DomainEventPublisherPort;
import com.booking.system.booking.domain.exception.BookingCancellationException;
import com.booking.system.booking.domain.exception.BookingNotFoundException;
import com.booking.system.booking.domain.model.Booking;
import com.booking.system.booking.domain.model.BookingId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelBookingService implements CancelBookingUseCase {

    private final BookingRepositoryPort bookingRepositoryPort;
    private final DomainEventPublisherPort domainEventPublisherPort;

    public CancelBookingService(
            BookingRepositoryPort bookingRepositoryPort,
            DomainEventPublisherPort domainEventPublisherPort) {
        this.bookingRepositoryPort = bookingRepositoryPort;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    @Transactional
    public void execute(CancelBookingCommand command) {
        BookingId bookingId = new BookingId(command.bookingId());

        // 1. Load the Booking aggregate
        Booking booking = bookingRepositoryPort.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        // 2. Validate client ownership (unless request is from an ADMIN)
        if (!command.isAdmin() && !booking.getUserId().equals(command.userId())) {
            throw new BookingCancellationException(
                    "Unauthorized: Client with ID " + command.userId() + " is not authorized to cancel booking " + bookingId
            );
        }

        // 3. Apply domain rules and transition state
        booking.cancel(command.isAdmin(), LocalDateTime.now());

        // 4. Save and publish events
        Booking savedBooking = bookingRepositoryPort.save(booking);
        domainEventPublisherPort.publishAll(savedBooking.getDomainEvents());
        savedBooking.clearDomainEvents();
    }
}
