package de.mueller_constantin.attoly.api.domain;

import com.stripe.model.Subscription;
import de.mueller_constantin.attoly.api.domain.model.Plan;

import java.time.Instant;
import java.util.UUID;

public interface PaymentService {
    void activateSubscription(UUID id, String customerId, Subscription subscription, String eventId);

    void deactivateSubscription(Subscription subscription, String eventId);

    void markPaymentSucceeded(String customerId, Instant periodStart, Instant periodEnd, String eventId);

    void markPaymentFailed(String customerId, String eventId);
}
