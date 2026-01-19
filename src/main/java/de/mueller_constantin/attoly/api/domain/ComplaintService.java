package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.model.Complaint;
import de.mueller_constantin.attoly.api.domain.payload.ComplaintCreationPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface ComplaintService {

    /**
     * Loads all available reports. This can lead to performance problems.
     *
     * @return The list of all reports.
     */
    List<Complaint> findAll();

    /**
     * Loads all available reports in individual pages.
     *
     * @param pageable The pagination settings.
     * @return The requested page of reports.
     */
    Page<Complaint> findAll(Pageable pageable);

    /**
     * Loads all available reports in individual pages.
     *
     * @param specification The specification to filter the reports.
     * @param pageable      The pagination settings.
     * @return The requested page of reports.
     */
    Page<Complaint> findAll(Specification<Complaint> specification, Pageable pageable);

    /**
     * Find all reports created for a specific shortcut.
     *
     * @param tag      The shortcuts's unique tag.
     * @param pageable The pagination settings.
     * @return The requested page of reports.
     */
    Page<Complaint> findAllByShortcut(String tag, Pageable pageable);

    /**
     * Find all reports created for a specific shortcut.
     *
     * @param id       The shortcuts's unique identifier.
     * @param pageable The pagination settings.
     * @return The requested page of reports.
     */
    Page<Complaint> findAllByShortcut(UUID id, Pageable pageable);

    /**
     * Loads a report by its identifier.
     *
     * @param id The report's unique identifier.
     * @return The loaded report.
     * @throws EntityNotFoundException Thrown if the report cannot be found.
     */
    Complaint findById(UUID id) throws EntityNotFoundException;

    void create(String tag, ComplaintCreationPayload payload) throws EntityNotFoundException;

    void create(UUID id, ComplaintCreationPayload payload) throws EntityNotFoundException;

    /**
     * Deletes a report using the identifier.
     *
     * @param id The report's unique identifier.
     * @throws EntityNotFoundException Thrown if the report cannot be found.
     */
    void deleteById(UUID id) throws EntityNotFoundException;
}
