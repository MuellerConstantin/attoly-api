package de.mueller_constantin.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.mueller_constantin.attoly.api.domain.ShortcutService;
import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.payload.ShortcutCreationPayload;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLOperator;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.PageDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutCreationDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.ShortcutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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

    @GetMapping("/shortcuts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    PageDto<ShortcutDto> findAll(@PageableDefault Pageable pageable,
                                 @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<Shortcut> specification = rootNode.accept(new JpaRSQLVisitor<>());
            Page<Shortcut> shortcuts = shortcutService.findAll(specification, pageable);
            return shortcutMapper.mapToDto(shortcuts);
        } else {
            Page<Shortcut> shortcuts = shortcutService.findAll(pageable);
            return shortcutMapper.mapToDto(shortcuts);
        }
    }

    @PostMapping("/shortcuts")
    @ResponseStatus(HttpStatus.CREATED)
    ShortcutDto create(@RequestBody @Valid ShortcutCreationDto dto) {
        ShortcutCreationPayload payload = shortcutMapper.mapToPayload(dto);
        Shortcut shortcut = shortcutService.create(payload);

        return shortcutMapper.mapToDto(shortcut);
    }

    @DeleteMapping("/shortcuts/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') || @domainMethodSecurityEvaluator.isShortcutOwnerOf(#tag)")
    void deleteById(@PathVariable("tag") String tag) {
        shortcutService.deleteByTag(tag);
    }

    @GetMapping("/shortcuts/{tag}")
    ShortcutDto findByTag(@PathVariable("tag") String tag) {
        Shortcut shortcut = shortcutService.findByTag(tag);
        return shortcutMapper.mapToDto(shortcut);
    }

    @GetMapping("/user/me/shortcuts")
    PageDto<ShortcutDto> findCurrentUser(@CurrentPrincipal Principal principal,
                                         @PageableDefault Pageable pageable) {
        Page<Shortcut> page = shortcutService.findAllByOwnership(principal.getEmail(), pageable);

        return shortcutMapper.mapToDto(page);
    }
}
