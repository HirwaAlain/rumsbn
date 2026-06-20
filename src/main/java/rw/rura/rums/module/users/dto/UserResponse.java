package rw.rura.rums.module.users.dto;

import rw.rura.rums.enums.UserDepartment;
import rw.rura.rums.enums.UserRole;
import rw.rura.rums.enums.UserStatus;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        UserRole role,
        UserStatus status,
        UserDepartment department,
        boolean mfaEnabled,
        Instant lastLogin,
        Instant createdAt
) {

    public static UserResponse fromEntity(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getDepartment(),
                user.isMfaEnabled(),
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }
}
