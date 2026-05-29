package com.booking.system.booking.infrastructure.adapter.in.web;

import com.booking.system.booking.application.query.ResourceQueryService;
import com.booking.system.booking.application.query.ResourceQueryService.GetAvailableResourcesQuery;
import com.booking.system.booking.application.query.ResourceReadModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceQueryController {

    private final ResourceQueryService resourceQueryService;

    public ResourceQueryController(ResourceQueryService resourceQueryService) {
        this.resourceQueryService = resourceQueryService;
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<ResourceReadModel>> getAvailableResources(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        GetAvailableResourcesQuery query = new GetAvailableResourcesQuery(start, end);
        List<ResourceReadModel> available = resourceQueryService.execute(query);
        return ResponseEntity.ok(available);
    }
}
