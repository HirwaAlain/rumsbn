package rw.rura.rums.module.workflows.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.rura.rums.module.workflows.entity.WorkflowStep;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, UUID> {

    Optional<WorkflowStep> findByIdAndWorkflow_Id(UUID stepId, UUID workflowId);
}
