package de.x1c1b.attoly.api.web.v1.dto.mapper;

import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto mapToDto(Role entity);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<RoleDto> mapToDto(Page<Role> roles);

    List<RoleDto> mapToDto(Collection<Role> roles);
}
