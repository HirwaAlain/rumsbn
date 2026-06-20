package rw.rura.rums.module.licenses.dto;

import rw.rura.rums.enums.LicenseCategory;
import rw.rura.rums.enums.LicenseStatus;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.licenses.entity.License;

import java.time.LocalDate;
import java.util.UUID;

public record LicenseResponse(
        UUID id,
        String licenseNumber,
        String operatorName,
        String contactPerson,
        String contactEmail,
        LicenseCategory category,
        Sector sector,
        LicenseStatus status,
        Province province,
        LocalDate issuedAt,
        LocalDate expiresAt,
        long annualFeeRwf,
        LocalDate lastRenewalAt
) {

    public static LicenseResponse fromEntity(License license) {
        return new LicenseResponse(
                license.getId(),
                license.getLicenseNumber(),
                license.getOperatorName(),
                license.getContactPerson(),
                license.getContactEmail(),
                license.getCategory(),
                license.getSector(),
                license.getStatus(),
                license.getProvince(),
                license.getIssuedAt(),
                license.getExpiresAt(),
                license.getAnnualFeeRwf(),
                license.getLastRenewalAt()
        );
    }
}
