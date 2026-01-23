package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.EmailService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfiguration;
    private final MessageSource messageSource;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, Configuration freemarkerConfiguration, MessageSource messageSource) {
        this.javaMailSender = javaMailSender;
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.messageSource = messageSource;
    }

    @Override
    public void sendSimpleMessage(String to, String from, String subject, String text) throws IOException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    @Override
    public void sendTemplateMessage(String to, String from, String subject, String template,
                                    Map<String, Object> arguments, Locale locale) throws IOException, MessagingException, TemplateException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        StringWriter stringWriter = new StringWriter();

        Map<String, Object> model = new HashMap<>(arguments);
        model.put("i18n", new EmailTemplate18nHelper(messageSource, locale));

        freemarkerConfiguration.getTemplate(template, locale).process(model, stringWriter);

        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(stringWriter.getBuffer().toString(), true);

        javaMailSender.send(message);
    }
}
