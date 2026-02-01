package de.mueller_constantin.attoly.api.web.v1.dto;

import de.mueller_constantin.attoly.api.web.v1.dto.validation.NullOrNotEmpty;
import de.mueller_constantin.attoly.api.web.v1.dto.validation.Password;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangePasswordDto {
    @NotEmpty
    private String currentPassword;

    @NotEmpty
    @Password
    private String newPassword;
}
