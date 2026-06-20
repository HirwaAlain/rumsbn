package rw.rura.rums.module.fraud.dto;

import rw.rura.rums.enums.FraudCaseStatus;
import rw.rura.rums.enums.FraudIndicatorType;
import rw.rura.rums.enums.FraudRiskLevel;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.fraud.entity.FraudCase;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.LocalDate;
import java.util.UUID;

public record FraudCaseResponse(
        UUID id,
        String caseNumber,
        String description,
        FraudIndicatorType indicatorType,
        String reportedBy,
        String operatorInvolved,
        Sector sector,
        FraudRiskLevel riskLevel,
        FraudCaseStatus status,
        LocalDate reportedAt,
        long estimatedLossRwf,
        UserSummary investigatingOfficer
) {
    public static FraudCaseResponse fromEntity(FraudCase f) {
        return new FraudCaseResponse(
                f.getId(),
                f.getCaseNumber(),
                f.getDescription(),
                f.getIndicatorType(),
                f.getReportedBy(),
                f.getOperatorInvolved(),
                f.getSector(),
                f.getRiskLevel(),
                f.getStatus(),
                f.getReportedAt(),
                f.getEstimatedLossRwf(),
                UserSummary.fromEntity(f.getInvestigatingOfficer())
        );
    }
}
