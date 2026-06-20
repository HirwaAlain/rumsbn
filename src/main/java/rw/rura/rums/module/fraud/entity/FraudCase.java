package rw.rura.rums.module.fraud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rw.rura.rums.enums.FraudCaseStatus;
import rw.rura.rums.enums.FraudIndicatorType;
import rw.rura.rums.enums.FraudRiskLevel;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fraud_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_number", nullable = false, unique = true, length = 50)
    private String caseNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "indicator_type", nullable = false)
    private FraudIndicatorType indicatorType;

    @Column(name = "reported_by", nullable = false, length = 200)
    private String reportedBy;

    @Column(name = "operator_involved", nullable = false, length = 200)
    private String operatorInvolved;

    @Column(nullable = false)
    private Sector sector;

    @Column(name = "risk_level", nullable = false)
    private FraudRiskLevel riskLevel;

    @Column(nullable = false)
    private FraudCaseStatus status;

    @Column(name = "reported_at", nullable = false)
    private LocalDate reportedAt;

    @Column(name = "estimated_loss_rwf", nullable = false)
    private long estimatedLossRwf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigating_officer_id")
    private UserEntity investigatingOfficer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
