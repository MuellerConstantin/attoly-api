package de.mueller_constantin.attoly.api.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingInfo {
    @Column(name = "payment_customer_id")
    private String customerId;

    @Column(name = "payment_subscription_id")
    private String subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_subscription_status")
    private SubscriptionStatus status;

    @Column(name = "payment_current_period_start")
    private Instant currentPeriodStart;

    @Column(name = "payment_current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "payment_last_event_id")
    private String lastEventId;
}
