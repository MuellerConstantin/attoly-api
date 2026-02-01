package de.mueller_constantin.attoly.api.web.v1;

import de.mueller_constantin.attoly.api.domain.PasswordResetService;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.domain.UserVerificationService;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.payload.UserCreationPayload;
import de.mueller_constantin.attoly.api.domain.payload.UserUpdatePayload;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Locale;

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

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto create(@RequestBody @Valid RegistrationDto dto) {
        UserCreationPayload payload = userMapper.mapToPayload(dto);
        User user = userService.create(payload);

        return userMapper.mapToDto(user);
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

    @PatchMapping("/user/me/password")
    UserDto changeCurrentUserPassword(@CurrentPrincipal Principal principal, @RequestBody @Valid ChangePasswordDto dto) {
        User user = userService.changePasswordByEmail(principal.getEmail(), dto.getCurrentPassword(), dto.getNewPassword());

        return userMapper.mapToDto(user);
    }

    @PostMapping("/user/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void verifyUser(@RequestBody @Valid UserVerificationDto dto) {
        userVerificationService.verifyByToken(dto.getVerificationToken());
    }

    @GetMapping("/user/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendUserVerificationMessage(@RequestParam(name = "email") String email, Locale locale) {
        userVerificationService.sendVerificationMessageByEmail(email, locale);
    }

    @PostMapping("/user/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resetPassword(@RequestBody @Valid UserResetDto dto) {
        passwordResetService.resetByToken(dto.getResetToken(), dto.getPassword());
    }

    @GetMapping("/user/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendPasswordResetMessage(@RequestParam(name = "email") String email, Locale locale) {
        passwordResetService.sendResetMessageByEmail(email, locale);
    }
}
