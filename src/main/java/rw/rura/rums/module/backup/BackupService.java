package rw.rura.rums.module.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.module.clms.repository.ClmsCaseRepository;
import rw.rura.rums.module.complaints.repository.ComplaintRepository;
import rw.rura.rums.module.compliance.repository.ComplianceRepository;
import rw.rura.rums.module.fraud.repository.FraudCaseRepository;
import rw.rura.rums.module.licenses.repository.LicenseRepository;
import rw.rura.rums.module.reports.repository.ReportRepository;
import rw.rura.rums.module.users.repository.UserRepository;
import rw.rura.rums.module.workflows.repository.WorkflowRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    @Value("${rums.backup.dir:./backups/}")
    private String backupDir;

    private final LicenseRepository licenseRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplianceRepository complianceRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final ClmsCaseRepository clmsCaseRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final WorkflowRepository workflowRepository;

    @Transactional(readOnly = true)
    public BackupResult triggerBackup() throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("generatedAt", LocalDateTime.now().toString());
        data.put("licenses", licenseRepository.findAll());
        data.put("complaints", complaintRepository.findAll());
        data.put("compliance", complianceRepository.findAll());
        data.put("fraudCases", fraudCaseRepository.findAll());
        data.put("clmsCases", clmsCaseRepository.findAll());
        data.put("reports", reportRepository.findAll());
        data.put("workflows", workflowRepository.findAll());
        // Export users without password hashes
        data.put("users", userRepository.findAll().stream()
                .filter(u -> u.getDeletedAt() == null)
                .map(u -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", u.getId());
                    m.put("name", u.getName());
                    m.put("email", u.getEmail());
                    m.put("phone", u.getPhone());
                    m.put("role", u.getRole());
                    m.put("status", u.getStatus());
                    m.put("department", u.getDepartment());
                    m.put("createdAt", u.getCreatedAt());
                    m.put("lastLogin", u.getLastLogin());
                    return m;
                }).toList());

        Path dir = Path.of(backupDir);
        Files.createDirectories(dir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "rums_backup_" + timestamp + ".json";
        Path filePath = dir.resolve(filename);

        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), data);
        log.info("Backup created: {}", filePath.toAbsolutePath());

        return new BackupResult(filename, filePath.toAbsolutePath().toString(), LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledDailyBackup() {
        try {
            BackupResult result = triggerBackup();
            log.info("Scheduled daily backup completed: {}", result.filePath());
        } catch (IOException e) {
            log.error("Scheduled daily backup failed", e);
        }
    }

    public record BackupResult(String filename, String filePath, LocalDateTime createdAt) {}
}
