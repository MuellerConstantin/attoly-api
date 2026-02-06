package de.mueller_constantin.attoly.api.config;

import com.stripe.Stripe;
import de.mueller_constantin.attoly.api.domain.payment.StripeProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Autowired
    private StripeProperties stripeProperties;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.getApiKey();
    }
}
