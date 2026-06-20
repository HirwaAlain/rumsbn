package rw.rura.rums.module.clms.dto;

import rw.rura.rums.enums.ClmsCaseType;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

import java.util.UUID;

public record ClmsCaseUpdateRequest(
        String title,
        ClmsCaseType type,
        String applicantName,
        String applicantEmail,
        Sector sector,
        Province province,
        UUID assignedToId,
        String notes
) {}
