package rw.rura.rums.module.alerts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rw.rura.rums.enums.AlertStatus;
import rw.rura.rums.enums.AlertType;
import rw.rura.rums.module.alerts.entity.Alert;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID>, JpaSpecificationExecutor<Alert> {

    boolean existsByTypeAndRelatedEntityIdAndStatus(AlertType type, String relatedEntityId, AlertStatus status);
}
