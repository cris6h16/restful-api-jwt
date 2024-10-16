package org.cris6h16.Config.SpringBoot;

import org.cris6h16.In.Ports.*;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.UseCases.*;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PD: this tests i consider irrelevant and is a test entirely wrote by ChatGPT
@ExtendWith(MockitoExtension.class)
public class ApplicationBeansTest {

    @InjectMocks
    private ApplicationBeans applicationBeans;

    // Mock the dependencies
    @Mock
    private ErrorMessages errorMessages;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MyPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserValidator userValidator;

    // Test userValidator bean creation
    @Test
    void testUserValidatorBean() {
        UserValidator userValidator = applicationBeans.userValidator(errorMessages);
        assertNotNull(userValidator);
    }

    // Test createAccountPort bean creation
    @Test
    void testCreateAccountPortBean() {
        CreateAccountPort createAccountPort = applicationBeans.createAccountPort(
                userRepository, passwordEncoder, emailService, jwtUtils, errorMessages, userValidator);
        assertNotNull(createAccountPort);
        assertTrue(createAccountPort instanceof CreateAccountUseCase);
    }

    // Test verifyEmailPort bean creation
    @Test
    void testVerifyEmailPortBean() {
        VerifyEmailPort verifyEmailPort = applicationBeans.verifyEmailPort(
                userRepository, userValidator, errorMessages);
        assertNotNull(verifyEmailPort);
        assertTrue(verifyEmailPort instanceof VerifyEmailUseCase);
    }

    // Test requestResetPasswordPort bean creation
    @Test
    void testRequestResetPasswordPortBean() {
        RequestResetPasswordPort requestResetPasswordPort = applicationBeans.requestResetPasswordPort(
                emailService, userValidator, userRepository, errorMessages);
        assertNotNull(requestResetPasswordPort);
        assertTrue(requestResetPasswordPort instanceof RequestResetPasswordUseCase);
    }

    // Test loginPort bean creation
    @Test
    void testLoginPortBean() {
        LoginPort loginPort = applicationBeans.loginPort(
                userRepository, passwordEncoder, jwtUtils, emailService, errorMessages, userValidator);
        assertNotNull(loginPort);
        assertTrue(loginPort instanceof LoginUseCase);
    }

    // Test resetPasswordPort bean creation
    @Test
    void testResetPasswordPortBean() {
        ResetPasswordPort resetPasswordPort = applicationBeans.getResetPasswordPort(
                userRepository, userValidator, passwordEncoder, errorMessages);
        assertNotNull(resetPasswordPort);
        assertTrue(resetPasswordPort instanceof ResetPasswordUseCase);
    }

    // Test refreshAccessTokenPort bean creation
    @Test
    void testRefreshAccessTokenPortBean() {
        RefreshAccessTokenPort refreshAccessTokenPort = applicationBeans.refreshAccessTokenPort(
                jwtUtils, userRepository, errorMessages, userValidator);
        assertNotNull(refreshAccessTokenPort);
        assertTrue(refreshAccessTokenPort instanceof RefreshAccessTokenUseCase);
    }

    // Test requestDeleteAccountPort bean creation
    @Test
    void testRequestDeleteAccountPortBean() {
        RequestDeleteAccountPort requestDeleteAccountPort = applicationBeans.requestDeleteAccountPort(
                userValidator, userRepository, emailService, errorMessages);
        assertNotNull(requestDeleteAccountPort);
        assertTrue(requestDeleteAccountPort instanceof RequestDeleteAccountUseCase);
    }

    // Test deleteAccountPort bean creation
    @Test
    void testDeleteAccountPortBean() {
        DeleteAccountPort deleteAccountPort = applicationBeans.deleteAccountPort(
                userValidator, userRepository, errorMessages);
        assertNotNull(deleteAccountPort);
        assertTrue(deleteAccountPort instanceof DeleteAccountUseCase);
    }

    // Test updateUsernamePort bean creation
    @Test
    void testUpdateUsernamePortBean() {
        UpdateUsernamePort updateUsernamePort = applicationBeans.updateUsernamePort(
                userValidator, userRepository, errorMessages);
        assertNotNull(updateUsernamePort);
        assertTrue(updateUsernamePort instanceof UpdateUsernameUseCase);
    }

    // Test updatePasswordPort bean creation
    @Test
    void testUpdatePasswordPortBean() {
        UpdatePasswordPort updatePasswordPort = applicationBeans.updatePasswordPort(
                userValidator, userRepository, passwordEncoder, errorMessages);
        assertNotNull(updatePasswordPort);
        assertTrue(updatePasswordPort instanceof UpdatePasswordUseCase);
    }

    // Test updateEmailPort bean creation
    @Test
    void testUpdateEmailPortBean() {
        UpdateEmailPort updateEmailPort = applicationBeans.updateEmailPort(
                userValidator, userRepository, emailService, errorMessages);
        assertNotNull(updateEmailPort);
        assertTrue(updateEmailPort instanceof UpdateEmailUseCase);
    }

    // Test requestUpdateEmailPort bean creation
    @Test
    void testRequestUpdateEmailPortBean() {
        RequestUpdateEmailPort requestUpdateEmailPort = applicationBeans.requestUpdateEmailPort(
                userValidator, userRepository, emailService, errorMessages);
        assertNotNull(requestUpdateEmailPort);
        assertTrue(requestUpdateEmailPort instanceof RequestUpdateEmailUseCase);
    }

    // Test getPublicProfilePort bean creation
    @Test
    void testGetPublicProfilePortBean() {
        GetPublicProfilePort getPublicProfilePort = applicationBeans.getPublicProfilePort(
                userValidator, userRepository, errorMessages);
        assertNotNull(getPublicProfilePort);
        assertTrue(getPublicProfilePort instanceof GetPublicProfileUseCase);
    }

    // Test getAllPublicProfilesPort bean creation
    @Test
    void testGetAllPublicProfilesPortBean() {
        GetAllPublicProfilesPort getAllPublicProfilesPort = applicationBeans.getAllPublicProfilesPort(
                userRepository);
        assertNotNull(getAllPublicProfilesPort);
        assertTrue(getAllPublicProfilesPort instanceof GetAllPublicProfilesUseCase);
    }
}
