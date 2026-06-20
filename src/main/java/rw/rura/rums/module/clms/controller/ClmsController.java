package rw.rura.rums.module.clms.controller;

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
import rw.rura.rums.enums.ClmsCaseStatus;
import rw.rura.rums.enums.ClmsCaseType;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.module.clms.dto.*;
import rw.rura.rums.module.clms.service.ClmsCaseService;
import rw.rura.rums.module.clms.service.ClmsFileService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "CLMS", description = "Case and Licence Management System — applications, renewals, amendments and documents")
@RestController
@RequestMapping("/api/clms")
@RequiredArgsConstructor
public class ClmsController {

    private final ClmsCaseService clmsCaseService;
    private final ClmsFileService clmsFileService;
    private final UserService userService;
    private final AuditService auditService;

    @Operation(summary = "List CLMS cases — filterable by status, type, sector and free-text search")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) ClmsCaseStatus status,
            @RequestParam(required = false) ClmsCaseType type,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "submittedAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                clmsCaseService.getAll(status, type, sector, search, pageable)));
    }

    @Operation(summary = "Get a single CLMS case by ID (includes attached documents)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClmsCaseResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(clmsCaseService.getById(id)));
    }

    @Operation(summary = "Create a CLMS case (roles: admin, supervisor, analyst)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ClmsCaseResponse>> create(
            @Valid @RequestBody ClmsCaseCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(clmsCaseService.create(req, actor, request),
                        "CLMS case created successfully"));
    }

    @Operation(summary = "Update a CLMS case (roles: admin, supervisor, analyst)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ClmsCaseResponse>> update(
            @PathVariable UUID id,
            @RequestBody ClmsCaseUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(clmsCaseService.update(id, req, actor, request)));
    }

    @Operation(summary = "Update CLMS case status — submitted / under_review / approved / rejected / appealed / closed (approval requires admin or supervisor)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ClmsCaseResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ClmsStatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(clmsCaseService.updateStatus(id, req, actor, request)));
    }

    @Operation(summary = "Upload a document to a CLMS case (multipart/form-data, max 20 MB; PDF/XLSX/CSV/PNG/JPEG) — roles: admin, supervisor, analyst")
    @PostMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ANALYST')")
    public ResponseEntity<ApiResponse<ClmsDocumentResponse>> uploadDocument(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String displayName,
            Authentication auth
    ) {
        UserEntity actor = resolveActor(auth);
        ClmsDocumentResponse doc = clmsFileService.uploadDocument(id, file, displayName, actor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(doc, "Document uploaded successfully"));
    }

    @Operation(summary = "Download a document attached to a CLMS case — streams file with Content-Disposition: attachment")
    @GetMapping("/{id}/documents/{docId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable UUID id,
            @PathVariable UUID docId,
            Authentication auth,
            HttpServletRequest request
    ) {
        Resource resource = clmsFileService.downloadDocument(id, docId);
        String filename = clmsFileService.getOriginalFilename(id, docId);

        UserEntity actor = resolveActor(auth);
        auditService.log(actor, AuditAction.EXPORT, AuditModule.CLMS,
                docId.toString(), filename, request, null);

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
