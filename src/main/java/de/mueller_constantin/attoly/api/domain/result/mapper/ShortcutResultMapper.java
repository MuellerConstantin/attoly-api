package de.mueller_constantin.attoly.api.domain.result.mapper;

import de.mueller_constantin.attoly.api.domain.result.ShortcutResult;
import de.mueller_constantin.attoly.api.repository.model.Shortcut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ShortcutResultMapper {
    @Mapping(target = "createdBy", source = "createdBy.id")
    ShortcutResult mapToResult(Shortcut shortcut);

    List<ShortcutResult> mapToResult(Collection<Shortcut> shortcuts);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    default Page<ShortcutResult> mapToResult(Page<Shortcut> page) {
        return page.map(this::mapToResult);
    }
}
