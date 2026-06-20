package rw.rura.rums.module.clms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.rura.rums.module.clms.entity.ClmsCase;

import java.util.UUID;

public interface ClmsCaseRepository extends JpaRepository<ClmsCase, UUID>,
        JpaSpecificationExecutor<ClmsCase> {

    @Query(value = "SELECT MAX(case_number) FROM clms_cases WHERE case_number LIKE :pattern",
            nativeQuery = true)
    String findMaxCaseNumberByPattern(@Param("pattern") String pattern);
}
