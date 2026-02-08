package de.mueller_constantin.attoly.api.web.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UsageInfoDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsageLimitsDto {
        private long maxPermanentShortcuts;
        private long maxExpirableShortcuts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrentUsageDto {
        private long currentPermanentShortcuts;
        private long currentExpirableShortcuts;
    }

    private String plan;
    private UsageLimitsDto usageLimits;
    private CurrentUsageDto currentUsage;
}
