package rw.rura.rums.module.clms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.ClmsCaseType;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

import java.util.UUID;

public record ClmsCaseCreateRequest(
        @NotBlank String title,
        @NotNull ClmsCaseType type,
        @NotBlank String applicantName,
        String applicantEmail,
        @NotNull Sector sector,
        @NotNull Province province,
        UUID assignedToId,
        String notes
) {}
