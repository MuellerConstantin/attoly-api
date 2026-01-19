package de.mueller_constantin.attoly.api.web.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ComplaintDto {

    private UUID id;
    private String reason;
    private String comment;
    private OffsetDateTime createdAt;
    private String shortcutTag;
}
