package rw.rura.rums.module.clms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rw.rura.rums.enums.ClmsCaseStatus;
import rw.rura.rums.enums.ClmsCaseType;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clms_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClmsCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_number", nullable = false, unique = true, length = 50)
    private String caseNumber;

    @Column(nullable = false, length = 400)
    private String title;

    @Column(nullable = false)
    private ClmsCaseType type;

    @Column(nullable = false)
    private ClmsCaseStatus status;

    @Column(name = "applicant_name", nullable = false, length = 200)
    private String applicantName;

    @Column(name = "applicant_email", length = 200)
    private String applicantEmail;

    @Column(nullable = false)
    private Sector sector;

    @Column(nullable = false)
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private UserEntity assignedTo;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "clmsCase", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ClmsDocument> documents = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
