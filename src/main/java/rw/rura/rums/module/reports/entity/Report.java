package rw.rura.rums.module.reports.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import rw.rura.rums.enums.ReportFormat;
import rw.rura.rums.enums.ReportStatus;
import rw.rura.rums.enums.ReportType;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 400)
    private String title;

    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false, length = 50)
    private String sector;

    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private ReportFormat format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private UserEntity createdBy;

    @Column(nullable = false, length = 100)
    private String period;

    @Column(name = "stored_path", length = 500)
    private String storedPath;

    @Column(name = "size_kb")
    private Integer sizeKb;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;
}
