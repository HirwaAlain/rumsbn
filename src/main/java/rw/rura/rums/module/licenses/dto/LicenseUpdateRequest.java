package rw.rura.rums.module.licenses.dto;

import rw.rura.rums.enums.LicenseCategory;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

import java.time.LocalDate;

public record LicenseUpdateRequest(
        String operatorName,
        String contactPerson,
        String contactEmail,
        LicenseCategory category,
        Sector sector,
        Province province,
        LocalDate issuedAt,
        LocalDate expiresAt,
        Long annualFeeRwf,
        LocalDate lastRenewalAt
) {}
