package rw.rura.rums.module.fraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.rura.rums.enums.FraudCaseStatus;
import rw.rura.rums.module.fraud.entity.FraudCase;

import java.time.LocalDate;
import java.util.UUID;

public interface FraudCaseRepository extends JpaRepository<FraudCase, UUID>,
        JpaSpecificationExecutor<FraudCase> {

    @Query(value = "SELECT MAX(case_number) FROM fraud_cases WHERE case_number LIKE :pattern",
            nativeQuery = true)
    String findMaxCaseNumberByPattern(@Param("pattern") String pattern);

    /**
     * Counts fraud cases in open or investigating statuses.
     */
    @Query(value = "SELECT COUNT(*) FROM fraud_cases WHERE status IN ('open','investigating')",
            nativeQuery = true)
    long countOpen();

    /**
     * Sums estimated_loss_rwf for all non-dismissed fraud cases reported on or after
     * the given date. Used to detect quarterly loss threshold breaches.
     */
    @Query("SELECT COALESCE(SUM(f.estimatedLossRwf), 0) FROM FraudCase f " +
           "WHERE f.status <> rw.rura.rums.enums.FraudCaseStatus.DISMISSED AND f.reportedAt >= :from")
    long sumEstimatedLossFromDate(@Param("from") LocalDate from);
}
