package de.mueller_constantin.attoly.api.domain;

import com.stripe.model.Invoice;
import com.stripe.model.Subscription;

import java.util.UUID;

public interface PaymentService {
    void linkCheckoutCustomer(UUID userId, String customerId, String subscriptionId, String eventId);

    void syncSubscription(Subscription subscription, String eventId);

    void deactivateSubscription(Subscription subscription, String eventId);
}
