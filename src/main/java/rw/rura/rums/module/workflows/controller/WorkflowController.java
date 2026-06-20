package rw.rura.rums.module.workflows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.enums.WorkflowStatus;
import rw.rura.rums.enums.WorkflowTrigger;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;
import rw.rura.rums.module.workflows.dto.*;
import rw.rura.rums.module.workflows.service.WorkflowService;

import java.util.UUID;

@Tag(name = "Workflows", description = "Multi-step process workflows with role-based step assignments")
@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;
    private final UserService userService;

    @Operation(summary = "List workflows — filterable by status, trigger, sector and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) WorkflowStatus status,
            @RequestParam(required = false) WorkflowTrigger trigger,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                workflowService.getAll(status, trigger, sector, search, pageable)));
    }

    @Operation(summary = "Get a single workflow by ID (includes all steps)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkflowResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowService.getById(id)));
    }

    @Operation(summary = "Create a workflow with steps (roles: admin, supervisor, analyst)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<WorkflowResponse>> create(
            @Valid @RequestBody WorkflowCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(workflowService.create(req, actor, request),
                        "Workflow created successfully"));
    }

    @Operation(summary = "Update workflow metadata — name, description, sector, relatedEntityId (roles: admin, supervisor, analyst)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<WorkflowResponse>> update(
            @PathVariable UUID id,
            @RequestBody WorkflowUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(workflowService.update(id, req, actor, request)));
    }

    @Operation(summary = "Update workflow status — active / paused / completed / failed (roles: admin, supervisor, analyst)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<WorkflowResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody WorkflowStatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                workflowService.updateStatus(id, req, actor, request)));
    }

    @Operation(summary = "Update a workflow step status — in_progress / completed / skipped / failed (roles: admin, supervisor, analyst, auditor)")
    @PatchMapping("/{id}/steps/{stepId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST', 'AUDITOR')")
    public ResponseEntity<ApiResponse<WorkflowStepResponse>> updateStep(
            @PathVariable UUID id,
            @PathVariable UUID stepId,
            @Valid @RequestBody WorkflowStepStatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                workflowService.updateStep(id, stepId, req, actor, request)));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
