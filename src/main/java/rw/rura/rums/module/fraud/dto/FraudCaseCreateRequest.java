package rw.rura.rums.module.fraud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.FraudIndicatorType;
import rw.rura.rums.enums.FraudRiskLevel;
import rw.rura.rums.enums.Sector;

import java.time.LocalDate;
import java.util.UUID;

public record FraudCaseCreateRequest(
        @NotBlank String description,
        @NotNull FraudIndicatorType indicatorType,
        @NotBlank String reportedBy,
        @NotBlank String operatorInvolved,
        @NotNull Sector sector,
        @NotNull FraudRiskLevel riskLevel,
        @NotNull LocalDate reportedAt,
        long estimatedLossRwf,
        UUID investigatingOfficerId
) {}
