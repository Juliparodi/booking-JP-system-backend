package com.example.booking.application.usecase;

import com.example.booking.domain.model.Booking;
import com.example.booking.domain.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CreateBookingUseCase {

    final BookingRepository bookingRepository;

    public CreateBookingUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking create(String resourceId, String userId, OffsetDateTime start, OffsetDateTime end) {
        List<Booking> overlapping = bookingRepository.findOverlapping(resourceId, start, end);
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Resource already booked for the requested time range");
        }
        Booking booking = new Booking(resourceId, userId, start, end);
        return bookingRepository.save(booking);
    }
}
