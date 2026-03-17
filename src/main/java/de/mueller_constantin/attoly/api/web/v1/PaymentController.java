package de.mueller_constantin.attoly.api.web.v1;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import de.mueller_constantin.attoly.api.domain.PaymentService;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.domain.exception.AlreadySubscribedException;
import de.mueller_constantin.attoly.api.domain.exception.NoCustomerYetException;
import de.mueller_constantin.attoly.api.repository.model.SubscriptionStatus;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.domain.payment.StripeProperties;
import de.mueller_constantin.attoly.api.security.CurrentPrincipal;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.web.v1.dto.PaymentCheckoutSessionDto;
import de.mueller_constantin.attoly.api.web.v1.dto.PaymentPortalSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    private final UserService userService;
    private final PaymentService paymentService;
    private final StripeProperties stripeProperties;

    @Autowired
    public PaymentController(UserService userService, PaymentService paymentService, StripeProperties stripeProperties) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.stripeProperties = stripeProperties;
    }

    @PostMapping("/payment/session")
    public PaymentCheckoutSessionDto createPaymentSession(@CurrentPrincipal Principal principal, @RequestParam String priceId) throws StripeException {
        var user = userService.findByEmail(principal.getEmail());
        String customerId = user.getBilling().getCustomerId();

        if (user.getBilling().getStatus() == SubscriptionStatus.ACTIVE) {
            throw new AlreadySubscribedException();
        }

        SessionCreateParams.Builder builder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(stripeProperties.getSuccessUrl())
                        .setCancelUrl(stripeProperties.getCancelUrl())
                        .setClientReferenceId(user.getId().toString())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(priceId)
                                        .setQuantity(1L)
                                        .build()
                        );

        if (customerId != null) {
            builder.setCustomer(customerId);
        } else {
            builder.setCustomerEmail(principal.getEmail());
        }

        Session session = Session.create(builder.build());

        return new PaymentCheckoutSessionDto(session.getUrl());
    }

    @PostMapping("/payment/portal")
    public PaymentPortalSessionDto createCustomerPortalSession(@CurrentPrincipal Principal principal) throws StripeException {
        String customerId = userService.findByEmail(principal.getEmail()).getBilling().getCustomerId();

        if(customerId == null) {
            throw new NoCustomerYetException();
        }

        com.stripe.param.billingportal.SessionCreateParams params =
                com.stripe.param.billingportal.SessionCreateParams.builder()
                        .setCustomer(customerId)
                        .setReturnUrl(stripeProperties.getPortalReturnUrl())
                        .build();

        com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);

        return new PaymentPortalSessionDto(session.getUrl());
    }

    @PostMapping("/payment/webhook")
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader)
            throws StripeException {

        Event event = Webhook.constructEvent(
                payload,
                sigHeader,
                stripeProperties.getWebhookSecret()
        );

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) event
                        .getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String clientReferenceId = session.getClientReferenceId();
                String customerId = session.getCustomer();
                String subscriptionId = session.getSubscription();
                String eventId = event.getId();

                if (clientReferenceId == null) {
                    throw new IllegalStateException("checkout.session.completed contains no client_reference_id");
                }
                if (customerId == null) {
                    throw new IllegalStateException("checkout.session.completed contains no customer id");
                }
                if (subscriptionId == null) {
                    throw new IllegalStateException("checkout.session.completed contains no subscription id");
                }

                UUID userId = UUID.fromString(clientReferenceId);

                paymentService.linkCheckoutCustomer(userId, customerId, subscriptionId, eventId);
            }
            case "customer.subscription.created",
                 "customer.subscription.updated" -> {

                Subscription subscription = (Subscription) event
                        .getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String eventId = event.getId();

                paymentService.syncSubscription(subscription, eventId);
            }
            case "customer.subscription.deleted" -> {
                Subscription subscription = (Subscription) event
                        .getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                paymentService.deactivateSubscription(subscription, event.getId());
            }
        }
    }
}
