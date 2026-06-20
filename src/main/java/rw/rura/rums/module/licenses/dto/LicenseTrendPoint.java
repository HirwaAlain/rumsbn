package rw.rura.rums.module.licenses.dto;

import rw.rura.rums.module.licenses.repository.LicenseTrendProjection;

public record LicenseTrendPoint(String month, long issued, long revoked, long expired) {

    public static LicenseTrendPoint fromProjection(LicenseTrendProjection p) {
        return new LicenseTrendPoint(
                p.getMonth(),
                p.getIssued() != null ? p.getIssued() : 0L,
                p.getRevoked() != null ? p.getRevoked() : 0L,
                p.getExpired() != null ? p.getExpired() : 0L
        );
    }
}
