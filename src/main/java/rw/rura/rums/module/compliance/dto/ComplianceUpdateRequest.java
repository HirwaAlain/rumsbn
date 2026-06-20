package rw.rura.rums.module.compliance.dto;

import rw.rura.rums.enums.ComplianceCheckType;
import rw.rura.rums.enums.ComplianceStatus;
import rw.rura.rums.enums.Sector;

import java.time.LocalDate;
import java.util.UUID;

public record ComplianceUpdateRequest(
        String operatorName,
        UUID licenseId,
        Sector sector,
        ComplianceCheckType checkType,
        ComplianceStatus status,
        LocalDate dueDate,
        LocalDate lastAuditDate,
        Short score,
        UUID auditorId,
        String findings
) {}
