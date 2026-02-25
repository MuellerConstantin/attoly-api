package de.mueller_constantin.attoly.api.domain.result.mapper;

import de.mueller_constantin.attoly.api.domain.result.UsageInfoResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsageInfoResultMapper {
    UsageInfoResult mapToResult(UsageInfoResult entity);
}
