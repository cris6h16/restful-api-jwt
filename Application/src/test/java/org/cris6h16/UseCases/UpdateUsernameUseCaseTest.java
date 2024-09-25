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

class UpdateUsernameUseCaseTest {
    @Mock
    private  UserValidator userValidator;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  ErrorMessages errorMessages;
    @InjectMocks
    private UpdateUsernameUseCase updateUsernameUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String username = "cris6h16";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("Any msg"))
                .when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> updateUsernameUseCase.handle(id, username))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("Any msg");
    }

    @Test
    void handle_invalidUsernameThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String username = "cris6h16";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("Any msg"))
                .when(userValidator).validateUsername(username);

        // Act & Assert
        assertThatThrownBy(() -> updateUsernameUseCase.handle(id, username))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("Any msg");
    }

    @Test
    void handle_userNotFoundThenNotFoundException(){
        // Arrange
        Long id = 1L;
        String username = "cris6h16";

        when(userRepository.existsById(id))
                .thenReturn(false);
        when(errorMessages.getUserNotFoundMessage())
                .thenReturn("Any msg");

        // Act & Assert
        assertThatThrownBy(() -> updateUsernameUseCase.handle(id, username))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Any msg");
        verify(errorMessages).getUserNotFoundMessage();
    }

    @Test
    void handle_successUntrimmed(){
        // Arrange
        Long id = 1L;
        String username = " cris6h16    ";

        when(userRepository.existsById(id))
                .thenReturn(true);

        // Act
        updateUsernameUseCase.handle(id, username);

        // Assert
        verify(userRepository).updateUsernameById(id, "cris6h16");
    }

}