package rw.rura.rums.module.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.enums.UserRole;
import rw.rura.rums.enums.UserStatus;
import rw.rura.rums.exception.ForbiddenException;
import rw.rura.rums.module.users.dto.*;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.service.UserService;

import java.util.UUID;

@Tag(name = "Users", description = "Internal staff user management — creation, role assignment and account status")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "List users — filterable by role, status and free-text search (roles: admin, supervisor)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAll(role, status, search, pageable)));
    }

    @Operation(summary = "Get the currently authenticated user's profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getByEmail(auth.getName())));
    }

    @Operation(summary = "Get a user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    @Operation(summary = "Create a new user and send invite email with temporary password (role: admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserCreateResponse>> create(
            @Valid @RequestBody UserCreateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        UserCreateResponse response = userService.create(req, actor, request);
        String message = response.inviteSent()
                ? "User created. " + response.inviteMessage()
                : "User created but invite could not be delivered. " + response.inviteMessage();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, message));
    }

    @Operation(summary = "Update a user profile — admins may update anyone; other roles may only update their own name and phone")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);

        // Non-admins may only update their own profile
        if (actor.getRole() != UserRole.ADMIN && !actor.getId().equals(id)) {
            throw new ForbiddenException("You can only update your own profile");
        }

        return ResponseEntity.ok(ApiResponse.ok(userService.update(id, req, actor, request)));
    }

    @Operation(summary = "Update a user's account status — active / inactive / suspended; suspension raises an alert (role: admin only)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(userService.updateStatus(id, req, actor, request)));
    }

    @Operation(summary = "Change a user's role — logs permission_change in audit trail (role: admin only)")
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        return ResponseEntity.ok(ApiResponse.ok(userService.updateRole(id, req, actor, request)));
    }

    @Operation(summary = "Soft-delete a user — sets deleted_at and status=inactive; cannot delete own account (role: admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            Authentication auth,
            HttpServletRequest request
    ) {
        UserEntity actor = resolveActor(auth);
        userService.softDelete(id, actor, request);
        return ResponseEntity.ok(ApiResponse.ok(null, "User deleted successfully"));
    }

    // -------------------------------------------------------------------------

    private UserEntity resolveActor(Authentication auth) {
        return userService.findEntityByEmail(auth.getName());
    }
}
