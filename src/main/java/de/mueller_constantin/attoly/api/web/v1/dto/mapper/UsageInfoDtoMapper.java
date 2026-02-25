package de.mueller_constantin.attoly.api.web.v1.dto.mapper;

import de.mueller_constantin.attoly.api.domain.result.UsageInfoResult;
import de.mueller_constantin.attoly.api.web.v1.dto.UsageInfoDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsageInfoDtoMapper {
    UsageInfoDto mapToDto(UsageInfoResult entity);
}
