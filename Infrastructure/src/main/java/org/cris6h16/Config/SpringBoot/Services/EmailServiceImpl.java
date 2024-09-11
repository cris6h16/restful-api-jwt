package org.cris6h16.Config.SpringBoot.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
@ComponentScan("org.cris6h16.*")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String subject, String text, boolean isHTML) {
        String failMsg = "";
        if (email == null || email.isEmpty()) failMsg = "Email cannot be null or empty";
        if (subject == null || subject.isEmpty()) failMsg = "Subject cannot be null or empty";
        if (text == null || text.isEmpty()) failMsg = "Text cannot be null or empty";
        if (!failMsg.isEmpty()) throw new IllegalArgumentException(failMsg);

        log.info("Sending email to {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("cristianmherrera21@gmail.com");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, isHTML);


            mailSender.send(message);
            log.info("Email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.toString());
        }
    }
}
