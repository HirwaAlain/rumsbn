package rw.rura.rums.module.licenses.repository;

/**
 * Spring Data projection for the monthly license trend native query.
 */
public interface LicenseTrendProjection {
    String getMonth();
    Long getIssued();
    Long getRevoked();
    Long getExpired();
}
