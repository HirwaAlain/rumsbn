package rw.rura.rums.module.workflows.dto;

import rw.rura.rums.enums.UserRole;
import rw.rura.rums.enums.WorkflowStepStatus;
import rw.rura.rums.module.users.dto.UserSummary;
import rw.rura.rums.module.workflows.entity.WorkflowStep;

import java.time.Instant;
import java.util.UUID;

public record WorkflowStepResponse(
        UUID id,
        short order,
        String name,
        String description,
        UserRole assignedRole,
        WorkflowStepStatus status,
        short dueInDays,
        Instant completedAt,
        UserSummary completedBy
) {
    public static WorkflowStepResponse fromEntity(WorkflowStep s) {
        return new WorkflowStepResponse(
                s.getId(),
                s.getStepOrder(),
                s.getName(),
                s.getDescription(),
                s.getAssignedRole(),
                s.getStatus(),
                s.getDueInDays(),
                s.getCompletedAt(),
                UserSummary.fromEntity(s.getCompletedBy())
        );
    }
}
