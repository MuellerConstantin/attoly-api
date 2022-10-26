package de.x1c1b.attoly.api.repository.auditing;

import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.security.Principal;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
public class PrincipalAuditorAware implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null != authentication && authentication.getPrincipal() instanceof Principal principal) {
            return Optional.ofNullable(principal.getUser());
        }

        return Optional.empty();
    }
}
