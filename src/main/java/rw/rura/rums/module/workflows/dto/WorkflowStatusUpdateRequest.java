package rw.rura.rums.module.workflows.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.WorkflowStatus;

public record WorkflowStatusUpdateRequest(@NotNull WorkflowStatus status) {}
