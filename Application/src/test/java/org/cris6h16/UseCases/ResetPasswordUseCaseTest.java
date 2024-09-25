package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ResetPasswordUseCaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private MyPasswordEncoder passwordEncoder;
    @Mock
    private ErrorMessages errorMessages;

    @InjectMocks
    private ResetPasswordUseCase resetPasswordUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows() {
        // Arrange
        Long id = 1L;
        String password = "password";

        // i dont care abpit the exception, that's a concern of the validator
        doThrow(new OutOfMemoryError("any msg"))
                .when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> resetPasswordUseCase.handle(id, password))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("any msg");
    }

    @Test
    void handle_userDoesNotExistThenThrowNotFoundException() {
        // Arrange
        Long id = 1L;
        String password = "password";

        when(userRepository.existsById(id))
                .thenReturn(false);
        when(errorMessages.getUserNotFoundMessage())
                .thenReturn("any msg");

        // Act & Assert
        assertThatThrownBy(() -> resetPasswordUseCase.handle(id, password))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("any msg");
    }

    @Test
    void handle_successUntrimmed() { // todo: verify every test to verify if exist tests for trimmed attributes
        // Arrange
        Long id = 1L;
        String password = "   password  ";

        when(userRepository.existsById(id))
                .thenReturn(true);
        when(passwordEncoder.encode(password.trim()))
                .thenReturn("encoded");

        // Act
        resetPasswordUseCase.handle(id, password);

        // Assert
        verify(userRepository).existsById(id);
        verify(passwordEncoder).encode(password.trim());
        verify(userRepository).updatePasswordById(id, "encoded");
    }
}
