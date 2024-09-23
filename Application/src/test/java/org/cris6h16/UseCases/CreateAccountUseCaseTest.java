package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.AlreadyExistException;
import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.CreateAccountCommand;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CreateAccountUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MyPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private ErrorMessages errorMessages;
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_nullCmdThenThrowsUnexpectedException() {
        // Arrange
        CreateAccountCommand cmd = null;

        // Act & Assert
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(UnexpectedException.class);
    }

    @Test
    void handle_invalidUsernameThenValidatorThrows() {
        // Arrange
        CreateAccountCommand cmd = mock(CreateAccountCommand.class);

        doNothing().when(userValidator).validatePassword(any());
        doNothing().when(userValidator).validateEmail(any());
        doNothing().when(userValidator).validateRoles(any());

        // I don't care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello username im cris6h16"))
                .when(userValidator)
                .validateUsername(any());

        // Act & Assert
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello username im cris6h16");
    }

    @Test
    void handle_invalidPasswordThenValidatorThrows() {
        // Arrange
        CreateAccountCommand cmd = mock(CreateAccountCommand.class);

        doNothing().when(userValidator).validateUsername(any());
        doNothing().when(userValidator).validateEmail(any());
        doNothing().when(userValidator).validateRoles(any());

        // I don't care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello password im cris6h16"))
                .when(userValidator)
                .validatePassword(any());

        // Act & Assert
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello password im cris6h16");
    }

    @Test
    void handle_invalidEmailThenValidatorThrows() {
        // Arrange
        CreateAccountCommand cmd = mock(CreateAccountCommand.class);

        doNothing().when(userValidator).validateUsername(any());
        doNothing().when(userValidator).validatePassword(any());
        doNothing().when(userValidator).validateRoles(any());

        // I don't care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello email im cris6h16"))
                .when(userValidator)
                .validateEmail(any());

        // Act & Assert
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello email im cris6h16");
    }

    @Test
    void handle_invalidRolesThenValidatorThrows() {
        // Arrange
        CreateAccountCommand cmd = mock(CreateAccountCommand.class);

        doNothing().when(userValidator).validateUsername(any());
        doNothing().when(userValidator).validatePassword(any());
        doNothing().when(userValidator).validateEmail(any());

        // I don't care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello roles im cris6h16"))
                .when(userValidator)
                .validateRoles(any());

        // Act & Assert
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello roles im cris6h16");
    }

    @Test
    void handle_correctWithUntrimmedValues() {
        // Arrange
        CreateAccountCommand cmd = createValidUntrimmed();
        CreateAccountCommand trimmed = trim(cmd);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.saveCustom(any())).thenReturn(new UserModel.Builder()
                .setId(15L)
                .setUsername(trimmed.getUsername())
                .setEmail(trimmed.getEmail())
                .build());

        // Act
        Long id = createAccountUseCase.handle(cmd);

        // Assert
        assertThat(id).isEqualTo(15L);
        verify(passwordEncoder).encode(trimmed.getPassword());
        verify(userRepository).existsByUsernameCustom(trimmed.getUsername());// duplicates checked with trimmed
        verify(userRepository).existsByEmailCustom(trimmed.getEmail());
        verify(userRepository).saveCustom(argThat(passedToDb -> {
            boolean usernameTrimmed = passedToDb.getUsername().equals(trimmed.getUsername().trim());
            boolean emailTrimmed = passedToDb.getEmail().equals(trimmed.getEmail().trim());
            boolean rolesEquals = passedToDb.getRoles().equals(trimmed.getRoles());
            return usernameTrimmed && emailTrimmed && rolesEquals;
        }));
        verify(emailService).sendVerificationEmail(id, trimmed.getEmail().trim());
    }

    private CreateAccountCommand trim(CreateAccountCommand cmd) {
        return new CreateAccountCommand(cmd.getUsername().trim(), cmd.getPassword().trim(), cmd.getEmail().trim(), cmd.getRoles());
    }

    private CreateAccountCommand createValidUntrimmed() {
        String username = "   cris6h16 ";
        String password = "  12345678   ";
        String email = "   cristiamherrera21@gmail.com     ";
        Set<ERoles> roles = mock(Set.class);
        return new CreateAccountCommand(username, password, email, roles);

    }

    @Test
    void handle_usernameAlreadyExistsThenAlreadyExistException() {
        // Arrange
        CreateAccountCommand cmd = createCmd();
        when(userRepository.existsByUsernameCustom(cmd.getUsername())).thenReturn(true);
        when(errorMessages.getUsernameAlreadyExistsMessage()).thenReturn("msg123");

        // Act
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessage("msg123");
    }

    @Test
    void handle_emailAlreadyExistsThenAlreadyExistException() {
        // Arrange
        CreateAccountCommand cmd = createCmd();
        when(userRepository.existsByEmailCustom(cmd.getEmail())).thenReturn(true);
        when(errorMessages.getEmailAlreadyExistsMessage()).thenReturn("msg000");

        // Act
        assertThatThrownBy(() -> createAccountUseCase.handle(cmd))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessage("msg000");
    }

    private CreateAccountCommand createCmd() {
        String username = "cris6h16";
        String password = "12345678";
        String email = "cristiamherrera21@gmail.com";
        Set<ERoles> roles = mock(Set.class);
        return new CreateAccountCommand(username, password, email, roles);
    }
}
