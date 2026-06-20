package rw.rura.rums.module.users.dto;

import rw.rura.rums.module.users.entity.UserEntity;

import java.util.UUID;

public record UserSummary(UUID id, String name) {

    public static UserSummary fromEntity(UserEntity user) {
        if (user == null) return null;
        return new UserSummary(user.getId(), user.getName());
    }
}
