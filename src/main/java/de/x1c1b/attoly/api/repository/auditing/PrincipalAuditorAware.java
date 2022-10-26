package de.x1c1b.attoly.api.repository.auditing;

import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.repository.UserRepository;
import de.x1c1b.attoly.api.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
public class PrincipalAuditorAware implements AuditorAware<User> {
    private final UserRepository userRepository;

    @Autowired
    public PrincipalAuditorAware(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(Principal.class::cast)
                .flatMap(principal -> userRepository.findByEmail(principal.getEmail()));
    }
}
