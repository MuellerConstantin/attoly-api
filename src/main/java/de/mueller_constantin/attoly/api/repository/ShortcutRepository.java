package de.mueller_constantin.attoly.api.repository;

import de.mueller_constantin.attoly.api.domain.model.Shortcut;
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
    /**
     * Resolves a shortcut by its tag. Only shortcuts that are not expired are returned.
     *
     * @param tag The tag of the shortcut to resolve.
     * @return An optional containing the resolved shortcut, or an empty optional if no such shortcut exists or if it is deleted or expired.
     */
    @Transactional(readOnly = true)
    @Query("SELECT s FROM Shortcut s WHERE s.deleted = false AND (s.expiresAt IS NULL OR s.expiresAt > CURRENT_TIMESTAMP) AND s.tag = ?1")
    Optional<Shortcut> findValidByTag(String tag);

    /**
     * Resolves a shortcut by its tag. Independent of whether it is expired.
     * This method is used for administrative purposes, e.g. for deleting shortcuts.
     *
     * @param tag The tag of the shortcut to resolve.
     * @return An optional containing the resolved shortcut, or an empty optional if no such shortcut exists or if it is deleted or expired.
     */
    @Transactional(readOnly = true)
    @Query("SELECT s FROM Shortcut s WHERE s.deleted = false AND s.tag = ?1")
    Optional<Shortcut> findByTag(String tag);

    @Transactional(readOnly = true)
    @Query("SELECT s FROM Shortcut s WHERE s.deleted = false AND s.createdBy.id = ?1")
    Page<Shortcut> findByOwnership(UUID creatorId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT s FROM Shortcut s WHERE s.deleted = false AND s.createdBy.email = ?1")
    Page<Shortcut> findByOwnership(String email, Pageable pageable);

    @Modifying
    @Query("UPDATE Shortcut s SET s.deleted = true WHERE s.createdBy IS NULL AND s.createdAt < ?1 AND s.permanent = false")
    void deleteAllTemporarySoftCreatedBefore(Instant dateTime);

    @Modifying
    @Query("DELETE FROM Shortcut s WHERE s.createdBy IS NULL AND s.createdAt < ?1 AND s.permanent = false")
    void deleteAllTemporaryCreatedBefore(Instant dateTime);

    @Query("SELECT COUNT(s) FROM Shortcut s WHERE s.deleted = false AND (s.expiresAt IS NULL OR s.expiresAt > CURRENT_TIMESTAMP) AND s.createdBy.id = ?1 AND s.permanent = true")
    Long countPermanentShortcutsByCreatorId(UUID creatorId);

    @Query("SELECT COUNT(s) FROM Shortcut s WHERE s.deleted = false AND (s.expiresAt IS NULL OR s.expiresAt > CURRENT_TIMESTAMP) AND s.createdBy.id = ?1 AND s.expiresAt IS NOT NULL")
    Long countExpirableShortcutsByCreatorId(UUID creatorId);
}
