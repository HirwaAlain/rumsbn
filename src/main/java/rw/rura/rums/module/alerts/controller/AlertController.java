package rw.rura.rums.module.alerts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.enums.AlertSeverity;
import rw.rura.rums.enums.AlertStatus;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.alerts.dto.AlertResponse;
import rw.rura.rums.module.alerts.service.AlertModuleService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Alerts", description = "System alerts — licence expiry, SLA breaches, fraud detections and more")
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertModuleService alertModuleService;
    private final UserService userService;

    @Operation(summary = "List alerts — filterable by severity, status, related module and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) AuditModule relatedModule,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                alertModuleService.getAll(severity, status, relatedModule, search, pageable)));
    }

    @Operation(summary = "Mark a single alert as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<AlertResponse>> markRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(alertModuleService.markRead(id)));
    }

    @Operation(summary = "Dismiss a single alert")
    @PatchMapping("/{id}/dismiss")
    public ResponseEntity<ApiResponse<AlertResponse>> dismiss(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(alertModuleService.dismiss(id)));
    }

    @Operation(summary = "Mark a single alert as actioned (roles: admin, supervisor)")
    @PatchMapping("/{id}/action")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<AlertResponse>> action(
            @PathVariable UUID id,
            Authentication auth
    ) {
        UserEntity actor = userService.findEntityByEmail(auth.getName());
        return ResponseEntity.ok(ApiResponse.ok(alertModuleService.action(id, actor)));
    }

    @Operation(summary = "Mark all unread alerts as read")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        alertModuleService.markAllRead();
        return ResponseEntity.ok(ApiResponse.ok(null, "All alerts marked as read"));
    }
}
