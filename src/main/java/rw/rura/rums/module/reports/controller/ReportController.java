package rw.rura.rums.module.reports.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.enums.ReportStatus;
import rw.rura.rums.enums.ReportType;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.module.reports.dto.ReportCreateRequest;
import rw.rura.rums.module.reports.dto.ReportResponse;
import rw.rura.rums.module.reports.dto.ReportUpdateRequest;
import rw.rura.rums.module.reports.service.ReportFileService;
import rw.rura.rums.module.reports.service.ReportService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Reports", description = "Regulatory report creation, file upload, publication and download")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportFileService reportFileService;
    private final UserService userService;
    private final AuditService auditService;

    @Operation(summary = "List reports — filterable by type, status, sector and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.getAll(type, status, sector, search, pageable)));
    }

    @Operation(summary = "Get a single report by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getById(id)));
    }

    @Operation(summary = "Create a report record (roles: admin, supervisor, analyst)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ReportResponse>> create(
            @Valid @RequestBody ReportCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(reportService.create(req, actor, request),
                        "Report created successfully"));
    }

    @Operation(summary = "Update report metadata (roles: admin, supervisor, analyst)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ReportResponse>> update(
            @PathVariable UUID id,
            @RequestBody ReportUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(reportService.update(id, req, actor, request)));
    }

    @Operation(summary = "Upload a report file (multipart/form-data, max 20 MB) — roles: admin, supervisor, analyst")
    @PostMapping("/{id}/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ReportResponse>> uploadFile(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                ReportResponse.fromEntity(reportFileService.uploadFile(id, file)),
                "File uploaded successfully"));
    }

    @Operation(summary = "Publish a report — sets status to published and raises a report_ready alert (roles: admin, supervisor)")
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<ReportResponse>> publish(
            @PathVariable UUID id,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(reportService.publish(id, actor, request)));
    }

    @Operation(summary = "Archive a report (roles: admin, supervisor, analyst)")
    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ReportResponse>> archive(
            @PathVariable UUID id,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(reportService.archive(id, actor, request)));
    }

    @Operation(summary = "Download a report file — streams file with Content-Disposition: attachment")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable UUID id,
            Authentication auth,
            HttpServletRequest request
    ) {
        Resource resource = reportFileService.downloadFile(id);
        String filename = reportFileService.getFilename(id);

        UserEntity actor = resolveActor(auth);
        auditService.log(actor, AuditAction.EXPORT, AuditModule.REPORTS,
                id.toString(), filename, request, null);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
