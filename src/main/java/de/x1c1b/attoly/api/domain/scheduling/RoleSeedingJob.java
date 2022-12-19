package de.x1c1b.attoly.api.domain.scheduling;

import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.RoleName;
import de.x1c1b.attoly.api.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Start up job for seeding the database with all required security roles. Roles are only created, if they didn't
 * already exists.
 */
@Component
@ConditionalOnProperty(prefix = "attoly.scheduling.jobs.role-seeding", name = "enabled", matchIfMissing = true, havingValue = "true")
public class RoleSeedingJob implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(RoleSeedingJob.class);

    private final RoleRepository roleRepository;

    @Autowired
    public RoleSeedingJob(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN, new HashSet<>()));
        }

        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            roleRepository.save(new Role(RoleName.ROLE_USER, new HashSet<>()));
        }
    }
}
