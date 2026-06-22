package rw.rura.rums.module.backup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.rura.rums.dto.ApiResponse;

import java.io.IOException;

@Tag(name = "Backup", description = "Database backup — manual trigger and download (admin only)")
@RestController
@RequestMapping("/api/admin/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @Operation(summary = "Trigger an immediate full backup of all data (admin only)")
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BackupService.BackupResult>> trigger() throws IOException {
        BackupService.BackupResult result = backupService.triggerBackup();
        return ResponseEntity.ok(ApiResponse.ok(result, "Backup created successfully: " + result.filename()));
    }

    @Operation(summary = "Download the latest backup file (admin only)")
    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> download() throws IOException {
        BackupService.BackupResult result = backupService.triggerBackup();
        Resource resource = new FileSystemResource(result.filePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }
}
