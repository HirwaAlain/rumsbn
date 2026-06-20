package rw.rura.rums.module.compliance.service;

import jakarta.persistence.criteria.Predicate;
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
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.compliance.dto.*;
import rw.rura.rums.module.compliance.entity.ComplianceRecord;
import rw.rura.rums.module.compliance.repository.ComplianceRepository;
import rw.rura.rums.module.licenses.entity.License;
import rw.rura.rums.module.licenses.repository.LicenseRepository;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceService {

    private static final Map<String, String> STATUS_COLORS = Map.of(
            "compliant",     "#10B981",
            "under_review",  "#F59E0B",
            "non_compliant", "#EF4444",
            "remediation",   "#6366f1"
    );

    private static final Map<String, String> STATUS_LABELS = Map.of(
            "compliant",     "Compliant",
            "under_review",  "Under Review",
            "non_compliant", "Non-Compliant",
            "remediation",   "Remediation"
    );

    private final ComplianceRepository complianceRepository;
    private final LicenseRepository licenseRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final AlertService alertService;

    @Transactional(readOnly = true)
    public Page<ComplianceRecordResponse> getAll(
            ComplianceStatus status, Sector sector, String search, Pageable pageable) {
        return complianceRepository.findAll(buildSpec(status, sector, search), pageable)
                .map(ComplianceRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ComplianceRecordResponse getById(UUID id) {
        return ComplianceRecordResponse.fromEntity(find(id));
    }

    public ComplianceRecordResponse create(ComplianceCreateRequest req,
                                           UserEntity actor, HttpServletRequest request) {
        ComplianceRecord record = new ComplianceRecord();
        record.setOperatorName(req.operatorName());
        record.setSector(req.sector());
        record.setCheckType(req.checkType());
        record.setStatus(ComplianceStatus.UNDER_REVIEW);
        record.setDueDate(req.dueDate());

        if (req.licenseId() != null) {
            record.setLicense(findLicense(req.licenseId()));
        }
        if (req.auditorId() != null) {
            record.setAuditor(findUser(req.auditorId()));
        }

        complianceRepository.save(record);

        auditService.log(actor, AuditAction.CREATE, AuditModule.COMPLIANCE,
                record.getId().toString(), record.getOperatorName(), request, null);

        return ComplianceRecordResponse.fromEntity(record);
    }

    public ComplianceRecordResponse update(UUID id, ComplianceUpdateRequest req,
                                           UserEntity actor, HttpServletRequest request) {
        ComplianceRecord record = find(id);
        Map<String, ChangeDto> changes = new LinkedHashMap<>();

        if (req.operatorName() != null && !req.operatorName().equals(record.getOperatorName())) {
            changes.put("operatorName", new ChangeDto(record.getOperatorName(), req.operatorName()));
            record.setOperatorName(req.operatorName());
        }
        if (req.sector() != null && req.sector() != record.getSector()) {
            changes.put("sector", new ChangeDto(record.getSector().getValue(), req.sector().getValue()));
            record.setSector(req.sector());
        }
        if (req.checkType() != null && req.checkType() != record.getCheckType()) {
            changes.put("checkType", new ChangeDto(record.getCheckType().getValue(), req.checkType().getValue()));
            record.setCheckType(req.checkType());
        }
        if (req.dueDate() != null && !req.dueDate().equals(record.getDueDate())) {
            changes.put("dueDate", new ChangeDto(
                    record.getDueDate() != null ? record.getDueDate().toString() : null,
                    req.dueDate().toString()));
            record.setDueDate(req.dueDate());
        }
        if (req.lastAuditDate() != null && !req.lastAuditDate().equals(record.getLastAuditDate())) {
            changes.put("lastAuditDate", new ChangeDto(
                    record.getLastAuditDate() != null ? record.getLastAuditDate().toString() : null,
                    req.lastAuditDate().toString()));
            record.setLastAuditDate(req.lastAuditDate());
        }
        if (req.score() != null && !req.score().equals(record.getScore())) {
            changes.put("score", new ChangeDto(
                    record.getScore() != null ? record.getScore().toString() : null,
                    req.score().toString()));
            record.setScore(req.score());
        }
        if (req.findings() != null && !req.findings().equals(record.getFindings())) {
            changes.put("findings", new ChangeDto(record.getFindings(), req.findings()));
            record.setFindings(req.findings());
        }
        if (req.licenseId() != null) {
            UUID currentLicId = record.getLicense() != null ? record.getLicense().getId() : null;
            if (!req.licenseId().equals(currentLicId)) {
                changes.put("licenseId", new ChangeDto(
                        currentLicId != null ? currentLicId.toString() : null,
                        req.licenseId().toString()));
                record.setLicense(findLicense(req.licenseId()));
            }
        }
        if (req.auditorId() != null) {
            UUID currentAudId = record.getAuditor() != null ? record.getAuditor().getId() : null;
            if (!req.auditorId().equals(currentAudId)) {
                record.setAuditor(findUser(req.auditorId()));
            }
        }

        // Status change — handle alert trigger
        if (req.status() != null && req.status() != record.getStatus()) {
            changes.put("status", new ChangeDto(record.getStatus().getValue(), req.status().getValue()));
            record.setStatus(req.status());

            if (req.status() == ComplianceStatus.NON_COMPLIANT) {
                alertService.createAlert(
                        AlertType.COMPLIANCE_BREACH,
                        "Compliance Breach: " + record.getOperatorName(),
                        record.getOperatorName() + " has been marked non-compliant for "
                                + record.getCheckType().getValue(),
                        AlertSeverity.WARNING,
                        AuditModule.COMPLIANCE,
                        record.getId().toString()
                );
            }
        }

        complianceRepository.save(record);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.COMPLIANCE,
                record.getId().toString(), record.getOperatorName(), request,
                changes.isEmpty() ? null : changes);

        return ComplianceRecordResponse.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public List<ComplianceOverviewItem> getOverview() {
        List<Object[]> rows = complianceRepository.countByStatus();
        return rows.stream()
                .map(row -> {
                    String statusKey = ((String) row[0]).toLowerCase();
                    long count = ((Number) row[1]).longValue();
                    String label = STATUS_LABELS.getOrDefault(statusKey, statusKey);
                    String color = STATUS_COLORS.getOrDefault(statusKey, "#6B7280");
                    return new ComplianceOverviewItem(label, count, color);
                })
                .toList();
    }

    // -------------------------------------------------------------------------

    private ComplianceRecord find(UUID id) {
        return complianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance record not found: " + id));
    }

    private License findLicense(UUID id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("License not found: " + id));
    }

    private UserEntity findUser(UUID id) {
        return userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private Specification<ComplianceRecord> buildSpec(
            ComplianceStatus status, Sector sector, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (sector != null) predicates.add(cb.equal(root.get("sector"), sector));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                // Match operator name via ILIKE; for checkType match enum constants whose
                // display value contains the search term and add an IN predicate.
                List<ComplianceCheckType> matchingTypes = java.util.Arrays.stream(ComplianceCheckType.values())
                        .filter(t -> t.getValue().toLowerCase().contains(search.toLowerCase()))
                        .toList();
                List<Predicate> searchOr = new ArrayList<>();
                searchOr.add(cb.like(cb.lower(root.get("operatorName")), pattern));
                if (!matchingTypes.isEmpty()) {
                    searchOr.add(root.get("checkType").in(matchingTypes));
                }
                predicates.add(cb.or(searchOr.toArray(new Predicate[0])));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
