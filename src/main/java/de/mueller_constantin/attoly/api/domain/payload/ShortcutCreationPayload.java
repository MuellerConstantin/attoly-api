package de.mueller_constantin.attoly.api.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShortcutCreationPayload {

    private String url;
}
