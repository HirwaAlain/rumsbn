package rw.rura.rums.module.audit.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.audit.dto.AuditLogResponse;
import rw.rura.rums.module.audit.entity.AuditLog;
import rw.rura.rums.module.audit.repository.AuditLogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLogResponse> getAll(
            AuditModule module, AuditAction action, UUID userId, String search, Pageable pageable) {
        Specification<AuditLog> spec = buildSpec(module, action, userId, search);
        return auditLogRepository.findAll(spec, pageable).map(AuditLogResponse::fromEntity);
    }

    private Specification<AuditLog> buildSpec(
            AuditModule module, AuditAction action, UUID userId, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (module != null) predicates.add(cb.equal(root.get("module"), module));
            if (action != null) predicates.add(cb.equal(root.get("action"), action));
            if (userId != null) predicates.add(cb.equal(root.get("userId"), userId));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("userName")), pattern),
                        cb.like(cb.lower(root.get("entityLabel")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
