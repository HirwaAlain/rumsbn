package rw.rura.rums.module.users.dto;

public record UserCreateResponse(
        UserResponse user,
        boolean inviteSent,
        String inviteMessage
) {}
