package de.mueller_constantin.attoly.api.web.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PrincipalDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class BillingInfoDto {
        private String customerId;
        private String subscriptionId;
        private String status;
        private OffsetDateTime currentPeriodStart;
        private OffsetDateTime currentPeriodEnd;
    }

    private UUID id;
    private String email;
    private boolean emailVerified;
    private OffsetDateTime createdAt;
    private boolean locked;
    private String identityProvider;
    private String plan;
    private BillingInfoDto billing;
}
