package de.mueller_constantin.attoly.api.web.v1.dto.mapper;

import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.payload.UserCreationPayload;
import de.mueller_constantin.attoly.api.domain.payload.UserUpdatePayload;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import de.mueller_constantin.attoly.api.web.v1.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreationPayload mapToPayload(RegistrationDto dto);

    UserUpdatePayload mapToPayload(UserUpdateDto dto);

    UserUpdatePayload mapToPayload(PrincipalUpdateDto dto);

    UserDto mapToDto(User entity);

    PrincipalDto mapToPrincipalDto(User entity);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<UserDto> mapToDto(Page<User> users);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<PrincipalDto> mapToPrincipalDto(Page<User> users);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}
