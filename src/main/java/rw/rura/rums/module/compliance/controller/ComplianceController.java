package rw.rura.rums.module.compliance.controller;

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
import rw.rura.rums.enums.ComplianceStatus;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.compliance.dto.ComplianceCreateRequest;
import rw.rura.rums.module.compliance.dto.ComplianceRecordResponse;
import rw.rura.rums.module.compliance.dto.ComplianceUpdateRequest;
import rw.rura.rums.module.compliance.service.ComplianceService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Compliance", description = "Operator compliance monitoring, audits and scoring")
@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;
    private final UserService userService;

    @Operation(summary = "List compliance records — filterable by status, sector and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) ComplianceStatus status,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                complianceService.getAll(status, sector, search, pageable)));
    }

    @Operation(summary = "Get compliance status distribution (compliant / under review / non-compliant / remediation)")
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<?>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(complianceService.getOverview()));
    }

    @Operation(summary = "Get a single compliance record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(complianceService.getById(id)));
    }

    @Operation(summary = "Create a compliance record (roles: admin, supervisor, analyst, auditor)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST', 'AUDITOR')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> create(
            @Valid @RequestBody ComplianceCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(complianceService.create(req, actor, request),
                        "Compliance record created successfully"));
    }

    @Operation(summary = "Update a compliance record — score and status changes trigger alerts for non-compliance (roles: admin, supervisor, analyst, auditor)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST', 'AUDITOR')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> update(
            @PathVariable UUID id,
            @RequestBody ComplianceUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(complianceService.update(id, req, actor, request)));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
