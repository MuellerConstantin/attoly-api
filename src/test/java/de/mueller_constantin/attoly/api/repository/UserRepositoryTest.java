package de.mueller_constantin.attoly.api.repository;

import de.mueller_constantin.attoly.api.domain.model.User;
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
@Sql(scripts = {"/sql/roles.sql", "/sql/users.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void save() {
        User user = User.builder()
                .email("john.doe@localhost.com")
                .password("Abc123")
                .build();

        User newUser = userRepository.save(user);

        assertNotNull(newUser.getId());
        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getLastModifiedAt());
        assertFalse(newUser.isDeleted());
        assertEquals(0, newUser.getVersion());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getPassword(), newUser.getPassword());
    }

    @Test
    void count() {
        assertEquals(2, userRepository.count());
    }

    @Test
    void countAny() {
        assertEquals(3, userRepository.countAny());
    }

    @Test
    void findByName() {
        Optional<User> optionalUser = userRepository.findByEmail("max.mustermann@localhost.com");
        assertTrue(optionalUser.isPresent());
    }

    @Test
    void findByNameMissing() {
        Optional<User> optionalUser = userRepository.findByEmail("jane.doe@localhost.com");
        assertFalse(optionalUser.isPresent());
    }

    @TestConfiguration
    @EnableJpaAuditing
    static class UserRepositoryTestConfig {
    }
}
