package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.User;
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
}
