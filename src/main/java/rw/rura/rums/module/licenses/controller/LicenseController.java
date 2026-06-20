package rw.rura.rums.module.licenses.controller;

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
import rw.rura.rums.enums.LicenseStatus;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.licenses.dto.*;
import rw.rura.rums.module.licenses.service.LicenseService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Licenses", description = "Operator licence lifecycle management")
@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;
    private final UserService userService;

    @Operation(summary = "List licenses — filterable by status, sector, province and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) LicenseStatus status,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) Province province,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                licenseService.getAll(status, sector, province, search, pageable)));
    }

    @Operation(summary = "Get monthly licence issuance / revocation / expiry trend for the last 6 months")
    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<?>> getTrend() {
        return ResponseEntity.ok(ApiResponse.ok(licenseService.getTrend()));
    }

    @Operation(summary = "Get a single licence by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LicenseResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(licenseService.getById(id)));
    }

    @Operation(summary = "Create a new licence (roles: admin, supervisor, analyst)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<LicenseResponse>> create(
            @Valid @RequestBody LicenseCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(licenseService.create(req, actor, request),
                        "License created successfully"));
    }

    @Operation(summary = "Update licence details (roles: admin, supervisor)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<LicenseResponse>> update(
            @PathVariable UUID id,
            @RequestBody LicenseUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(licenseService.update(id, req, actor, request)));
    }

    @Operation(summary = "Update licence status — active/suspended/revoked/expired (roles: admin, supervisor)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<LicenseResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody LicenseStatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(licenseService.updateStatus(id, req, actor, request)));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
