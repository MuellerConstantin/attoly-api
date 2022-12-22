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
public class PrincipalDto {

    private UUID id;
    private String email;
    private OffsetDateTime createdAt;
    private boolean emailVerified;
    private boolean locked;
}
