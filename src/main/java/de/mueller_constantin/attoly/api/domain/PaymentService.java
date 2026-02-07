package de.mueller_constantin.attoly.api.domain;

import com.stripe.model.Invoice;
import com.stripe.model.Subscription;

import java.util.UUID;

public interface PaymentService {
    void activateSubscription(UUID id, String customerId, Subscription subscription, String eventId);

    void deactivateSubscription(Subscription subscription, String eventId);

    void markPaymentSucceeded(Invoice invoice, String eventId);

    void markPaymentFailed(Invoice invoice, String eventId);
}
