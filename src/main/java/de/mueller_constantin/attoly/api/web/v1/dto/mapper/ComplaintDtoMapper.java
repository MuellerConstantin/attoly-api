package de.mueller_constantin.attoly.api.web.v1.dto.mapper;

import de.mueller_constantin.attoly.api.domain.result.ComplaintResult;
import de.mueller_constantin.attoly.api.domain.payload.ComplaintCreationPayload;
import de.mueller_constantin.attoly.api.web.v1.dto.ComplaintCreationDto;
import de.mueller_constantin.attoly.api.web.v1.dto.ComplaintDto;
import de.mueller_constantin.attoly.api.web.v1.dto.PageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ComplaintDtoMapper {
    ComplaintCreationPayload mapToPayload(ComplaintCreationDto dto);

    ComplaintDto mapToDto(ComplaintResult complaint);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<ComplaintDto> mapToDto(Page<ComplaintResult> reports);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
