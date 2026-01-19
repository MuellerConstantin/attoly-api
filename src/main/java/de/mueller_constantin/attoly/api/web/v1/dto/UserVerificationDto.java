package de.mueller_constantin.attoly.api.web.v1.dto;

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
public class UserVerificationDto {

    @NotNull
    @NotEmpty
    private String verificationToken;
}
