package com.booking.system.booking.application.usecase;

import com.booking.system.booking.application.port.in.CreateBookingUseCase.CreateBookingCommand;
import com.booking.system.booking.application.port.out.BookingRepositoryPort;
import com.booking.system.booking.application.port.out.DomainEventPublisherPort;
import com.booking.system.booking.application.port.out.ResourceRepositoryPort;
import com.booking.system.booking.domain.exception.BookingOverlappingException;
import com.booking.system.booking.domain.exception.ResourceNotFoundException;
import com.booking.system.booking.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookingServiceTest {

    @Mock
    private BookingRepositoryPort bookingRepositoryPort;
    @Mock
    private ResourceRepositoryPort resourceRepositoryPort;
    @Mock
    private DomainEventPublisherPort domainEventPublisherPort;

    private CreateBookingService createBookingService;

    private final UUID resourceUuid = UUID.randomUUID();
    private final ResourceId resourceId = new ResourceId(resourceUuid);
    private final UUID userId = UUID.randomUUID();
    private final LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2026, 6, 1, 11, 0);
    private final CreateBookingCommand command = new CreateBookingCommand(resourceUuid, userId, start, end);

    @BeforeEach
    void setUp() {
        createBookingService = new CreateBookingService(bookingRepositoryPort, resourceRepositoryPort, domainEventPublisherPort);
    }

    @Test
    @DisplayName("Should successfully create a booking when resource is active and no overlap exists")
    void createBookingSuccess() {
        Resource resource = new Resource(resourceId, "Consultant Room 1", "ROOM", true);
        
        when(resourceRepositoryPort.findById(resourceId)).thenReturn(Optional.of(resource));
        when(bookingRepositoryPort.findActiveOverlappingBookings(eq(resourceId), any(TimeRange.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepositoryPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingId resultId = createBookingService.execute(command);

        assertNotNull(resultId);
        verify(resourceRepositoryPort).findById(resourceId);
        verify(bookingRepositoryPort).findActiveOverlappingBookings(eq(resourceId), any(TimeRange.class));
        verify(bookingRepositoryPort).save(any(Booking.class));
        verify(domainEventPublisherPort).publishAll(anyList());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when the requested resource does not exist")
    void createBookingResourceNotFoundFailure() {
        when(resourceRepositoryPort.findById(resourceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> createBookingService.execute(command));
        
        verify(resourceRepositoryPort).findById(resourceId);
        verifyNoInteractions(bookingRepositoryPort, domainEventPublisherPort);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when the resource exists but is inactive")
    void createBookingResourceInactiveFailure() {
        Resource resource = new Resource(resourceId, "Consultant Room 1", "ROOM", false);
        when(resourceRepositoryPort.findById(resourceId)).thenReturn(Optional.of(resource));

        assertThrows(ResourceNotFoundException.class, () -> createBookingService.execute(command));
        
        verify(resourceRepositoryPort).findById(resourceId);
        verifyNoInteractions(bookingRepositoryPort, domainEventPublisherPort);
    }

    @Test
    @DisplayName("Should throw BookingOverlappingException when there is an active booking during the requested range")
    void createBookingOverlappingFailure() {
        Resource resource = new Resource(resourceId, "Consultant Room 1", "ROOM", true);
        Booking overlappingBooking = Booking.create(
                BookingId.generate(),
                resourceId,
                UUID.randomUUID(),
                new TimeRange(start.minusMinutes(30), start.plusMinutes(30)),
                LocalDateTime.now()
        );

        when(resourceRepositoryPort.findById(resourceId)).thenReturn(Optional.of(resource));
        when(bookingRepositoryPort.findActiveOverlappingBookings(eq(resourceId), any(TimeRange.class)))
                .thenReturn(List.of(overlappingBooking));

        assertThrows(BookingOverlappingException.class, () -> createBookingService.execute(command));

        verify(resourceRepositoryPort).findById(resourceId);
        verify(bookingRepositoryPort).findActiveOverlappingBookings(eq(resourceId), any(TimeRange.class));
        verifyNoMoreInteractions(bookingRepositoryPort);
        verifyNoInteractions(domainEventPublisherPort);
    }
}
