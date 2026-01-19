package de.mueller_constantin.attoly.api.security.token;

public interface Token {

    String getRawToken();

    long getExpiresIn();

    String getPrincipal();
}
