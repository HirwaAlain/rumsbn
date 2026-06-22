package rw.rura.rums.module.licenses.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import rw.rura.rums.enums.LicenseCategory;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

import java.time.LocalDate;

public record LicenseCreateRequest(
        String licenseNumber,          // optional — auto-generated if omitted
        @NotBlank String operatorName,
        String contactPerson,
        String contactEmail,
        @NotNull LicenseCategory category,
        @NotNull Sector sector,
        @NotNull Province province,
        @NotNull @FutureOrPresent(message = "Issue date cannot be set to a past date") LocalDate issuedAt,
        @NotNull LocalDate expiresAt,
        @PositiveOrZero long annualFeeRwf
) {}
