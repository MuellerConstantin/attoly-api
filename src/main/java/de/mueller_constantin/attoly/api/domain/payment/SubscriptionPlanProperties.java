package de.mueller_constantin.attoly.api.domain.payment;

import de.mueller_constantin.attoly.api.repository.model.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubscriptionPlanProperties {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class SubscriptionPlanConfig {
        private Long maxPermanentShortcuts;
        private Long maxExpirableShortcuts;
    }

    private Long maxAgeTemporaryShortcuts;
    private Map<String, SubscriptionPlanConfig> plans;

    public SubscriptionPlanConfig getConfigForPlan(SubscriptionPlan plan) {
        return plans.get(plan.name());
    }
}
