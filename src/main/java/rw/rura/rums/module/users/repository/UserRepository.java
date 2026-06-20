package rw.rura.rums.module.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rw.rura.rums.module.users.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    // Checks ALL rows (including soft-deleted) — needed because email has a DB-level UNIQUE constraint
    boolean existsByEmail(String email);

    /**
     * Returns all non-deleted users matching the given spec.
     * Combines the caller's spec with a deletedAt IS NULL guard.
     */
    default Page<UserEntity> findAllByDeletedAtIsNull(Specification<UserEntity> spec, Pageable pageable) {
        Specification<UserEntity> notDeleted = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        return findAll(spec == null ? notDeleted : spec.and(notDeleted), pageable);
    }
}
