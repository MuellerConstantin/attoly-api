package de.mueller_constantin.attoly.api.domain.result;

import de.mueller_constantin.attoly.api.repository.model.IdentityProvider;
import de.mueller_constantin.attoly.api.repository.model.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResult {
    private UUID id;
    private String email;
    private boolean emailVerified;
    private boolean locked;
    private SubscriptionPlan plan;
    private IdentityProvider identityProvider;
    private String identityProviderId;

    @Builder.Default
    private BillingInfoResult billing = new BillingInfoResult();

    @Builder.Default
    private Set<RoleResult> roles = new HashSet<>();
}
