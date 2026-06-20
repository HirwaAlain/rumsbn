package rw.rura.rums.module.licenses.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.LicenseStatus;

public record LicenseStatusUpdateRequest(@NotNull LicenseStatus status) {}
