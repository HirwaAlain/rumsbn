package rw.rura.rums.module.audit.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rw.rura.rums.module.audit.entity.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {

    List<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
