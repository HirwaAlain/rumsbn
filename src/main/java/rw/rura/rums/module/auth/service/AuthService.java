package rw.rura.rums.module.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.exception.ForbiddenException;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.auth.dto.*;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.repository.UserRepository;
import rw.rura.rums.security.JwtUtil;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRevocationService tokenRevocationService;
    private final AuditService auditService;

    @Value("${rums.jwt.access-expiry-seconds}")
    private long accessExpirySeconds;

    public LoginResponse login(LoginRequest req, HttpServletRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        UserEntity user = findByEmail(req.email());
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        auditService.log(user, AuditAction.LOGIN, AuditModule.SYSTEM,
                user.getId().toString(), user.getName(), request, null);

        return new LoginResponse(
                accessToken,
                refreshToken,
                accessExpirySeconds,
                LoginResponse.AuthUserInfo.fromEntity(user)
        );
    }

    public LoginResponse refresh(RefreshRequest req) {
        String refreshToken = req.refreshToken();

        // Validate signature and expiry
        String username;
        String jti;
        try {
            username = jwtUtil.extractUsername(refreshToken);
            jti = jwtUtil.extractJti(refreshToken);
            if (jwtUtil.isTokenExpired(refreshToken)) {
                throw new ForbiddenException("Refresh token has expired");
            }
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            throw new ForbiddenException("Invalid refresh token");
        }

        if (tokenRevocationService.isRevoked(jti)) {
            throw new ForbiddenException("Refresh token has been revoked");
        }

        // Rotate: revoke old refresh token and issue a new pair
        tokenRevocationService.revoke(jti);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        UserEntity user = findByEmail(username);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                accessExpirySeconds,
                LoginResponse.AuthUserInfo.fromEntity(user)
        );
    }

    public void logout(UserEntity actor, HttpServletRequest request) {
        // Revoke the access token presented with this request
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            try {
                String jti = jwtUtil.extractJti(accessToken);
                if (jti != null) {
                    tokenRevocationService.revoke(jti);
                }
            } catch (Exception ignored) {
                // Token already invalid — nothing to revoke
            }
        }

        auditService.log(actor, AuditAction.LOGOUT, AuditModule.SYSTEM,
                actor.getId().toString(), actor.getName(), request, null);
    }

    public void changePassword(UserEntity actor, ChangePasswordRequest req, HttpServletRequest request) {
        if (!passwordEncoder.matches(req.currentPassword(), actor.getPasswordHash())) {
            throw new ForbiddenException("Current password is incorrect");
        }

        actor.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(actor);

        auditService.log(actor, AuditAction.PASSWORD_RESET, AuditModule.SYSTEM,
                actor.getId().toString(), actor.getName(), request,
                null);
    }

    // -------------------------------------------------------------------------

    private UserEntity findByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
