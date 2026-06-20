package rw.rura.rums.module.users.dto;

import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.UserRole;

public record RoleUpdateRequest(@NotNull UserRole role) {}
