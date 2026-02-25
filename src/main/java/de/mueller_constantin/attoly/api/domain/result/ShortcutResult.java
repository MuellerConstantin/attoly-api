package de.mueller_constantin.attoly.api.domain.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShortcutResult {
    private UUID id;
    private String tag;
    private String url;
    private boolean permanent;
    private Instant expiresAt;
    private UUID createdBy;
}
