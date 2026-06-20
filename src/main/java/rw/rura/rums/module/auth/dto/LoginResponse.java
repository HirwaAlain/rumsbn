package rw.rura.rums.module.auth.dto;

import rw.rura.rums.enums.UserDepartment;
import rw.rura.rums.enums.UserRole;
import rw.rura.rums.module.users.entity.UserEntity;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        AuthUserInfo user
) {

    public record AuthUserInfo(
            UUID id,
            String name,
            UserRole role,
            UserDepartment department
    ) {
        public static AuthUserInfo fromEntity(UserEntity user) {
            return new AuthUserInfo(
                    user.getId(),
                    user.getName(),
                    user.getRole(),
                    user.getDepartment()
            );
        }
    }
}
