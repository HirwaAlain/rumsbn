package rw.rura.rums.module.reports.dto;

import rw.rura.rums.enums.ReportFormat;
import rw.rura.rums.enums.ReportType;

public record ReportUpdateRequest(
        String title,
        ReportType type,
        String sector,
        ReportFormat format,
        String period
) {}
