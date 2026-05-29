package com.booking.system.booking.domain.model;

import com.booking.system.booking.domain.event.BookingCancelled;
import com.booking.system.booking.domain.event.BookingCreated;
import com.booking.system.booking.domain.event.DomainEvent;
import com.booking.system.booking.domain.exception.BookingCancellationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Booking {
    private final BookingId id;
    private final ResourceId resourceId;
    private final UUID userId;
    private final TimeRange timeRange;
    private BookingStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    // Track domain events in-memory to be dispatched by repository or application layer
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Booking(BookingId id, ResourceId resourceId, UUID userId, TimeRange timeRange, BookingStatus status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = Objects.requireNonNull(id, "Booking ID cannot be null");
        this.resourceId = Objects.requireNonNull(resourceId, "Resource ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.timeRange = Objects.requireNonNull(timeRange, "Time range cannot be null");
        this.status = Objects.requireNonNull(status, "Booking status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation timestamp cannot be null");
        this.cancelledAt = cancelledAt;
    }

    /**
     * Factory method to create a new Booking aggregate root and register a BookingCreated event.
     */
    public static Booking create(BookingId id, ResourceId resourceId, UUID userId, TimeRange timeRange, LocalDateTime now) {
        Booking booking = new Booking(
                id,
                resourceId,
                userId,
                timeRange,
                BookingStatus.CREATED,
                now,
                null
        );
        booking.registerEvent(new BookingCreated(id, resourceId, userId, timeRange, now));
        return booking;
    }

    /**
     * Enforces the business rules for cancelling a booking.
     * Rule: Must not be already CANCELLED, and must be cancelled at least 24 hours before start time,
     * unless performed by an ADMIN.
     */
    public void cancel(boolean isAdmin, LocalDateTime now) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new BookingCancellationException("Booking with ID " + id + " is already cancelled.");
        }

        if (!isAdmin) {
            LocalDateTime cancellationDeadline = this.timeRange.start().minusHours(24);
            if (now.isAfter(cancellationDeadline)) {
                throw new BookingCancellationException(
                        "Clients can only cancel bookings up to 24 hours before the scheduled start time. " +
                        "Booking start: " + this.timeRange.start() + ", Current time: " + now
                );
            }
        }

        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = now;
        registerEvent(new BookingCancelled(this.id, this.resourceId, now));
    }

    public BookingId getId() {
        return id;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}
