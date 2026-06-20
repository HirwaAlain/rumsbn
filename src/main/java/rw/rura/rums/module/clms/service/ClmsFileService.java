package rw.rura.rums.module.clms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rw.rura.rums.config.FileStorageConfig;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.clms.dto.ClmsDocumentResponse;
import rw.rura.rums.module.clms.entity.ClmsCase;
import rw.rura.rums.module.clms.entity.ClmsDocument;
import rw.rura.rums.module.clms.repository.ClmsCaseRepository;
import rw.rura.rums.module.clms.repository.ClmsDocumentRepository;
import rw.rura.rums.module.users.entity.UserEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClmsFileService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv",
            "image/png",
            "image/jpeg"
    );

    private static final long MAX_SIZE_BYTES = 20L * 1024 * 1024;

    private final FileStorageConfig fileStorageConfig;
    private final ClmsCaseRepository clmsCaseRepository;
    private final ClmsDocumentRepository clmsDocumentRepository;

    public ClmsDocumentResponse uploadDocument(
            UUID caseId, MultipartFile file, String displayName, UserEntity uploader) {

        ClmsCase clmsCase = clmsCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("CLMS case not found: " + caseId));

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String sanitised = sanitise(originalFilename != null ? originalFilename : "file");
        String storedFilename = UUID.randomUUID() + "_" + sanitised;

        Path dest = Paths.get(fileStorageConfig.getUploadDir(), "clms-documents", storedFilename);
        try {
            Files.copy(file.getInputStream(), dest);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }

        int sizeKb = (int) Math.max(1, file.getSize() / 1024);
        String name = (displayName != null && !displayName.isBlank()) ? displayName : originalFilename;

        ClmsDocument doc = new ClmsDocument();
        doc.setClmsCase(clmsCase);
        doc.setName(name);
        doc.setStoredPath(dest.toString());
        doc.setSizeKb(sizeKb);
        doc.setUploadedBy(uploader);
        doc.setUploadedAt(Instant.now());

        clmsDocumentRepository.save(doc);

        return ClmsDocumentResponse.fromEntity(doc);
    }

    public Resource downloadDocument(UUID caseId, UUID docId) {
        ClmsDocument doc = clmsDocumentRepository.findByIdAndClmsCase_Id(docId, caseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document " + docId + " not found in case " + caseId));

        Path path = Paths.get(doc.getStoredPath());
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found on server for document " + docId);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Invalid file path for document " + docId);
        }
    }

    public String getOriginalFilename(UUID caseId, UUID docId) {
        return clmsDocumentRepository.findByIdAndClmsCase_Id(docId, caseId)
                .map(ClmsDocument::getName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document " + docId + " not found in case " + caseId));
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
        // Keep only alphanumeric chars, dots, hyphens and underscores
        return filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }

    // -------------------------------------------------------------------------
    // Inline exception classes — avoid creating extra files for file-only errors

    public static class FileTooLargeException extends RuntimeException {
        public FileTooLargeException(String message) { super(message); }
    }

    public static class UnsupportedFileTypeException extends RuntimeException {
        public UnsupportedFileTypeException(String message) { super(message); }
    }
}
