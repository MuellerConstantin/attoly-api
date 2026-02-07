package de.mueller_constantin.attoly.api.web.v1.dto.mapper;

import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.payload.ShortcutCreationPayload;
import de.mueller_constantin.attoly.api.web.v1.dto.PageDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutCreationDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutDetailsDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ShortcutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ShortcutMapper {

    ShortcutCreationPayload mapToPayload(ShortcutCreationDto dto);

    @Mapping(target = "anonymous", source = "createdBy", qualifiedByName = "mapToAnonymousFlag")
    ShortcutDto mapToDto(Shortcut shortcut);

    ShortcutDetailsDto mapToDetailsDto(Shortcut shortcut);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<ShortcutDetailsDto> mapToDetailsDto(Page<Shortcut> shortcuts);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @Named("mapToAnonymousFlag")
    default boolean mapToAnonymousFlag(User createdBy) {
        return createdBy == null;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}
