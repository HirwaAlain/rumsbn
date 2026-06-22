package rw.rura.rums.module.users.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.alert.AlertService;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.*;
import rw.rura.rums.exception.ConflictException;
import rw.rura.rums.exception.ForbiddenException;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.users.dto.*;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.repository.UserRepository;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final AlertService alertService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(UserRole role, UserStatus status, String search, Pageable pageable) {
        Specification<UserEntity> spec = buildFilterSpec(role, status, search);
        return userRepository.findAllByDeletedAtIsNull(spec, pageable)
                .map(UserResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return UserResponse.fromEntity(findActiveById(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        UserEntity user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return UserResponse.fromEntity(user);
    }

    public UserCreateResponse create(UserCreateRequest req, UserEntity actor, HttpServletRequest request) {
        String rumsEmail = generateRumsEmail(req.name());
        String tempPassword = generateTempPassword();

        UserEntity user = UserEntity.builder()
                .name(req.name())
                .email(rumsEmail)
                .phone(req.phone())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .role(req.role())
                .status(UserStatus.ACTIVE)
                .department(req.department())
                .mfaEnabled(false)
                .build();

        userRepository.save(user);

        auditService.log(actor, AuditAction.CREATE, AuditModule.USERS,
                user.getId().toString(), user.getName(), request, null);

        // Send invite email AFTER the transaction commits so the user row is
        // guaranteed to exist in the DB before credentials arrive in the inbox.
        final String contactEmail  = req.contactEmail();
        final String displayName   = req.name();
        final String loginEmail    = rumsEmail;
        final String pw            = tempPassword;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                emailService.sendInvite(contactEmail, displayName, loginEmail, pw);
            }
        });

        return new UserCreateResponse(UserResponse.fromEntity(user),
                true, "Invite will be delivered to " + req.contactEmail());
    }

    public UserResponse update(UUID id, UserUpdateRequest req, UserEntity actor, HttpServletRequest request) {
        UserEntity user = findActiveById(id);

        Map<String, ChangeDto> changes = new java.util.LinkedHashMap<>();
        if (req.name() != null && !req.name().equals(user.getName())) {
            changes.put("name", new ChangeDto(user.getName(), req.name()));
            user.setName(req.name());
        }
        if (req.phone() != null && !req.phone().equals(user.getPhone())) {
            changes.put("phone", new ChangeDto(user.getPhone(), req.phone()));
            user.setPhone(req.phone());
        }

        userRepository.save(user);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.USERS,
                user.getId().toString(), user.getName(), request,
                changes.isEmpty() ? null : changes);

        return UserResponse.fromEntity(user);
    }

    public UserResponse updateStatus(UUID id, StatusUpdateRequest req, UserEntity actor, HttpServletRequest request) {
        UserEntity user = findActiveById(id);

        UserStatus before = user.getStatus();
        user.setStatus(req.status());
        userRepository.save(user);

        AuditAction action = switch (req.status()) {
            case SUSPENDED -> AuditAction.SUSPEND;
            case ACTIVE -> AuditAction.REINSTATE;
            default -> AuditAction.UPDATE;
        };

        auditService.log(actor, action, AuditModule.USERS,
                user.getId().toString(), user.getName(), request,
                Map.of("status", new ChangeDto(before.getValue(), req.status().getValue())));

        if (req.status() == UserStatus.SUSPENDED) {
            alertService.createAlert(
                    AlertType.USER_SUSPENDED,
                    "User suspended: " + user.getName(),
                    "User account " + user.getEmail() + " has been suspended.",
                    AlertSeverity.WARNING,
                    AuditModule.USERS,
                    user.getId().toString()
            );
        }

        return UserResponse.fromEntity(user);
    }

    public UserResponse updateRole(UUID id, RoleUpdateRequest req, UserEntity actor, HttpServletRequest request) {
        UserEntity user = findActiveById(id);

        UserRole before = user.getRole();
        user.setRole(req.role());
        userRepository.save(user);

        auditService.log(actor, AuditAction.PERMISSION_CHANGE, AuditModule.USERS,
                user.getId().toString(), user.getName(), request,
                Map.of("role", new ChangeDto(before.getValue(), req.role().getValue())));

        return UserResponse.fromEntity(user);
    }

    public void softDelete(UUID id, UserEntity actor, HttpServletRequest request) {
        if (actor.getId().equals(id)) {
            throw new ForbiddenException("Cannot delete your own account");
        }

        UserEntity user = findActiveById(id);
        user.setDeletedAt(Instant.now());
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        auditService.log(actor, AuditAction.DELETE, AuditModule.USERS,
                user.getId().toString(), user.getName(), request, null);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    public UserEntity findEntityById(UUID id) {
        return findActiveById(id);
    }

    public UserEntity findEntityByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private UserEntity findActiveById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User with ID " + id + " not found");
        }
        return user;
    }

    private Specification<UserEntity> buildFilterSpec(UserRole role, UserStatus status, String search) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                ));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private String generateTempPassword() {
        // Ensure at least 1 uppercase, 1 digit, 1 special char, total 12 chars
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = TEMP_PASSWORD_CHARS;

        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt(RANDOM.nextInt(upper.length())));
        sb.append(digits.charAt(RANDOM.nextInt(digits.length())));
        sb.append(special.charAt(RANDOM.nextInt(special.length())));
        for (int i = 3; i < 12; i++) {
            sb.append(all.charAt(RANDOM.nextInt(all.length())));
        }
        // Shuffle
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }

    private String generateRumsEmail(String name) {
        String[] parts = name.trim().split("\\s+");
        // First char of first name (handles hyphenated: "Jean-Claude" → 'j')
        String firstPart = parts[0].toLowerCase().replaceAll("[^a-z]", "");
        // Last word as surname
        String lastPart = parts[parts.length - 1].toLowerCase().replaceAll("[^a-z]", "");

        String base = firstPart.charAt(0) + "." + lastPart;
        String candidate = base + "@rura.rw";

        if (!userRepository.existsByEmail(candidate)) {
            return candidate;
        }
        int suffix = 2;
        while (true) {
            candidate = base + suffix + "@rura.rw";
            if (!userRepository.existsByEmail(candidate)) {
                return candidate;
            }
            suffix++;
        }
    }

}
