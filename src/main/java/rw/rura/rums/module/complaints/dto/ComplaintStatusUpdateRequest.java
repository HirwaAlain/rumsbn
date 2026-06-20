package rw.rura.rums.module.complaints.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.ComplaintStatus;

public record ComplaintStatusUpdateRequest(@NotNull ComplaintStatus status) {}
