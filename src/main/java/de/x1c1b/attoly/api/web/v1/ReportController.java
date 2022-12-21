package de.x1c1b.attoly.api.web.v1;

import de.x1c1b.attoly.api.domain.ReportService;
import de.x1c1b.attoly.api.domain.model.Report;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.ReportCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.ReportDto;
import de.x1c1b.attoly.api.web.v1.dto.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;
    private final ReportMapper reportMapper;

    @Autowired
    public ReportController(ReportService reportService, ReportMapper reportMapper) {
        this.reportService = reportService;
        this.reportMapper = reportMapper;
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    PageDto<ReportDto> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") int selectedPage,
                               @RequestParam(value = "perPage", required = false, defaultValue = "25") int perPage) {
        Page<Report> page = reportService.findAll(PageRequest.of(selectedPage, perPage));

        return reportMapper.mapToDto(page);
    }

    @GetMapping("/reports/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    ReportDto findById(@PathVariable("id") UUID id) {
        Report report = reportService.findById(id);
        return reportMapper.mapToDto(report);
    }

    @PostMapping("/shortcuts/{tag}/reports")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reportShortcut(@PathVariable("tag") String tag, @Valid @RequestBody ReportCreationDto dto) {
        reportService.create(tag, reportMapper.mapToPayload(dto));
    }

    @DeleteMapping("/reports/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    void deleteById(@PathVariable("id") UUID id) {
        reportService.deleteById(id);
    }
}
