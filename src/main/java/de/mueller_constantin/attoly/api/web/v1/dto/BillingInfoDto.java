package de.mueller_constantin.attoly.api.web.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BillingInfoDto {
    private String customerId;
    private String subscriptionId;
    private String status;
    private OffsetDateTime currentPeriodStart;
    private OffsetDateTime currentPeriodEnd;
}
