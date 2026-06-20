package rw.rura.rums.module.fraud.controller;

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
import rw.rura.rums.enums.FraudCaseStatus;
import rw.rura.rums.enums.FraudRiskLevel;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.fraud.dto.*;
import rw.rura.rums.module.fraud.service.FraudService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Fraud", description = "Fraud and anomaly case management")
@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudService fraudService;
    private final UserService userService;

    @Operation(summary = "List fraud cases — filterable by risk level, status, sector and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) FraudRiskLevel riskLevel,
            @RequestParam(required = false) FraudCaseStatus status,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                fraudService.getAll(riskLevel, status, sector, search, pageable)));
    }

    @Operation(summary = "Get a single fraud case by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FraudCaseResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(fraudService.getById(id)));
    }

    @Operation(summary = "Create a fraud case — critical risk level auto-raises an alert (roles: admin, supervisor, analyst)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<FraudCaseResponse>> create(
            @Valid @RequestBody FraudCaseCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(fraudService.create(req, actor, request),
                        "Fraud case created successfully"));
    }

    @Operation(summary = "Update fraud case details (roles: admin, supervisor, analyst)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<FraudCaseResponse>> update(
            @PathVariable UUID id,
            @RequestBody FraudCaseUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(fraudService.update(id, req, actor, request)));
    }

    @Operation(summary = "Update fraud case status — investigating / confirmed / dismissed / referred (roles: admin, supervisor, analyst)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<FraudCaseResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody FraudStatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(fraudService.updateStatus(id, req, actor, request)));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
