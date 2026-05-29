package com.booking.system.booking.domain.model;

import com.booking.system.booking.domain.exception.InvalidTimeRangeException;
import java.time.LocalDateTime;
import java.util.Objects;

public record TimeRange(LocalDateTime start, LocalDateTime end) {
    public TimeRange {
        Objects.requireNonNull(start, "Start time cannot be null");
        Objects.requireNonNull(end, "End time cannot be null");
        
        if (!start.isBefore(end)) {
            throw new InvalidTimeRangeException("Start time must be strictly before end time. Provided: start=" + start + ", end=" + end);
        }
    }

    public boolean overlapsWith(TimeRange other) {
        Objects.requireNonNull(other, "Other TimeRange cannot be null");
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }
}
