package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.RoleName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/roles.sql"})
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void save() {
        Role role = Role.builder()
                .name(RoleName.ROLE_ADMIN)
                .build();

        Role newRole = roleRepository.save(role);

        assertNotNull(newRole.getId());
        assertNotNull(newRole.getCreatedAt());
        assertNotNull(newRole.getLastModifiedAt());
        assertFalse(newRole.isDeleted());
        assertEquals(0, newRole.getVersion());
        assertEquals(role.getName(), newRole.getName());
    }

    @Test
    void count() {
        assertEquals(2, roleRepository.count());
    }

    @Test
    void countAny() {
        assertEquals(2, roleRepository.countAny());
    }

    @Test
    void findByName() {
        Optional<Role> optionalRole = roleRepository.findByName(RoleName.ROLE_USER);
        assertTrue(optionalRole.isPresent());
    }

    @TestConfiguration
    @EnableJpaAuditing
    static class RoleRepositoryTestConfig {
    }
}
