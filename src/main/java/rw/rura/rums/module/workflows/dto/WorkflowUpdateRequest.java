package rw.rura.rums.module.workflows.dto;

public record WorkflowUpdateRequest(
        String name,
        String description,
        String sector,
        String relatedEntityId
) {}
