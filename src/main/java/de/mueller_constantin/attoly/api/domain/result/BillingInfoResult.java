package de.mueller_constantin.attoly.api.domain.result;

import de.mueller_constantin.attoly.api.repository.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BillingInfoResult {
    private String customerId;
    private String subscriptionId;
    private SubscriptionStatus status;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
}
