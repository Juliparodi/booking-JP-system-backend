package com.booking.system.booking.application.port.out;

import com.booking.system.booking.domain.model.Booking;
import com.booking.system.booking.domain.model.BookingId;
import com.booking.system.booking.domain.model.ResourceId;
import com.booking.system.booking.domain.model.TimeRange;
import java.util.List;
import java.util.Optional;

public interface BookingRepositoryPort {
    Optional<Booking> findById(BookingId id);
    Booking save(Booking booking);
    List<Booking> findActiveOverlappingBookings(ResourceId resourceId, TimeRange timeRange);
}
