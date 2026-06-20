package rw.rura.rums.module.workflows.entity;

import jakarta.persistence.*;
import lombok.*;
import rw.rura.rums.enums.UserRole;
import rw.rura.rums.enums.WorkflowStepStatus;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workflow_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @Column(name = "step_order", nullable = false)
    private short stepOrder;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "assigned_role", nullable = false)
    private UserRole assignedRole;

    @Column(nullable = false)
    private WorkflowStepStatus status;

    @Column(name = "due_in_days", nullable = false)
    private short dueInDays;

    @Column(name = "completed_at")
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_id")
    private UserEntity completedBy;
}
