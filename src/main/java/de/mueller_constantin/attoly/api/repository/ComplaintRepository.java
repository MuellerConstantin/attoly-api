package de.mueller_constantin.attoly.api.repository;

import de.mueller_constantin.attoly.api.domain.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ComplaintRepository extends BaseRepository<Complaint, UUID> {

    @Transactional(readOnly = true)
    @Query("SELECT c FROM Complaint c WHERE c.deleted = false AND c.shortcut.id = ?1")
    Page<Complaint> findByShortcut(UUID shortcutId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT c FROM Complaint c WHERE c.deleted = false AND c.shortcut.tag = ?1")
    Page<Complaint> findByShortcut(String tag, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT count(c) FROM Complaint c WHERE c.deleted = false AND c.shortcut.id = ?1")
    long countByShortcut(UUID shortcutId);

    @Transactional(readOnly = true)
    @Query("SELECT count(c) FROM Complaint c WHERE c.deleted = false AND c.shortcut.tag = ?1")
    long countByShortcut(String tag);
}
