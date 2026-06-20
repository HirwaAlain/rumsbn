package rw.rura.rums.module.clms.dto;

import rw.rura.rums.enums.ClmsCaseStatus;
import rw.rura.rums.enums.ClmsCaseType;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.clms.entity.ClmsCase;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ClmsCaseResponse(
        UUID id,
        String caseNumber,
        String title,
        ClmsCaseType type,
        ClmsCaseStatus status,
        String applicantName,
        String applicantEmail,
        Sector sector,
        Province province,
        Instant submittedAt,
        Instant updatedAt,
        UserSummary assignedTo,
        List<ClmsDocumentResponse> documents,
        String notes
) {
    public static ClmsCaseResponse fromEntity(ClmsCase c) {
        return new ClmsCaseResponse(
                c.getId(),
                c.getCaseNumber(),
                c.getTitle(),
                c.getType(),
                c.getStatus(),
                c.getApplicantName(),
                c.getApplicantEmail(),
                c.getSector(),
                c.getProvince(),
                c.getSubmittedAt(),
                c.getUpdatedAt(),
                UserSummary.fromEntity(c.getAssignedTo()),
                c.getDocuments().stream().map(ClmsDocumentResponse::fromEntity).toList(),
                c.getNotes()
        );
    }
}
