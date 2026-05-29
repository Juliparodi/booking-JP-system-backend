package com.booking.system.booking.application.query;

import java.util.UUID;

public record ResourceReadModel(
        UUID resourceId,
        String name,
        String type,
        boolean active
) {}
