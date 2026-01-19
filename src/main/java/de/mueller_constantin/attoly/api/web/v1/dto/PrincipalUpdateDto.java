package de.mueller_constantin.attoly.api.web.v1.dto;

import de.mueller_constantin.attoly.api.web.v1.dto.validation.NullOrNotEmpty;
import de.mueller_constantin.attoly.api.web.v1.dto.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PrincipalUpdateDto {

    private String password;

    private Boolean locked;

    public Optional<@NullOrNotEmpty @Password String> getPassword() {
        return Optional.ofNullable(this.password);
    }

    public Optional<Boolean> getLocked() {
        return Optional.ofNullable(this.locked);
    }
}
