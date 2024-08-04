package de.x1c1b.attoly.api.web.v1.dto;

import de.x1c1b.attoly.api.web.v1.dto.validation.EnumValues;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ComplaintCreationDto {

    @NotNull
    @EnumValues({"SPAM", "PHISHING", "MALWARE", "DEFACEMENT"})
    private String reason;

    @Size(max = 2000)
    @NotNull
    @NotEmpty
    private String comment;
}
