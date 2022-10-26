package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.Shortcut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortcutRepository extends BaseRepository<Shortcut, UUID> {

    @Transactional(readOnly = true)
    @Query("SELECT s FROM Shortcut s WHERE s.deleted = false AND s.tag = ?1")
    Optional<Shortcut> findByTag(String tag);

    @Transactional(readOnly = true)
    @Query("SELECT s FROM Shortcut s WHERE s.deleted = false AND s.createdBy.id = ?1")
    Page<Shortcut> findByCreatorId(UUID creatorId, Pageable pageable);

    @Modifying
    @Query("UPDATE Shortcut s SET s.deleted = true WHERE s.createdBy IS NULL AND s.createdAt < ?1")
    void deleteAllAnonymousSoftCreatedBefore(Instant dateTime);

    @Modifying
    @Query("DELETE FROM Shortcut s WHERE s.createdBy IS NULL AND s.createdAt < ?1")
    void deleteAllAnonymousCreatedBefore(Instant dateTime);

    @Transactional
    @Modifying
    default void deleteSoftByTag(String tag) {
        Shortcut entity = findByTag(tag).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("Entity with tag [%s] wasn't found in the database. Nothing to soft-delete.", tag), 1));

        if (entity.isDeleted()) {
            throw new DataIntegrityViolationException(String.format("Entity with tag [%s] is already soft-deleted.", tag));
        }

        entity.setDeleted(true);
        save(entity);
    }
}
