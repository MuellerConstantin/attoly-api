package de.x1c1b.attoly.api.domain;

import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.exception.RoleNameAlreadyInUseException;
import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.payload.RoleCreationPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Central interface for all business operations related to user roles.
 */
public interface RoleService {

    /**
     * Loads all available roles. This can lead to performance problems.
     *
     * @return The list of all roles.
     */
    List<Role> findAll();

    /**
     * Loads all available roles in individual pages.
     *
     * @param pageable The pagination settings.
     * @return The requested page of roles.
     */
    Page<Role> findAll(Pageable pageable);

    /**
     * Loads a user by its identifier.
     *
     * @param id The user's unique identifier.
     * @return The loaded user.
     * @throws EntityNotFoundException Thrown if the role cannot be found.
     */
    Role findById(UUID id) throws EntityNotFoundException;

    /**
     * Loads a role by its name.
     *
     * @param name The role's unique name.
     * @return The loaded name.
     * @throws EntityNotFoundException Thrown if the role cannot be found.
     */
    Role findByName(String name) throws EntityNotFoundException;

    /**
     * Checks whether a role exists using the identifier.
     *
     * @param id The role's unique identifier.
     * @return True if the role exists, otherwise false.
     */
    boolean existsById(UUID id);

    /**
     * Checks whether a role exists using the identifier.
     *
     * @param name The role's unique name.
     * @return True if the role exists, otherwise false.
     */
    boolean existsByName(String name);

    /**
     * Creates a new user role.
     *
     * @param payload The payload data from which the role is created.
     * @return The newly created role.
     * @throws RoleNameAlreadyInUseException Thrown if a role is already using the specified name.
     */
    Role create(RoleCreationPayload payload) throws RoleNameAlreadyInUseException;

    /**
     * Deletes a user role using the identifier.
     *
     * @param id The role's unique identifier.
     * @throws EntityNotFoundException Thrown if the role cannot be found.
     */
    void deleteById(UUID id) throws EntityNotFoundException;

    /**
     * Deletes a user role using the name.
     *
     * @param name The role's unique name.
     * @throws EntityNotFoundException Thrown if the role cannot be found.
     */
    void deleteByName(String name) throws EntityNotFoundException;
}
