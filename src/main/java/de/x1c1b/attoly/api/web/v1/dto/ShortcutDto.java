package de.x1c1b.attoly.api.web.v1.dto;

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
public class ShortcutDto {

    private UUID id;
    private String tag;
    private String url;
    private OffsetDateTime createdAt;
    private boolean anonymous;
}
