package rw.rura.rums.module.complaints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.ComplaintCategory;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

public record ComplaintCreateRequest(
        @NotBlank String subject,
        @NotNull ComplaintCategory category,
        @NotBlank String complainantName,
        String complainantPhone,
        @NotBlank String respondentOperator,
        @NotNull Sector sector,
        @NotNull Province province,
        @NotBlank String description
) {}
