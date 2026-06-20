package rw.rura.rums.module.alerts.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.enums.AlertSeverity;
import rw.rura.rums.enums.AlertStatus;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.exception.ForbiddenException;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.alerts.dto.AlertResponse;
import rw.rura.rums.module.alerts.entity.Alert;
import rw.rura.rums.module.alerts.repository.AlertRepository;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertModuleService {

    private final AlertRepository alertRepository;

    @Transactional(readOnly = true)
    public Page<AlertResponse> getAll(
            AlertSeverity severity, AlertStatus status, AuditModule relatedModule,
            String search, Pageable pageable) {
        Specification<Alert> spec = buildSpec(severity, status, relatedModule, search);
        return alertRepository.findAll(spec, pageable).map(AlertResponse::fromEntity);
    }

    public AlertResponse markRead(UUID id) {
        Alert alert = find(id);
        if (alert.getStatus() == AlertStatus.UNREAD) {
            alert.setStatus(AlertStatus.READ);
            alert.setReadAt(Instant.now());
        }
        return AlertResponse.fromEntity(alertRepository.save(alert));
    }

    public AlertResponse dismiss(UUID id) {
        Alert alert = find(id);
        alert.setStatus(AlertStatus.DISMISSED);
        return AlertResponse.fromEntity(alertRepository.save(alert));
    }

    public AlertResponse action(UUID id, UserEntity actor) {
        Alert alert = find(id);
        alert.setStatus(AlertStatus.ACTIONED);
        alert.setActionedBy(actor);
        return AlertResponse.fromEntity(alertRepository.save(alert));
    }

    public void markAllRead() {
        List<Alert> unread = alertRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("status"), AlertStatus.UNREAD));
        Instant now = Instant.now();
        for (Alert a : unread) {
            a.setStatus(AlertStatus.READ);
            a.setReadAt(now);
        }
        alertRepository.saveAll(unread);
    }

    // -------------------------------------------------------------------------

    private Alert find(UUID id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
    }

    private Specification<Alert> buildSpec(
            AlertSeverity severity, AlertStatus status, AuditModule relatedModule, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (severity != null) predicates.add(cb.equal(root.get("severity"), severity));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (relatedModule != null) predicates.add(cb.equal(root.get("relatedModule"), relatedModule));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("message")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
