package rw.rura.rums.module.reports.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rw.rura.rums.config.FileStorageConfig;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.reports.entity.Report;
import rw.rura.rums.module.reports.repository.ReportRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportFileService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv",
            "image/png",
            "image/jpeg"
    );

    private static final long MAX_SIZE_BYTES = 20L * 1024 * 1024;

    private final FileStorageConfig fileStorageConfig;
    private final ReportRepository reportRepository;

    public Report uploadFile(UUID reportId, MultipartFile file) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String sanitised = sanitise(originalFilename != null ? originalFilename : "file");
        String storedFilename = UUID.randomUUID() + "_" + sanitised;

        Path dest = Paths.get(fileStorageConfig.getUploadDir(), "reports", storedFilename);
        try {
            Files.copy(file.getInputStream(), dest);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }

        report.setStoredPath(dest.toString());
        report.setSizeKb((int) Math.max(1, file.getSize() / 1024));
        return reportRepository.save(report);
    }

    public Resource downloadFile(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        if (report.getStoredPath() == null) {
            throw new ResourceNotFoundException("No file attached to report " + reportId);
        }

        Path path = Paths.get(report.getStoredPath());
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found on server for report " + reportId);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Invalid file path for report " + reportId);
        }
    }

    public String getFilename(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));
        if (report.getStoredPath() == null) {
            return "report";
        }
        return Paths.get(report.getStoredPath()).getFileName().toString();
    }

    // -------------------------------------------------------------------------

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new FileTooLargeException("File size exceeds the 20 MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new UnsupportedFileTypeException(
                    "File type '" + contentType + "' is not allowed. "
                            + "Allowed types: PDF, XLSX, CSV, PNG, JPEG");
        }
    }

    private String sanitise(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }

    // -------------------------------------------------------------------------

    public static class FileTooLargeException extends RuntimeException {
        public FileTooLargeException(String message) { super(message); }
    }

    public static class UnsupportedFileTypeException extends RuntimeException {
        public UnsupportedFileTypeException(String message) { super(message); }
    }
}
