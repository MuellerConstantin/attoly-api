package de.mueller_constantin.attoly.api.security.token.opaque;

import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.security.token.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
public class OpaqueTokenProvider implements OneTimeTokenProvider<RefreshToken> {
    private static final long GRACE_WINDOW_SECONDS = 10;

    private final TokenProperties tokenProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ExpirableLockRegistry lockRegistry;

    @Autowired
    public OpaqueTokenProvider(TokenProperties tokenProperties,
                               RedisTemplate<String, Object> redisTemplate,
                               ExpirableLockRegistry lockRegistry) {
        this.tokenProperties = tokenProperties;
        this.redisTemplate = redisTemplate;
        this.lockRegistry = lockRegistry;
    }

    @Override
    public RefreshToken generateToken(Authentication authentication) {
        Principal user = (Principal) authentication.getPrincipal();
        return buildToken(user.getUsername());
    }

    @Override
    public RefreshToken validateToken(String rawToken) throws InvalidTokenException {
        String storageKey = "attoly:refresh-token:" + rawToken;
        String rotatedKey = "attoly:refresh-token:rotated:" + rawToken;

        String principal = (String) redisTemplate.opsForValue().get(storageKey);

        if (principal != null) {
            long expiresIn = redisTemplate.getExpire(storageKey, TimeUnit.MILLISECONDS);
            return new RefreshToken(rawToken, expiresIn, principal);
        }

        // Check if there's a rotated token reference

        String rotatedRawToken = (String) redisTemplate.opsForValue().get(rotatedKey);

        if (rotatedRawToken != null) {
            return validateToken(rotatedRawToken);
        }

        throw new InvalidTokenException("Invalid token");
    }

    @Override
    public RefreshToken consume(String rawToken) {
        String storageKey = String.format("attoly:refresh-token:%s", rawToken);
        String principal = (String) redisTemplate.opsForValue().getAndDelete(storageKey);

        if (principal == null) {
            throw new InvalidTokenException("Invalid token");
        }

        return RefreshToken.builder()
                .rawToken(rawToken)
                .expiresIn(0)
                .principal(principal)
                .build();
    }

    @Override
    @SneakyThrows
    public RefreshToken exchange(String rawToken) {
        return this.lockRegistry.executeLocked(String.format("refresh-token-exchange:%s", rawToken), () -> {
            String storageKey = String.format("attoly:refresh-token:%s", rawToken);
            String rotatedKey = String.format("attoly:refresh-token:rotated:%s", rawToken);

            // Check for existing rotated token reference

            String rotatedRawToken = (String) redisTemplate.opsForValue().get(rotatedKey);

            if (rotatedRawToken != null) {
                return validateToken(rotatedRawToken);
            }

            // Generate new rotated token

            String principal = (String) redisTemplate.opsForValue().get(storageKey);

            if (principal == null) {
                throw new InvalidTokenException("Invalid token");
            }

            RefreshToken newRefreshToken = buildToken(principal);

            // Store rotated token reference with grace period

            redisTemplate.opsForValue()
                    .setIfAbsent(rotatedKey, newRefreshToken.getRawToken(), GRACE_WINDOW_SECONDS, TimeUnit.SECONDS);

            redisTemplate.delete(storageKey);

            return newRefreshToken;
        });
    }

    private RefreshToken buildToken(String principal) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[tokenProperties.getRefresh().getLength()];
        secureRandom.nextBytes(secret);

        String rawToken = Base64.getEncoder().encodeToString(secret);
        String storageKey = String.format("attoly:refresh-token:%s", rawToken);

        redisTemplate.opsForValue().set(storageKey, principal,
                tokenProperties.getRefresh().getExpiresIn(), TimeUnit.MILLISECONDS);

        return RefreshToken.builder()
                .rawToken(rawToken)
                .expiresIn(tokenProperties.getRefresh().getExpiresIn())
                .principal(principal)
                .build();
    }
}
