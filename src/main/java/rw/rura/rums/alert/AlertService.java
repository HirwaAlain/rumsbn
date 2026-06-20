package rw.rura.rums.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.rura.rums.enums.AlertSeverity;
import rw.rura.rums.enums.AlertStatus;
import rw.rura.rums.enums.AlertType;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.alerts.entity.Alert;
import rw.rura.rums.module.alerts.repository.AlertRepository;

import java.time.Instant;

/**
 * Cross-cutting alert service. Called programmatically by other services and
 * by scheduled jobs. Includes deduplication: if an unread alert of the same
 * type and relatedEntityId already exists, no new alert is created.
 */
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    /**
     * Create a new alert, skipping creation if a matching unread alert already exists.
     *
     * @param type            the alert category
     * @param title           short display title
     * @param message         full descriptive message
     * @param severity        info / warning / critical
     * @param relatedModule   the functional module this alert relates to
     * @param relatedEntityId PK of the related entity as a string; may be null
     */
    public void createAlert(
            AlertType type,
            String title,
            String message,
            AlertSeverity severity,
            AuditModule relatedModule,
            String relatedEntityId
    ) {
        // Deduplication: skip if an unread alert for the same type + entity already exists
        boolean duplicate = alertRepository
                .existsByTypeAndRelatedEntityIdAndStatus(type, relatedEntityId, AlertStatus.UNREAD);
        if (duplicate) {
            return;
        }

        Alert alert = new Alert();
        alert.setType(type);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setStatus(AlertStatus.UNREAD);
        alert.setRelatedModule(relatedModule);
        alert.setRelatedEntityId(relatedEntityId);
        alert.setCreatedAt(Instant.now());

        alertRepository.save(alert);
    }
}
