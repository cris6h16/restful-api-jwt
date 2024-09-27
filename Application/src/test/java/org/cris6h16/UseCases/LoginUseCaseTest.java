package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.EmailNotVerifiedException;
import org.cris6h16.Exceptions.Impls.InvalidCredentialsException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MyPasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private EmailService emailService;
    @Mock
    private ErrorMessages errorMessages;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidEmail() {
        // Arrange
        String password = "pass";
        String email = "email";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello"))
                .when(userValidator).validateEmail(email);

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.handle(email, password))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello");
    }

    @Test
    void handle_invalidPassword() {
        // Arrange
        String password = "pass";
        String email = "email";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello pass"))
                .when(userValidator).validatePassword(password);

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.handle(email, password))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello pass");
    }

    @Test
    void handle_userNotFoundThenInvalidCredentialsException() {
        // Arrange
        String password = "pass";
        String email = "email";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        when(errorMessages.getInvalidCredentialsMessage())
                .thenReturn("your crdentiaslare worng etc ect etc");

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.handle(email, password))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("your crdentiaslare worng etc ect etc");

        verify(userRepository).findByEmail(email);
    }

    @Test
    void handle_userIsNotActiveThenInvalidCredentialsException() {
        // Arrange
        String password = "pass";
        String email = "email";
        UserModel model = new UserModel.Builder().setActive(false).build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(model));
        when(errorMessages.getInvalidCredentialsMessage())
                .thenReturn("you credentials re wrogn etc etc ");

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.handle(email, password))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("you credentials re wrogn etc etc ");

        verify(errorMessages).getInvalidCredentialsMessage();
    }

    @Test
    void handle_passwordNotMatchThenInvalidCredentialsException() {
        // Arrange
        String password = "pass";
        String email = "email";
        UserModel model = new UserModel.Builder().setActive(true).setPassword("encoded").build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(model));
        when(passwordEncoder.matches(password, "encoded"))
                .thenReturn(false);
        when(errorMessages.getInvalidCredentialsMessage())
                .thenReturn("your credentials are wrong etc etc ");

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.handle(email, password))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("your credentials are wrong etc etc ");

        verify(passwordEncoder).matches(password, "encoded");
        verify(errorMessages).getInvalidCredentialsMessage();
    }


    @Test
    void handle_emailNotVerifiedThenEmailNotVerifiedException() {
        // Arrange
        String password = "pass";
        String email = "email";
        UserModel model = new UserModel.Builder().setActive(true).setEmailVerified(false).build();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(model));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);
        when(errorMessages.getEmailNotVerifiedMessage())
                .thenReturn("email not verified msg");

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.handle(email, password))
                .isInstanceOf(EmailNotVerifiedException.class)
                .hasMessage("email not verified msg");

        verify(errorMessages).getEmailNotVerifiedMessage();
        verify(emailService).sendVerificationEmail(model.getId(), model.getEmail());
    }

    @Test
    void handle_validUserThenReturnLoginOutput() {
        // Arrange
        String password = "pass";
        String email = "email";
        UserModel model = new UserModel.Builder()
                .setActive(true)
                .setEmailVerified(true)
                .setId(1L)
                .setRoles(Collections.singleton(ERoles.ROLE_USER))
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(model));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtils.genRefreshToken(1L)).thenReturn("refresh");
        when(jwtUtils.genAccessToken(1L, model.getRoles())).thenReturn("access");

        // Act
        LoginOutput output = loginUseCase.handle(email, password);

        // Assert
        assertThat(output)
                .hasFieldOrPropertyWithValue("accessToken", "access")
                .hasFieldOrPropertyWithValue("refreshToken", "refresh");

        verify(jwtUtils).genRefreshToken(1L);
        verify(jwtUtils).genAccessToken(1L, model.getRoles());
    }


}