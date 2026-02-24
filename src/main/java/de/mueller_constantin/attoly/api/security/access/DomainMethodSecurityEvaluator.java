package de.mueller_constantin.attoly.api.security.access;

import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.repository.ShortcutRepository;
import de.mueller_constantin.attoly.api.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Service component which contains domain-specific security expressions to validate access.
 */
@Component
public class DomainMethodSecurityEvaluator {

    private final ShortcutRepository shortcutRepository;

    @Autowired
    public DomainMethodSecurityEvaluator(ShortcutRepository shortcutRepository) {
        this.shortcutRepository = shortcutRepository;
    }

    /**
     * Loads the currently authenticated principal.
     *
     * @return The loaded principal.
     */
    protected final Principal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null != authentication && authentication.getPrincipal() instanceof Principal principal) {
            return principal;
        } else {
            return null;
        }
    }

    /**
     * Checks if the current user is the account owner. Therefore, whether the current user
     * accesses their own account.
     *
     * @param accountId Identifier of the account being accessed.
     * @return True if it is your own account, false otherwise.
     */
    public boolean isAccountOwnerOf(UUID accountId) {
        Principal principal = getPrincipal();
        return principal.getUser().getId().equals(accountId);
    }

    /**
     * Checks if the current user is the shortcut owner. Hence whether the current user is
     * accessing their own shortcut.
     *
     * @param tag Tag of the shortcut being accessed.
     * @return True if it is your own shortcut, false otherwise.
     */
    public boolean isShortcutOwnerOf(String tag) {
        Principal principal = getPrincipal();
        Optional<Shortcut> optionalShortcut = shortcutRepository.findByTag(tag);

        return optionalShortcut.isPresent() && principal.getUser().getId().equals(optionalShortcut.get().getCreatedBy().getId());
    }
}
