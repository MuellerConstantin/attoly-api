package de.x1c1b.attoly.api.domain;

import de.x1c1b.attoly.api.domain.model.Shortcut;
import de.x1c1b.attoly.api.domain.payload.ShortcutCreationPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    List<Shortcut> findAll();

    /**
     * Loads all available shortcuts in individual pages.
     *
     * @param pageable The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<Shortcut> findAll(Pageable pageable);

    /**
     * Find all shortcuts created of a specific user.
     *
     * @param ownerId  The shortcut's unique identifier.
     * @param pageable The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<Shortcut> findAllByOwnership(UUID ownerId, Pageable pageable);

    /**
     * Find all shortcuts created of a specific user.
     *
     * @param tag      The shortcut's unique tag.
     * @param pageable The pagination settings.
     * @return The requested page of shortcuts.
     */
    Page<Shortcut> findAllByOwnership(String tag, Pageable pageable);

    /**
     * Loads a shortcut by its identifier.
     *
     * @param id The shortcut's unique identifier.
     * @return The loaded shortcut.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    Shortcut findById(UUID id) throws EntityNotFoundException;

    /**
     * Loads a shortcut by its tag.
     *
     * @param tag The shortcut's unique tag.
     * @return The loaded shortcut.
     * @throws EntityNotFoundException Thrown if the shortcut cannot be found.
     */
    Shortcut findByTag(String tag) throws EntityNotFoundException;

    /**
     * Creates a new shortcut.
     *
     * @param payload The payload data from which the shortcut is created.
     * @return The newly created shortcut.
     */
    Shortcut create(ShortcutCreationPayload payload);

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
}
