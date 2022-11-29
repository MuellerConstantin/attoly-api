package de.x1c1b.attoly.api.web.v1;

import de.x1c1b.attoly.api.domain.ReportService;
import de.x1c1b.attoly.api.web.v1.dto.ReportCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/shortcuts/{tag}/reports")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reportShortcut(@PathVariable("tag") String tag, @Valid @RequestBody ReportCreationDto dto) {
        reportService.create(tag, reportMapper.mapToPayload(dto));
    }
}
