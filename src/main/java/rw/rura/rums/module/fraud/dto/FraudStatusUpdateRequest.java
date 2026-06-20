package rw.rura.rums.module.fraud.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.FraudCaseStatus;

public record FraudStatusUpdateRequest(@NotNull FraudCaseStatus status) {}
