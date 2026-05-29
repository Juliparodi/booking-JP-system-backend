package com.booking.system.booking.application.query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingQueryService {
    Optional<BookingReadModel> execute(GetBookingByIdQuery query);
    List<BookingReadModel> execute(GetBookingsByUserQuery query);

    record GetBookingByIdQuery(UUID bookingId) {}
    record GetBookingsByUserQuery(UUID userId) {}
}
