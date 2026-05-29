package com.booking.system.booking.infrastructure.adapter.out.persistence;

import com.booking.system.booking.application.query.ResourceReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataResourceRepository extends JpaRepository<ResourceJpaEntity, UUID> {

    @Query("""
        SELECT new com.booking.system.booking.application.query.ResourceReadModel(r.id, r.name, r.type, r.active)
        FROM ResourceJpaEntity r
        WHERE r.active = true
          AND r.id NOT IN (
            SELECT b.resourceId
            FROM BookingJpaEntity b
            WHERE b.status <> 'CANCELLED'
              AND b.startTime < :end
              AND b.endTime > :start
          )
    """)
    List<ResourceReadModel> findAvailableResources(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
