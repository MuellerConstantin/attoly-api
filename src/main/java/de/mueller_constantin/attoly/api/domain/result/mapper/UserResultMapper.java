package de.mueller_constantin.attoly.api.domain.result.mapper;

import de.mueller_constantin.attoly.api.domain.result.UserResult;
import de.mueller_constantin.attoly.api.repository.model.IdentityProvider;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserResultMapper {
    UserResult mapToResult(User entity);

    List<UserResult> mapToResult(Collection<User> users);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    default Page<UserResult> mapToResult(Page<User> page) {
        return page.map(this::mapToResult);
    }
}
