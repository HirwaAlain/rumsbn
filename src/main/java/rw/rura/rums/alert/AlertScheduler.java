package rw.rura.rums.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rw.rura.rums.enums.AlertSeverity;
import rw.rura.rums.enums.AlertType;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.enums.ComplaintStatus;
import rw.rura.rums.enums.LicenseStatus;
import rw.rura.rums.enums.WorkflowStatus;
import rw.rura.rums.module.complaints.entity.Complaint;
import rw.rura.rums.module.complaints.repository.ComplaintRepository;
import rw.rura.rums.module.fraud.repository.FraudCaseRepository;
import rw.rura.rums.module.licenses.entity.License;
import rw.rura.rums.module.licenses.repository.LicenseRepository;
import rw.rura.rums.module.workflows.entity.Workflow;
import rw.rura.rums.module.workflows.repository.WorkflowRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduled jobs that scan entity tables and emit alerts.
 * Method bodies are intentional TODO stubs — they will be implemented
 * once the module repositories and entities are created.
 */
@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final LicenseRepository licenseRepository;
    private final ComplaintRepository complaintRepository;
    private final WorkflowRepository workflowRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final AlertService alertService;

    /**
     * Daily at 06:00 UTC.
     * Marks licences as expired and raises expiry alerts for those expiring
     * within 30 days (critical within 9 days).
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void checkLicenceExpiry() {
        LocalDate today = LocalDate.now();
        LocalDate cutoff30 = today.plusDays(30);

        List<License> expiring = licenseRepository.findActiveExpiringBefore(cutoff30);
        for (License license : expiring) {
            LocalDate expiresAt = license.getExpiresAt();

            if (!expiresAt.isAfter(today)) {
                // Past expiry — mark the license expired and raise a critical alert
                license.setStatus(LicenseStatus.EXPIRED);
                licenseRepository.save(license);
                alertService.createAlert(
                        AlertType.LICENSE_EXPIRY,
                        "License Expired: " + license.getLicenseNumber(),
                        "License " + license.getLicenseNumber() + " (" + license.getOperatorName()
                                + ") expired on " + expiresAt + ".",
                        AlertSeverity.CRITICAL,
                        AuditModule.LICENSES,
                        license.getId().toString()
                );
            } else if (!expiresAt.isAfter(today.plusDays(9))) {
                // Expiring within 9 days — critical
                alertService.createAlert(
                        AlertType.LICENSE_EXPIRY,
                        "License Expiring Soon: " + license.getLicenseNumber(),
                        "License " + license.getLicenseNumber() + " (" + license.getOperatorName()
                                + ") expires on " + expiresAt + " (within 9 days).",
                        AlertSeverity.CRITICAL,
                        AuditModule.LICENSES,
                        license.getId().toString()
                );
            } else {
                // Expiring within 30 days — warning
                alertService.createAlert(
                        AlertType.LICENSE_EXPIRY,
                        "License Expiring: " + license.getLicenseNumber(),
                        "License " + license.getLicenseNumber() + " (" + license.getOperatorName()
                                + ") expires on " + expiresAt + " (within 30 days).",
                        AlertSeverity.WARNING,
                        AuditModule.LICENSES,
                        license.getId().toString()
                );
            }
        }
    }

    /**
     * Daily at 08:00 UTC.
     * Raises a complaint_sla_breach / warning alert for every open or
     * under_review complaint filed more than 14 days ago.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkSlaBreaches() {
        Instant cutoff = Instant.now().minus(14, ChronoUnit.DAYS);
        List<Complaint> breached = complaintRepository.findSlaBreached(
                List.of(ComplaintStatus.OPEN, ComplaintStatus.UNDER_REVIEW), cutoff);

        for (Complaint complaint : breached) {
            alertService.createAlert(
                    AlertType.COMPLAINT_SLA_BREACH,
                    "SLA Breach: " + complaint.getReferenceNumber(),
                    "Complaint " + complaint.getReferenceNumber() + " (\"" + complaint.getSubject()
                            + "\") has been open for more than 14 days without resolution.",
                    AlertSeverity.WARNING,
                    AuditModule.COMPLAINTS,
                    complaint.getId().toString()
            );
        }
    }

    /**
     * Daily at 08:30 UTC.
     * Raises a workflow_stalled / warning alert for every active or paused
     * workflow that has had no step completed in the last 5 days.
     */
    @Scheduled(cron = "0 30 8 * * *")
    public void checkWorkflowStalls() {
        Instant threshold = Instant.now().minus(5, ChronoUnit.DAYS);
        List<Workflow> stalled = workflowRepository.findStalledWorkflows(
                List.of(WorkflowStatus.ACTIVE, WorkflowStatus.PAUSED),
                threshold,
                threshold
        );
        for (Workflow w : stalled) {
            alertService.createAlert(
                    AlertType.WORKFLOW_STALLED,
                    "Workflow Stalled: " + w.getName(),
                    "Workflow '" + w.getName() + "' has had no step activity in the last 5 days.",
                    AlertSeverity.WARNING,
                    AuditModule.WORKFLOWS,
                    w.getId().toString()
            );
        }
    }

    /**
     * Every Monday at 09:00 UTC.
     * Sums estimated_loss_rwf for non-dismissed fraud cases in the current
     * quarter; raises threshold_exceeded / critical if the total exceeds
     * 200,000,000 RWF.
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void checkFraudLossThreshold() {
        LocalDate today = LocalDate.now();
        // First day of the current calendar quarter
        int quarterStartMonth = ((today.getMonthValue() - 1) / 3) * 3 + 1;
        LocalDate quarterStart = LocalDate.of(today.getYear(), quarterStartMonth, 1);

        long totalLoss = fraudCaseRepository.sumEstimatedLossFromDate(quarterStart);
        if (totalLoss > 200_000_000L) {
            alertService.createAlert(
                    AlertType.THRESHOLD_EXCEEDED,
                    "Fraud Loss Threshold Exceeded",
                    "Total estimated fraud loss for the current quarter has exceeded 200,000,000 RWF"
                            + " (current total: " + String.format("%,d", totalLoss) + " RWF).",
                    AlertSeverity.CRITICAL,
                    AuditModule.FRAUD,
                    null
            );
        }
    }
}
