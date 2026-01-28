package de.mueller_constantin.attoly.api.security.token;

public interface OneTimeTokenProvider<T extends Token> extends TokenProvider<T> {
    /**
     * Consumes the token, making it invalid for future use.
     *
     * @param rawToken The raw token string to be consumed.
     * @return The consumed token.
     * @throws InvalidTokenException If the token is invalid or has already been consumed.
     */
    T consume(String rawToken) throws InvalidTokenException;

    /**
     * Exchanges the token for a new one, while consuming/invalidating the old one.
     *
     * @param rawToken The raw token string to be exchanged.
     * @return An exchange result containing the new token and whether it's primary.
     * @throws InvalidTokenException If the token is invalid.
     */
    T exchange(String rawToken) throws InvalidTokenException;
}
