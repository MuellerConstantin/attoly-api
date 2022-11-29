package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ReportRepository extends BaseRepository<Report, UUID> {

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Report r WHERE r.deleted = false AND r.shortcut.id = ?1")
    Page<Report> findByShortcut(UUID shortcutId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Report r WHERE r.deleted = false AND r.shortcut.tag = ?1")
    Page<Report> findByShortcut(String tag, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT count(r) FROM Report r WHERE r.deleted = false AND r.shortcut.id = ?1")
    long countByShortcut(UUID shortcutId);

    @Transactional(readOnly = true)
    @Query("SELECT count(r) FROM Report r WHERE r.deleted = false AND r.shortcut.tag = ?1")
    long countByShortcut(String tag);
}
