package de.x1c1b.attoly.api.web.v1.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvalidParameterErrorDetails {

    private String parameter;
    private String message;
}
