package de.mueller_constantin.attoly.api.domain.result;

import de.mueller_constantin.attoly.api.repository.model.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoleResult {
    private RoleName name;
}
