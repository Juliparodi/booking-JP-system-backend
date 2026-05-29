package com.booking.system.booking.application.query;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceQueryService {
    List<ResourceReadModel> execute(GetAvailableResourcesQuery query);

    record GetAvailableResourcesQuery(LocalDateTime start, LocalDateTime end) {}
}
