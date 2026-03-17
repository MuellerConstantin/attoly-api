package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.ShortcutService;
import de.mueller_constantin.attoly.api.domain.SubscriptionEntitlementService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.exception.InvalidShortcutPasswordException;
import de.mueller_constantin.attoly.api.domain.exception.ShortcutPasswordRequiredException;
import de.mueller_constantin.attoly.api.domain.result.ShortcutResult;
import de.mueller_constantin.attoly.api.domain.result.mapper.ShortcutResultMapper;
import de.mueller_constantin.attoly.api.repository.model.Shortcut;
import de.mueller_constantin.attoly.api.domain.payload.ShortcutCreationPayload;
import de.mueller_constantin.attoly.api.repository.ShortcutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ShortcutServiceImpl implements ShortcutService {
    private final ShortcutRepository shortcutRepository;
    private final SubscriptionEntitlementService subscriptionEntitlementService;
    private final ShortcutResultMapper shortcutResultMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ShortcutServiceImpl(ShortcutRepository shortcutRepository,
                               SubscriptionEntitlementService subscriptionEntitlementService,
                               ShortcutResultMapper shortcutResultMapper,
                               PasswordEncoder passwordEncoder) {
        this.shortcutRepository = shortcutRepository;
        this.subscriptionEntitlementService = subscriptionEntitlementService;
        this.shortcutResultMapper = shortcutResultMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ShortcutResult> findAll() {
        var shortcuts = shortcutRepository.findAll();
        return shortcutResultMapper.mapToResult(shortcuts);
    }

    @Override
    public Page<ShortcutResult> findAll(Pageable pageable) {
        var shortcuts = shortcutRepository.findAll(pageable);
        return shortcuts.map(shortcutResultMapper::mapToResult);
    }

    @Override
    public Page<ShortcutResult> findAll(Specification<Shortcut> specification, Pageable pageable) {
        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false))
                .and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("expiresAt")),
                        criteriaBuilder.greaterThan(root.get("expiresAt"), Instant.now())
                ));
        var shortcuts = shortcutRepository.findAll(specification, pageable);
        return shortcuts.map(shortcutResultMapper::mapToResult);
    }

    @Override
    public Page<ShortcutResult> findAllByOwnership(UUID ownerId, Pageable pageable) {
        var shortcuts = shortcutRepository.findByOwnership(ownerId, pageable);
        return shortcuts.map(shortcutResultMapper::mapToResult);
    }

    @Override
    public Page<ShortcutResult> findAllByOwnership(String email, Pageable pageable) {
        var shortcuts = shortcutRepository.findByOwnership(email, pageable);
        return shortcuts.map(shortcutResultMapper::mapToResult);
    }

    @Override
    public ShortcutResult findById(UUID id) throws EntityNotFoundException {
        var shortcut = shortcutRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return shortcutResultMapper.mapToResult(shortcut);
    }

    @Override
    public ShortcutResult findByTag(String tag) throws EntityNotFoundException {
        var shortcut = shortcutRepository.findByTag(tag).orElseThrow(EntityNotFoundException::new);
        return shortcutResultMapper.mapToResult(shortcut);
    }

    @Override
    public ShortcutResult findValidByTag(String tag) throws EntityNotFoundException {
        var shortcut = shortcutRepository.findValidByTag(tag).orElseThrow(EntityNotFoundException::new);

        if(shortcut.isPasswordProtected()) {
            throw new ShortcutPasswordRequiredException();
        }

        return shortcutResultMapper.mapToResult(shortcut);
    }

    @Override
    public ShortcutResult resolveValidByTag(String tag, String password) throws EntityNotFoundException {
        var shortcut = shortcutRepository.findValidByTag(tag).orElseThrow(EntityNotFoundException::new);

        if(shortcut.isPasswordProtected()) {
            if (password == null) {
                throw new ShortcutPasswordRequiredException();
            }

            if (!passwordEncoder.matches(password, shortcut.getPassword())) {
                throw new InvalidShortcutPasswordException();
            }
        }

        return shortcutResultMapper.mapToResult(shortcut);
    }

    @Override
    public ShortcutResult create(ShortcutCreationPayload payload, UUID ownerId) {
        if(payload.isPermanent()) {
            subscriptionEntitlementService.checkCanCreatePermanentShortcut(ownerId);
        }

        if(payload.getExpiresAt().isPresent()) {
            subscriptionEntitlementService.checkCanCreateExpirableShortcut(ownerId);
        }

        if(payload.getPassword().isPresent()) {
            subscriptionEntitlementService.checkCanCreatePasswordProtectedShortcut(ownerId);
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[6];

        secureRandom.nextBytes(secret);
        String tag = Base64.getUrlEncoder().withoutPadding().encodeToString(secret);

        Instant expiresAt = payload.isPermanent() ?
                null : payload.getExpiresAt().map(OffsetDateTime::toInstant).orElse(null);

        String password = payload.getPassword().isPresent() ?
                passwordEncoder.encode(payload.getPassword().get()) : null;

        Shortcut shortcut = Shortcut.builder()
                .tag(tag)
                .url(payload.getUrl())
                .permanent(payload.isPermanent())
                .expiresAt(expiresAt)
                .password(password)
                .build();

        var newShortcut = shortcutRepository.save(shortcut);
        return shortcutResultMapper.mapToResult(newShortcut);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) throws EntityNotFoundException {
        var shortcut = shortcutRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        delete(shortcut);
    }

    @Override
    @Transactional
    public void deleteByTag(String tag) throws EntityNotFoundException {
        var shortcut = shortcutRepository.findByTag(tag).orElseThrow(EntityNotFoundException::new);
        delete(shortcut);
    }

    protected void delete(Shortcut shortcut) throws EntityNotFoundException {
        shortcutRepository.deleteSoft(shortcut);
    }

    @Override
    public void deleteAllTemporaryCreatedBefore(OffsetDateTime date) {
        shortcutRepository.deleteAllTemporaryCreatedBefore(date.toInstant());
    }
}
