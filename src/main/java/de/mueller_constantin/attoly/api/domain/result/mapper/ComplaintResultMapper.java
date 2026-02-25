package de.mueller_constantin.attoly.api.domain.result.mapper;

import de.mueller_constantin.attoly.api.domain.result.ComplaintResult;
import de.mueller_constantin.attoly.api.repository.model.Complaint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ComplaintResultMapper {
    @Mapping(target = "shortcutTag", source = "shortcut.tag")
    ComplaintResult mapToResult(Complaint complaint);

    List<ComplaintResult> mapToResult(Collection<Complaint> complaints);

    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    default Page<ComplaintResult> mapToResult(Page<Complaint> page) {
        return page.map(this::mapToResult);
    }
}
