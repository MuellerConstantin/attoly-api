package de.x1c1b.attoly.api.web.v1;

import de.x1c1b.attoly.api.domain.RoleService;
import de.x1c1b.attoly.api.domain.UserService;
import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.security.CurrentPrincipal;
import de.x1c1b.attoly.api.security.Principal;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.RoleDto;
import de.x1c1b.attoly.api.web.v1.dto.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;
    private final UserService userService;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(RoleService roleService, UserService userService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.userService = userService;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    PageDto<RoleDto> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") int selectedPage,
                             @RequestParam(value = "perPage", required = false, defaultValue = "25") int perPage) {
        Page<Role> page = roleService.findAll(PageRequest.of(selectedPage, perPage));

        return roleMapper.mapToDto(page);
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    RoleDto findById(@PathVariable("id") UUID id) {
        Role role = roleService.findById(id);

        return roleMapper.mapToDto(role);
    }

    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    Set<RoleDto> findAllByUser(@PathVariable("userId") UUID userId) {
        User user = userService.findById(userId);

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

    @GetMapping("/user/me/roles")
    Set<RoleDto> findCurrentUser(@CurrentPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getEmail());

        return roleMapper.mapToDto(user.getRoles());
    }
}
