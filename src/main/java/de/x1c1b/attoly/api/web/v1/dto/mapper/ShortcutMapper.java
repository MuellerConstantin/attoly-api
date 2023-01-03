package de.x1c1b.attoly.api.web.v1.dto.mapper;

import de.x1c1b.attoly.api.domain.model.Shortcut;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.domain.payload.ShortcutCreationPayload;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.ShortcutCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.ShortcutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ShortcutMapper {

    ShortcutCreationPayload mapToPayload(ShortcutCreationDto dto);

    @Mapping(target = "anonymous", source = "createdBy", qualifiedByName = "mapToAnonymousFlag")
    ShortcutDto mapToDto(Shortcut shortcut);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<ShortcutDto> mapToDto(Page<Shortcut> shortcuts);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @Named("mapToAnonymousFlag")
    default boolean mapToAnonymousFlag(User createdBy) {
        return createdBy == null;
    }
}
