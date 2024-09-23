package org.cris6h16.UseCases;

import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DeleteAccountUseCaseTest {
    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteAccountUseCase deleteAccountUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_InvalidId() {
        //Arrange
        Long id = 999L;

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello cris6h16"))
                .when(userValidator).validateId(id);

        //Act & Assert
        assertThatThrownBy(() -> deleteAccountUseCase.handle(id))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello cris6h16");
    }

    @Test
    void handle_deactivateSuccessfully(){
        // Arrange
        Long id = 999L;
        doNothing().when(userValidator).validateId(anyLong());
        doNothing().when(userRepository).deactivate(anyLong());

        // Act
        deleteAccountUseCase.handle(id);

        // Assert
        verify(userRepository).deactivate(id);
        verify(userValidator).validateId(id);
    }

}