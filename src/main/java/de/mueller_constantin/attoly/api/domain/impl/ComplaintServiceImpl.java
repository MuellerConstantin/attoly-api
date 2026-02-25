package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.ComplaintService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.result.ComplaintResult;
import de.mueller_constantin.attoly.api.domain.result.mapper.ComplaintResultMapper;
import de.mueller_constantin.attoly.api.repository.ShortcutRepository;
import de.mueller_constantin.attoly.api.repository.model.Complaint;
import de.mueller_constantin.attoly.api.repository.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.payload.ComplaintCreationPayload;
import de.mueller_constantin.attoly.api.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final ShortcutRepository shortcutRepository;
    private final ComplaintResultMapper complaintResultMapper;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                ShortcutRepository shortcutRepository,
                                ComplaintResultMapper complaintResultMapper) {
        this.complaintRepository = complaintRepository;
        this.shortcutRepository = shortcutRepository;
        this.complaintResultMapper = complaintResultMapper;
    }

    @Override
    public List<ComplaintResult> findAll() {
        var complaints = complaintRepository.findAll();
        return complaintResultMapper.mapToResult(complaints);
    }

    @Override
    public Page<ComplaintResult> findAll(Pageable pageable) {
        var complaints = complaintRepository.findAll(pageable);
        return complaintResultMapper.mapToResult(complaints);
    }

    @Override
    public Page<ComplaintResult> findAll(Specification<Complaint> specification, Pageable pageable) {
        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false));
        var complaints = complaintRepository.findAll(specification, pageable);
        return complaintResultMapper.mapToResult(complaints);
    }

    @Override
    public Page<ComplaintResult> findAllByShortcut(String tag, Pageable pageable) {
        var complaints = complaintRepository.findByShortcut(tag, pageable);
        return complaintResultMapper.mapToResult(complaints);
    }

    @Override
    public Page<ComplaintResult> findAllByShortcut(UUID id, Pageable pageable) {
        var complaints = complaintRepository.findByShortcut(id, pageable);
        return complaintResultMapper.mapToResult(complaints);
    }

    @Override
    public ComplaintResult findById(UUID id) throws EntityNotFoundException {
        var complaint = complaintRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return complaintResultMapper.mapToResult(complaint);
    }

    @Override
    @Transactional
    public ComplaintResult create(String tag, ComplaintCreationPayload payload) throws EntityNotFoundException {
        Shortcut shortcut = shortcutRepository.findByTag(tag).orElseThrow(EntityNotFoundException::new);
        return create(shortcut, payload);
    }

    @Override
    @Transactional
    public ComplaintResult create(UUID id, ComplaintCreationPayload payload) throws EntityNotFoundException {
        Shortcut shortcut = shortcutRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return create(shortcut, payload);
    }

    protected ComplaintResult create(Shortcut shortcut, ComplaintCreationPayload payload) {
        Complaint complaint = Complaint.builder()
                .shortcut(shortcut)
                .comment(payload.getComment())
                .reason(Complaint.Reason.valueOf(payload.getReason()))
                .build();

        complaintRepository.save(complaint);
        return complaintResultMapper.mapToResult(complaint);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        var complaint = complaintRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        delete(complaint);
    }

    protected void delete(Complaint complaint) throws EntityNotFoundException {
        complaintRepository.deleteSoft(complaint);
    }
}
