package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Exceptions.Impls.PasswordNotMatchException;
import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UpdatePasswordUseCaseTest {
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MyPasswordEncoder myPasswordEncoder;
    @Mock
    private ErrorMessages errorMessages;
    @InjectMocks
    private UpdatePasswordUseCase updatePasswordUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("any msg")).when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> updatePasswordUseCase.handle(id, currentPassword, newPassword))
               .isInstanceOf(OutOfMemoryError.class)
               .hasMessage("any msg");
    }

    @Test
    void handle_invalidCurrentPasswordThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("any msg")).when(userValidator).validatePassword(currentPassword);

        // Act & Assert
        assertThatThrownBy(() -> updatePasswordUseCase.handle(id, currentPassword, newPassword))
               .isInstanceOf(OutOfMemoryError.class)
               .hasMessage("any msg");
    }

    @Test
    void handle_invalidNewPasswordThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("any msg")).when(userValidator).validatePassword(newPassword);

        // Act & Assert
        assertThatThrownBy(() -> updatePasswordUseCase.handle(id, currentPassword, newPassword))
               .isInstanceOf(OutOfMemoryError.class)
               .hasMessage("any msg");
    }

    @Test
    void handle_userNotFoundThenNotFoundException(){
        // Arrange
        Long id = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        when(userRepository.existsById(id))
                .thenReturn(false);
        when(errorMessages.getUserNotFoundMessage())
                .thenReturn("any msg");

        // Act & Assert
        assertThatThrownBy(() -> updatePasswordUseCase.handle(id, currentPassword, newPassword))
               .isInstanceOf(NotFoundException.class)
               .hasMessage("any msg");
    }

    @Test
    void handle_currentPasswordIsNullThenUnexpectedException(){
        // Arrange
        Long id = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        when(userRepository.existsById(id))
                .thenReturn(true);
        when(userRepository.findPasswordById(id))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updatePasswordUseCase.handle(id, currentPassword, newPassword))
               .isInstanceOf(UnexpectedException.class)
               .hasMessage("User must have a non-null password");
    }

    @Test
    void handle_currentPasswordNotMatchThenPasswordNotMatchException(){
        // Arrange
        Long id = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        when(userRepository.existsById(id))
                .thenReturn(true);
        when(userRepository.findPasswordById(id))
                .thenReturn(Optional.of("storedPassword"));
        when(myPasswordEncoder.matches(currentPassword, "storedPassword"))
                .thenReturn(false);
        when(errorMessages.getCurrentPasswordNotMacthMessage())
                .thenReturn("any msg");

        // Act & Assert
        assertThatThrownBy(() -> updatePasswordUseCase.handle(id, currentPassword, newPassword))
               .isInstanceOf(PasswordNotMatchException.class)
               .hasMessage("any msg");
    }

    @Test
    void handle_successUntrimmed(){
        // Arrange
        Long id = 1L;
        String currentPassword = "  currentPassword   ";
        String newPassword = "      newPassword    ";

        when(userRepository.existsById(id))
                .thenReturn(true);
        when(userRepository.findPasswordById(id))
                .thenReturn(Optional.of("storedPassword"));
        when(myPasswordEncoder.matches(currentPassword.trim(), "storedPassword"))
                .thenReturn(true);
        when(myPasswordEncoder.encode(newPassword.trim()))
                .thenReturn("encodedNewPassword");

        // Act
        updatePasswordUseCase.handle(id, currentPassword, newPassword);

        // Assert
        verify(userRepository).updatePasswordById(id, "encodedNewPassword");
        verify(myPasswordEncoder).encode(newPassword.trim());
        verify(myPasswordEncoder).matches(currentPassword.trim(), "storedPassword");
        verify(userRepository).findPasswordById(id);
        verify(userRepository).existsById(id);
        verify(userValidator).validateId(id);
        verify(userValidator).validatePassword(newPassword.trim());
        verify(userValidator).validatePassword(currentPassword.trim());
    }
}