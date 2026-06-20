package rw.rura.rums.module.workflows.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.UserRole;

public record WorkflowStepCreateRequest(
        @NotNull @Min(1) int order,
        @NotBlank String name,
        String description,
        @NotNull UserRole assignedRole,
        @Min(1) int dueInDays
) {}
