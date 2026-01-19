package de.mueller_constantin.attoly.api.security.token;

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

    @Builder.Default
    private AccessTokenProperties access = new AccessTokenProperties();

    @Builder.Default
    private RefreshTokenProperties refresh = new RefreshTokenProperties();

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class AccessTokenProperties {

        private String secret;

        @Builder.Default
        private long expiresIn = 300000L; // 5 minutes
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class RefreshTokenProperties {

        @Builder.Default
        private int length = 16;

        @Builder.Default
        private long expiresIn = 7200000L; // 2 hours
    }
}
