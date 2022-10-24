package de.x1c1b.attoly.api.web.v1.dto;

import de.x1c1b.attoly.api.web.v1.dto.validation.NullOrNotEmpty;
import de.x1c1b.attoly.api.web.v1.dto.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdateDto {

    private String password;

    public Optional<@NullOrNotEmpty @Password String> getPassword() {
        return Optional.ofNullable(this.password);
    }
}
