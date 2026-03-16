package de.mueller_constantin.attoly.api.web.v1.dto;

import de.mueller_constantin.attoly.api.web.v1.dto.validation.NullOrNotEmpty;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShortcutCreationDto {
    @NotNull
    @URL
    private String url;

    private boolean permanent;
    private OffsetDateTime expiresAt;
    private String password;

    public Optional<@Future OffsetDateTime> getExpiresAt() {
        return Optional.ofNullable(this.expiresAt);
    }

    public Optional<@NullOrNotEmpty @Length(max=64) String> getPassword() {
        return Optional.ofNullable(this.password);
    }
}
