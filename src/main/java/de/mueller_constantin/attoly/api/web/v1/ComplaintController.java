package de.mueller_constantin.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.mueller_constantin.attoly.api.domain.ComplaintService;
import de.mueller_constantin.attoly.api.domain.model.Complaint;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLOperator;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.mueller_constantin.attoly.api.web.v1.dto.ComplaintCreationDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ComplaintDto;
import de.mueller_constantin.attoly.api.web.v1.dto.PageDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.ComplaintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final ComplaintMapper complaintMapper;

    @Autowired
    public ComplaintController(ComplaintService complaintService, ComplaintMapper complaintMapper) {
        this.complaintService = complaintService;
        this.complaintMapper = complaintMapper;
    }

    @GetMapping("/complaints")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    PageDto<ComplaintDto> findAll(@PageableDefault Pageable pageable,
                                  @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<Complaint> specification = rootNode.accept(new JpaRSQLVisitor<>());
            Page<Complaint> reports = complaintService.findAll(specification, pageable);
            return complaintMapper.mapToDto(reports);
        } else {
            Page<Complaint> reports = complaintService.findAll(pageable);
            return complaintMapper.mapToDto(reports);
        }
    }

    @GetMapping("/complaints/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    ComplaintDto findById(@PathVariable("id") UUID id) {
        Complaint complaint = complaintService.findById(id);
        return complaintMapper.mapToDto(complaint);
    }

    @PostMapping("/shortcuts/{tag}/complaints")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reportShortcut(@PathVariable("tag") String tag, @Valid @RequestBody ComplaintCreationDto dto) {
        complaintService.create(tag, complaintMapper.mapToPayload(dto));
    }

    @DeleteMapping("/complaints/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    void deleteById(@PathVariable("id") UUID id) {
        complaintService.deleteById(id);
    }
}
