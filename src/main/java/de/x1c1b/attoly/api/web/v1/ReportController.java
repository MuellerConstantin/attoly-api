package de.x1c1b.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.x1c1b.attoly.api.domain.ReportService;
import de.x1c1b.attoly.api.domain.model.Report;
import de.x1c1b.attoly.api.repository.rsql.JpaRSQLOperator;
import de.x1c1b.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.ReportCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.ReportDto;
import de.x1c1b.attoly.api.web.v1.dto.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
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
    PageDto<ReportDto> findAll(@PageableDefault Pageable pageable,
                               @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<Report> specification = rootNode.accept(new JpaRSQLVisitor<>());
            Page<Report> reports = reportService.findAll(specification, pageable);
            return reportMapper.mapToDto(reports);
        } else {
            Page<Report> reports = reportService.findAll(pageable);
            return reportMapper.mapToDto(reports);
        }
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
