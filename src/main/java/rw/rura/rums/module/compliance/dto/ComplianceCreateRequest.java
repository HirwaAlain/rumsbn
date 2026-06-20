package rw.rura.rums.module.compliance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.ComplianceCheckType;
import rw.rura.rums.enums.Sector;

import java.time.LocalDate;
import java.util.UUID;

public record ComplianceCreateRequest(
        @NotBlank String operatorName,
        UUID licenseId,
        @NotNull Sector sector,
        @NotNull ComplianceCheckType checkType,
        @NotNull LocalDate dueDate,
        UUID auditorId
) {}
