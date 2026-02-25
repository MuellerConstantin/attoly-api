package de.mueller_constantin.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLOperator;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.UserDtoMapper;
import de.mueller_constantin.attoly.api.web.v1.dto.rsql.UserJpaRSQLFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/amc")
public class UserAMCController {
    private final UserService userService;
    private final UserDtoMapper userMapper;

    @Autowired
    public UserAMCController(UserService userService,
                             UserDtoMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    PageDto<PrincipalDto> findAll(@PageableDefault Pageable pageable,
                                  @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<User> specification = rootNode.accept(new JpaRSQLVisitor<>(new UserJpaRSQLFieldMapper()));
            var users = userService.findAll(specification, pageable);
            return userMapper.mapToPrincipalDto(users);
        } else {
            var users = userService.findAll(pageable);
            return userMapper.mapToPrincipalDto(users);
        }
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    PrincipalDto findById(@PathVariable("id") UUID id) {
        var user = userService.findById(id);

        return userMapper.mapToPrincipalDto(user);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
    }
}
