package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.SubscriptionEntitlementService;
import de.mueller_constantin.attoly.api.domain.exception.FeatureNotAvailableException;
import de.mueller_constantin.attoly.api.domain.exception.PermanentShortcutLimitExceededException;
import de.mueller_constantin.attoly.api.domain.model.SubscriptionPlan;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.payment.SubscriptionPlanProperties;
import de.mueller_constantin.attoly.api.repository.ShortcutRepository;
import de.mueller_constantin.attoly.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubscriptionEntitlementServiceImpl implements SubscriptionEntitlementService {
    private final SubscriptionPlanProperties subscriptionPlanProperties;
    private final ShortcutRepository shortcutRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubscriptionEntitlementServiceImpl(SubscriptionPlanProperties subscriptionPlanProperties,
                                              ShortcutRepository shortcutRepository,
                                              UserRepository userRepository) {
        this.subscriptionPlanProperties = subscriptionPlanProperties;
        this.shortcutRepository = shortcutRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void checkCanCreatePermanentShortcut(UUID ownerId) {
        if(ownerId == null) {
            throw new FeatureNotAvailableException();
        }

        User user = userRepository.findById(ownerId)
                .orElseThrow();

        SubscriptionPlan plan = user.getPlan();
        SubscriptionPlanProperties.SubscriptionPlanConfig config =
                subscriptionPlanProperties.getConfigForPlan(plan);

        if (config == null || config.getMaxPermanentShortcuts() == null) {
            return;
        }

        Long currentPermanentShortcutCount = shortcutRepository.countPermanentShortcutsByCreatorId(ownerId);
        Long maxAllowedPermanentShortcutCount = config.getMaxPermanentShortcuts();

        if (currentPermanentShortcutCount >= maxAllowedPermanentShortcutCount) {
            throw new PermanentShortcutLimitExceededException();
        }
    }
}
