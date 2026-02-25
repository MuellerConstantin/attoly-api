package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.result.UsageInfoResult;

import java.util.UUID;

public interface SubscriptionEntitlementService {
    void checkCanCreatePermanentShortcut(UUID ownerId);

    void checkCanCreateExpirableShortcut(UUID ownerId);

    UsageInfoResult getUsageInfoForUser(java.util.UUID ownerId);
}
