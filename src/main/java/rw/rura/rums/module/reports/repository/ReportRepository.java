package rw.rura.rums.module.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rw.rura.rums.module.reports.entity.Report;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID>,
        JpaSpecificationExecutor<Report> {
}
