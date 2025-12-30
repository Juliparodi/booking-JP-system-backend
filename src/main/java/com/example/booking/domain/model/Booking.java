package com.example.booking.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings", uniqueConstraints = @UniqueConstraint(columnNames = {"resource_id", "start_time", "end_time"}))
public class Booking {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "status", nullable = false)
    private String status;

    protected Booking() {}

    public Booking(String resourceId, String userId, OffsetDateTime startTime, OffsetDateTime endTime) {
        this.id = UUID.randomUUID().toString();
        this.resourceId = resourceId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "CREATED";
    }

    // getters and setters
    public String getId() { return id; }
    public String getResourceId() { return resourceId; }
    public String getUserId() { return userId; }
    public OffsetDateTime getStartTime() { return startTime; }
    public OffsetDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
