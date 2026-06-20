package rw.rura.rums.module.workflows.dto;

import rw.rura.rums.enums.WorkflowStatus;
import rw.rura.rums.enums.WorkflowTrigger;
import rw.rura.rums.module.users.dto.UserSummary;
import rw.rura.rums.module.workflows.entity.Workflow;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkflowResponse(
        UUID id,
        String name,
        String description,
        WorkflowTrigger trigger,
        WorkflowStatus status,
        String sector,
        List<WorkflowStepResponse> steps,
        UserSummary createdBy,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        String relatedEntityId
) {
    public static WorkflowResponse fromEntity(Workflow w) {
        return new WorkflowResponse(
                w.getId(),
                w.getName(),
                w.getDescription(),
                w.getTrigger(),
                w.getStatus(),
                w.getSector(),
                w.getSteps().stream().map(WorkflowStepResponse::fromEntity).toList(),
                UserSummary.fromEntity(w.getCreatedBy()),
                w.getCreatedAt(),
                w.getStartedAt(),
                w.getCompletedAt(),
                w.getRelatedEntityId()
        );
    }
}
