package rw.rura.rums.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import rw.rura.rums.enums.*;

/**
 * Registers Spring MVC converters for enums used as @RequestParam.
 * Default Enum.valueOf() is case-sensitive; these converters use each enum's
 * fromValue() method which matches the lowercase DB/API values.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, AlertSeverity.class,   AlertSeverity::fromValue);
        registry.addConverter(String.class, AlertStatus.class,     AlertStatus::fromValue);
        registry.addConverter(String.class, AuditAction.class,     AuditAction::fromValue);
        registry.addConverter(String.class, AuditModule.class,     AuditModule::fromValue);
        registry.addConverter(String.class, ClmsCaseStatus.class,  ClmsCaseStatus::fromValue);
        registry.addConverter(String.class, ClmsCaseType.class,    ClmsCaseType::fromValue);
        registry.addConverter(String.class, ComplianceStatus.class,ComplianceStatus::fromValue);
        registry.addConverter(String.class, FraudCaseStatus.class, FraudCaseStatus::fromValue);
        registry.addConverter(String.class, FraudRiskLevel.class,  FraudRiskLevel::fromValue);
        registry.addConverter(String.class, LicenseStatus.class,   LicenseStatus::fromValue);
        registry.addConverter(String.class, Province.class,        Province::fromValue);
        registry.addConverter(String.class, ReportStatus.class,    ReportStatus::fromValue);
        registry.addConverter(String.class, ReportType.class,      ReportType::fromValue);
        registry.addConverter(String.class, Sector.class,          Sector::fromValue);
        registry.addConverter(String.class, UserDepartment.class,  UserDepartment::fromValue);
        registry.addConverter(String.class, UserRole.class,        UserRole::fromValue);
        registry.addConverter(String.class, UserStatus.class,      UserStatus::fromValue);
        registry.addConverter(String.class, WorkflowStatus.class,  WorkflowStatus::fromValue);
        registry.addConverter(String.class, WorkflowTrigger.class, WorkflowTrigger::fromValue);
    }
}
