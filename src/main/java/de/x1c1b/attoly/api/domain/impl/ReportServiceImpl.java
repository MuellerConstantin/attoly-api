package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.ReportService;
import de.x1c1b.attoly.api.domain.ShortcutService;
import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.model.Report;
import de.x1c1b.attoly.api.domain.model.Shortcut;
import de.x1c1b.attoly.api.domain.payload.ReportCreationPayload;
import de.x1c1b.attoly.api.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ShortcutService shortcutService;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, ShortcutService shortcutService) {
        this.reportRepository = reportRepository;
        this.shortcutService = shortcutService;
    }

    @Override
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    @Override
    public Page<Report> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    @Override
    public Page<Report> findAllByShortcut(String tag, Pageable pageable) {
        return reportRepository.findByShortcut(tag, pageable);
    }

    @Override
    public Page<Report> findAllByShortcut(UUID id, Pageable pageable) {
        return reportRepository.findByShortcut(id, pageable);
    }

    @Override
    public Report findById(UUID id) throws EntityNotFoundException {
        return reportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void create(String tag, ReportCreationPayload payload) throws EntityNotFoundException {
        Shortcut shortcut = shortcutService.findByTag(tag);
        create(shortcut, payload);
    }

    @Override
    public void create(UUID id, ReportCreationPayload payload) throws EntityNotFoundException {
        Shortcut shortcut = shortcutService.findById(id);
        create(shortcut, payload);
    }

    protected void create(Shortcut shortcut, ReportCreationPayload payload) {
        Report report = Report.builder()
                .shortcut(shortcut)
                .comment(payload.getComment())
                .reason(Report.Reason.valueOf(payload.getReason()))
                .build();

        reportRepository.save(report);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        delete(findById(id));
    }

    protected void delete(Report report) throws EntityNotFoundException {
        reportRepository.deleteSoft(report);
    }
}
