package rw.rura.rums.module.compliance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rw.rura.rums.enums.ComplianceCheckType;
import rw.rura.rums.enums.ComplianceStatus;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.licenses.entity.License;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "compliance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "operator_name", nullable = false, length = 200)
    private String operatorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private License license;

    @Column(nullable = false)
    private Sector sector;

    @Column(name = "check_type", nullable = false)
    private ComplianceCheckType checkType;

    @Column(nullable = false)
    private ComplianceStatus status;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "last_audit_date")
    private LocalDate lastAuditDate;

    @Column
    private Short score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id")
    private UserEntity auditor;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
