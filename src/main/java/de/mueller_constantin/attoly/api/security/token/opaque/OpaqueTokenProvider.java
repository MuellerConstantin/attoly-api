package de.mueller_constantin.attoly.api.security.token.opaque;

import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.security.token.InvalidTokenException;
import de.mueller_constantin.attoly.api.security.token.RefreshToken;
import de.mueller_constantin.attoly.api.security.token.TokenProperties;
import de.mueller_constantin.attoly.api.security.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
public class OpaqueTokenProvider implements TokenProvider<RefreshToken> {

    private final TokenProperties tokenProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public OpaqueTokenProvider(TokenProperties tokenProperties, RedisTemplate<String, Object> redisTemplate) {
        this.tokenProperties = tokenProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RefreshToken generateToken(Authentication authentication) {
        Principal user = (Principal) authentication.getPrincipal();

        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[tokenProperties.getRefresh().getLength()];
        secureRandom.nextBytes(secret);

        String rawToken = Base64.getEncoder().encodeToString(secret);
        String storageKey = String.format("attoly:refresh-token:%s", rawToken);

        redisTemplate.opsForValue().set(storageKey, user.getUsername(),
                tokenProperties.getRefresh().getExpiresIn(), TimeUnit.MILLISECONDS);

        return RefreshToken.builder()
                .rawToken(rawToken)
                .expiresIn(tokenProperties.getRefresh().getExpiresIn())
                .principal(user.getUsername())
                .build();
    }

    @Override
    public RefreshToken validateToken(String rawToken) throws InvalidTokenException {
        String storageKey = String.format("attoly:refresh-token:%s", rawToken);

        if (redisTemplate.hasKey(storageKey)) {
            String principal = (String) redisTemplate.opsForValue().getAndDelete(storageKey);

            return RefreshToken.builder()
                    .rawToken(rawToken)
                    .expiresIn(0)
                    .principal(principal)
                    .build();
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
