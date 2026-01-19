package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.model.Role;
import de.mueller_constantin.attoly.api.domain.model.RoleName;
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
    Role findByName(RoleName name) throws EntityNotFoundException;

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
    boolean existsByName(RoleName name);
}
