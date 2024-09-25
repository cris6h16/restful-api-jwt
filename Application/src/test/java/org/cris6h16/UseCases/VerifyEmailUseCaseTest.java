package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class VerifyEmailUseCaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private ErrorMessages errorMessages;
    @InjectMocks
    private VerifyEmailUseCase verifyEmailUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows(){
        // Arrange
        Long id = 1L;

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("Any msg"))
                .when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> verifyEmailUseCase.handle(id))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("Any msg");
    }

    @Test
    void handle_userNotFoundThenNotFoundException(){
        // Arrange
        Long id = 1L;

        when(userRepository.existsById(id))
                .thenReturn(false);
        when(errorMessages.getUserNotFoundMessage())
                .thenReturn("Any msg");

        // Act & Assert
        assertThatThrownBy(() -> verifyEmailUseCase.handle(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Any msg");
    }

    @Test
    void handle_success(){
        // Arrange
        Long id = 1L;

        when(userRepository.existsById(id))
                .thenReturn(true);

        // Act
        verifyEmailUseCase.handle(id);

        // Assert
        verify(userRepository).updateEmailVerifiedById(id, true);
    }
}