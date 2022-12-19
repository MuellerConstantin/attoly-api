package de.x1c1b.attoly.api.domain.scheduling;

import de.x1c1b.attoly.api.domain.model.RoleName;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.repository.RoleRepository;
import de.x1c1b.attoly.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Start up job for creating the initial admin user. The user is only created, if it didn't already exists.
 */
@Component
@ConditionalOnProperty(prefix = "attoly.scheduling.jobs.initial-admin-creation", name = "enabled", havingValue = "true")
@Order(2)
public class InitialAdminCreationJob implements ApplicationListener<ApplicationStartedEvent> {

    private final Logger logger = LoggerFactory.getLogger(InitialAdminCreationJob.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    @Autowired
    public InitialAdminCreationJob(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${attoly.scheduling.jobs.initial-admin-creation.email}") String email,
                                   @Value("${attoly.scheduling.jobs.initial-admin-creation.password}") String password) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (email == null || email.isEmpty()) {
            logger.warn("No email address for default admin user specified");
            return;
        }

        if (password == null || password.isEmpty()) {
            logger.warn("No password for default admin user specified");
            return;
        }

        if (userRepository.existsByEmail(email)) {
            logger.info("Skipping seeding of default admin user, because it already exists");
            return;
        }

        logger.info("Seeding the default admin user in database");

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow()))
                .emailVerified(true)
                .build();

        userRepository.save(user);
    }
}
