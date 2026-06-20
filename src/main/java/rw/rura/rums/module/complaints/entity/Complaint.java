package rw.rura.rums.module.complaints.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rw.rura.rums.enums.ComplaintCategory;
import rw.rura.rums.enums.ComplaintSeverity;
import rw.rura.rums.enums.ComplaintStatus;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @Column(nullable = false, length = 300)
    private String subject;

    @Column(nullable = false)
    private ComplaintCategory category;

    @Column(name = "complainant_name", nullable = false, length = 120)
    private String complainantName;

    @Column(name = "complainant_phone", length = 20)
    private String complainantPhone;

    @Column(name = "respondent_operator", nullable = false, length = 200)
    private String respondentOperator;

    @Column(nullable = false)
    private Sector sector;

    @Column(nullable = false)
    private Province province;

    @Column(nullable = false)
    private ComplaintStatus status;

    @Column(nullable = false)
    private ComplaintSeverity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private UserEntity assignedTo;

    @CreationTimestamp
    @Column(name = "filed_at", nullable = false, updatable = false)
    private Instant filedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
