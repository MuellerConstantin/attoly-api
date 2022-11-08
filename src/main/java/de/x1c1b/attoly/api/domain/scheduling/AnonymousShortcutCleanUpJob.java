package de.x1c1b.attoly.api.domain.scheduling;

import de.x1c1b.attoly.api.domain.impl.ShortcutServiceImpl;
import de.x1c1b.attoly.api.repository.ShortcutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Cron job for deleting all expired and anonymously created shortcuts. This
 * operation deletes all shortcuts that were not assigned to an account and
 * which were created before a certain point in time, thus cleaning up the
 * database.
 */
@Component
@ConditionalOnProperty(prefix = "attoly.scheduling.jobs.anonymous-shortcut-clean-up", name = "enabled", matchIfMissing = true, havingValue = "true")
public class AnonymousShortcutCleanUpJob {

    private final Logger logger = LoggerFactory.getLogger(ShortcutServiceImpl.class);

    private final ShortcutRepository shortcutRepository;
    private final long anonymousShortcutExpiresIn;

    @Autowired
    public AnonymousShortcutCleanUpJob(ShortcutRepository shortcutRepository,
                                       @Value("${attoly.scheduling.jobs.anonymous-shortcut-clean-up.expires-in:2419200000}") long anonymousShortcutExpiresIn) {
        this.shortcutRepository = shortcutRepository;
        this.anonymousShortcutExpiresIn = anonymousShortcutExpiresIn;
    }

    @Scheduled(cron = "${attoly.scheduling.jobs.anonymous-shortcut-clean-up.cron:@daily}")
    protected void run() {
        logger.info("Deletion job for anonymous expired shortcuts is running");
        shortcutRepository.deleteAllAnonymousSoftCreatedBefore(Instant.now().minusMillis(anonymousShortcutExpiresIn));
    }
}
