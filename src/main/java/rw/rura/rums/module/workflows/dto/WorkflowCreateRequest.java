package rw.rura.rums.module.workflows.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.WorkflowTrigger;

import java.util.List;

public record WorkflowCreateRequest(
        @NotBlank String name,
        String description,
        @NotNull WorkflowTrigger trigger,
        @NotBlank String sector,
        String relatedEntityId,
        @NotEmpty @Valid List<WorkflowStepCreateRequest> steps
) {}
