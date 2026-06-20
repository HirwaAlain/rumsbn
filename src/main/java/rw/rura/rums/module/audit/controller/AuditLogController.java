package rw.rura.rums.module.audit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.audit.service.AuditLogService;

import java.util.UUID;

@Tag(name = "Audit", description = "Append-only audit trail — read-only, no mutations permitted via API")
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "List audit log entries — filterable by module, action, user ID and free-text search; ordered by timestamp descending")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) AuditModule module,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                auditLogService.getAll(module, action, userId, search, pageable)));
    }
}
