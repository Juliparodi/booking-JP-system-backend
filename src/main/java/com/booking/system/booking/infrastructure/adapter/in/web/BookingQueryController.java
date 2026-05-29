package com.booking.system.booking.infrastructure.adapter.in.web;

import com.booking.system.booking.application.query.BookingQueryService;
import com.booking.system.booking.application.query.BookingQueryService.GetBookingByIdQuery;
import com.booking.system.booking.application.query.BookingQueryService.GetBookingsByUserQuery;
import com.booking.system.booking.application.query.BookingReadModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingQueryController {

    private final BookingQueryService bookingQueryService;

    public BookingQueryController(BookingQueryService bookingQueryService) {
        this.bookingQueryService = bookingQueryService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<BookingReadModel> getBookingById(
            @PathVariable("id") UUID id,
            JwtAuthenticationToken principal) {

        UUID authenticatedUserId = UUID.fromString(principal.getName());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        GetBookingByIdQuery query = new GetBookingByIdQuery(id);
        BookingReadModel booking = bookingQueryService.execute(query)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Booking not found"
                ));

        // Security check: CLIENT can only retrieve their own bookings
        if (!isAdmin && !booking.userId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Unauthorized: You are not permitted to view this booking.");
        }

        return ResponseEntity.ok(booking);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<BookingReadModel>> getBookingsByUser(
            @RequestParam("userId") UUID userId,
            JwtAuthenticationToken principal) {

        UUID authenticatedUserId = UUID.fromString(principal.getName());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // Security check: CLIENT can only retrieve their own bookings
        if (!isAdmin && !userId.equals(authenticatedUserId)) {
            throw new AccessDeniedException("Unauthorized: You are not permitted to view bookings for other users.");
        }

        GetBookingsByUserQuery query = new GetBookingsByUserQuery(userId);
        List<BookingReadModel> bookings = bookingQueryService.execute(query);
        return ResponseEntity.ok(bookings);
    }
}
