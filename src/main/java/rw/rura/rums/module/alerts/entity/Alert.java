package rw.rura.rums.module.alerts.entity;

import jakarta.persistence.*;
import lombok.*;
import rw.rura.rums.enums.AlertSeverity;
import rw.rura.rums.enums.AlertStatus;
import rw.rura.rums.enums.AlertType;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false)
    private AlertStatus status;

    @Column(name = "related_module", nullable = false)
    private AuditModule relatedModule;

    @Column(name = "related_entity_id", length = 100)
    private String relatedEntityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actioned_by_id")
    private UserEntity actionedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "read_at")
    private Instant readAt;
}
