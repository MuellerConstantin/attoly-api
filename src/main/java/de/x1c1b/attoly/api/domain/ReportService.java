package de.x1c1b.attoly.api.domain;

import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.model.Report;
import de.x1c1b.attoly.api.domain.payload.ReportCreationPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ReportService {

    /**
     * Loads all available reports. This can lead to performance problems.
     *
     * @return The list of all reports.
     */
    List<Report> findAll();

    /**
     * Loads all available reports in individual pages.
     *
     * @param pageable The pagination settings.
     * @return The requested page of reports.
     */
    Page<Report> findAll(Pageable pageable);

    /**
     * Find all reports created for a specific shortcut.
     *
     * @param tag      The shortcuts's unique tag.
     * @param pageable The pagination settings.
     * @return The requested page of reports.
     */
    Page<Report> findAllByShortcut(String tag, Pageable pageable);

    /**
     * Find all reports created for a specific shortcut.
     *
     * @param id       The shortcuts's unique identifier.
     * @param pageable The pagination settings.
     * @return The requested page of reports.
     */
    Page<Report> findAllByShortcut(UUID id, Pageable pageable);

    /**
     * Loads a report by its identifier.
     *
     * @param id The report's unique identifier.
     * @return The loaded report.
     * @throws EntityNotFoundException Thrown if the report cannot be found.
     */
    Report findById(UUID id) throws EntityNotFoundException;

    void create(String tag, ReportCreationPayload payload) throws EntityNotFoundException;

    void create(UUID id, ReportCreationPayload payload) throws EntityNotFoundException;

    /**
     * Deletes a report using the identifier.
     *
     * @param id The report's unique identifier.
     * @throws EntityNotFoundException Thrown if the report cannot be found.
     */
    void deleteById(UUID id) throws EntityNotFoundException;
}
