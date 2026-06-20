package rw.rura.rums.module.clms.dto;

import rw.rura.rums.module.clms.entity.ClmsDocument;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record ClmsDocumentResponse(
        UUID id,
        String name,
        Instant uploadedAt,
        UserSummary uploadedBy,
        int sizeKb,
        String downloadUrl
) {
    public static ClmsDocumentResponse fromEntity(ClmsDocument doc) {
        return new ClmsDocumentResponse(
                doc.getId(),
                doc.getName(),
                doc.getUploadedAt(),
                UserSummary.fromEntity(doc.getUploadedBy()),
                doc.getSizeKb(),
                "/api/clms/" + doc.getClmsCase().getId() + "/documents/" + doc.getId() + "/download"
        );
    }
}
