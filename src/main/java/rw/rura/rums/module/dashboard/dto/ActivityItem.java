package rw.rura.rums.module.dashboard.dto;

import java.time.Instant;

public record ActivityItem(
        String id,
        String type,
        String description,
        String actor,
        String module,
        Instant timestamp,
        String entityId
) {}
