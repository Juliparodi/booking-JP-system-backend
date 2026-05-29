package com.booking.system.booking.infrastructure.adapter.out.persistence;

import com.booking.system.booking.domain.model.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class BookingJpaEntity {

    @Id
    private UUID id;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    public BookingJpaEntity() {
    }

    public BookingJpaEntity(UUID id, UUID resourceId, UUID userId, LocalDateTime startTime, LocalDateTime endTime, String status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.resourceId = resourceId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public static BookingJpaEntity fromDomain(Booking booking) {
        return new BookingJpaEntity(
                booking.getId().value(),
                booking.getResourceId().value(),
                booking.getUserId(),
                booking.getTimeRange().start(),
                booking.getTimeRange().end(),
                booking.getStatus().name(),
                booking.getCreatedAt(),
                booking.getCancelledAt()
        );
    }

    public Booking toDomain() {
        return new Booking(
                new BookingId(id),
                new ResourceId(resourceId),
                userId,
                new TimeRange(startTime, endTime),
                BookingStatus.valueOf(status),
                createdAt,
                cancelledAt
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
