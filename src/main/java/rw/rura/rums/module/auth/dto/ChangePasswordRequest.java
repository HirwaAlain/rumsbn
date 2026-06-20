package rw.rura.rums.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,

        @NotBlank
        @Size(min = 10, message = "Password must be at least 10 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
                message = "Password must contain at least 1 uppercase letter, 1 digit, and 1 special character"
        )
        String newPassword
) {}
