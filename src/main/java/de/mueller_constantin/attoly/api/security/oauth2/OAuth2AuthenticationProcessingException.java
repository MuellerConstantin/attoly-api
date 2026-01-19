package de.mueller_constantin.attoly.api.security.oauth2;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {

    public OAuth2AuthenticationProcessingException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
