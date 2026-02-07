package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.ShortcutService;
import de.mueller_constantin.attoly.api.domain.SubscriptionEntitlementService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.payload.ShortcutCreationPayload;
import de.mueller_constantin.attoly.api.repository.ShortcutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ShortcutServiceImpl implements ShortcutService {
    private final ShortcutRepository shortcutRepository;
    private final SubscriptionEntitlementService subscriptionEntitlementService;

    @Autowired
    public ShortcutServiceImpl(ShortcutRepository shortcutRepository, SubscriptionEntitlementService subscriptionEntitlementService) {
        this.shortcutRepository = shortcutRepository;
        this.subscriptionEntitlementService = subscriptionEntitlementService;
    }

    @Override
    public List<Shortcut> findAll() {
        return shortcutRepository.findAll();
    }

    @Override
    public Page<Shortcut> findAll(Pageable pageable) {
        return shortcutRepository.findAll(pageable);
    }

    @Override
    public Page<Shortcut> findAll(Specification<Shortcut> specification, Pageable pageable) {
        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false));
        return shortcutRepository.findAll(specification, pageable);
    }

    @Override
    public Page<Shortcut> findAllByOwnership(UUID ownerId, Pageable pageable) {
        return shortcutRepository.findByOwnership(ownerId, pageable);
    }

    @Override
    public Page<Shortcut> findAllByOwnership(String email, Pageable pageable) {
        return shortcutRepository.findByOwnership(email, pageable);
    }

    @Override
    public Shortcut findById(UUID id) throws EntityNotFoundException {
        return shortcutRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Shortcut findByTag(String tag) throws EntityNotFoundException {
        return shortcutRepository.findByTag(tag).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Shortcut create(ShortcutCreationPayload payload, UUID ownerId) {
        if(payload.isPermanent()) {
            subscriptionEntitlementService.checkCanCreatePermanentShortcut(ownerId);
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[6];

        secureRandom.nextBytes(secret);
        String tag = Base64.getUrlEncoder().withoutPadding().encodeToString(secret);

        Shortcut shortcut = Shortcut.builder()
                .tag(tag)
                .url(payload.getUrl())
                .permanent(payload.isPermanent())
                .build();

        return shortcutRepository.save(shortcut);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) throws EntityNotFoundException {
        delete(findById(id));
    }

    @Override
    @Transactional
    public void deleteByTag(String tag) throws EntityNotFoundException {
        delete(findByTag(tag));
    }

    protected void delete(Shortcut shortcut) throws EntityNotFoundException {
        shortcutRepository.deleteSoft(shortcut);
    }

    @Override
    public void deleteAllTemporaryCreatedBefore(OffsetDateTime date) {
        shortcutRepository.deleteAllTemporaryCreatedBefore(date.toInstant());
    }
}
