package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.model.Shortcut;
import de.mueller_constantin.attoly.api.repository.ShortcutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortcutServiceImplTest {

    @Mock
    private ShortcutRepository shortcutRepository;

    @InjectMocks
    private ShortcutServiceImpl shortcutService;

    private Shortcut sampleShortcut;

    @BeforeEach
    void setUp() {
        sampleShortcut = Shortcut.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0)
                .tag("vT6Nafaz")
                .url("http://localhost:8080")
                .build();
    }

    @Test
    void findById() {
        when(shortcutRepository.findById(eq(sampleShortcut.getId()))).thenReturn(Optional.of(sampleShortcut));

        Shortcut shortcut = shortcutService.findById(sampleShortcut.getId());

        verify(shortcutRepository, times(1)).findById(eq(sampleShortcut.getId()));

        assertEquals(sampleShortcut.getId(), shortcut.getId());
    }
}