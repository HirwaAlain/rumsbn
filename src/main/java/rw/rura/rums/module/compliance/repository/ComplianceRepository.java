package rw.rura.rums.module.compliance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import rw.rura.rums.module.compliance.entity.ComplianceRecord;

import java.util.List;
import java.util.UUID;

public interface ComplianceRepository extends JpaRepository<ComplianceRecord, UUID>,
        JpaSpecificationExecutor<ComplianceRecord> {

    @Query(value = """
            SELECT status, COUNT(*) AS cnt
            FROM compliance_records
            GROUP BY status
            """, nativeQuery = true)
    List<Object[]> countByStatus();
}
