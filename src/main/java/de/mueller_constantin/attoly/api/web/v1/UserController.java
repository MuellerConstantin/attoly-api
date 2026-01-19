package de.mueller_constantin.attoly.api.web.v1;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.mueller_constantin.attoly.api.domain.PasswordResetService;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.domain.UserVerificationService;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.payload.UserCreationPayload;
import de.mueller_constantin.attoly.api.domain.payload.UserUpdatePayload;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLOperator;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLVisitor;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.UserMapper;
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
public class UserController {

    private final UserService userService;
    private final UserVerificationService userVerificationService;
    private final PasswordResetService passwordResetService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService,
                          UserVerificationService userVerificationService,
                          PasswordResetService passwordResetService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userVerificationService = userVerificationService;
        this.passwordResetService = passwordResetService;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    PageDto<PrincipalDto> findAll(@PageableDefault Pageable pageable,
                                  @RequestParam(value = "filter", required = false) String filter) {
        if (filter != null && !filter.isEmpty()) {
            Node rootNode = new RSQLParser(JpaRSQLOperator.getOperators()).parse(filter);
            Specification<User> specification = rootNode.accept(new JpaRSQLVisitor<>());
            Page<User> users = userService.findAll(specification, pageable);
            return userMapper.mapToPrincipalDto(users);
        } else {
            Page<User> users = userService.findAll(pageable);
            return userMapper.mapToPrincipalDto(users);
        }
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    PrincipalDto findById(@PathVariable("id") UUID id) {
        User user = userService.findById(id);

        return userMapper.mapToPrincipalDto(user);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto create(@RequestBody @Valid RegistrationDto dto) {
        UserCreationPayload payload = userMapper.mapToPayload(dto);
        User user = userService.create(payload);

        return userMapper.mapToDto(user);
    }

    @PatchMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    PrincipalDto updateById(@PathVariable("id") UUID id, @RequestBody @Valid PrincipalUpdateDto dto) {
        UserUpdatePayload payload = userMapper.mapToPayload(dto);
        User user = userService.updateById(id, payload);

        return userMapper.mapToPrincipalDto(user);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
    }

    @GetMapping("/user/me")
    UserDto findCurrentUser(@CurrentPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getEmail());

        return userMapper.mapToDto(user);
    }

    @DeleteMapping("/user/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCurrentUser(@CurrentPrincipal Principal principal) {
        userService.deleteByEmail(principal.getEmail());
    }

    @PatchMapping("/user/me")
    UserDto updateCurrentUser(@CurrentPrincipal Principal principal, @RequestBody @Valid UserUpdateDto dto) {
        UserUpdatePayload payload = userMapper.mapToPayload(dto);
        User user = userService.updateByEmail(principal.getEmail(), payload);

        return userMapper.mapToDto(user);
    }

    @PostMapping("/user/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void verifyUser(@RequestBody @Valid UserVerificationDto dto) {
        userVerificationService.verifyByToken(dto.getVerificationToken());
    }

    @GetMapping("/user/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendUserVerificationMessage(@RequestParam(name = "email") String email) {
        userVerificationService.sendVerificationMessageByEmail(email);
    }

    @PostMapping("/user/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resetPassword(@RequestBody @Valid UserResetDto dto) {
        passwordResetService.resetByToken(dto.getResetToken(), dto.getPassword());
    }

    @GetMapping("/user/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendPasswordResetMessage(@RequestParam(name = "email") String email) {
        passwordResetService.sendResetMessageByEmail(email);
    }
}
