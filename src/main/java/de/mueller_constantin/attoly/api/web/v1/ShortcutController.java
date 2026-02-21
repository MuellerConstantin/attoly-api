package de.mueller_constantin.attoly.api.web.v1;

import de.mueller_constantin.attoly.api.domain.ShortcutService;
import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.payload.ShortcutCreationPayload;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.PageDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutCreationDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutDetailsDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.ShortcutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ShortcutController {
    private final ShortcutService shortcutService;
    private final ShortcutMapper shortcutMapper;

    @Autowired
    public ShortcutController(ShortcutService shortcutService, ShortcutMapper shortcutMapper) {
        this.shortcutService = shortcutService;
        this.shortcutMapper = shortcutMapper;
    }

    @PostMapping("/shortcuts")
    @ResponseStatus(HttpStatus.CREATED)
    ShortcutDto create(@RequestBody @Valid ShortcutCreationDto dto, @CurrentPrincipal Principal principal) {
        ShortcutCreationPayload payload = shortcutMapper.mapToPayload(dto);
        UUID ownerId = principal != null ? principal.getUser().getId() : null;
        Shortcut shortcut = shortcutService.create(payload, ownerId);

        return shortcutMapper.mapToDto(shortcut);
    }

    @DeleteMapping("/user/me/shortcuts/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@domainMethodSecurityEvaluator.isShortcutOwnerOf(#tag)")
    void deleteById(@PathVariable("tag") String tag) {
        shortcutService.deleteByTag(tag);
    }

    @GetMapping("/shortcuts/{tag}")
    ShortcutDto findValidByTag(@PathVariable("tag") String tag) {
        Shortcut shortcut = shortcutService.findValidByTag(tag);
        return shortcutMapper.mapToDto(shortcut);
    }

    @GetMapping("/user/me/shortcuts")
    PageDto<ShortcutDetailsDto> findCurrentUserShortcuts(@CurrentPrincipal Principal principal,
                                                @PageableDefault Pageable pageable) {
        Page<Shortcut> page = shortcutService.findAllByOwnership(principal.getEmail(), pageable);

        return shortcutMapper.mapToDetailsDto(page);
    }
}
