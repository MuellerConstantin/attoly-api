package de.mueller_constantin.attoly.api.domain.result.mapper;

import de.mueller_constantin.attoly.api.domain.result.RoleResult;
import de.mueller_constantin.attoly.api.repository.model.Role;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleResultMapper {
    RoleResult mapToResult(Role entity);

    List<RoleResult> mapToResult(Collection<Role> roles);

    default Page<RoleResult> mapToResult(Page<Role> page) {
        return page.map(this::mapToResult);
    }
}
