package de.x1c1b.attoly.api.security.token;

public interface Token {

    String getRawToken();

    long getExpiresIn();

    String getPrincipal();
}
