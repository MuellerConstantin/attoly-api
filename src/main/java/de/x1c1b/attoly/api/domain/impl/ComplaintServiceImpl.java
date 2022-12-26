package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.ComplaintService;
import de.x1c1b.attoly.api.domain.ShortcutService;
import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.model.Complaint;
import de.x1c1b.attoly.api.domain.model.Shortcut;
import de.x1c1b.attoly.api.domain.payload.ComplaintCreationPayload;
import de.x1c1b.attoly.api.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ShortcutService shortcutService;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, ShortcutService shortcutService) {
        this.complaintRepository = complaintRepository;
        this.shortcutService = shortcutService;
    }

    @Override
    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    @Override
    public Page<Complaint> findAll(Pageable pageable) {
        return complaintRepository.findAll(pageable);
    }

    @Override
    public Page<Complaint> findAll(Specification<Complaint> specification, Pageable pageable) {
        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false));
        return complaintRepository.findAll(specification, pageable);
    }

    @Override
    public Page<Complaint> findAllByShortcut(String tag, Pageable pageable) {
        return complaintRepository.findByShortcut(tag, pageable);
    }

    @Override
    public Page<Complaint> findAllByShortcut(UUID id, Pageable pageable) {
        return complaintRepository.findByShortcut(id, pageable);
    }

    @Override
    public Complaint findById(UUID id) throws EntityNotFoundException {
        return complaintRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void create(String tag, ComplaintCreationPayload payload) throws EntityNotFoundException {
        Shortcut shortcut = shortcutService.findByTag(tag);
        create(shortcut, payload);
    }

    @Override
    public void create(UUID id, ComplaintCreationPayload payload) throws EntityNotFoundException {
        Shortcut shortcut = shortcutService.findById(id);
        create(shortcut, payload);
    }

    protected void create(Shortcut shortcut, ComplaintCreationPayload payload) {
        Complaint complaint = Complaint.builder()
                .shortcut(shortcut)
                .comment(payload.getComment())
                .reason(Complaint.Reason.valueOf(payload.getReason()))
                .build();

        complaintRepository.save(complaint);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        delete(findById(id));
    }

    protected void delete(Complaint complaint) throws EntityNotFoundException {
        complaintRepository.deleteSoft(complaint);
    }
}
