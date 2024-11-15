package de.x1c1b.attoly.api.web.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorDto {

    private String error;
    private String message;
    private int status;
    private OffsetDateTime timestamp;
    private String path;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Object> details = new ArrayList<>();

    public static class ErrorDtoBuilder {

        public ErrorDtoBuilder detail(Object detail) {
            if (!this.details$set) {
                this.details$value = new ArrayList<>();
                this.details$set = true;
            }

            this.details$value.add(detail);
            return this;
        }
    }
}
