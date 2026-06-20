package rw.rura.rums.module.workflows.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.alert.AlertService;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.*;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.workflows.dto.*;
import rw.rura.rums.module.workflows.entity.Workflow;
import rw.rura.rums.module.workflows.entity.WorkflowStep;
import rw.rura.rums.module.workflows.repository.WorkflowRepository;
import rw.rura.rums.module.workflows.repository.WorkflowStepRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final AuditService auditService;
    private final AlertService alertService;

    @Transactional(readOnly = true)
    public Page<WorkflowResponse> getAll(
            WorkflowStatus status, WorkflowTrigger trigger, String sector,
            String search, Pageable pageable) {
        return workflowRepository.findAll(buildSpec(status, trigger, sector, search), pageable)
                .map(WorkflowResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getById(UUID id) {
        return WorkflowResponse.fromEntity(find(id));
    }

    public WorkflowResponse create(WorkflowCreateRequest req,
                                   UserEntity actor, HttpServletRequest request) {
        Workflow workflow = new Workflow();
        workflow.setName(req.name());
        workflow.setDescription(req.description());
        workflow.setTrigger(req.trigger());
        workflow.setStatus(WorkflowStatus.DRAFT);
        workflow.setSector(req.sector());
        workflow.setCreatedBy(actor);
        workflow.setRelatedEntityId(req.relatedEntityId());

        // Build steps and link parent before saving (cascade handles insert)
        List<WorkflowStep> steps = req.steps().stream().map(s -> {
            WorkflowStep step = new WorkflowStep();
            step.setWorkflow(workflow);
            step.setStepOrder((short) s.order());
            step.setName(s.name());
            step.setDescription(s.description());
            step.setAssignedRole(s.assignedRole());
            step.setStatus(WorkflowStepStatus.PENDING);
            step.setDueInDays((short) s.dueInDays());
            return step;
        }).toList();
        workflow.setSteps(new ArrayList<>(steps));

        workflowRepository.save(workflow);

        auditService.log(actor, AuditAction.CREATE, AuditModule.WORKFLOWS,
                workflow.getId().toString(), workflow.getName(), request, null);

        return WorkflowResponse.fromEntity(workflow);
    }

    public WorkflowResponse update(UUID id, WorkflowUpdateRequest req,
                                   UserEntity actor, HttpServletRequest request) {
        Workflow workflow = find(id);
        List<ChangeDto> changePairs = new ArrayList<>();
        java.util.LinkedHashMap<String, ChangeDto> changes = new java.util.LinkedHashMap<>();

        if (req.name() != null && !req.name().equals(workflow.getName())) {
            changes.put("name", new ChangeDto(workflow.getName(), req.name()));
            workflow.setName(req.name());
        }
        if (req.description() != null && !req.description().equals(workflow.getDescription())) {
            changes.put("description", new ChangeDto(workflow.getDescription(), req.description()));
            workflow.setDescription(req.description());
        }
        if (req.sector() != null && !req.sector().equals(workflow.getSector())) {
            changes.put("sector", new ChangeDto(workflow.getSector(), req.sector()));
            workflow.setSector(req.sector());
        }
        if (req.relatedEntityId() != null && !req.relatedEntityId().equals(workflow.getRelatedEntityId())) {
            changes.put("relatedEntityId",
                    new ChangeDto(workflow.getRelatedEntityId(), req.relatedEntityId()));
            workflow.setRelatedEntityId(req.relatedEntityId());
        }

        workflowRepository.save(workflow);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.WORKFLOWS,
                workflow.getId().toString(), workflow.getName(), request,
                changes.isEmpty() ? null : changes);

        return WorkflowResponse.fromEntity(workflow);
    }

    public WorkflowResponse updateStatus(UUID id, WorkflowStatusUpdateRequest req,
                                         UserEntity actor, HttpServletRequest request) {
        Workflow workflow = find(id);
        WorkflowStatus before = workflow.getStatus();
        WorkflowStatus next = req.status();

        workflow.setStatus(next);

        if (next == WorkflowStatus.ACTIVE && workflow.getStartedAt() == null) {
            workflow.setStartedAt(Instant.now());
        }
        if (next == WorkflowStatus.COMPLETED && workflow.getCompletedAt() == null) {
            workflow.setCompletedAt(Instant.now());
        }

        workflowRepository.save(workflow);

        if (next == WorkflowStatus.FAILED) {
            alertService.createAlert(
                    AlertType.WORKFLOW_STALLED,
                    "Workflow Failed: " + workflow.getName(),
                    "Workflow '" + workflow.getName() + "' has transitioned to failed status.",
                    AlertSeverity.CRITICAL,
                    AuditModule.WORKFLOWS,
                    workflow.getId().toString()
            );
        }

        auditService.log(actor, AuditAction.UPDATE, AuditModule.WORKFLOWS,
                workflow.getId().toString(), workflow.getName(), request,
                Map.of("status", new ChangeDto(before.getValue(), next.getValue())));

        return WorkflowResponse.fromEntity(workflow);
    }

    public WorkflowStepResponse updateStep(UUID workflowId, UUID stepId,
                                           WorkflowStepStatusUpdateRequest req,
                                           UserEntity actor, HttpServletRequest request) {
        WorkflowStep step = workflowStepRepository.findByIdAndWorkflow_Id(stepId, workflowId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Step " + stepId + " not found in workflow " + workflowId));

        WorkflowStepStatus before = step.getStatus();
        WorkflowStepStatus next = req.status();

        step.setStatus(next);
        if (next == WorkflowStepStatus.COMPLETED) {
            step.setCompletedAt(Instant.now());
            step.setCompletedBy(actor);
        }

        workflowStepRepository.save(step);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.WORKFLOWS,
                stepId.toString(), step.getName(), request,
                Map.of("status", new ChangeDto(before.getValue(), next.getValue())));

        return WorkflowStepResponse.fromEntity(step);
    }

    // -------------------------------------------------------------------------

    private Workflow find(UUID id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found: " + id));
    }

    private Specification<Workflow> buildSpec(
            WorkflowStatus status, WorkflowTrigger trigger, String sector, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null)  predicates.add(cb.equal(root.get("status"), status));
            if (trigger != null) predicates.add(cb.equal(root.get("trigger"), trigger));
            if (sector != null && !sector.isBlank()) {
                predicates.add(cb.equal(root.get("sector"), sector));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
