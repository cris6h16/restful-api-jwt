package org.cris6h16.Config.SpringBoot.Services.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.cris6h16.Config.SpringBoot.Properties.EmailServiceProperties;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.io.IOException;
import java.util.function.Function;

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
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private EmailServiceProperties emailServiceProperties;


    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private MimeMessage mimeMessage;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmail_Success() throws Exception {
        // Arrange
        String email = "cristianmherrera21@gmail.com";
        String subject = "Subject";
        String content = "Content";

        doNothing().when(mailSender).send(mimeMessage);
        when(emailServiceProperties.getUpdateEmail().getSubject()).thenReturn(subject);

        // Act
        emailService.sendEmail(
                email,
                props -> props.getUpdateEmail().getSubject(),
                content
        );

        // Assert
        verify(mailSender, times(1)).send(mimeMessage);
        verify(mailSender, times(1)).send(argThat((MimeMessage msg) -> {
            try {
                assertEquals(subject, msg.getSubject());
                assertEquals(email, msg.getAllRecipients()[0].toString());
                assertEquals(content, msg.getContent());

                return true;

            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Test
    void testSendEmail_InvalidArguments() {
        // Arrange
        when(emailServiceProperties.getUpdateEmail().getSubject()).thenReturn("Subject");
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
        long emailVerificationTokenTimeLive = 9999L;
        String expectedContent = "Email Content";

        when(jwtProperties.getToken().getAccess().getRequest().getEmail().getVerification().getSecs())
                .thenReturn(emailVerificationTokenTimeLive);
        when(jwtUtils.genToken(id, null, emailVerificationTokenTimeLive))
                .thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn(expectedContent);

        // Act
        emailService.sendVerificationEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, emailVerificationTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendResetPasswordEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        String token = "generated-token";
        long requestPasswordTokenTimeLive = 1234567L;
        String expectedContent = "Reset Password Content";

        when(jwtProperties.getToken().getAccess().getRequest().getEmail().getReset().getPassword().getSecs())
                .thenReturn(requestPasswordTokenTimeLive);
        when(jwtUtils.genToken(id, null, requestPasswordTokenTimeLive))
                .thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn(expectedContent);

        // Act
        emailService.sendResetPasswordEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, requestPasswordTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRequestDeleteAccountEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        String token = "generated-token";
        long requestDeleteAccountTokenTimeLive = 987654L;
        String expectedContent = "Delete Account Content";

        when(jwtProperties.getToken().getAccess().getRequest().getEmail().getDeleteAccount().getSecs())
                .thenReturn(requestDeleteAccountTokenTimeLive);
        when(jwtUtils.genToken(id, null, requestDeleteAccountTokenTimeLive))
                .thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn(expectedContent);

        // Act
        emailService.sendRequestDeleteAccountEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, requestDeleteAccountTokenTimeLive);
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRequestUpdateEmail() {
        // Arrange
        Long id = 1L;
        String email = "cristianmherrera21@gmail.com";
        String token = "generated-token";
        long requestUpdateEmailTokenTimeLive = 1010101010L;
        String expectedContent = "Update Email Content";

        when(jwtProperties.getToken().getAccess().getRequest().getEmail().getUpdateEmail().getSecs())
                .thenReturn(requestUpdateEmailTokenTimeLive);
        when(jwtUtils.genToken(id, null, requestUpdateEmailTokenTimeLive))
                .thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn(expectedContent);

        // Act
        emailService.sendRequestUpdateEmail(id, email);

        // Assert
        verify(jwtUtils, times(1)).genToken(id, null, requestUpdateEmailTokenTimeLive);
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

}