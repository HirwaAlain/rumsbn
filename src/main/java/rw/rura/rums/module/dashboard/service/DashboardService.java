package rw.rura.rums.module.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.audit.entity.AuditLog;
import rw.rura.rums.module.audit.repository.AuditLogRepository;
import rw.rura.rums.module.compliance.dto.ComplianceOverviewItem;
import rw.rura.rums.module.compliance.service.ComplianceService;
import rw.rura.rums.module.complaints.repository.ComplaintRepository;
import rw.rura.rums.module.dashboard.dto.ActivityItem;
import rw.rura.rums.module.dashboard.dto.ComplaintsBySectorPoint;
import rw.rura.rums.module.dashboard.dto.DashboardKpisResponse;
import rw.rura.rums.module.fraud.repository.FraudCaseRepository;
import rw.rura.rums.module.licenses.dto.LicenseTrendPoint;
import rw.rura.rums.module.licenses.repository.LicenseRepository;
import rw.rura.rums.module.licenses.service.LicenseService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final LicenseRepository licenseRepository;
    private final ComplaintRepository complaintRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final AuditLogRepository auditLogRepository;
    private final LicenseService licenseService;
    private final ComplianceService complianceService;

    public DashboardKpisResponse getKpis() {
        long activeLicenses = licenseRepository.countActive();
        long activeComplaints = complaintRepository.countActive();
        long openFraudCases = fraudCaseRepository.countOpen();

        // complianceRate = compliant count as integer percentage of total
        List<ComplianceOverviewItem> overview = complianceService.getOverview();
        long total = overview.stream().mapToLong(ComplianceOverviewItem::value).sum();
        long compliant = overview.stream()
                .filter(item -> "Compliant".equals(item.name()))
                .mapToLong(ComplianceOverviewItem::value)
                .findFirst()
                .orElse(0L);
        long complianceRate = total == 0 ? 0 : Math.round((compliant * 100.0) / total);

        return new DashboardKpisResponse(activeLicenses, activeComplaints, complianceRate, openFraudCases);
    }

    public List<LicenseTrendPoint> getLicenseTrend() {
        return licenseService.getTrend();
    }

    public List<ComplaintsBySectorPoint> getComplaintsBySector() {
        return complaintRepository.countBySector().stream()
                .map(p -> new ComplaintsBySectorPoint(p.getSector(), p.getCount()))
                .toList();
    }

    public List<ComplianceOverviewItem> getComplianceOverview() {
        return complianceService.getOverview();
    }

    public List<ActivityItem> getActivity(int limit) {
        return auditLogRepository
                .findAllByOrderByTimestampDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toActivityItem)
                .toList();
    }

    // -------------------------------------------------------------------------

    private ActivityItem toActivityItem(AuditLog log) {
        String type = resolveActivityType(log.getAction(), log.getModule(), log.getEntityLabel(), log.getChanges());
        return new ActivityItem(
                log.getId().toString(),
                type,
                log.getEntityLabel(),
                log.getUserName(),
                log.getModule().getValue(),
                log.getTimestamp(),
                log.getEntityId()
        );
    }

    /**
     * Maps AuditAction + AuditModule (and optional change context) to an ActivityType string
     * as specified in the dashboard spec (section 7.2).
     */
    private String resolveActivityType(AuditAction action, AuditModule module,
                                       String entityLabel, Object changes) {
        if (action == AuditAction.LOGIN)  return "user_login";
        if (action == AuditAction.CREATE && module == AuditModule.USERS)      return "user_created";
        if (action == AuditAction.EXPORT && module == AuditModule.COMPLIANCE) return "audit_completed";

        return switch (module) {
            case LICENSES -> switch (action) {
                case CREATE  -> "license_issued";
                case SUSPEND -> "license_suspended";
                case DELETE  -> "license_revoked";
                default      -> "license_issued";
            };
            case COMPLAINTS -> {
                // resolved_at set means complaint was resolved
                if (action == AuditAction.UPDATE && entityLabel != null
                        && hasStatusChange("resolved", changes)) {
                    yield "complaint_resolved";
                }
                yield action == AuditAction.CREATE ? "complaint_filed" : "complaint_resolved";
            }
            case COMPLIANCE -> {
                if (hasStatusChange("non_compliant", changes)) yield "compliance_breach";
                yield action == AuditAction.CREATE ? "compliance_check" : "compliance_check";
            }
            case FRAUD -> {
                if (hasStatusChange("dismissed", changes)) yield "fraud_resolved";
                yield action == AuditAction.CREATE ? "fraud_flagged" : "fraud_flagged";
            }
            case WORKFLOWS -> "workflow_triggered";
            case ALERTS    -> "alert_raised";
            case USERS     -> action == AuditAction.CREATE ? "user_created" : "user_login";
            case REPORTS   -> {
                if (action == AuditAction.APPROVE) yield "report_published";
                yield "report_published";
            }
            default -> action.getValue() + "_" + module.getValue().toLowerCase();
        };
    }

    /**
     * Returns true when the changes map contains a "status" entry whose "after" value
     * matches the given target status string.
     */
    @SuppressWarnings("unchecked")
    private boolean hasStatusChange(String targetStatus, Object changes) {
        if (changes == null) return false;
        try {
            var map = (java.util.Map<String, ?>) changes;
            var statusChange = map.get("status");
            if (statusChange == null) return false;
            // ChangeDto has getAfter(); support both ChangeDto and Map<String,String>
            if (statusChange instanceof rw.rura.rums.audit.ChangeDto cd) {
                return targetStatus.equals(cd.after());
            }
            if (statusChange instanceof java.util.Map<?, ?> m) {
                return targetStatus.equals(m.get("after"));
            }
        } catch (Exception ignored) {}
        return false;
    }
}
