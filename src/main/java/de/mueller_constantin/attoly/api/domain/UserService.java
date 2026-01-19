package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.exception.EmailAlreadyInUseException;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.payload.UserCreationPayload;
import de.mueller_constantin.attoly.api.domain.payload.UserUpdatePayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

/**
 * Central interface for all business operations related to user accounts.
 */
public interface UserService {

    /**
     * Loads all available users. This can lead to performance problems.
     *
     * @return The list of all users.
     */
    List<User> findAll();

    /**
     * Loads all available users in individual pages.
     *
     * @param pageable The pagination settings.
     * @return The requested page of users.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Loads all available users in individual pages.
     *
     * @param specification The specification to filter the users.
     * @param pageable      The pagination settings.
     * @return The requested page of users.
     */
    Page<User> findAll(Specification<User> specification, Pageable pageable);

    /**
     * Loads a user by its identifier.
     *
     * @param id The user's unique identifier.
     * @return The loaded user.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    User findById(UUID id) throws EntityNotFoundException;

    /**
     * Loads a user by its email.
     *
     * @param email The user's unique email.
     * @return The loaded user.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    User findByEmail(String email) throws EntityNotFoundException;

    /**
     * Checks whether a user exists using the identifier.
     *
     * @param id The user's unique identifier.
     * @return True if the user exists, otherwise false.
     */
    boolean existsById(UUID id);

    /**
     * Checks whether a user exists using the identifier.
     *
     * @param email The user's unique email.
     * @return True if the user exists, otherwise false.
     */
    boolean existsByEmail(String email);

    /**
     * Counts the number of users.
     *
     * @return The number of users.
     */
    long count();

    /**
     * Creates a new user account.
     *
     * @param payload The payload data from which the user is created.
     * @return The newly created user.
     * @throws EmailAlreadyInUseException Thrown if an account is already using the specified email.
     */
    User create(UserCreationPayload payload) throws EmailAlreadyInUseException;

    /**
     * Partially updates an existing user based on the identifier.
     *
     * @param id      The user's unique identifier.
     * @param payload Payload with the optional changes.
     * @return The updated user.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    User updateById(UUID id, UserUpdatePayload payload) throws EntityNotFoundException;

    /**
     * Partially updates an existing user based on the email.
     *
     * @param email   The user's unique email.
     * @param payload Payload with the optional changes.
     * @return The updated user.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    User updateByEmail(String email, UserUpdatePayload payload) throws EntityNotFoundException;

    void assignRoleById(UUID userId, UUID roleId) throws EntityNotFoundException;

    void assignRoleByEmail(String email, UUID roleId) throws EntityNotFoundException;

    void removeRoleById(UUID userId, UUID roleId) throws EntityNotFoundException;

    void removeRoleByEmail(String email, UUID roleId) throws EntityNotFoundException;

    /**
     * Deletes a user account using the identifier.
     *
     * @param id The user's unique identifier.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void deleteById(UUID id) throws EntityNotFoundException;

    /**
     * Deletes a user account using the email.
     *
     * @param email The user's unique email.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void deleteByEmail(String email) throws EntityNotFoundException;
}
