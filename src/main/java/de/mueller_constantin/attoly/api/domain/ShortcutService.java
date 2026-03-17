package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.result.ShortcutResult;
import de.mueller_constantin.attoly.api.repository.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.payload.ShortcutCreationPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Interface for accessing shortcuts and related entities and operations.
 */
public interface ShortcutService {

    /**
     * Loads all available shortcuts. This can lead to performance problems.
     *
     * @return The list of all shortcuts.
     */
    List<ShortcutResult> findAll();

    /**
     * Loads all available shortcuts in individual pages.
     *
     * @param pageable The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<ShortcutResult> findAll(Pageable pageable);

    /**
     * Loads all available shortcuts in individual pages.
     *
     * @param specification The specification to filter the results.
     * @param pageable      The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<ShortcutResult> findAll(Specification<Shortcut> specification, Pageable pageable);

    /**
     * Find all shortcuts created of a specific user.
     *
     * @param ownerId  The shortcut's unique identifier.
     * @param pageable The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<ShortcutResult> findAllByOwnership(UUID ownerId, Pageable pageable);

    /**
     * Find all shortcuts created of a specific user.
     *
     * @param email    The users's unique email.
     * @param pageable The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<ShortcutResult> findAllByOwnership(String email, Pageable pageable);

    /**
     * Loads a shortcut by its identifier.
     *
     * @param id The shortcut's unique identifier.
     * @return The loaded shortcut.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    ShortcutResult findById(UUID id) throws EntityNotFoundException;

    /**
     * Loads a shortcut by its tag. Only shortcuts that are still valid, i.e.
     * not deleted and not expired, are returned. This method also considers password protected shortcuts,
     * but does not require a password. If the shortcut is password protected,
     * an exception is thrown.
     *
     * @param tag The shortcut's unique tag.
     * @return The loaded shortcut.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    ShortcutResult findValidByTag(String tag) throws EntityNotFoundException;

    /**
     * Loads a shortcut by its tag. Only shortcuts that are still valid, i.e. not deleted and not
     * expired, are returned. If the shortcut is password protected, the provided password must
     * be correct.
     *
     * @param tag The shortcut's unique tag.
     * @param password The password to access the shortcut, if it is password protected.
     * @return The loaded shortcut.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found or if the password is incorrect.
     */
    ShortcutResult resolveValidByTag(String tag, String password) throws EntityNotFoundException;

    /**
     * Loads a shortcut by its tag.
     *
     * @param tag The shortcut's unique tag.
     * @return The loaded shortcut.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    ShortcutResult findByTag(String tag) throws EntityNotFoundException;

    /**
     * Creates a new shortcut.
     *
     * @param payload The payload data from which the shortcut is created.
     * @return The newly created shortcut.
     */
    ShortcutResult create(ShortcutCreationPayload payload, UUID ownerId);

    /**
     * Deletes a shortcut using the identifier.
     *
     * @param id The shortcut's unique identifier.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    void deleteById(UUID id) throws EntityNotFoundException;

    /**
     * Deletes a shortcut using the tag.
     *
     * @param tag The shortcut's unique tag.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    void deleteByTag(String tag) throws EntityNotFoundException;

    /**
     * Deletes all expired and anonymously created shortcuts.
     *
     * @param date The offset date.
     */
    void deleteAllTemporaryCreatedBefore(OffsetDateTime date);
}
