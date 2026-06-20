package rw.rura.rums.module.clms.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.ClmsCaseStatus;

public record ClmsStatusUpdateRequest(@NotNull ClmsCaseStatus status) {}
