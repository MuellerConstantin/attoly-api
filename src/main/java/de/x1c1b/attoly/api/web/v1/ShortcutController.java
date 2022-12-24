package de.x1c1b.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.x1c1b.attoly.api.domain.ShortcutService;
import de.x1c1b.attoly.api.domain.model.Shortcut;
import de.x1c1b.attoly.api.domain.payload.ShortcutCreationPayload;
import de.x1c1b.attoly.api.repository.rsql.JpaRSQLOperator;
import de.x1c1b.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.x1c1b.attoly.api.security.CurrentPrincipal;
import de.x1c1b.attoly.api.security.Principal;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.ShortcutCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.ShortcutDto;
import de.x1c1b.attoly.api.web.v1.dto.mapper.ShortcutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    PageDto<ShortcutDto> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") int selectedPage,
                                 @RequestParam(value = "perPage", required = false, defaultValue = "25") int perPage,
                                 @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<Shortcut> specification = rootNode.accept(new JpaRSQLVisitor<>());
            Page<Shortcut> shortcuts = shortcutService.findAll(specification, PageRequest.of(selectedPage, perPage));
            return shortcutMapper.mapToDto(shortcuts);
        } else {
            Page<Shortcut> shortcuts = shortcutService.findAll(PageRequest.of(selectedPage, perPage));
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
                                         @RequestParam(value = "page", required = false, defaultValue = "0") int selectedPage,
                                         @RequestParam(value = "perPage", required = false, defaultValue = "25") int perPage) {
        Page<Shortcut> page = shortcutService.findAllByOwnership(principal.getEmail(), PageRequest.of(selectedPage, perPage));

        return shortcutMapper.mapToDto(page);
    }
}
