package rw.rura.rums.module.clms.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.*;
import rw.rura.rums.exception.ForbiddenException;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.clms.dto.*;
import rw.rura.rums.module.clms.entity.ClmsCase;
import rw.rura.rums.module.clms.repository.ClmsCaseRepository;
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
public class ClmsCaseService {

    private final ClmsCaseRepository clmsCaseRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public Page<ClmsCaseResponse> getAll(
            ClmsCaseStatus status, ClmsCaseType type, Sector sector,
            String search, Pageable pageable) {
        return clmsCaseRepository.findAll(buildSpec(status, type, sector, search), pageable)
                .map(ClmsCaseResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ClmsCaseResponse getById(UUID id) {
        return ClmsCaseResponse.fromEntity(find(id));
    }

    public ClmsCaseResponse create(ClmsCaseCreateRequest req,
                                   UserEntity actor, HttpServletRequest request) {
        ClmsCase clmsCase = new ClmsCase();
        clmsCase.setCaseNumber(generateCaseNumber());
        clmsCase.setTitle(req.title());
        clmsCase.setType(req.type());
        clmsCase.setStatus(ClmsCaseStatus.DRAFT);
        clmsCase.setApplicantName(req.applicantName());
        clmsCase.setApplicantEmail(req.applicantEmail());
        clmsCase.setSector(req.sector());
        clmsCase.setProvince(req.province());
        clmsCase.setNotes(req.notes());

        if (req.assignedToId() != null) {
            clmsCase.setAssignedTo(findUser(req.assignedToId()));
        }

        clmsCaseRepository.save(clmsCase);

        auditService.log(actor, AuditAction.CREATE, AuditModule.CLMS,
                clmsCase.getId().toString(), clmsCase.getCaseNumber(), request, null);

        return ClmsCaseResponse.fromEntity(clmsCase);
    }

    public ClmsCaseResponse update(UUID id, ClmsCaseUpdateRequest req,
                                   UserEntity actor, HttpServletRequest request) {
        ClmsCase clmsCase = find(id);
        Map<String, ChangeDto> changes = new LinkedHashMap<>();

        if (req.title() != null && !req.title().equals(clmsCase.getTitle())) {
            changes.put("title", new ChangeDto(clmsCase.getTitle(), req.title()));
            clmsCase.setTitle(req.title());
        }
        if (req.type() != null && req.type() != clmsCase.getType()) {
            changes.put("type", new ChangeDto(clmsCase.getType().getValue(), req.type().getValue()));
            clmsCase.setType(req.type());
        }
        if (req.applicantName() != null && !req.applicantName().equals(clmsCase.getApplicantName())) {
            changes.put("applicantName", new ChangeDto(clmsCase.getApplicantName(), req.applicantName()));
            clmsCase.setApplicantName(req.applicantName());
        }
        if (req.applicantEmail() != null && !req.applicantEmail().equals(clmsCase.getApplicantEmail())) {
            changes.put("applicantEmail", new ChangeDto(clmsCase.getApplicantEmail(), req.applicantEmail()));
            clmsCase.setApplicantEmail(req.applicantEmail());
        }
        if (req.sector() != null && req.sector() != clmsCase.getSector()) {
            changes.put("sector", new ChangeDto(clmsCase.getSector().getValue(), req.sector().getValue()));
            clmsCase.setSector(req.sector());
        }
        if (req.province() != null && req.province() != clmsCase.getProvince()) {
            changes.put("province", new ChangeDto(clmsCase.getProvince().getValue(), req.province().getValue()));
            clmsCase.setProvince(req.province());
        }
        if (req.notes() != null && !req.notes().equals(clmsCase.getNotes())) {
            changes.put("notes", new ChangeDto(clmsCase.getNotes(), req.notes()));
            clmsCase.setNotes(req.notes());
        }
        if (req.assignedToId() != null) {
            UUID currentId = clmsCase.getAssignedTo() != null ? clmsCase.getAssignedTo().getId() : null;
            if (!req.assignedToId().equals(currentId)) {
                UserEntity assignee = findUser(req.assignedToId());
                changes.put("assignedTo", new ChangeDto(
                        currentId != null ? currentId.toString() : null,
                        req.assignedToId().toString()));
                clmsCase.setAssignedTo(assignee);
            }
        }

        clmsCaseRepository.save(clmsCase);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.CLMS,
                clmsCase.getId().toString(), clmsCase.getCaseNumber(), request,
                changes.isEmpty() ? null : changes);

        return ClmsCaseResponse.fromEntity(clmsCase);
    }

    public ClmsCaseResponse updateStatus(UUID id, ClmsStatusUpdateRequest req,
                                         UserEntity actor, HttpServletRequest request) {
        ClmsCase clmsCase = find(id);
        ClmsCaseStatus before = clmsCase.getStatus();
        ClmsCaseStatus next = req.status();

        // Approval restricted to admin/supervisor — enforced in controller via @PreAuthorize,
        // but guard here too for programmatic calls
        if (next == ClmsCaseStatus.APPROVED &&
                actor.getRole() != UserRole.ADMIN && actor.getRole() != UserRole.SUPERVISOR) {
            throw new ForbiddenException("Only admin or supervisor can approve a CLMS case");
        }

        clmsCase.setStatus(next);
        clmsCaseRepository.save(clmsCase);

        AuditAction action = switch (next) {
            case APPROVED -> AuditAction.APPROVE;
            case REJECTED -> AuditAction.REJECT;
            default       -> AuditAction.UPDATE;
        };

        auditService.log(actor, action, AuditModule.CLMS,
                clmsCase.getId().toString(), clmsCase.getCaseNumber(), request,
                Map.of("status", new ChangeDto(before.getValue(), next.getValue())));

        return ClmsCaseResponse.fromEntity(clmsCase);
    }

    // -------------------------------------------------------------------------

    ClmsCase find(UUID id) {
        return clmsCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CLMS case not found: " + id));
    }

    private UserEntity findUser(UUID id) {
        return userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private String generateCaseNumber() {
        int year = LocalDate.now().getYear();
        String pattern = "CLMS-" + year + "-%";
        String prefix  = "CLMS-" + year + "-";

        String max = clmsCaseRepository.findMaxCaseNumberByPattern(pattern);
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

    private Specification<ClmsCase> buildSpec(
            ClmsCaseStatus status, ClmsCaseType type, Sector sector, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (type != null)   predicates.add(cb.equal(root.get("type"), type));
            if (sector != null) predicates.add(cb.equal(root.get("sector"), sector));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("caseNumber")), pattern),
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("applicantName")), pattern)
                ));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
