package rw.rura.rums.module.alerts.dto;

import rw.rura.rums.enums.AlertSeverity;
import rw.rura.rums.enums.AlertStatus;
import rw.rura.rums.enums.AlertType;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.alerts.entity.Alert;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        AlertType type,
        String title,
        String message,
        AlertSeverity severity,
        AlertStatus status,
        AuditModule relatedModule,
        String relatedEntityId,
        Instant createdAt,
        Instant readAt,
        UserSummary actionedBy
) {
    public static AlertResponse fromEntity(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getType(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getSeverity(),
                alert.getStatus(),
                alert.getRelatedModule(),
                alert.getRelatedEntityId(),
                alert.getCreatedAt(),
                alert.getReadAt(),
                UserSummary.fromEntity(alert.getActionedBy())
        );
    }
}
