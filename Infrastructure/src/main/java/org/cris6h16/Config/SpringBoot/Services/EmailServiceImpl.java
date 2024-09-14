package org.cris6h16.Config.SpringBoot.Services;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Constants.EmailContent;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@ComponentScan("org.cris6h16.*")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final JwtUtils jwtUtils;
    @Value("${jwt.expiration.token.verification.email.secs}")
    private long EMAIL_VERIFICATION_TOKEN_TIME_LIVE;

    public EmailServiceImpl(JavaMailSender mailSender, JwtUtils jwtUtils) {
        this.mailSender = mailSender;
        this.jwtUtils = jwtUtils;
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

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, isHTML);


            mailSender.send(message);
            log.info("Email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.toString());
        }
    }

    @Override
    public void sendAsychVerificationEmail(UserModel userModel) {
        // Send email in async way ( non-blocking ) -> also I can use a ExecutorService
        CompletableFuture.runAsync(() -> {
            try {
                String token = jwtUtils.genToken(userModel.getId(), null, EMAIL_VERIFICATION_TOKEN_TIME_LIVE);
                sendEmail(userModel.getEmail(), EmailContent.HTML_SIGNUP_SUBJECT, EmailContent.getSignUpHtmlBody(token), true);
            } catch (Exception e) {
                log.error("Error sending email: {}", e.toString());
            }
        });
    }
}
