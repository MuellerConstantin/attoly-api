package de.mueller_constantin.attoly.api.security.token.jwt;

import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.security.token.AccessToken;
import de.mueller_constantin.attoly.api.security.token.InvalidTokenException;
import de.mueller_constantin.attoly.api.security.token.TokenProperties;
import de.mueller_constantin.attoly.api.security.token.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Primary
public class JwtTokenProvider implements TokenProvider<AccessToken> {

    private final TokenProperties tokenProperties;

    @Autowired
    public JwtTokenProvider(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    @Override
    public AccessToken generateToken(Authentication authentication) {
        Principal user = (Principal) authentication.getPrincipal();

        SecretKey secretKey = Keys.hmacShaKeyFor(tokenProperties.getAccess().getSecret().getBytes());

        String rawToken = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + tokenProperties.getAccess().getExpiresIn()))
                .signWith(secretKey)
                .compact();

        return AccessToken.builder()
                .rawToken(rawToken)
                .expiresIn(tokenProperties.getAccess().getExpiresIn())
                .principal(user.getUsername())
                .build();
    }

    @Override
    public AccessToken validateToken(String rawToken) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(tokenProperties.getAccess().getSecret().getBytes());

            Claims claims = Jwts.parser().verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(rawToken)
                    .getPayload();

            return AccessToken.builder()
                    .rawToken(rawToken)
                    .expiresIn(claims.getExpiration().getTime() - new Date().getTime())
                    .principal(claims.getSubject())
                    .build();
        } catch (JwtException exc) {
            throw new InvalidTokenException("Invalid token", exc);
        }
    }
}
