package de.mueller_constantin.attoly.api.domain.scheduling;

import de.mueller_constantin.attoly.api.domain.ShortcutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Cron job for deleting all expired and temporary created shortcuts. This
 * operation deletes all temporary shortcuts that were created before a
 * certain point in time, thus cleaning up the database.
 */
@Component
@ConditionalOnProperty(prefix = "attoly.scheduling.jobs.temporary-shortcut-clean-up", name = "enabled", matchIfMissing = true, havingValue = "true")
public class TemporaryShortcutCleanUpJob {

    private final Logger logger = LoggerFactory.getLogger(TemporaryShortcutCleanUpJob.class);

    private final ShortcutService shortcutService;
    private final long temporaryShortcutExpiresIn;

    @Autowired
    public TemporaryShortcutCleanUpJob(ShortcutService shortcutService,
                                       @Value("${attoly.scheduling.jobs.temporary-shortcut-clean-up.expires-in:2419200000}") long temporaryShortcutExpiresIn) {
        this.shortcutService = shortcutService;
        this.temporaryShortcutExpiresIn = temporaryShortcutExpiresIn;
    }

    @Scheduled(cron = "${attoly.scheduling.jobs.temporary-shortcut-clean-up.cron:@daily}")
    protected void run() {
        logger.info("Deletion job for temporary expired shortcuts is running");
        shortcutService.deleteAllTemporaryCreatedBefore(OffsetDateTime.now().minusSeconds(temporaryShortcutExpiresIn / 60));
    }
}
