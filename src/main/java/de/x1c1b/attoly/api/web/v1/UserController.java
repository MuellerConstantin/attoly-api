package de.x1c1b.attoly.api.web.v1;

import de.x1c1b.attoly.api.domain.UserService;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.domain.payload.UserCreationPayload;
import de.x1c1b.attoly.api.domain.payload.UserUpdatePayload;
import de.x1c1b.attoly.api.security.CurrentPrincipal;
import de.x1c1b.attoly.api.security.Principal;
import de.x1c1b.attoly.api.web.v1.dto.RegistrationDto;
import de.x1c1b.attoly.api.web.v1.dto.UserDto;
import de.x1c1b.attoly.api.web.v1.dto.UserUpdateDto;
import de.x1c1b.attoly.api.web.v1.dto.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto create(@RequestBody @Valid RegistrationDto dto) {
        UserCreationPayload payload = userMapper.mapToPayload(dto);
        User user = userService.create(payload);

        return userMapper.mapToDto(user);
    }

    @PatchMapping("/users/{id}")
    UserDto updateById(@PathVariable("id") UUID id, @RequestBody @Valid UserUpdateDto dto) {
        UserUpdatePayload payload = userMapper.mapToPayload(dto);
        User user = userService.updateById(id, payload);

        return userMapper.mapToDto(user);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
    }

    @GetMapping("/user/me")
    UserDto findCurrentUser(@CurrentPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getEmail());

        return userMapper.mapToDto(user);
    }
}
