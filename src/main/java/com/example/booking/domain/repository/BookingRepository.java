package com.example.booking.domain.repository;

import com.example.booking.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {

    @Query("select b from Booking b where b.resourceId = :resourceId and not (b.endTime <= :start or b.startTime >= :end)")
    List<Booking> findOverlapping(@Param("resourceId") String resourceId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    List<Booking> findByUserId(String userId);
}
