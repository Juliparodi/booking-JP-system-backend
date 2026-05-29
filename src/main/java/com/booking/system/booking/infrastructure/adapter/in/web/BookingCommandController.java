package com.booking.system.booking.infrastructure.adapter.in.web;

import com.booking.system.booking.application.port.in.CancelBookingUseCase;
import com.booking.system.booking.application.port.in.CancelBookingUseCase.CancelBookingCommand;
import com.booking.system.booking.application.port.in.CreateBookingUseCase;
import com.booking.system.booking.application.port.in.CreateBookingUseCase.CreateBookingCommand;
import com.booking.system.booking.domain.model.BookingId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingCommandController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;

    public BookingCommandController(CreateBookingUseCase createBookingUseCase, CancelBookingUseCase cancelBookingUseCase) {
        this.createBookingUseCase = createBookingUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Map<String, String>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            JwtAuthenticationToken principal) {

        // Extract user identity from authenticated JWT claims
        UUID userId = UUID.fromString(principal.getName());

        CreateBookingCommand command = new CreateBookingCommand(
                request.resourceId(),
                userId,
                request.start(),
                request.end()
        );

        BookingId bookingId = createBookingUseCase.execute(command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookingId.value())
                .toUri();

        return ResponseEntity.created(location)
                .body(Map.of("bookingId", bookingId.value().toString()));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable("id") UUID bookingId,
            JwtAuthenticationToken principal) {

        // Extract user identity and roles from authenticated JWT claims
        UUID userId = UUID.fromString(principal.getName());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        CancelBookingCommand command = new CancelBookingCommand(bookingId, userId, isAdmin);
        cancelBookingUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    public record CreateBookingRequest(
            @NotNull(message = "Resource ID cannot be null") UUID resourceId,
            @NotNull(message = "Start time cannot be null") LocalDateTime start,
            @NotNull(message = "End time cannot be null") LocalDateTime end
    ) {}
}
