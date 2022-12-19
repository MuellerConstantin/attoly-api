package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.ShortcutService;
import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.model.Shortcut;
import de.x1c1b.attoly.api.domain.payload.ShortcutCreationPayload;
import de.x1c1b.attoly.api.repository.ShortcutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public ShortcutServiceImpl(ShortcutRepository shortcutRepository) {
        this.shortcutRepository = shortcutRepository;
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
    public Shortcut create(ShortcutCreationPayload payload) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[6];

        secureRandom.nextBytes(secret);
        String tag = Base64.getEncoder().encodeToString(secret);

        Shortcut shortcut = Shortcut.builder()
                .tag(tag)
                .url(payload.getUrl())
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
    public void deleteAllAnonymousCreatedBefore(OffsetDateTime date) {
        shortcutRepository.deleteAllAnonymousSoftCreatedBefore(date.toInstant());
    }
}
