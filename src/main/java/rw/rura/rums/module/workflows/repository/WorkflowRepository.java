package rw.rura.rums.module.workflows.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.rura.rums.enums.WorkflowStatus;
import rw.rura.rums.module.workflows.entity.Workflow;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID>,
        JpaSpecificationExecutor<Workflow> {

    /**
     * Returns workflows in ACTIVE or PAUSED state that started more than
     * {@code staleBefore} ago AND have no step completed after {@code activeAfter}.
     */
    @Query("""
            SELECT DISTINCT w FROM Workflow w
            LEFT JOIN w.steps s
            WHERE w.status IN :statuses
              AND w.startedAt < :staleBefore
            GROUP BY w
            HAVING MAX(s.completedAt) IS NULL
                OR MAX(s.completedAt) < :activeAfter
            """)
    List<Workflow> findStalledWorkflows(
            @Param("statuses") List<WorkflowStatus> statuses,
            @Param("staleBefore") Instant staleBefore,
            @Param("activeAfter") Instant activeAfter);
}
