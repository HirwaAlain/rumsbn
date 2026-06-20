package rw.rura.rums.module.reports.service;

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
import rw.rura.rums.module.reports.dto.ReportCreateRequest;
import rw.rura.rums.module.reports.dto.ReportResponse;
import rw.rura.rums.module.reports.dto.ReportUpdateRequest;
import rw.rura.rums.module.reports.entity.Report;
import rw.rura.rums.module.reports.repository.ReportRepository;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final AuditService auditService;
    private final AlertService alertService;

    @Transactional(readOnly = true)
    public Page<ReportResponse> getAll(
            ReportType type, ReportStatus status, String sector,
            String search, Pageable pageable) {
        return reportRepository.findAll(buildSpec(type, status, sector, search), pageable)
                .map(ReportResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ReportResponse getById(UUID id) {
        return ReportResponse.fromEntity(find(id));
    }

    public ReportResponse create(ReportCreateRequest req,
                                 UserEntity actor, HttpServletRequest request) {
        Report report = new Report();
        report.setTitle(req.title());
        report.setType(req.type());
        report.setSector(req.sector());
        report.setStatus(ReportStatus.DRAFT);
        report.setFormat(req.format());
        report.setCreatedBy(actor);
        report.setPeriod(req.period());

        reportRepository.save(report);

        auditService.log(actor, AuditAction.CREATE, AuditModule.REPORTS,
                report.getId().toString(), report.getTitle(), request, null);

        return ReportResponse.fromEntity(report);
    }

    public ReportResponse update(UUID id, ReportUpdateRequest req,
                                 UserEntity actor, HttpServletRequest request) {
        Report report = find(id);
        Map<String, ChangeDto> changes = new LinkedHashMap<>();

        if (req.title() != null && !req.title().equals(report.getTitle())) {
            changes.put("title", new ChangeDto(report.getTitle(), req.title()));
            report.setTitle(req.title());
        }
        if (req.type() != null && req.type() != report.getType()) {
            changes.put("type", new ChangeDto(report.getType().getValue(), req.type().getValue()));
            report.setType(req.type());
        }
        if (req.sector() != null && !req.sector().equals(report.getSector())) {
            changes.put("sector", new ChangeDto(report.getSector(), req.sector()));
            report.setSector(req.sector());
        }
        if (req.format() != null && req.format() != report.getFormat()) {
            changes.put("format", new ChangeDto(report.getFormat().getValue(), req.format().getValue()));
            report.setFormat(req.format());
        }
        if (req.period() != null && !req.period().equals(report.getPeriod())) {
            changes.put("period", new ChangeDto(report.getPeriod(), req.period()));
            report.setPeriod(req.period());
        }

        reportRepository.save(report);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.REPORTS,
                report.getId().toString(), report.getTitle(), request,
                changes.isEmpty() ? null : changes);

        return ReportResponse.fromEntity(report);
    }

    public ReportResponse publish(UUID id, UserEntity actor, HttpServletRequest request) {
        Report report = find(id);
        report.setStatus(ReportStatus.PUBLISHED);
        report.setPublishedAt(Instant.now());
        reportRepository.save(report);

        alertService.createAlert(
                AlertType.REPORT_READY,
                "Report Published: " + report.getTitle(),
                "Report '" + report.getTitle() + "' (" + report.getPeriod() + ") has been published.",
                AlertSeverity.INFO,
                AuditModule.REPORTS,
                report.getId().toString()
        );

        auditService.log(actor, AuditAction.APPROVE, AuditModule.REPORTS,
                report.getId().toString(), report.getTitle(), request,
                Map.of("status", new ChangeDto(ReportStatus.DRAFT.getValue(), ReportStatus.PUBLISHED.getValue())));

        return ReportResponse.fromEntity(report);
    }

    public ReportResponse archive(UUID id, UserEntity actor, HttpServletRequest request) {
        Report report = find(id);
        ReportStatus before = report.getStatus();
        report.setStatus(ReportStatus.ARCHIVED);
        reportRepository.save(report);

        auditService.log(actor, AuditAction.UPDATE, AuditModule.REPORTS,
                report.getId().toString(), report.getTitle(), request,
                Map.of("status", new ChangeDto(before.getValue(), ReportStatus.ARCHIVED.getValue())));

        return ReportResponse.fromEntity(report);
    }

    // -------------------------------------------------------------------------

    Report find(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + id));
    }

    private Specification<Report> buildSpec(
            ReportType type, ReportStatus status, String sector, String search) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (type != null)   predicates.add(cb.equal(root.get("type"), type));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (sector != null && !sector.isBlank())
                predicates.add(cb.equal(root.get("sector"), sector));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(
                                root.join("createdBy", jakarta.persistence.criteria.JoinType.LEFT)
                                        .get("name")), pattern)
                ));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
