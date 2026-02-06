package de.mueller_constantin.attoly.api.domain.payment;

import de.mueller_constantin.attoly.api.domain.model.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "attoly.payment.stripe")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StripeProperties {
    private String apiKey;
    private String successUrl;
    private String cancelUrl;
    private String portalReturnUrl;
    private String webhookSecret;
    private Map<Plan, String> plans;

    public Plan resolvePlan(String priceId) {
        return plans.entrySet().stream()
                .filter(entry -> entry.getValue().equals(priceId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

}
