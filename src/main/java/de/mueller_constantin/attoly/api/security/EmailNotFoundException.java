package de.mueller_constantin.attoly.api.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EmailNotFoundException extends UsernameNotFoundException {

    public EmailNotFoundException(String msg) {
        super(msg);
    }

    public EmailNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
