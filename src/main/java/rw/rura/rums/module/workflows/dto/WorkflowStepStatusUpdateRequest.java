package rw.rura.rums.module.workflows.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.WorkflowStepStatus;

public record WorkflowStepStatusUpdateRequest(@NotNull WorkflowStepStatus status) {}
