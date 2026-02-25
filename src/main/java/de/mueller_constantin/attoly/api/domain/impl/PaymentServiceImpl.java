package de.mueller_constantin.attoly.api.domain.impl;

import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import de.mueller_constantin.attoly.api.domain.PaymentService;
import de.mueller_constantin.attoly.api.repository.model.SubscriptionPlan;
import de.mueller_constantin.attoly.api.repository.model.SubscriptionStatus;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.domain.payment.StripeProperties;
import de.mueller_constantin.attoly.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final UserRepository userRepository;
    private final StripeProperties stripeProperties;

    @Autowired
    public PaymentServiceImpl(UserRepository userRepository, StripeProperties stripeProperties) {
        this.userRepository = userRepository;
        this.stripeProperties = stripeProperties;
    }

    @Override
    @Transactional
    public void activateSubscription(UUID id, String customerId, Subscription subscription, String eventId) {
        User user = userRepository.findById(id).orElseThrow();

        if(eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        String subscriptionId = subscription.getId();
        SubscriptionItem item = subscription
                .getItems()
                .getData()
                .get(0);

        Instant currentPeriodStart = Instant.ofEpochSecond(item.getCurrentPeriodStart());
        Instant currentPeriodEnd = Instant.ofEpochSecond(item.getCurrentPeriodEnd());

        String priceId = item.getPrice().getId();
        SubscriptionPlan plan = stripeProperties.resolvePlan(priceId);

        user.getBilling().setCustomerId(customerId);
        user.getBilling().setSubscriptionId(subscriptionId);
        user.getBilling().setStatus(SubscriptionStatus.ACTIVE);
        user.getBilling().setCurrentPeriodStart(currentPeriodStart);
        user.getBilling().setCurrentPeriodEnd(currentPeriodEnd);
        user.getBilling().setLastEventId(eventId);

        user.setPlan(plan);

        userRepository.save(user);
    }

    @Override
    public void deactivateSubscription(Subscription subscription, String eventId) {
        User user = userRepository.findByBillingSubscriptionId(subscription.getId()).orElseThrow();

        if(eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        SubscriptionItem item = subscription
                .getItems()
                .getData()
                .get(0);

        user.getBilling().setStatus(SubscriptionStatus.CANCELED);
        user.getBilling().setSubscriptionId(null);
        user.getBilling().setCurrentPeriodStart(null);
        user.getBilling().setCurrentPeriodEnd(null);
        user.setPlan(SubscriptionPlan.FREE);

        userRepository.save(user);
    }

    @Override
    public void markPaymentSucceeded(Invoice invoice, String eventId) {
        String customerId = invoice.getCustomer();
        User user = userRepository
                .findByBillingCustomerId(customerId)
                .orElseThrow();

        if (eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        Instant periodStart = Instant.ofEpochSecond(invoice.getPeriodStart());
        Instant periodEnd = Instant.ofEpochSecond(invoice.getPeriodEnd());

        user.getBilling().setStatus(SubscriptionStatus.ACTIVE);
        user.getBilling().setCurrentPeriodStart(periodStart);
        user.getBilling().setCurrentPeriodEnd(periodEnd);
        user.getBilling().setLastEventId(eventId);

        userRepository.save(user);
    }

    @Override
    public void markPaymentFailed(Invoice invoice, String eventId) {
        String customerId = invoice.getCustomer();
        User user = userRepository
                .findByBillingCustomerId(customerId)
                .orElseThrow();

        if (eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        user.getBilling().setStatus(SubscriptionStatus.PAST_DUE);
        user.getBilling().setLastEventId(eventId);

        userRepository.save(user);
    }
}
