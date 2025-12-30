package com.example.booking.infrastructure.inbound.rest;

import com.example.booking.application.usecase.CreateBookingUseCase;
import com.example.booking.application.usecase.CancelBookingUseCase;
import com.example.booking.domain.model.Booking;
import com.example.booking.domain.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    final BookingRepository bookingRepository;
    private final CancelBookingUseCase cancelBookingUseCase;

    public BookingController(CreateBookingUseCase createBookingUseCase, CancelBookingUseCase cancelBookingUseCase, BookingRepository bookingRepository) {
        this.createBookingUseCase = createBookingUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.bookingRepository = bookingRepository;
    }

    record CreateBookingRequest(String resourceId, String userId, OffsetDateTime startTime, OffsetDateTime endTime) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateBookingRequest req) {
        Booking booking = createBookingUseCase.create(req.resourceId(), req.userId(), req.startTime(), req.endTime());
        return ResponseEntity.created(URI.create("/api/v1/bookings/" + booking.getId())).body(booking);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        Booking booking = cancelBookingUseCase.cancel(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> listByUser(@RequestParam(required = false) String userId) {
        if (userId == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(bookingRepository.findByUserId(userId));
    }
}
