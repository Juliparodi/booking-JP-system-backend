package com.example.booking.application.usecase;

import com.example.booking.domain.model.Booking;
import com.example.booking.domain.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;

    public CancelBookingUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking cancel(String bookingId) {
        Optional<Booking> o = bookingRepository.findById(bookingId);
        Booking booking = o.orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if ("CANCELLED".equals(booking.getStatus())) {
            return booking;
        }
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
}
