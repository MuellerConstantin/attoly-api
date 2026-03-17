package de.mueller_constantin.attoly.api.domain.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageInfoResult {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsageLimits {
        private long maxPermanentShortcuts;
        private long maxExpirableShortcuts;
        private boolean canCreatePasswordProtectedShortcuts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrentUsage {
        private long currentPermanentShortcuts;
        private long currentExpirableShortcuts;
    }

    private String plan;
    private UsageLimits usageLimits;
    private CurrentUsage currentUsage;
}
