package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.model.UsageInfo;

import java.util.UUID;

public interface SubscriptionEntitlementService {
    void checkCanCreatePermanentShortcut(UUID ownerId);

    void checkCanCreateExpirableShortcut(UUID ownerId);

    UsageInfo getUsageInfoForUser(java.util.UUID ownerId);
}
