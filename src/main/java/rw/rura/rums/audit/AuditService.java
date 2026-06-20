package rw.rura.rums.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.audit.entity.AuditLog;
import rw.rura.rums.module.audit.repository.AuditLogRepository;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.Map;

/**
 * Cross-cutting audit service. Called synchronously within the same transaction
 * as the business operation that triggers it.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Persist an immutable audit log entry.
     *
     * @param actor       currently authenticated user; pass null for system-generated entries
     * @param action      the action performed
     * @param module      the functional module affected
     * @param entityId    primary key of the affected entity (as string)
     * @param entityLabel human-readable label (e.g. licence number, complaint reference)
     * @param request     HTTP request used to extract IP address and User-Agent; may be null
     * @param changes     field-level diff map; may be null for actions with no field changes
     */
    public void log(
            UserEntity actor,
            AuditAction action,
            AuditModule module,
            String entityId,
            String entityLabel,
            HttpServletRequest request,
            Map<String, ChangeDto> changes
    ) {
        AuditLog entry = new AuditLog();
        entry.setUserId(actor != null ? actor.getId() : null);
        entry.setUserName(actor != null ? actor.getName() : "System");
        entry.setAction(action);
        entry.setModule(module);
        entry.setEntityId(entityId);
        entry.setEntityLabel(entityLabel);
        entry.setIpAddress(request != null ? request.getRemoteAddr() : null);
        entry.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        entry.setChanges(changes);
        entry.setTimestamp(Instant.now());

        auditLogRepository.save(entry);
    }
}
