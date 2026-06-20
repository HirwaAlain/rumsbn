package rw.rura.rums.module.clms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.rura.rums.module.clms.entity.ClmsDocument;

import java.util.Optional;
import java.util.UUID;

public interface ClmsDocumentRepository extends JpaRepository<ClmsDocument, UUID> {

    Optional<ClmsDocument> findByIdAndClmsCase_Id(UUID docId, UUID caseId);
}
