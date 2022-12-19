package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.RoleName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends BaseRepository<Role, UUID> {

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Role r WHERE r.deleted = false AND r.name = ?1")
    Optional<Role> findByName(RoleName name);

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Role r WHERE r.deleted = true AND r.name = ?1")
    Optional<Role> findDeletedByName(RoleName name);

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Role r WHERE r.name = ?1")
    Optional<Role> findAnyByName(RoleName name);

    @Transactional(readOnly = true)
    default boolean existsByName(RoleName name) {
        return findByName(name).isPresent();
    }

    @Transactional(readOnly = true)
    default boolean existsDeletedByName(RoleName name) {
        return findDeletedByName(name).isPresent();
    }

    @Transactional(readOnly = true)
    default boolean existsAnyByName(RoleName name) {
        return findAnyByName(name).isPresent();
    }
}
