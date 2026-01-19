package de.mueller_constantin.attoly.api.domain.scheduling;

import de.mueller_constantin.attoly.api.domain.model.Role;
import de.mueller_constantin.attoly.api.domain.model.RoleName;
import de.mueller_constantin.attoly.api.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Start up job for seeding the database with all required security roles. Roles are only created, if they didn't
 * already exists.
 */
@Component
@ConditionalOnProperty(prefix = "attoly.scheduling.jobs.role-seeding", name = "enabled", havingValue = "true")
@Order(1)
public class RoleSeedingJob implements ApplicationListener<ApplicationStartedEvent> {

    private final Logger logger = LoggerFactory.getLogger(RoleSeedingJob.class);

    private final RoleRepository roleRepository;

    @Autowired
    public RoleSeedingJob(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        logger.info("Seeding security roles in database");

        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN, new HashSet<>()));
        }

        if (!roleRepository.existsByName(RoleName.ROLE_MODERATOR)) {
            roleRepository.save(new Role(RoleName.ROLE_MODERATOR, new HashSet<>()));
        }

        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            roleRepository.save(new Role(RoleName.ROLE_USER, new HashSet<>()));
        }
    }
}
