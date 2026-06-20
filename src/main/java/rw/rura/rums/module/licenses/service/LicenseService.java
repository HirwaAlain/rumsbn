package rw.rura.rums.module.licenses.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.alert.AlertService;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.*;
import rw.rura.rums.exception.ConflictException;
import rw.rura.rums.exception.ForbiddenException;
import rw.rura.rums.exception.InvalidStatusTransitionException;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.licenses.dto.*;
import rw.rura.rums.module.licenses.entity.License;
import rw.rura.rums.module.licenses.repository.LicenseRepository;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final AuditService auditService;
    private final AlertService alertService;

    @Transactional(readOnly = true)
    public Page<LicenseResponse> getAll(
            LicenseStatus status, Sector sector, Province province, String search, Pageable pageable) {
        Specification<License> spec = buildFilterSpec(status, sector, province, search);
        return licenseRepository.findAll(spec, pageable).map(LicenseResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public LicenseResponse getById(UUID id) {
        return LicenseResponse.fromEntity(findById(id));
    }

    public LicenseResponse create(LicenseCreateRequest req, UserEntity actor, HttpServletRequest request) {
        String licenseNumber = (req.licenseNumber() != null && !req.licenseNumber().isBlank())
                ? req.licenseNumber()
                : generateLicenseNumber(req.sector());

        if (licenseRepository.existsByLicenseNumber(licenseNumber)) {
            throw new ConflictException("License number already exists: " + licenseNumber);
        }

        License license = License.builder()
                .licenseNumber(licenseNumber)
                .operatorName(req.operatorName())
                .contactPerson(req.contactPerson())
                .contactEmail(req.contactEmail())
                .category(req.category())
                .sector(req.sector())
                .province(req.province())
                .status(LicenseStatus.PENDING)
                .issuedAt(req.issuedAt())
                .expiresAt(req.expiresAt())
                .annualFeeRwf(req.annualFeeRwf())
                .build();

        license = licenseRepository.save(license);

        auditService.log(actor, AuditAction.CREATE, AuditModule.LICENSES,
                license.getId().toString(), license.getLicenseNumber(), request, null);

        return LicenseResponse.fromEntity(license);
    }

    public LicenseResponse update(UUID id, LicenseUpdateRequest req, UserEntity actor, HttpServletRequest request) {
        License license = findById(id);
        Map<String, ChangeDto> changes = new LinkedHashMap<>();

        if (req.operatorName() != null && !req.operatorName().equals(license.getOperatorName())) {
            changes.put("operatorName", new ChangeDto(license.getOperatorName(), req.operatorName()));
            license.setOperatorName(req.operatorName());
        }
        if (req.contactPerson() != null && !req.contactPerson().equals(license.getContactPerson())) {
            changes.put("contactPerson", new ChangeDto(license.getContactPerson(), req.contactPerson()));
            license.setContactPerson(req.contactPerson());
        }
        if (req.contactEmail() != null && !req.contactEmail().equals(license.getContactEmail())) {
            changes.put("contactEmail", new ChangeDto(license.getContactEmail(), req.contactEmail()));
            license.setContactEmail(req.contactEmail());
        }
        if (req.category() != null && req.category() != license.getCategory()) {
            changes.put("category", new ChangeDto(license.getCategory().getValue(), req.category().getValue()));
            license.setCategory(req.category());
        }
        if (req.sector() != null && req.sector() != license.getSector()) {
            changes.put("sector", new ChangeDto(license.getSector().getValue(), req.sector().getValue()));
            license.setSector(req.sector());
        }
        if (req.province() != null && req.province() != license.getProvince()) {
            changes.put("province", new ChangeDto(license.getProvince().getValue(), req.province().getValue()));
            license.setProvince(req.province());
        }
        if (req.issuedAt() != null && !req.issuedAt().equals(license.getIssuedAt())) {
            changes.put("issuedAt", new ChangeDto(str(license.getIssuedAt()), str(req.issuedAt())));
            license.setIssuedAt(req.issuedAt());
        }
        if (req.expiresAt() != null && !req.expiresAt().equals(license.getExpiresAt())) {
            changes.put("expiresAt", new ChangeDto(str(license.getExpiresAt()), str(req.expiresAt())));
            license.setExpiresAt(req.expiresAt());
        }
        if (req.annualFeeRwf() != null && req.annualFeeRwf() != license.getAnnualFeeRwf()) {
            changes.put("annualFeeRwf", new ChangeDto(str(license.getAnnualFeeRwf()), str(req.annualFeeRwf())));
            license.setAnnualFeeRwf(req.annualFeeRwf());
        }
        if (req.lastRenewalAt() != null && !req.lastRenewalAt().equals(license.getLastRenewalAt())) {
            changes.put("lastRenewalAt", new ChangeDto(str(license.getLastRenewalAt()), str(req.lastRenewalAt())));
            license.setLastRenewalAt(req.lastRenewalAt());
        }

        licenseRepository.save(license);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.LICENSES,
                license.getId().toString(), license.getLicenseNumber(), request,
                changes.isEmpty() ? null : changes);

        return LicenseResponse.fromEntity(license);
    }

    public LicenseResponse updateStatus(
            UUID id, LicenseStatusUpdateRequest req, UserEntity actor, HttpServletRequest request) {

        License license = findById(id);
        LicenseStatus before = license.getStatus();
        LicenseStatus target = req.status();

        // Cannot transition out of revoked or rejected
        if (before == LicenseStatus.REVOKED || before == LicenseStatus.REJECTED) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status of a " + before.getValue() + " license (requested: " + target.getValue() + ")");
        }

        // Revoking requires admin role
        if (target == LicenseStatus.REVOKED && actor.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only admins can revoke a license");
        }

        // Approving (active) or suspending requires admin or supervisor
        if ((target == LicenseStatus.ACTIVE || target == LicenseStatus.SUSPENDED)
                && actor.getRole() != UserRole.ADMIN && actor.getRole() != UserRole.SUPERVISOR) {
            throw new ForbiddenException("Only admins and supervisors can approve or suspend a license");
        }

        license.setStatus(target);
        licenseRepository.save(license);

        AuditAction action = switch (target) {
            case ACTIVE -> AuditAction.APPROVE;
            case SUSPENDED -> AuditAction.SUSPEND;
            case REVOKED -> AuditAction.DELETE;
            case REJECTED -> AuditAction.REJECT;
            default -> AuditAction.UPDATE;
        };

        auditService.log(actor, action, AuditModule.LICENSES,
                license.getId().toString(), license.getLicenseNumber(), request,
                Map.of("status", new ChangeDto(before.getValue(), target.getValue())));

        if (target == LicenseStatus.SUSPENDED || target == LicenseStatus.REVOKED || target == LicenseStatus.REJECTED) {
            AlertSeverity severity = target == LicenseStatus.REVOKED
                    ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;
            String title = switch (target) {
                case REVOKED  -> "License revoked: " + license.getLicenseNumber();
                case REJECTED -> "License rejected: " + license.getLicenseNumber();
                default       -> "License suspended: " + license.getLicenseNumber();
            };
            String message = "License " + license.getLicenseNumber()
                    + " (" + license.getOperatorName() + ") status changed to " + target.getValue() + ".";
            alertService.createAlert(AlertType.LICENSE_EXPIRY, title, message,
                    severity, AuditModule.LICENSES, license.getId().toString());
        }

        return LicenseResponse.fromEntity(license);
    }

    @Transactional(readOnly = true)
    public List<LicenseTrendPoint> getTrend() {
        return licenseRepository.findMonthlyTrend()
                .stream()
                .map(LicenseTrendPoint::fromProjection)
                .toList();
    }

    // -------------------------------------------------------------------------

    private License findById(UUID id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("License with ID " + id + " not found"));
    }

    private Specification<License> buildFilterSpec(
            LicenseStatus status, Sector sector, Province province, String search) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (sector != null) {
                predicates.add(cb.equal(root.get("sector"), sector));
            }
            if (province != null) {
                predicates.add(cb.equal(root.get("province"), province));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("operatorName")), pattern),
                        cb.like(cb.lower(root.get("licenseNumber")), pattern)
                ));
            }

            return predicates.isEmpty() ? null
                    : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private String generateLicenseNumber(Sector sector) {
        String code = switch (sector) {
            case TELECOM   -> "TLC";
            case ENERGY    -> "ENG";
            case WATER     -> "WAT";
            case TRANSPORT -> "TRP";
        };
        int year = Year.now().getValue();
        String prefix = "RURA-" + code + "-" + year + "-";
        long count = licenseRepository.countByLicenseNumberPrefix(prefix);
        String candidate;
        do {
            count++;
            candidate = prefix + String.format("%04d", count);
        } while (licenseRepository.existsByLicenseNumber(candidate));
        return candidate;
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }
}
