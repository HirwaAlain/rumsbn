package rw.rura.rums.module.audit.dto;

import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.audit.entity.AuditLog;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID userId,
        String userName,
        AuditAction action,
        AuditModule module,
        String entityId,
        String entityLabel,
        String ipAddress,
        String userAgent,
        Instant timestamp,
        Map<String, ChangeDto> changes
) {
    public static AuditLogResponse fromEntity(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getUserId(),
                log.getUserName(),
                log.getAction(),
                log.getModule(),
                log.getEntityId(),
                log.getEntityLabel(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getTimestamp(),
                log.getChanges()
        );
    }
}
