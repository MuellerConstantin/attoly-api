package de.mueller_constantin.attoly.api.domain.impl;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class EmailTemplate18nHelper {
    private final MessageSource messageSource;
    private final Locale locale;

    public EmailTemplate18nHelper(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, locale);
    }

    public String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, locale);
    }
}
