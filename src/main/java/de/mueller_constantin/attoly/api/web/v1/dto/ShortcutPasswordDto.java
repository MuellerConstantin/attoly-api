package de.mueller_constantin.attoly.api.web.v1.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShortcutPasswordDto {
    @NotEmpty
    private String password;
}
