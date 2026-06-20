package rw.rura.rums.module.reports.dto;

import rw.rura.rums.enums.ReportFormat;
import rw.rura.rums.enums.ReportStatus;
import rw.rura.rums.enums.ReportType;
import rw.rura.rums.module.reports.entity.Report;
import rw.rura.rums.module.users.dto.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        String title,
        ReportType type,
        String sector,
        ReportStatus status,
        ReportFormat format,
        UserSummary createdBy,
        Instant createdAt,
        Instant publishedAt,
        String period,
        Integer sizeKb,
        String downloadUrl
) {
    public static ReportResponse fromEntity(Report r) {
        String downloadUrl = r.getStoredPath() != null
                ? "/api/reports/" + r.getId() + "/download"
                : null;
        return new ReportResponse(
                r.getId(),
                r.getTitle(),
                r.getType(),
                r.getSector(),
                r.getStatus(),
                r.getFormat(),
                UserSummary.fromEntity(r.getCreatedBy()),
                r.getCreatedAt(),
                r.getPublishedAt(),
                r.getPeriod(),
                r.getSizeKb(),
                downloadUrl
        );
    }
}
