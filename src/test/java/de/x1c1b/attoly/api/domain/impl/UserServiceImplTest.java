package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.repository.RoleRepository;
import de.x1c1b.attoly.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role sampleRole;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        this.sampleRole = Role.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0)
                .deleted(false)
                .name("ROLE_USER")
                .build();

        this.sampleUser = User.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0)
                .deleted(false)
                .email("john.doe@localhost.com")
                .password("Abc123")
                .build();
    }

    @Test
    void findById() {
        when(userRepository.findById(eq(sampleUser.getId()))).thenReturn(Optional.of(sampleUser));

        User user = userService.findById(sampleUser.getId());

        verify(userRepository, times(1)).findById(eq(sampleUser.getId()));

        assertEquals(sampleUser.getId(), user.getId());
    }

    @Test
    void findByEmail() {
        when(userRepository.findByEmail(eq(sampleUser.getEmail()))).thenReturn(Optional.of(sampleUser));

        User user = userService.findByEmail(sampleUser.getEmail());

        verify(userRepository, times(1)).findByEmail(eq(sampleUser.getEmail()));

        assertEquals(sampleUser.getEmail(), user.getEmail());
    }

    @Test
    void assignRolesByEmail() {
        when(userRepository.findByEmail(eq(sampleUser.getEmail()))).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findByName(eq(sampleRole.getName()))).thenReturn(Optional.of(sampleRole));
        when(userRepository.save(any())).thenReturn(sampleUser);

        User user = userService.assignRolesByEmail(sampleUser.getEmail(), sampleRole.getName());

        verify(userRepository, times(1)).findByEmail(eq(sampleUser.getEmail()));

        assertEquals(1, user.getRoles().size());
    }
}
