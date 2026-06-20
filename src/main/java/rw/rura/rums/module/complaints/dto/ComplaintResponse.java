package rw.rura.rums.module.complaints.dto;

import rw.rura.rums.enums.ComplaintCategory;
import rw.rura.rums.enums.ComplaintSeverity;
import rw.rura.rums.enums.ComplaintStatus;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.complaints.entity.Complaint;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record ComplaintResponse(
        UUID id,
        String referenceNumber,
        String subject,
        ComplaintCategory category,
        String complainantName,
        String complainantPhone,
        String respondentOperator,
        Sector sector,
        Province province,
        ComplaintStatus status,
        ComplaintSeverity severity,
        String description,
        UserSummary assignedTo,
        Instant filedAt,
        Instant updatedAt,
        Instant resolvedAt
) {

    public static ComplaintResponse fromEntity(Complaint c) {
        return new ComplaintResponse(
                c.getId(),
                c.getReferenceNumber(),
                c.getSubject(),
                c.getCategory(),
                c.getComplainantName(),
                c.getComplainantPhone(),
                c.getRespondentOperator(),
                c.getSector(),
                c.getProvince(),
                c.getStatus(),
                c.getSeverity(),
                c.getDescription(),
                UserSummary.fromEntity(c.getAssignedTo()),
                c.getFiledAt(),
                c.getUpdatedAt(),
                c.getResolvedAt()
        );
    }
}
