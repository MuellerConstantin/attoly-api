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
    public void linkCheckoutCustomer(UUID userId, String customerId, String subscriptionId, String eventId) {
        User user = userRepository.findById(userId).orElseThrow();

        if (eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        user.getBilling().setCustomerId(customerId);
        user.getBilling().setSubscriptionId(subscriptionId);
        user.getBilling().setLastEventId(eventId);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void syncSubscription(Subscription subscription, String eventId) {
        User user = userRepository
                .findByBillingCustomerId(subscription.getCustomer())
                .orElseThrow();

        if (eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        SubscriptionItem item = extractSingleSubscriptionItem(subscription);

        Instant currentPeriodStart = item.getCurrentPeriodStart() != null
                ? Instant.ofEpochSecond(item.getCurrentPeriodStart())
                : null;

        Instant currentPeriodEnd = item.getCurrentPeriodEnd() != null
                ? Instant.ofEpochSecond(item.getCurrentPeriodEnd())
                : null;

        String priceId = item.getPrice() != null ? item.getPrice().getId() : null;
        SubscriptionPlan plan = resolvePlanSafely(priceId, subscription.getStatus());

        user.getBilling().setSubscriptionId(subscription.getId());
        user.getBilling().setStatus(mapStripeStatus(subscription.getStatus()));
        user.getBilling().setCurrentPeriodStart(currentPeriodStart);
        user.getBilling().setCurrentPeriodEnd(currentPeriodEnd);
        user.getBilling().setLastEventId(eventId);

        user.setPlan(plan);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateSubscription(Subscription subscription, String eventId) {
        User user = userRepository
                .findByBillingCustomerId(subscription.getCustomer())
                .orElseThrow();

        if (eventId != null && eventId.equals(user.getBilling().getLastEventId())) {
            return;
        }

        user.getBilling().setStatus(SubscriptionStatus.CANCELED);
        user.getBilling().setSubscriptionId(null);
        user.getBilling().setCurrentPeriodStart(null);
        user.getBilling().setCurrentPeriodEnd(null);
        user.getBilling().setLastEventId(eventId);

        user.setPlan(SubscriptionPlan.FREE);

        userRepository.save(user);
    }

    private SubscriptionItem extractSingleSubscriptionItem(Subscription subscription) {
        if (subscription.getItems() == null
                || subscription.getItems().getData() == null
                || subscription.getItems().getData().isEmpty()) {
            throw new IllegalStateException("Stripe subscription contains no subscription items");
        }

        return subscription.getItems().getData().get(0);
    }

    private SubscriptionPlan resolvePlanSafely(String priceId, String stripeStatus) {
        if (priceId == null) {
            if (isTerminalOrFreeLikeStatus(stripeStatus)) {
                return SubscriptionPlan.FREE;
            }

            throw new IllegalStateException("Stripe subscription item has no price id");
        }

        return stripeProperties.resolvePlan(priceId);
    }

    private boolean isTerminalOrFreeLikeStatus(String stripeStatus) {
        return "canceled".equals(stripeStatus)
                || "incomplete_expired".equals(stripeStatus)
                || "unpaid".equals(stripeStatus);
    }

    private SubscriptionStatus mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "trialing", "active" -> SubscriptionStatus.ACTIVE;
            case "incomplete", "past_due", "paused" -> SubscriptionStatus.PAST_DUE;
            case "canceled", "incomplete_expired", "unpaid" -> SubscriptionStatus.CANCELED;
            default -> throw new IllegalArgumentException("Unsupported Stripe subscription status: " + stripeStatus);
        };
    }
}
