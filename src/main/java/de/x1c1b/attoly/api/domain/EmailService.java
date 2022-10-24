package de.x1c1b.attoly.api.domain;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

public interface EmailService {

    void sendSimpleMessage(String to, String from, String subject, String text) throws IOException, MessagingException;

    void sendTemplateMessage(String to, String from, String subject, String template, Map<String, Object> arguments) throws IOException, MessagingException, TemplateException;
}
