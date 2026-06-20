package rw.rura.rums.module.reports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.ReportFormat;
import rw.rura.rums.enums.ReportType;

public record ReportCreateRequest(
        @NotBlank String title,
        @NotNull ReportType type,
        @NotBlank String sector,
        @NotNull ReportFormat format,
        @NotBlank String period
) {}
