package de.x1c1b.attoly.api.web.v1.dto.mapper;

import de.x1c1b.attoly.api.domain.model.Report;
import de.x1c1b.attoly.api.domain.payload.ReportCreationPayload;
import de.x1c1b.attoly.api.web.v1.dto.PageDto;
import de.x1c1b.attoly.api.web.v1.dto.ReportCreationDto;
import de.x1c1b.attoly.api.web.v1.dto.ReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportCreationPayload mapToPayload(ReportCreationDto dto);

    ReportDto mapToDto(Report report);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "perPage", source = "size")
    PageDto<ReportDto> mapToDto(Page<Report> reports);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
