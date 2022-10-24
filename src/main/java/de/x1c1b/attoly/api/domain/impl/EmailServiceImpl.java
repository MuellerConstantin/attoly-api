package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.EmailService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfiguration;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, Configuration freemarkerConfiguration) {
        this.javaMailSender = javaMailSender;
        this.freemarkerConfiguration = freemarkerConfiguration;
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
                                    Map<String, Object> arguments) throws IOException, MessagingException, TemplateException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        StringWriter stringWriter = new StringWriter();

        freemarkerConfiguration.getTemplate(template).process(arguments, stringWriter);

        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(stringWriter.getBuffer().toString(), true);

        javaMailSender.send(message);
    }
}
