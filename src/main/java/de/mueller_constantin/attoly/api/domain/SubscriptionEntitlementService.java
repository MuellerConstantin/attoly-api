package de.mueller_constantin.attoly.api.domain;

import java.util.UUID;

public interface SubscriptionEntitlementService {
    void checkCanCreatePermanentShortcut(UUID ownerId);
}
