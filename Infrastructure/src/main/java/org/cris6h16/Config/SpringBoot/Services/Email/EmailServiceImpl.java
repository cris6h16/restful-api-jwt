package org.cris6h16.Config.SpringBoot.Services.Email;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Properties.EmailServiceProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Services.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.function.Function;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final ITemplateEngine templateEngine;

    private final JavaMailSender mailSender;
    private final JwtUtilsImpl jwtUtils;
    private final JwtProperties jwtProperties;
    private final EmailServiceProperties emailServiceProperties;

    public EmailServiceImpl(ITemplateEngine templateEngine,
                            JavaMailSender mailSender,
                            JwtUtilsImpl jwtUtils, JwtProperties jwtProperties, EmailServiceProperties webFrontProperties) {

        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.jwtUtils = jwtUtils;
        this.jwtProperties = jwtProperties;
        this.emailServiceProperties = webFrontProperties;
        log.trace("EmailServiceImpl created with templateEngine: {}, mailSender: {}, jwtUtils: {}, jwtProperties: {}, emailServiceProperties: {}",
                templateEngine, mailSender, jwtUtils, jwtProperties, emailServiceProperties);
    }


    void sendEmail(String email, Function<EmailServiceProperties, String> subjectExtractor, String content) {
        validateArguments(email, subjectExtractor, content);
        String subject = subjectExtractor.apply(emailServiceProperties);
        log.debug("Extracted subject: {}", subject);

        log.debug("Trying to send an email to {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);

            log.debug("Sending email to {}, Content: {}, Subject: {}",
                    Arrays.toString(message.getAllRecipients()),
                    message.getContent(),
                    message.getSubject()
            );

            mailSender.send(message);
            log.debug("Email sent to {}", email);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.toString());
        }
    }

    private void validateArguments(String email, Function<EmailServiceProperties, String> subjectExtractor, String content) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (subjectExtractor == null || subjectExtractor.apply(emailServiceProperties) == null || subjectExtractor.apply(emailServiceProperties).isBlank()) {
            throw new IllegalArgumentException("SubjectExtractor cannot be blank");
        }
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        log.debug("Arguments are valid");
    }

    //todo: logg the app
    @Async
    @Override
    public void sendVerificationEmail(Long id, String email) {
        log.debug("Creating a verification email for id:{}, email: {}", id, email);

        long emailVerificationTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getVerification().getSecs();
        String token = jwtUtils.genToken(id, null, emailVerificationTokenTimeLive);

        log.trace("emailVerificationTokenTimeLive: {}, token: {}", emailVerificationTokenTimeLive, token);

        String content = buildHtmlContent(
                token,
                props -> props.getVerification().getLinkTemplate(),
                props -> props.getVerification().getHtml().getHrefVariable(),
                props -> props.getVerification().getHtml().getName(),
                props -> props.getToken().getVariableInLinkTemplate()
        );

        sendEmail(
                email,
                props -> props.getVerification().getSubject(),
                content
        );
    }


    @Async
    @Override
    public void sendResetPasswordEmail(Long id, String email) {
        log.debug("Creating a Reset Password email for id:{}, email: {}", id, email);

        long requestPasswordTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getReset().getPassword().getSecs();
        String token = jwtUtils.genToken(id, null, requestPasswordTokenTimeLive);

        String content = buildHtmlContent(
                token,
                props -> props.getResetPassword().getLinkTemplate(),
                props -> props.getResetPassword().getHtml().getHrefVariable(),
                props -> props.getResetPassword().getHtml().getName(),
                props -> props.getToken().getVariableInLinkTemplate()
        );

        sendEmail(
                email,
                props -> props.getResetPassword().getSubject(),
                content
        );
    }

    @Async
    @Override
    public void sendRequestDeleteAccountEmail(Long id, String email) {
        log.debug("Creating a Request Delete Account email for id:{}, email: {}", id, email);

        long requestDeleteAccountTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getDeleteAccount().getSecs();
        String token = jwtUtils.genToken(id, null, requestDeleteAccountTokenTimeLive);

        String content = buildHtmlContent(
                token,
                props -> props.getDeleteAccount().getLinkTemplate(),
                props -> props.getDeleteAccount().getHtml().getHrefVariable(),
                props -> props.getDeleteAccount().getHtml().getName(),
                props -> props.getToken().getVariableInLinkTemplate()
        );

        sendEmail(
                email,
                props -> props.getDeleteAccount().getSubject(),
                content
        );
    }

    @Async
    @Override
    public void sendRequestUpdateEmail(Long id, String email) {
        log.debug("Creating a Request Update Email email for id:{}, email: {}", id, email);

        long requestUpdateEmailTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getUpdateEmail().getSecs();
        String token = jwtUtils.genToken(id, null, requestUpdateEmailTokenTimeLive);

        String content = buildHtmlContent(
                token,
                props -> props.getUpdateEmail().getLinkTemplate(),
                props -> props.getUpdateEmail().getHtml().getHrefVariable(),
                props -> props.getUpdateEmail().getHtml().getName(),
                props -> props.getToken().getVariableInLinkTemplate()
        );

        sendEmail(
                email,
                props -> props.getUpdateEmail().getSubject(),
                content
        );
    }


    String buildHtmlContent(String token,
                            Function<EmailServiceProperties, String> linkTemplateExtractor,
                            Function<EmailServiceProperties, String> hrefVariableInHtmlExtractor,
                            Function<EmailServiceProperties, String> htmlTemplateNameExtractor,
                            Function<EmailServiceProperties, String> variableInLinkTemplateExtractor) {

        String linkTemplate = linkTemplateExtractor.apply(emailServiceProperties);
        String hrefVariableInHtml = hrefVariableInHtmlExtractor.apply(emailServiceProperties);
        String htmlTemplateName = htmlTemplateNameExtractor.apply(emailServiceProperties);
        String variableInLinkTemplate = variableInLinkTemplateExtractor.apply(emailServiceProperties);

        log.debug("building html content with token: {}, linkTemplate: {}, hrefVariableInHtml: {}, htmlTemplateName: {}, variableInLinkTemplate: {}",
                token, linkTemplate, hrefVariableInHtml, htmlTemplateName, variableInLinkTemplate);

        String link = linkTemplate.replace(variableInLinkTemplate, token);
        Context context = new Context();
        context.setVariable(hrefVariableInHtml, link);
        String res = templateEngine.process(htmlTemplateName, context);

        log.trace("generated html content: {}", res);

        return res;
    }

}
