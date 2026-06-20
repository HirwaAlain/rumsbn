package rw.rura.rums.module.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.module.auth.dto.*;
import rw.rura.rums.module.auth.service.AuthService;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

@Tag(name = "Authentication", description = "Login, token refresh, logout and password management")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Login — obtain access and refresh tokens", security = {})
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req, request)));
    }

    @Operation(summary = "Refresh — exchange a refresh token for a new token pair", security = {})
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody RefreshRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(req)));
    }

    @Operation(summary = "Logout — revoke the current token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        authService.logout(actor, request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully"));
    }

    @Operation(summary = "Change password")
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        authService.changePassword(actor, req, request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully"));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
