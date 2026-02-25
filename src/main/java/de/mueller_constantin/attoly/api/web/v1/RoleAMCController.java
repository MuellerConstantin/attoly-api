package de.mueller_constantin.attoly.api.web.v1;

import de.mueller_constantin.attoly.api.domain.RoleService;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.repository.model.Role;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.web.v1.dto.RoleDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.RoleDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/amc")
public class RoleAMCController {
    private final RoleService roleService;
    private final UserService userService;
    private final RoleDtoMapper roleMapper;

    @Autowired
    public RoleAMCController(RoleService roleService, UserService userService, RoleDtoMapper roleMapper) {
        this.roleService = roleService;
        this.userService = userService;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    List<RoleDto> findAll() {
        var roles = roleService.findAll();
        return roleMapper.mapToDto(roles);
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    RoleDto findById(@PathVariable("id") UUID id) {
        var role = roleService.findById(id);

        return roleMapper.mapToDto(role);
    }

    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    List<RoleDto> findAllByUser(@PathVariable("userId") UUID userId) {
        var user = userService.findById(userId);
        return roleMapper.mapToDto(user.getRoles());
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteByIdAndUser(@PathVariable("userId") UUID userId,
                           @PathVariable("roleId") UUID roleId) {
        userService.removeRoleById(userId, roleId);
    }

    @PostMapping("/users/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void addByIdAndUser(@PathVariable("userId") UUID userId,
                        @PathVariable("roleId") UUID roleId) {
        userService.assignRoleById(userId, roleId);
    }
}
