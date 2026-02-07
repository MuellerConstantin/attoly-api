package de.mueller_constantin.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.mueller_constantin.attoly.api.domain.ShortcutService;
import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLOperator;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.mueller_constantin.attoly.api.web.v1.dto.PageDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutDetailsDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.ShortcutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amc")
public class ShortcutAMCController {
    private final ShortcutService shortcutService;
    private final ShortcutMapper shortcutMapper;

    @Autowired
    public ShortcutAMCController(ShortcutService shortcutService, ShortcutMapper shortcutMapper) {
        this.shortcutService = shortcutService;
        this.shortcutMapper = shortcutMapper;
    }

    @GetMapping("/shortcuts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    PageDto<ShortcutDetailsDto> findAll(@PageableDefault Pageable pageable,
                                            @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<Shortcut> specification = rootNode.accept(new JpaRSQLVisitor<>());
            Page<Shortcut> shortcuts = shortcutService.findAll(specification, pageable);
            return shortcutMapper.mapToDetailsDto(shortcuts);
        } else {
            Page<Shortcut> shortcuts = shortcutService.findAll(pageable);
            return shortcutMapper.mapToDetailsDto(shortcuts);
        }
    }

    @DeleteMapping("/shortcuts/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    void deleteById(@PathVariable("tag") String tag) {
        shortcutService.deleteByTag(tag);
    }
}
