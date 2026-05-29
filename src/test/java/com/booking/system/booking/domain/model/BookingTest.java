package com.booking.system.booking.domain.model;

import com.booking.system.booking.domain.event.BookingCancelled;
import com.booking.system.booking.domain.event.BookingCreated;
import com.booking.system.booking.domain.event.DomainEvent;
import com.booking.system.booking.domain.exception.BookingCancellationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private final BookingId bookingId = BookingId.generate();
    private final ResourceId resourceId = ResourceId.generate();
    private final UUID userId = UUID.randomUUID();
    private final LocalDateTime baseTime = LocalDateTime.of(2026, 6, 1, 12, 0);
    private final TimeRange timeRange = new TimeRange(baseTime, baseTime.plusHours(1));

    @Test
    @DisplayName("Should successfully instantiate booking and register BookingCreated event")
    void createBookingSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.create(bookingId, resourceId, userId, timeRange, now);

        assertEquals(bookingId, booking.getId());
        assertEquals(resourceId, booking.getResourceId());
        assertEquals(userId, booking.getUserId());
        assertEquals(timeRange, booking.getTimeRange());
        assertEquals(BookingStatus.CREATED, booking.getStatus());
        assertEquals(now, booking.getCreatedAt());
        assertNull(booking.getCancelledAt());

        List<DomainEvent> events = booking.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.getFirst() instanceof BookingCreated);
        
        BookingCreated createdEvent = (BookingCreated) events.getFirst();
        assertEquals(bookingId, createdEvent.bookingId());
        assertEquals(resourceId, createdEvent.resourceId());
        assertEquals(userId, createdEvent.userId());
        assertEquals(timeRange, createdEvent.timeRange());
        assertEquals(now, createdEvent.timestamp());

        // Event clearing check
        booking.clearDomainEvents();
        assertEquals(0, booking.getDomainEvents().size());
    }

    @Test
    @DisplayName("Should allow CLIENT to cancel booking when cancelled more than 24 hours prior to start time")
    void clientCancelBookingSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.create(bookingId, resourceId, userId, timeRange, now);
        booking.clearDomainEvents();

        // 25 hours before start time of booking (2026-06-01T12:00:00)
        LocalDateTime clientCancellationTime = baseTime.minusHours(25);
        
        booking.cancel(false, clientCancellationTime);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(clientCancellationTime, booking.getCancelledAt());
        
        List<DomainEvent> events = booking.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.getFirst() instanceof BookingCancelled);
        
        BookingCancelled cancelledEvent = (BookingCancelled) events.getFirst();
        assertEquals(bookingId, cancelledEvent.bookingId());
        assertEquals(resourceId, cancelledEvent.resourceId());
        assertEquals(clientCancellationTime, cancelledEvent.timestamp());
    }

    @Test
    @DisplayName("Should throw BookingCancellationException when CLIENT attempts to cancel booking less than 24 hours prior to start time")
    void clientCancelBookingLateFailure() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.create(bookingId, resourceId, userId, timeRange, now);

        // 23 hours before start time
        LocalDateTime clientCancellationTime = baseTime.minusHours(23);

        BookingCancellationException exception = assertThrows(BookingCancellationException.class, () ->
                booking.cancel(false, clientCancellationTime)
        );

        assertTrue(exception.getMessage().contains("up to 24 hours before"));
        assertEquals(BookingStatus.CREATED, booking.getStatus()); // State preserved
    }

    @Test
    @DisplayName("Should allow ADMIN to cancel booking at any time, even less than 24 hours prior to start time")
    void adminCancelBookingSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.create(bookingId, resourceId, userId, timeRange, now);
        booking.clearDomainEvents();

        // 2 hours before start time
        LocalDateTime adminCancellationTime = baseTime.minusHours(2);
        
        booking.cancel(true, adminCancellationTime);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(adminCancellationTime, booking.getCancelledAt());
        assertEquals(1, booking.getDomainEvents().size());
    }

    @Test
    @DisplayName("Should throw BookingCancellationException when trying to cancel an already cancelled booking")
    void cancelAlreadyCancelledBookingFailure() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.create(bookingId, resourceId, userId, timeRange, now);
        
        booking.cancel(true, baseTime.minusHours(10));
        
        assertThrows(BookingCancellationException.class, () ->
                booking.cancel(true, baseTime.minusHours(5))
        );
    }
}
