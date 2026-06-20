package rw.rura.rums.module.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.rura.rums.enums.UserDepartment;
import rw.rura.rums.enums.UserRole;

public record UserCreateRequest(
        @NotBlank String name,
        @NotBlank @Email String contactEmail,   // personal/work email — invite is sent here
        String phone,
        @NotNull UserRole role,
        @NotNull UserDepartment department
) {}
