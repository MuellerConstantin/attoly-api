package de.x1c1b.attoly.api.web.v1.dto;

import de.x1c1b.attoly.api.web.v1.dto.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResetDto {

    @NotNull
    @NotEmpty
    private String resetToken;

    @NotNull
    @Password
    private String password;
}
