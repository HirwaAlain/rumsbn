package rw.rura.rums.module.fraud.service;

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
import rw.rura.rums.module.fraud.dto.*;
import rw.rura.rums.module.fraud.entity.FraudCase;
import rw.rura.rums.module.fraud.repository.FraudCaseRepository;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FraudService {

    private final FraudCaseRepository fraudCaseRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final AlertService alertService;

    @Transactional(readOnly = true)
    public Page<FraudCaseResponse> getAll(
            FraudRiskLevel riskLevel, FraudCaseStatus status, Sector sector,
            String search, Pageable pageable) {
        return fraudCaseRepository.findAll(buildSpec(riskLevel, status, sector, search), pageable)
                .map(FraudCaseResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public FraudCaseResponse getById(UUID id) {
        return FraudCaseResponse.fromEntity(find(id));
    }

    public FraudCaseResponse create(FraudCaseCreateRequest req,
                                    UserEntity actor, HttpServletRequest request) {
        FraudCase fraudCase = new FraudCase();
        fraudCase.setCaseNumber(generateCaseNumber());
        fraudCase.setDescription(req.description());
        fraudCase.setIndicatorType(req.indicatorType());
        fraudCase.setReportedBy(req.reportedBy());
        fraudCase.setOperatorInvolved(req.operatorInvolved());
        fraudCase.setSector(req.sector());
        fraudCase.setRiskLevel(req.riskLevel());
        fraudCase.setStatus(FraudCaseStatus.OPEN);
        fraudCase.setReportedAt(req.reportedAt());
        fraudCase.setEstimatedLossRwf(req.estimatedLossRwf());

        if (req.investigatingOfficerId() != null) {
            fraudCase.setInvestigatingOfficer(findUser(req.investigatingOfficerId()));
        }

        fraudCaseRepository.save(fraudCase);

        if (req.riskLevel() == FraudRiskLevel.CRITICAL) {
            alertService.createAlert(
                    AlertType.FRAUD_DETECTED,
                    "Critical Fraud Case: " + fraudCase.getCaseNumber(),
                    "A critical fraud case has been filed involving " + fraudCase.getOperatorInvolved()
                            + " — " + fraudCase.getIndicatorType().getValue(),
                    AlertSeverity.CRITICAL,
                    AuditModule.FRAUD,
                    fraudCase.getId().toString()
            );
        }

        auditService.log(actor, AuditAction.CREATE, AuditModule.FRAUD,
                fraudCase.getId().toString(), fraudCase.getCaseNumber(), request, null);

        return FraudCaseResponse.fromEntity(fraudCase);
    }

    public FraudCaseResponse update(UUID id, FraudCaseUpdateRequest req,
                                    UserEntity actor, HttpServletRequest request) {
        FraudCase fraudCase = find(id);
        Map<String, ChangeDto> changes = new LinkedHashMap<>();

        if (req.description() != null && !req.description().equals(fraudCase.getDescription())) {
            changes.put("description", new ChangeDto(fraudCase.getDescription(), req.description()));
            fraudCase.setDescription(req.description());
        }
        if (req.indicatorType() != null && req.indicatorType() != fraudCase.getIndicatorType()) {
            changes.put("indicatorType", new ChangeDto(
                    fraudCase.getIndicatorType().getValue(), req.indicatorType().getValue()));
            fraudCase.setIndicatorType(req.indicatorType());
        }
        if (req.reportedBy() != null && !req.reportedBy().equals(fraudCase.getReportedBy())) {
            changes.put("reportedBy", new ChangeDto(fraudCase.getReportedBy(), req.reportedBy()));
            fraudCase.setReportedBy(req.reportedBy());
        }
        if (req.operatorInvolved() != null && !req.operatorInvolved().equals(fraudCase.getOperatorInvolved())) {
            changes.put("operatorInvolved", new ChangeDto(fraudCase.getOperatorInvolved(), req.operatorInvolved()));
            fraudCase.setOperatorInvolved(req.operatorInvolved());
        }
        if (req.sector() != null && req.sector() != fraudCase.getSector()) {
            changes.put("sector", new ChangeDto(fraudCase.getSector().getValue(), req.sector().getValue()));
            fraudCase.setSector(req.sector());
        }
        if (req.riskLevel() != null && req.riskLevel() != fraudCase.getRiskLevel()) {
            changes.put("riskLevel", new ChangeDto(fraudCase.getRiskLevel().getValue(), req.riskLevel().getValue()));
            fraudCase.setRiskLevel(req.riskLevel());
        }
        if (req.reportedAt() != null && !req.reportedAt().equals(fraudCase.getReportedAt())) {
            changes.put("reportedAt", new ChangeDto(
                    fraudCase.getReportedAt().toString(), req.reportedAt().toString()));
            fraudCase.setReportedAt(req.reportedAt());
        }
        if (req.estimatedLossRwf() != null && req.estimatedLossRwf() != fraudCase.getEstimatedLossRwf()) {
            changes.put("estimatedLossRwf", new ChangeDto(
                    String.valueOf(fraudCase.getEstimatedLossRwf()), String.valueOf(req.estimatedLossRwf())));
            fraudCase.setEstimatedLossRwf(req.estimatedLossRwf());
        }
        if (req.investigatingOfficerId() != null) {
            UUID currentId = fraudCase.getInvestigatingOfficer() != null
                    ? fraudCase.getInvestigatingOfficer().getId() : null;
            if (!req.investigatingOfficerId().equals(currentId)) {
                fraudCase.setInvestigatingOfficer(findUser(req.investigatingOfficerId()));
            }
        }

        fraudCaseRepository.save(fraudCase);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.FRAUD,
                fraudCase.getId().toString(), fraudCase.getCaseNumber(), request,
                changes.isEmpty() ? null : changes);

        return FraudCaseResponse.fromEntity(fraudCase);
    }

    public FraudCaseResponse updateStatus(UUID id, FraudStatusUpdateRequest req,
                                          UserEntity actor, HttpServletRequest request) {
        FraudCase fraudCase = find(id);
        FraudCaseStatus before = fraudCase.getStatus();

        fraudCase.setStatus(req.status());
        fraudCaseRepository.save(fraudCase);

        AuditAction action = switch (req.status()) {
            case CONFIRMED    -> AuditAction.APPROVE;
            case DISMISSED    -> AuditAction.REJECT;
            default           -> AuditAction.UPDATE;
        };

        auditService.log(actor, action, AuditModule.FRAUD,
                fraudCase.getId().toString(), fraudCase.getCaseNumber(), request,
                Map.of("status", new ChangeDto(before.getValue(), req.status().getValue())));

        return FraudCaseResponse.fromEntity(fraudCase);
    }

    // -------------------------------------------------------------------------

    private FraudCase find(UUID id) {
        return fraudCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud case not found: " + id));
    }

    private UserEntity findUser(UUID id) {
        return userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private String generateCaseNumber() {
        int year = LocalDate.now().getYear();
        String pattern = "RURA-FRD-" + year + "-%";
        String prefix  = "RURA-FRD-" + year + "-";

        String max = fraudCaseRepository.findMaxCaseNumberByPattern(pattern);
        int next = 1;
        if (max != null) {
            try {
                next = Integer.parseInt(max.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
                // Malformed stored value — start at 1
            }
        }
        return String.format("%s%04d", prefix, next);
    }

    private Specification<FraudCase> buildSpec(
            FraudRiskLevel riskLevel, FraudCaseStatus status, Sector sector, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (riskLevel != null) predicates.add(cb.equal(root.get("riskLevel"), riskLevel));
            if (status != null)    predicates.add(cb.equal(root.get("status"), status));
            if (sector != null)    predicates.add(cb.equal(root.get("sector"), sector));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("caseNumber")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("operatorInvolved")), pattern),
                        cb.like(cb.lower(root.get("reportedBy")), pattern)
                ));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
