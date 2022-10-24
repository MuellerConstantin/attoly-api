package de.x1c1b.attoly.api.web.v1.dto.mapper;

import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.domain.payload.UserCreationPayload;
import de.x1c1b.attoly.api.domain.payload.UserUpdatePayload;
import de.x1c1b.attoly.api.web.v1.dto.RegistrationDto;
import de.x1c1b.attoly.api.web.v1.dto.UserDto;
import de.x1c1b.attoly.api.web.v1.dto.UserUpdateDto;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreationPayload mapToPayload(RegistrationDto dto);

    UserUpdatePayload mapToPayload(UserUpdateDto dto);

    UserDto mapToDto(User entity);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}
