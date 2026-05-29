package com.booking.system.booking.infrastructure.adapter.out.persistence;

import com.booking.system.booking.application.port.out.BookingRepositoryPort;
import com.booking.system.booking.application.query.BookingQueryService;
import com.booking.system.booking.application.query.BookingReadModel;
import com.booking.system.booking.domain.model.Booking;
import com.booking.system.booking.domain.model.BookingId;
import com.booking.system.booking.domain.model.ResourceId;
import com.booking.system.booking.domain.model.TimeRange;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookingPersistenceAdapter implements BookingRepositoryPort, BookingQueryService {

    private final SpringDataBookingRepository repository;

    public BookingPersistenceAdapter(SpringDataBookingRepository repository) {
        this.repository = repository;
    }

    // --- Outbound Port Implementations (Command / Write path) ---

    @Override
    public Optional<Booking> findById(BookingId id) {
        return repository.findById(id.value())
                .map(BookingJpaEntity::toDomain);
    }

    @Override
    public Booking save(Booking booking) {
        BookingJpaEntity entity = BookingJpaEntity.fromDomain(booking);
        BookingJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<Booking> findActiveOverlappingBookings(ResourceId resourceId, TimeRange timeRange) {
        return repository.findOverlappingActiveBookings(
                resourceId.value(),
                timeRange.start(),
                timeRange.end()
        ).stream()
         .map(BookingJpaEntity::toDomain)
         .collect(Collectors.toList());
    }

    // --- Query Service Implementations (Read / CQRS path) ---

    @Override
    public Optional<BookingReadModel> execute(GetBookingByIdQuery query) {
        return repository.findBookingReadModelById(query.bookingId());
    }

    @Override
    public List<BookingReadModel> execute(GetBookingsByUserQuery query) {
        return repository.findBookingReadModelsByUserId(query.userId());
    }
}
