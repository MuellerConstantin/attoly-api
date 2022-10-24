package de.x1c1b.attoly.api.security.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "attoly.security.token")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenProperties {

    private AccessTokenProperties access;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class AccessTokenProperties {

        private String secret;

        @Builder.Default
        private long expiresIn = 300000L; // 5 minutes
    }
}
