package de.x1c1b.attoly.api.web.v1.dto.mapper;

import de.x1c1b.attoly.api.domain.payload.ReportCreationPayload;
import de.x1c1b.attoly.api.web.v1.dto.ReportCreationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportCreationPayload mapToPayload(ReportCreationDto dto);
}
