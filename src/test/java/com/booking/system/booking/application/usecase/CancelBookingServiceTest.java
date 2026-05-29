package com.booking.system.booking.application.usecase;

import com.booking.system.booking.application.port.in.CancelBookingUseCase.CancelBookingCommand;
import com.booking.system.booking.application.port.out.BookingRepositoryPort;
import com.booking.system.booking.application.port.out.DomainEventPublisherPort;
import com.booking.system.booking.domain.exception.BookingCancellationException;
import com.booking.system.booking.domain.exception.BookingNotFoundException;
import com.booking.system.booking.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelBookingServiceTest {

    @Mock
    private BookingRepositoryPort bookingRepositoryPort;
    @Mock
    private DomainEventPublisherPort domainEventPublisherPort;

    private CancelBookingService cancelBookingService;

    private final UUID bookingUuid = UUID.randomUUID();
    private final BookingId bookingId = new BookingId(bookingUuid);
    private final ResourceId resourceId = ResourceId.generate();
    private final UUID clientUser = UUID.randomUUID();
    private final LocalDateTime start = LocalDateTime.now().plusDays(2); // In 2 days
    private final TimeRange timeRange = new TimeRange(start, start.plusHours(1));

    @BeforeEach
    void setUp() {
        cancelBookingService = new CancelBookingService(bookingRepositoryPort, domainEventPublisherPort);
    }

    @Test
    @DisplayName("Should successfully cancel booking when requested by CLIENT who owns it")
    void clientCancelBookingSuccess() {
        Booking booking = Booking.create(bookingId, resourceId, clientUser, timeRange, LocalDateTime.now());
        CancelBookingCommand command = new CancelBookingCommand(bookingUuid, clientUser, false);

        when(bookingRepositoryPort.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepositoryPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cancelBookingService.execute(command);

        verify(bookingRepositoryPort).findById(bookingId);
        verify(bookingRepositoryPort).save(booking);
        verify(domainEventPublisherPort).publishAll(anyList());
    }

    @Test
    @DisplayName("Should throw BookingNotFoundException when booking does not exist")
    void cancelBookingNotFoundFailure() {
        CancelBookingCommand command = new CancelBookingCommand(bookingUuid, clientUser, false);
        when(bookingRepositoryPort.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> cancelBookingService.execute(command));

        verify(bookingRepositoryPort).findById(bookingId);
        verifyNoMoreInteractions(bookingRepositoryPort);
        verifyNoInteractions(domainEventPublisherPort);
    }

    @Test
    @DisplayName("Should throw BookingCancellationException when CLIENT attempts to cancel another user's booking")
    void clientCancelBookingUnauthorizedFailure() {
        Booking booking = Booking.create(bookingId, resourceId, clientUser, timeRange, LocalDateTime.now());
        UUID rogueClient = UUID.randomUUID();
        CancelBookingCommand command = new CancelBookingCommand(bookingUuid, rogueClient, false);

        when(bookingRepositoryPort.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingCancellationException exception = assertThrows(BookingCancellationException.class, () ->
                cancelBookingService.execute(command)
        );

        assertTrue(exception.getMessage().contains("Unauthorized"));
        verify(bookingRepositoryPort).findById(bookingId);
        verifyNoMoreInteractions(bookingRepositoryPort);
        verifyNoInteractions(domainEventPublisherPort);
    }

    @Test
    @DisplayName("Should successfully cancel booking when requested by ADMIN even if they don't own it")
    void adminCancelBookingSuccess() {
        Booking booking = Booking.create(bookingId, resourceId, clientUser, timeRange, LocalDateTime.now());
        UUID adminUser = UUID.randomUUID();
        CancelBookingCommand command = new CancelBookingCommand(bookingUuid, adminUser, true);

        when(bookingRepositoryPort.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepositoryPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cancelBookingService.execute(command);

        verify(bookingRepositoryPort).findById(bookingId);
        verify(bookingRepositoryPort).save(booking);
        verify(domainEventPublisherPort).publishAll(anyList());
    }
}
