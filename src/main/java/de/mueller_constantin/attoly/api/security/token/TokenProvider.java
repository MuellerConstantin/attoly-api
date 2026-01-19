package de.mueller_constantin.attoly.api.security.token;

import org.springframework.security.core.Authentication;

public interface TokenProvider<T extends Token> {

    T generateToken(Authentication authentication);

    T validateToken(String rawToken) throws InvalidTokenException;
}
