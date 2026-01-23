package de.mueller_constantin.attoly.api.domain;

import freemarker.template.TemplateException;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * Central interface for sending emails using an external SMTP server.
 */
public interface EmailService {

    /**
     * Sends a classic plain text message via SMTP.
     *
     * @param to      The target email address.
     * @param from    The e-mail address of the sender.
     * @param subject The subject of the email.
     * @param text    The content of the message.
     * @throws IOException        Thrown when writing/formatting is difficult.
     * @throws MessagingException Thrown when there is difficulty sending.
     */
    void sendSimpleMessage(String to, String from, String subject, String text) throws IOException, MessagingException;

    /**
     * Sends an email using a prepared template.
     *
     * @param to        The target email address.
     * @param from      The e-mail address of the sender.
     * @param subject   The subject of the email.
     * @param template  The name of the used template.
     * @param locale    The locale to be used for the template.
     * @param arguments Optional arguments which are used for the placeholders in the template.
     * @throws IOException        Thrown when writing/formatting is difficult.
     * @throws MessagingException Thrown when there is difficulty sending.
     * @throws TemplateException  Thrown when there are problems with the template.
     */
    void sendTemplateMessage(String to, String from, String subject, String template, Map<String, Object> arguments, Locale locale) throws IOException, MessagingException, TemplateException;
}
