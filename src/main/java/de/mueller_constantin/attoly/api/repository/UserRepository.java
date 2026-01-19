package de.mueller_constantin.attoly.api.repository;

import de.mueller_constantin.attoly.api.domain.model.RoleName;
import de.mueller_constantin.attoly.api.domain.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    @Transactional(readOnly = true)
    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.email = ?1")
    Optional<User> findByEmail(String email);

    @Transactional(readOnly = true)
    @Query("SELECT u FROM User u WHERE u.deleted = true AND u.email = ?1")
    Optional<User> findDeletedByEmail(String email);

    @Transactional(readOnly = true)
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findAnyByEmail(String email);

    @Transactional(readOnly = true)
    default boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    default boolean existsDeletedByEmail(String email) {
        return findDeletedByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    default boolean existsAnyByEmail(String email) {
        return findAnyByEmail(email).isPresent();
    }

    @Query("SELECT COUNT(u) >= 1 FROM User u INNER JOIN u.roles r WHERE u.deleted = false AND r.name = ?1 AND u.id <> ?2")
    boolean existsAnyWithRoleExceptId(RoleName role, UUID id);

    @Query("SELECT COUNT(u) >= 1 FROM User u INNER JOIN u.roles r WHERE u.deleted = false AND r.name = ?1 AND u.email <> ?2")
    boolean existsAnyWithRoleExceptEmail(RoleName role, String email);
}
