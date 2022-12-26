package de.x1c1b.attoly.api.web.v1.dto.mapper;

import de.x1c1b.attoly.api.domain.model.Complaint;
import de.x1c1b.attoly.api.domain.payload.ComplaintCreationPayload;
import de.x1c1b.attoly.api.web.v1.dto.ComplaintCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.ComplaintDto;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {

    ComplaintCreationPayload mapToPayload(ComplaintCreationDto dto);

    ComplaintDto mapToDto(Complaint complaint);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<ComplaintDto> mapToDto(Page<Complaint> reports);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
