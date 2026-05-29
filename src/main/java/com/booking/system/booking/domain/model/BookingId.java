package com.booking.system.booking.domain.model;

import java.util.Objects;
import java.util.UUID;

public record BookingId(UUID value) {
    public BookingId {
        Objects.requireNonNull(value, "Booking ID value cannot be null");
    }

    public static BookingId generate() {
        return new BookingId(UUID.randomUUID());
    }

    public static BookingId fromString(String uuid) {
        return new BookingId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
