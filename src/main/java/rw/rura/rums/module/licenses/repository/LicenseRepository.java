package rw.rura.rums.module.licenses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.rura.rums.module.licenses.entity.License;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LicenseRepository extends JpaRepository<License, UUID>, JpaSpecificationExecutor<License> {

    boolean existsByLicenseNumber(String licenseNumber);

    @Query(value = "SELECT COUNT(*) FROM licenses WHERE license_number LIKE :prefix%", nativeQuery = true)
    long countByLicenseNumberPrefix(@Param("prefix") String prefix);

    @Query(value = "SELECT COUNT(*) FROM licenses WHERE status = 'active'", nativeQuery = true)
    long countActive();

    /**
     * Returns all active licenses whose expiry date falls on or before the given cutoff.
     * Used by the scheduler to find licenses expiring within the next 30 days.
     */
    @Query("SELECT l FROM License l WHERE l.status = rw.rura.rums.enums.LicenseStatus.ACTIVE AND l.expiresAt <= :cutoff")
    List<License> findActiveExpiringBefore(@Param("cutoff") LocalDate cutoff);

    /**
     * Returns monthly aggregates (issued count, revoked count, expired count) for
     * the current month and the 5 preceding months, ordered chronologically.
     *
     * "issued" = all licenses whose issued_at falls in that month.
     * "revoked" / "expired" = subset of those whose current status is revoked / expired.
     *
     * Status values are lowercase to match the PostgreSQL enum definition.
     */
    @Query(value = """
            SELECT
                TO_CHAR(DATE_TRUNC('month', issued_at), 'Mon') AS month,
                COUNT(*)                                         AS issued,
                SUM(CASE WHEN status = 'revoked' THEN 1 ELSE 0 END) AS revoked,
                SUM(CASE WHEN status = 'expired' THEN 1 ELSE 0 END) AS expired
            FROM licenses
            WHERE issued_at >= DATE_TRUNC('month', NOW()) - INTERVAL '5 months'
            GROUP BY DATE_TRUNC('month', issued_at)
            ORDER BY DATE_TRUNC('month', issued_at) ASC
            """, nativeQuery = true)
    List<LicenseTrendProjection> findMonthlyTrend();
}
