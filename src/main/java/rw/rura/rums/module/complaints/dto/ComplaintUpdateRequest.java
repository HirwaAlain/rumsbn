package rw.rura.rums.module.complaints.dto;

import rw.rura.rums.enums.ComplaintCategory;
import rw.rura.rums.enums.ComplaintSeverity;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

public record ComplaintUpdateRequest(
        String subject,
        ComplaintCategory category,
        String complainantName,
        String complainantPhone,
        String respondentOperator,
        Sector sector,
        Province province,
        ComplaintSeverity severity,
        String description
) {}
