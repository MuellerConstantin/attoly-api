package de.mueller_constantin.attoly.api.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageInfo {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsageLimits {
        private long maxPermanentShortcuts;
        private long maxExpirableShortcuts;
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
