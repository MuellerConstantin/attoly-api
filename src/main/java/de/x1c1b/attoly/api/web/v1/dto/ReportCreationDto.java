package de.x1c1b.attoly.api.web.v1.dto;

import de.x1c1b.attoly.api.web.v1.dto.validation.EnumValues;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportCreationDto {

    @NotNull
    @EnumValues({"SPAM", "PHISHING", "MALWARE", "DEFACEMENT"})
    private String reason;

    @Size(max = 2000)
    @NotNull
    @NotEmpty
    private String comment;
}
