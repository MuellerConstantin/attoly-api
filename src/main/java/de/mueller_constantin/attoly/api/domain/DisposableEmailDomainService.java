package de.mueller_constantin.attoly.api.domain;

/**
 * Interface for checking disposable email domains.
 */
public interface DisposableEmailDomainService {
    /**
     * Checks if the given email address belongs to a disposable email domain.
     *
     * @param email The email address to check.
     * @return True if the email address is disposable, false otherwise.
     */
    public boolean isDisposable(String email);
}
