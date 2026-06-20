package rw.rura.rums.module.complaints.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.enums.ComplaintCategory;
import rw.rura.rums.enums.ComplaintSeverity;
import rw.rura.rums.enums.ComplaintStatus;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.complaints.dto.*;
import rw.rura.rums.module.complaints.entity.Complaint;
import rw.rura.rums.module.complaints.repository.ComplaintRepository;
import rw.rura.rums.module.users.entity.UserEntity;
import rw.rura.rums.module.users.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final SeverityClassifier severityClassifier;

    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getAll(
            ComplaintStatus status, Sector sector, ComplaintSeverity severity,
            String search, Pageable pageable) {
        return complaintRepository.findAll(buildFilterSpec(status, sector, severity, search), pageable)
                .map(ComplaintResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getById(UUID id) {
        return ComplaintResponse.fromEntity(findById(id));
    }

    public ComplaintResponse create(ComplaintCreateRequest req, UserEntity actor, HttpServletRequest request) {
        Complaint complaint = Complaint.builder()
                .referenceNumber(generateReferenceNumber())
                .subject(req.subject())
                .category(req.category())
                .complainantName(req.complainantName())
                .complainantPhone(req.complainantPhone())
                .respondentOperator(req.respondentOperator())
                .sector(req.sector())
                .province(req.province())
                .status(ComplaintStatus.OPEN)
                .severity(severityClassifier.classify(req.category(), req.description()))
                .description(req.description())
                .build();

        complaintRepository.save(complaint);

        auditService.log(actor, AuditAction.CREATE, AuditModule.COMPLAINTS,
                complaint.getId().toString(), complaint.getReferenceNumber(), request, null);

        return ComplaintResponse.fromEntity(complaint);
    }

    public PublicComplaintResponse createPublic(PublicComplaintRequest req) {
        Complaint complaint = Complaint.builder()
                .referenceNumber(generateReferenceNumber())
                .subject(req.subject())
                .category(req.category())
                .complainantName(req.complainantName())
                .complainantPhone(req.complainantPhone())
                .respondentOperator(req.respondentOperator())
                .sector(req.sector())
                .province(req.province())
                .status(ComplaintStatus.OPEN)
                .severity(severityClassifier.classify(req.category(), req.description()))
                .description(req.description())
                .build();

        complaintRepository.save(complaint);
        // No audit log — no authenticated actor for public submissions
        return new PublicComplaintResponse(
                complaint.getId(),
                complaint.getReferenceNumber(),
                "Your complaint has been filed. Keep your reference number for tracking."
        );
    }

    public ComplaintResponse update(UUID id, ComplaintUpdateRequest req,
                                    UserEntity actor, HttpServletRequest request) {
        Complaint complaint = findById(id);
        Map<String, ChangeDto> changes = new LinkedHashMap<>();

        if (req.subject() != null && !req.subject().equals(complaint.getSubject())) {
            changes.put("subject", new ChangeDto(complaint.getSubject(), req.subject()));
            complaint.setSubject(req.subject());
        }
        if (req.category() != null && req.category() != complaint.getCategory()) {
            changes.put("category", new ChangeDto(complaint.getCategory().getValue(), req.category().getValue()));
            complaint.setCategory(req.category());
        }
        if (req.complainantName() != null && !req.complainantName().equals(complaint.getComplainantName())) {
            changes.put("complainantName", new ChangeDto(complaint.getComplainantName(), req.complainantName()));
            complaint.setComplainantName(req.complainantName());
        }
        if (req.complainantPhone() != null && !req.complainantPhone().equals(complaint.getComplainantPhone())) {
            changes.put("complainantPhone", new ChangeDto(complaint.getComplainantPhone(), req.complainantPhone()));
            complaint.setComplainantPhone(req.complainantPhone());
        }
        if (req.respondentOperator() != null && !req.respondentOperator().equals(complaint.getRespondentOperator())) {
            changes.put("respondentOperator", new ChangeDto(complaint.getRespondentOperator(), req.respondentOperator()));
            complaint.setRespondentOperator(req.respondentOperator());
        }
        if (req.sector() != null && req.sector() != complaint.getSector()) {
            changes.put("sector", new ChangeDto(complaint.getSector().getValue(), req.sector().getValue()));
            complaint.setSector(req.sector());
        }
        if (req.province() != null && req.province() != complaint.getProvince()) {
            changes.put("province", new ChangeDto(complaint.getProvince().getValue(), req.province().getValue()));
            complaint.setProvince(req.province());
        }
        if (req.severity() != null && req.severity() != complaint.getSeverity()) {
            changes.put("severity", new ChangeDto(complaint.getSeverity().getValue(), req.severity().getValue()));
            complaint.setSeverity(req.severity());
        }
        if (req.description() != null && !req.description().equals(complaint.getDescription())) {
            changes.put("description", new ChangeDto(complaint.getDescription(), req.description()));
            complaint.setDescription(req.description());
        }

        complaintRepository.save(complaint);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.COMPLAINTS,
                complaint.getId().toString(), complaint.getReferenceNumber(), request,
                changes.isEmpty() ? null : changes);

        return ComplaintResponse.fromEntity(complaint);
    }

    public ComplaintResponse updateStatus(UUID id, ComplaintStatusUpdateRequest req,
                                          UserEntity actor, HttpServletRequest request) {
        Complaint complaint = findById(id);
        ComplaintStatus before = complaint.getStatus();

        complaint.setStatus(req.status());
        if (req.status() == ComplaintStatus.RESOLVED && complaint.getResolvedAt() == null) {
            complaint.setResolvedAt(Instant.now());
        }

        complaintRepository.save(complaint);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.COMPLAINTS,
                complaint.getId().toString(), complaint.getReferenceNumber(), request,
                Map.of("status", new ChangeDto(before.getValue(), req.status().getValue())));

        return ComplaintResponse.fromEntity(complaint);
    }

    public ComplaintResponse assign(UUID id, ComplaintAssignRequest req,
                                    UserEntity actor, HttpServletRequest request) {
        Complaint complaint = findById(id);

        String beforeName = complaint.getAssignedTo() != null
                ? complaint.getAssignedTo().getName() : "Unassigned";

        UserEntity assignee = userRepository.findById(req.userId())
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + req.userId() + " not found"));

        complaint.setAssignedTo(assignee);
        complaintRepository.save(complaint);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.COMPLAINTS,
                complaint.getId().toString(), complaint.getReferenceNumber(), request,
                Map.of("assignedTo", new ChangeDto(beforeName, assignee.getName())));

        return ComplaintResponse.fromEntity(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintsBySectorPoint> getBySector() {
        return complaintRepository.countBySector()
                .stream()
                .map(ComplaintsBySectorPoint::fromProjection)
                .toList();
    }

    // -------------------------------------------------------------------------

    private Complaint findById(UUID id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint with ID " + id + " not found"));
    }

    /**
     * Generates the next reference number in the format RURA-CMP-{YYYY}-{NNNN}.
     * Queries the current maximum for this year and increments. Must be called
     * within an existing transaction so the read and subsequent insert are atomic.
     */
    private String generateReferenceNumber() {
        int year = LocalDate.now().getYear();
        String pattern = "RURA-CMP-" + year + "-%";
        String prefix  = "RURA-CMP-" + year + "-";

        String max = complaintRepository.findMaxReferenceNumberByPattern(pattern);
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

    private Specification<Complaint> buildFilterSpec(
            ComplaintStatus status, Sector sector, ComplaintSeverity severity, String search) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (sector != null) {
                predicates.add(cb.equal(root.get("sector"), sector));
            }
            if (severity != null) {
                predicates.add(cb.equal(root.get("severity"), severity));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("referenceNumber")), pattern),
                        cb.like(cb.lower(root.get("subject")), pattern),
                        cb.like(cb.lower(root.get("complainantName")), pattern)
                ));
            }

            return predicates.isEmpty() ? null
                    : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
