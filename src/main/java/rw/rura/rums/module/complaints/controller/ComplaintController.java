package rw.rura.rums.module.complaints.controller;

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
import rw.rura.rums.enums.ComplaintSeverity;
import rw.rura.rums.enums.ComplaintStatus;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.complaints.dto.*;
import rw.rura.rums.module.complaints.service.ComplaintService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Complaints", description = "Consumer complaint filing, tracking and resolution")
@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;

    @Operation(summary = "List complaints — filterable by status, sector, severity and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) ComplaintSeverity severity,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "filedAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                complaintService.getAll(status, sector, severity, search, pageable)));
    }

    @Operation(summary = "Get complaint counts grouped by sector")
    @GetMapping("/by-sector")
    public ResponseEntity<ApiResponse<?>> getBySector() {
        return ResponseEntity.ok(ApiResponse.ok(complaintService.getBySector()));
    }

    @Operation(summary = "Get a single complaint by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(complaintService.getById(id)));
    }

    @Operation(summary = "File a new complaint (roles: admin, supervisor, analyst)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> create(
            @Valid @RequestBody ComplaintCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(complaintService.create(req, actor, request),
                        "Complaint filed successfully"));
    }

    @Operation(summary = "Update complaint details (roles: admin, supervisor, analyst)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> update(
            @PathVariable UUID id,
            @RequestBody ComplaintUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(complaintService.update(id, req, actor, request)));
    }

    @Operation(summary = "Update complaint status — under_review / escalated / resolved / closed")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST', 'AUDITOR')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ComplaintStatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(complaintService.updateStatus(id, req, actor, request)));
    }

    @Operation(summary = "Assign a complaint to a staff member (roles: admin, supervisor, analyst)")
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> assign(
            @PathVariable UUID id,
            @Valid @RequestBody ComplaintAssignRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(complaintService.assign(id, req, actor, request)));
    }

    @Operation(summary = "File a complaint as a member of the public — no authentication required",
               security = {})
    @PostMapping("/public")
    public ResponseEntity<ApiResponse<PublicComplaintResponse>> createPublic(
            @Valid @RequestBody PublicComplaintRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(complaintService.createPublic(req), "Complaint filed successfully"));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
