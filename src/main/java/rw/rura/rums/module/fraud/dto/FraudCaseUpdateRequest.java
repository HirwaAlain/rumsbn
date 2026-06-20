package rw.rura.rums.module.fraud.dto;

import rw.rura.rums.enums.FraudIndicatorType;
import rw.rura.rums.enums.FraudRiskLevel;
import rw.rura.rums.enums.Sector;

import java.time.LocalDate;
import java.util.UUID;

public record FraudCaseUpdateRequest(
        String description,
        FraudIndicatorType indicatorType,
        String reportedBy,
        String operatorInvolved,
        Sector sector,
        FraudRiskLevel riskLevel,
        LocalDate reportedAt,
        Long estimatedLossRwf,
        UUID investigatingOfficerId
) {}
