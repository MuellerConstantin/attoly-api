package de.mueller_constantin.attoly.api.web.v1;

import de.mueller_constantin.attoly.api.domain.ComplaintService;
import de.mueller_constantin.attoly.api.web.v1.dto.ComplaintCreationDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.ComplaintDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ComplaintController {
    private final ComplaintService complaintService;
    private final ComplaintDtoMapper complaintMapper;

    @Autowired
    public ComplaintController(ComplaintService complaintService, ComplaintDtoMapper complaintMapper) {
        this.complaintService = complaintService;
        this.complaintMapper = complaintMapper;
    }

    @PostMapping("/shortcuts/{tag}/complaints")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reportShortcut(@PathVariable("tag") String tag, @Valid @RequestBody ComplaintCreationDto dto) {
        complaintService.create(tag, complaintMapper.mapToPayload(dto));
    }
}
