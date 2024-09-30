package org.cris6h16.Config.SpringBoot.Services.Email;

import jakarta.mail.internet.MimeMessage;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private EmailServiceImpl emailService;

    @Mock
    private MimeMessage mimeMessage;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(
                templateEngine, mailSender, jwtUtils,
                0, 0,
                "emailVerificationLinkTemplate",
                "resetPasswordLinkTemplate",
                0,
                "deleteAccountLinkTemplate",
                0,
                "updateEmailLinkTemplate"

        );
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    }

    @Test
    void testSendEmail_Success() throws Exception {
        // Arrange
        String email = "test@example.com";
        String subject = "Subject";
        String content = "Content";

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        doNothing().when(mailSender).send(mimeMessage);

        // Act
        emailService.sendEmail(email, subject, content);

        // Assert
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_InvalidArguments() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail(null, "Subject", "Content"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("test@example.com", null, "Content"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail("test@example.com", "Subject", null));
    }

    @Test
    void testSendVerificationEmail() {
        // Arrange
        String email = "test@example.com";
        Long id = 1L;
        String token = "generated-token";
        String expectedContent = "Email Content";

        when(jwtUtils.genToken(id, null, 0)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendVerificationEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, 0);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendResetPasswordEmail() {
        // Arrange
        String email = "test@example.com";
        Long id = 1L;
        String token = "generated-token";
        String expectedContent = "Reset Password Content";

        when(jwtUtils.genToken(id, null, 0)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendResetPasswordEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, 0);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRequestDeleteAccountEmail() {
        // Arrange
        String email = "test@example.com";
        Long id = 1L;
        String token = "generated-token";
        String expectedContent = "Delete Account Content";

        when(jwtUtils.genToken(id, null, 0)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendRequestDeleteAccountEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, 0);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRequestUpdateEmail() {
        // Arrange
        String email = "test@example.com";
        Long id = 1L;
        String token = "generated-token";
        String expectedContent = "Update Email Content";

        when(jwtUtils.genToken(id, null, 0)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        emailService.sendRequestUpdateEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, 0);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testBuildHtmlContent() {
        // Arrange
        String token = "generated-token";
        String linkTemplate = "http://example.com/verify?token={token}";
        String expectedLink = "http://example.com/verify?token=generated-token";
        String htmlTemplateName = "verification-email.html";
        String expectedContent = "HTML content";

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedContent);

        // Act
        String result = emailService.buildHtmlContent(token, linkTemplate, "variable-name-in-html", htmlTemplateName);

        // Assert
        assertEquals(expectedContent, result);
        verify(templateEngine).process(
                eq(htmlTemplateName),
                argThat((IContext argument) ->
                        argument.getVariable("variable-name-in-html").equals(expectedLink)
                ));
    }
}