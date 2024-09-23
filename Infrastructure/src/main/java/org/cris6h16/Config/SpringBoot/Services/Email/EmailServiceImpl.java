package org.cris6h16.Config.SpringBoot.Services.Email;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Services.EmailService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@ComponentScan("org.cris6h16.*")
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine templateEngine;

    private final JavaMailSender mailSender;
    private final JwtUtilsImpl jwtUtils;
    private final long emailVerificationTokenTimeLive;
    private final long requestDeleteAccountTokenTimeLive;
    private final String emailVerificationLinkTemplate;
    private final String resetPasswordLinkTemplate;
    private final long requestPasswordTokenTimeLive;
    private final String deleteAccountLinkTemplate;
    private final long requestUpdateEmailTokenTimeLive;
    private final String updateEmailLinkTemplate;

    public EmailServiceImpl(SpringTemplateEngine templateEngine, JavaMailSender mailSender, JwtUtilsImpl jwtUtils, long emailVerificationTokenTimeLive, long requestDeleteAccountTokenTimeLive, String emailVerificationLink, String resetPasswordLink, long requestPasswordTokenTimeLive, String deleteAccountLink, long requestUpdateEmailTokenTimeLive, String updateEmailLinkTemplate) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.jwtUtils = jwtUtils;
        this.emailVerificationTokenTimeLive = emailVerificationTokenTimeLive;
        this.requestDeleteAccountTokenTimeLive = requestDeleteAccountTokenTimeLive;
        this.emailVerificationLinkTemplate = emailVerificationLink;
        this.resetPasswordLinkTemplate = resetPasswordLink;
        this.requestPasswordTokenTimeLive = requestPasswordTokenTimeLive;
        this.deleteAccountLinkTemplate = deleteAccountLink;
        this.requestUpdateEmailTokenTimeLive = requestUpdateEmailTokenTimeLive;
        this.updateEmailLinkTemplate = updateEmailLinkTemplate;
    }

    public void sendEmail(String email, String subject, String content) {
        String failMsg = "";
        if (email == null || email.isEmpty()) failMsg = "Email cannot be null or empty";
        if (subject == null || subject.isEmpty()) failMsg = "Subject cannot be null or empty";
        if (content == null || content.isEmpty()) failMsg = "Text cannot be null or empty";
        if (!failMsg.isEmpty()) throw new IllegalArgumentException(failMsg);

        log.info("Sending email to {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);


            mailSender.send(message);
            log.info("Email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.toString());
        }
    }

    @Override
    public void sendVerificationEmail(Long id, String email) {
        String token = jwtUtils.genToken(id, null, emailVerificationTokenTimeLive);
        String content = buildHtmlContent(token, emailVerificationLinkTemplate, "EmailVerificationLink", "verification-email.html");
        sendEmail(email, "Email Verification needed", content);
    }


    @Override
    public void sendResetPasswordEmail(Long id, String email) {
        String token = jwtUtils.genToken(id, null, requestPasswordTokenTimeLive);
        String content = buildHtmlContent(token, resetPasswordLinkTemplate, "ResetPasswordLink", "request-reset-password.html");
        sendEmail(email, "Password Reset Request", content);
    }

    @Override
    public void sendRequestDeleteAccountEmail(Long id, String email) {
        String token = jwtUtils.genToken(id, null, requestDeleteAccountTokenTimeLive);
        String content = buildHtmlContent(token, deleteAccountLinkTemplate, "DeleteAccountLink", "request-delete-account.html");
        sendEmail(email, "Request Delete Account", content);
    }

    @Override
    public void sendRequestUpdateEmail(Long id, String email) {
        String token = jwtUtils.genToken(id, null, requestUpdateEmailTokenTimeLive);
        String content = buildHtmlContent(token, updateEmailLinkTemplate, "UpdateEmailLink", "request-update-email.html");
        sendEmail(email, "Request Update Email", content);
    }

    /**
     * @param token
     * @param linkTemplate     for insert token, example {@code example.com?token={token}}
     * @param linkVariableName the thymeleaf variable name inside the html, example of a variable in thymeleaf {@code <a th:href="${var}" }, in this case the variable name is {@code var}
     * @param htmlTemplateName html template name, example {@code email-verification.html}
     * @return
     */
    private String buildHtmlContent(String token, String linkTemplate, String linkVariableName, String htmlTemplateName) {
        String link = linkTemplate.replace("{token}", token);

        Context context = new Context();
        context.setVariable(linkVariableName, link);
        return templateEngine.process(htmlTemplateName, context);

    }

}
