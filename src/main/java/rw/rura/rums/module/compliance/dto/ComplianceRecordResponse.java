package rw.rura.rums.module.compliance.dto;

import rw.rura.rums.enums.ComplianceCheckType;
import rw.rura.rums.enums.ComplianceStatus;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.compliance.entity.ComplianceRecord;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.LocalDate;
import java.util.UUID;

public record ComplianceRecordResponse(
        UUID id,
        String operatorName,
        UUID licenseId,
        Sector sector,
        ComplianceCheckType checkType,
        ComplianceStatus status,
        LocalDate dueDate,
        LocalDate lastAuditDate,
        Short score,
        UserSummary auditor,
        String findings
) {
    public static ComplianceRecordResponse fromEntity(ComplianceRecord r) {
        return new ComplianceRecordResponse(
                r.getId(),
                r.getOperatorName(),
                r.getLicense() != null ? r.getLicense().getId() : null,
                r.getSector(),
                r.getCheckType(),
                r.getStatus(),
                r.getDueDate(),
                r.getLastAuditDate(),
                r.getScore(),
                UserSummary.fromEntity(r.getAuditor()),
                r.getFindings()
        );
    }
}
