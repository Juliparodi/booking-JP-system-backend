package com.booking.system.booking.infrastructure.adapter.out.persistence;

import com.booking.system.booking.application.query.BookingReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataBookingRepository extends JpaRepository<BookingJpaEntity, UUID> {

    @Query("""
        SELECT b FROM BookingJpaEntity b
        WHERE b.resourceId = :resourceId
          AND b.status <> 'CANCELLED'
          AND b.startTime < :end
          AND b.endTime > :start
    """)
    List<BookingJpaEntity> findOverlappingActiveBookings(
            @Param("resourceId") UUID resourceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT new com.booking.system.booking.application.query.BookingReadModel(
            b.id, b.resourceId, r.name, b.userId, b.startTime, b.endTime, b.status, b.createdAt, b.cancelledAt
        )
        FROM BookingJpaEntity b
        JOIN ResourceJpaEntity r ON b.resourceId = r.id
        WHERE b.id = :bookingId
    """)
    Optional<BookingReadModel> findBookingReadModelById(@Param("bookingId") UUID bookingId);

    @Query("""
        SELECT new com.booking.system.booking.application.query.BookingReadModel(
            b.id, b.resourceId, r.name, b.userId, b.startTime, b.endTime, b.status, b.createdAt, b.cancelledAt
        )
        FROM BookingJpaEntity b
        JOIN ResourceJpaEntity r ON b.resourceId = r.id
        WHERE b.userId = :userId
        ORDER BY b.startTime DESC
    """)
    List<BookingReadModel> findBookingReadModelsByUserId(@Param("userId") UUID userId);
}
