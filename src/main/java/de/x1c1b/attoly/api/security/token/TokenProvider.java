package de.x1c1b.attoly.api.security.token;

import org.springframework.security.core.Authentication;

public interface TokenProvider<T extends Token> {

    T generateToken(Authentication authentication);

    T validateToken(String rawToken) throws InvalidTokenException;
}
