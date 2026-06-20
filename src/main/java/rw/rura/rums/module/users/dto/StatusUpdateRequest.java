package rw.rura.rums.module.users.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.UserStatus;

public record StatusUpdateRequest(@NotNull UserStatus status) {}
