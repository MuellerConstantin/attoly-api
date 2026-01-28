package de.mueller_constantin.attoly.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

import java.time.Duration;

@Configuration
public class SynchronizationConfig {
    private static final String LOCK_REGISTRY_KEY = "attoly:lock";
    private static final Duration RELEASE_TIME_DURATION = Duration.ofSeconds(30);

    @Bean
    public ExpirableLockRegistry redisLockRegistry(RedisConnectionFactory connectionFactory) {
        return new RedisLockRegistry(connectionFactory, LOCK_REGISTRY_KEY, RELEASE_TIME_DURATION.toMillis());
    }
}
