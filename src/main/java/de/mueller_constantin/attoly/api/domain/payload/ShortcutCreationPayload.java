package de.mueller_constantin.attoly.api.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShortcutCreationPayload {
    private String url;
    private boolean permanent;
    private OffsetDateTime expiresAt;
    private String password;

    public Optional<OffsetDateTime> getExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }
}
