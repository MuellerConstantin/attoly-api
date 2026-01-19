package de.mueller_constantin.attoly.api.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdatePayload {

    private String password;
    private Boolean locked;

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<Boolean> getLocked() {
        return Optional.ofNullable(locked);
    }
}
