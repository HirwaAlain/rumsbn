package rw.rura.rums.module.workflows.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import rw.rura.rums.enums.WorkflowStatus;
import rw.rura.rums.enums.WorkflowTrigger;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private WorkflowTrigger trigger;

    @Column(nullable = false)
    private WorkflowStatus status;

    // Accepts Sector enum values + "All Sectors" literal
    @Column(nullable = false, length = 30)
    private String sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private UserEntity createdBy;

    @Column(name = "related_entity_id", length = 100)
    private String relatedEntityId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<WorkflowStep> steps = new ArrayList<>();
}
