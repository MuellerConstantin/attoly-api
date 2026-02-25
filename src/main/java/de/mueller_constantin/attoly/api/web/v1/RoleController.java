package de.mueller_constantin.attoly.api.web.v1;

import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.RoleDto;
import de.mueller_constantin.attoly.api.web.v1.dto.mapper.RoleDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final UserService userService;
    private final RoleDtoMapper roleMapper;

    @Autowired
    public RoleController(UserService userService, RoleDtoMapper roleMapper) {
        this.userService = userService;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/user/me/roles")
    List<RoleDto> findCurrentUser(@CurrentPrincipal Principal principal) {
        var user = userService.findByEmail(principal.getEmail());
        return roleMapper.mapToDto(user.getRoles());
    }
}
