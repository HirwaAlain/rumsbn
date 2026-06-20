package rw.rura.rums.module.complaints.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.rura.rums.enums.ComplaintStatus;
import rw.rura.rums.module.complaints.entity.Complaint;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID>, JpaSpecificationExecutor<Complaint> {

    /**
     * Returns the highest reference_number for a given year prefix, e.g. "RURA-CMP-2026-%".
     * Used by the reference-number generator to determine the next sequence value.
     */
    @Query(value = "SELECT MAX(reference_number) FROM complaints WHERE reference_number LIKE :pattern",
            nativeQuery = true)
    String findMaxReferenceNumberByPattern(@Param("pattern") String pattern);

    /**
     * Counts complaints grouped by sector across all records.
     */
    @Query(value = "SELECT sector, COUNT(*) AS count FROM complaints GROUP BY sector ORDER BY count DESC",
            nativeQuery = true)
    List<ComplaintsBySectorProjection> countBySector();

    /**
     * Counts complaints in active statuses (open, under_review, escalated).
     */
    @Query(value = "SELECT COUNT(*) FROM complaints WHERE status IN ('open','under_review','escalated')",
            nativeQuery = true)
    long countActive();

    /**
     * Returns complaints that have breached the SLA: status is open or under_review
     * and filed_at is older than the given cutoff instant.
     */
    @Query("SELECT c FROM Complaint c WHERE c.status IN :statuses AND c.filedAt < :cutoff")
    List<Complaint> findSlaBreached(@Param("statuses") List<ComplaintStatus> statuses,
                                    @Param("cutoff") Instant cutoff);
}
