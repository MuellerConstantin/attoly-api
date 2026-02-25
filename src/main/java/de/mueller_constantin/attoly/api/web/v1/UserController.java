package de.mueller_constantin.attoly.api.web.v1;

import de.mueller_constantin.attoly.api.domain.PasswordResetService;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.domain.UserVerificationService;
import de.mueller_constantin.attoly.api.domain.SubscriptionEntitlementService;
import de.mueller_constantin.attoly.api.domain.result.UsageInfoResult;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.domain.payload.UserCreationPayload;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.UserDtoMapper;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.UsageInfoDtoMapper;
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
    private final UserDtoMapper userMapper;
    private final SubscriptionEntitlementService subscriptionEntitlementService;
    private final UsageInfoDtoMapper usageInfoMapper;

    @Autowired
    public UserController(UserService userService,
                          UserVerificationService userVerificationService,
                          PasswordResetService passwordResetService,
                          UserDtoMapper userMapper,
                          SubscriptionEntitlementService subscriptionEntitlementService,
                          UsageInfoDtoMapper usageInfoMapper) {
        this.userService = userService;
        this.userVerificationService = userVerificationService;
        this.passwordResetService = passwordResetService;
        this.userMapper = userMapper;
        this.subscriptionEntitlementService = subscriptionEntitlementService;
        this.usageInfoMapper = usageInfoMapper;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    MeDto create(@RequestBody @Valid RegistrationDto dto) {
        UserCreationPayload payload = userMapper.mapToPayload(dto);
        var user = userService.create(payload);

        return userMapper.mapToMeDto(user);
    }

    @GetMapping("/user/me")
    MeDto findCurrentUser(@CurrentPrincipal Principal principal) {
        var user = userService.findByEmail(principal.getEmail());

        return userMapper.mapToMeDto(user);
    }

    @DeleteMapping("/user/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCurrentUser(@CurrentPrincipal Principal principal) {
        userService.deleteByEmail(principal.getEmail());
    }

    @PatchMapping("/user/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void changeCurrentUserPassword(@CurrentPrincipal Principal principal, @RequestBody @Valid ChangePasswordDto dto) {
        userService.changePasswordByEmail(principal.getEmail(), dto.getCurrentPassword(), dto.getNewPassword());
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

    @GetMapping("/user/me/usage")
    public UsageInfoDto getCurrentUsage(@CurrentPrincipal Principal principal) {
        var user = userService.findByEmail(principal.getEmail());
        UsageInfoResult usage = subscriptionEntitlementService.getUsageInfoForUser(user.getId());

        return usageInfoMapper.mapToDto(usage);
    }
}
