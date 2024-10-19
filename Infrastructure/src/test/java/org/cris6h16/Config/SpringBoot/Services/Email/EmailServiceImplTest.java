package org.cris6h16.Config.SpringBoot.Services.Email;

import jakarta.mail.internet.MimeMessage;
import org.cris6h16.Config.SpringBoot.Properties.EmailServiceProperties;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class EmailServiceImplTest {


    @Mock
    private JavaMailSender mailSender;

    @Mock
    private JwtUtilsImpl jwtUtils;

    @Mock
    private ITemplateEngine templateEngine;

    private JwtProperties jwtProperties;
    private EmailServiceProperties emailServiceProperties;

    private EmailServiceImpl emailService;

    @Mock
    private MimeMessage mimeMessage;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        jwtProperties = createJwtProperties();
        emailServiceProperties = createEmailServiceProperties();

        emailService = new EmailServiceImpl(
                templateEngine,
                mailSender,
                jwtUtils,
                jwtProperties,
                emailServiceProperties
        );
    }


    @Test
    void testSendEmail_Success() throws Exception {
        // Arrange
        String email = "cristianmherrera21@gmail.com";
        String subject = emailServiceProperties.getUpdateEmail().getSubject();
        String content = "Content";

        doNothing().when(mailSender).send(mimeMessage);

        // Act
        emailService.sendEmail(
                email,
                props -> props.getUpdateEmail().getSubject(),
                content
        );

        // Assert
        verify(mailSender, times(1)).send(mimeMessage);
        verify(mimeMessage).setSubject(subject);
    }

    @Test
    void testSendEmail_MailSenderThrowsException_caughtAndIgnored() { // the service was written to work async so exceptions are caught and ignored ( & logged of course )
        // Arrange
        String email = "cristianmherrera21@gmail.com";
        String subject = emailServiceProperties.getUpdateEmail().getSubject();
        String content = "Content";
        doThrow(RuntimeException.class).when(mailSender).send(mimeMessage);

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendEmail(
                email,
                props -> props.getUpdateEmail().getSubject(),
                content
        ));
    }

    @Test
    void testSendEmail_InvalidArguments() {
        // Arrange
        Function<EmailServiceProperties, String> subjectExtractor = props -> props.getUpdateEmail().getSubject();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail(null, subjectExtractor, "Content"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("cristianmherrera21@gmail.com", null, "Content"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("cristianmherrera21@gmail.com", subjectExtractor, null));
    }

    @Test
    void testSendVerificationEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        String token = "generated-token";
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);
        long emailVerificationTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getVerification().getSecs();
        String expectedContent = "Email Content";

        when(jwtUtils.genAccessToken(id, roles, emailVerificationTokenTimeLive)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendVerificationEmail(id, roles, email);

        // Assert
        verify(jwtUtils, times(1)).genAccessToken(id, roles, emailVerificationTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendResetPasswordEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        String token = "generated-token";
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);
        long requestPasswordTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getReset().getPassword().getSecs();
        String expectedContent = "Reset Password Content";

        when(jwtUtils.genAccessToken(id, roles, requestPasswordTokenTimeLive)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendResetPasswordEmail(id, roles, email);

        // Assert
        verify(jwtUtils, times(1)).genAccessToken(id, roles, requestPasswordTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRequestDeleteAccountEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);
        String token = "generated-token";
        long requestDeleteAccountTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getDeleteAccount().getSecs();
        String expectedContent = "Delete Account Content";

        when(jwtUtils.genAccessToken(id, roles, requestDeleteAccountTokenTimeLive))
                .thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn(expectedContent);

        // Act
        emailService.sendRequestDeleteAccountEmail(id, roles, email);

        // Assert
        verify(jwtUtils, times(1)).genAccessToken(id, roles, requestDeleteAccountTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRequestUpdateEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        String token = "generated-token";
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);
        long requestUpdateEmailTokenTimeLive = jwtProperties.getToken().getAccess().getRequest().getEmail().getUpdateEmail().getSecs();
        String expectedContent = "Update Email Content";

        when(jwtUtils.genAccessToken(id,roles, requestUpdateEmailTokenTimeLive)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendRequestUpdateEmail(id, roles, email);

        // Assert
        verify(jwtUtils, times(1)).genAccessToken(id,roles, requestUpdateEmailTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testBuildHtmlContent() {
        // Arrange
        String token = "generated-token";
        String linkTemplate = "http://example.com/verify?token={token}";
        String variableInLinkTemplate = "{token}";
        String hrefVariableInHtml = "variable-name-in-html";
        String htmlTemplateName = "email-verification.html";
        String expectedLink = "http://example.com/verify?token=generated-token";
        String expectedContent = "HTML content";

        Function<EmailServiceProperties, String> linkTemplateExtractor = mock(Function.class);
        Function<EmailServiceProperties, String> hrefVariableInHtmlExtractor = mock(Function.class);
        Function<EmailServiceProperties, String> htmlTemplateNameExtractor = mock(Function.class);
        Function<EmailServiceProperties, String> variableInLinkTemplateExtractor = mock(Function.class);

        when(linkTemplateExtractor.apply(any())).thenReturn(linkTemplate);
        when(hrefVariableInHtmlExtractor.apply(any())).thenReturn(hrefVariableInHtml);
        when(htmlTemplateNameExtractor.apply(any())).thenReturn(htmlTemplateName);
        when(variableInLinkTemplateExtractor.apply(any())).thenReturn(variableInLinkTemplate);

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        String result = emailService.buildHtmlContent(
                token,
                linkTemplateExtractor,
                hrefVariableInHtmlExtractor,
                htmlTemplateNameExtractor,
                variableInLinkTemplateExtractor
        );

        // Assert
        assertEquals(expectedContent, result);
        verify(templateEngine).process(
                eq(htmlTemplateName),
                argThat((IContext argument) ->
                        argument.getVariable(hrefVariableInHtml).equals(expectedLink)
                )
        );
    }

    private JwtProperties createJwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("secret-key");


        // Token
        JwtProperties.Token token = new JwtProperties.Token();
        jwtProperties.setToken(token);

        // Refresh
        JwtProperties.Token.Refresh refresh = new JwtProperties.Token.Refresh();
        JwtProperties.Token.Refresh.Expiration refreshExpiration = new JwtProperties.Token.Refresh.Expiration();
        refreshExpiration.setSecs(3600);
        refresh.setExpiration(refreshExpiration);


        token.setRefresh(refresh);

        // Access
        JwtProperties.Token.Access access = new JwtProperties.Token.Access();
        JwtProperties.Token.Access.Expiration accessExpiration = new JwtProperties.Token.Access.Expiration();
        accessExpiration.setSecs(300);
        access.setExpiration(accessExpiration);

        // Request
        JwtProperties.Token.Access.Request request = new JwtProperties.Token.Access.Request();

        // Email
        JwtProperties.Token.Access.Request.Email email = new JwtProperties.Token.Access.Request.Email();

        // Verification
        JwtProperties.Token.Access.Request.Email.Verification verification = new JwtProperties.Token.Access.Request.Email.Verification();
        verification.setSecs(120);
        email.setVerification(verification);

        // Delete Account
        JwtProperties.Token.Access.Request.Email.DeleteAccount deleteAccount = new JwtProperties.Token.Access.Request.Email.DeleteAccount();
        deleteAccount.setSecs(3600);
        email.setDeleteAccount(deleteAccount);

        // Update Email
        JwtProperties.Token.Access.Request.Email.UpdateEmail updateEmail = new JwtProperties.Token.Access.Request.Email.UpdateEmail();
        updateEmail.setSecs(600);
        email.setUpdateEmail(updateEmail);

        // Reset
        JwtProperties.Token.Access.Request.Email.Reset reset = new JwtProperties.Token.Access.Request.Email.Reset();
        JwtProperties.Token.Access.Request.Email.Reset.Password password = new JwtProperties.Token.Access.Request.Email.Reset.Password();
        password.setSecs(1800);
        reset.setPassword(password);
        email.setReset(reset);

        request.setEmail(email);
        access.setRequest(request);
        token.setAccess(access);

        return jwtProperties;
    }

    private EmailServiceProperties createEmailServiceProperties() {
        EmailServiceProperties emailServiceProperties = new EmailServiceProperties();

        // Set host
        emailServiceProperties.setHost("smtp.example.com");

        // Token
        EmailServiceProperties.Token token = new EmailServiceProperties.Token();
        token.setParameter("tokenParam");
        token.setVariableInLinkTemplate("tokenVar");
        emailServiceProperties.setToken(token);

        // Verification
        EmailServiceProperties.Verification verification = new EmailServiceProperties.Verification();
        verification.setLinkTemplate("http://example.com/verify?token=${token}");
        verification.setSubject("Please verify your email");

        EmailServiceProperties.Verification.Html verificationHtml = new EmailServiceProperties.Verification.Html();
        verificationHtml.setName("Verify your email");
        verificationHtml.setHrefVariable("token");
        verification.setHtml(verificationHtml);
        emailServiceProperties.setVerification(verification);

        // Reset Password
        EmailServiceProperties.ResetPassword resetPassword = new EmailServiceProperties.ResetPassword();
        resetPassword.setLinkTemplate("http://example.com/reset-password?token=${token}");
        resetPassword.setSubject("Reset your password");

        EmailServiceProperties.ResetPassword.Html resetPasswordHtml = new EmailServiceProperties.ResetPassword.Html();
        resetPasswordHtml.setName("Reset your password");
        resetPasswordHtml.setHrefVariable("token");
        resetPassword.setHtml(resetPasswordHtml);
        emailServiceProperties.setResetPassword(resetPassword);

        // Delete Account
        EmailServiceProperties.DeleteAccount deleteAccount = new EmailServiceProperties.DeleteAccount();
        deleteAccount.setLinkTemplate("http://example.com/delete-account?token=${token}");
        deleteAccount.setSubject("Delete your account");

        EmailServiceProperties.DeleteAccount.Html deleteAccountHtml = new EmailServiceProperties.DeleteAccount.Html();
        deleteAccountHtml.setName("Delete your account");
        deleteAccountHtml.setHrefVariable("token");
        deleteAccount.setHtml(deleteAccountHtml);
        emailServiceProperties.setDeleteAccount(deleteAccount);

        // Update Email
        EmailServiceProperties.UpdateEmail updateEmail = new EmailServiceProperties.UpdateEmail();
        updateEmail.setLinkTemplate("http://example.com/update-email?token=${token}");
        updateEmail.setSubject("Update your email");

        EmailServiceProperties.UpdateEmail.Html updateEmailHtml = new EmailServiceProperties.UpdateEmail.Html();
        updateEmailHtml.setName("Update your email");
        updateEmailHtml.setHrefVariable("token");
        updateEmail.setHtml(updateEmailHtml);
        emailServiceProperties.setUpdateEmail(updateEmail);

        return emailServiceProperties;
    }
}