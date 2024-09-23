package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
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

public class RequestDeleteAccountUseCaseTest {

    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private ErrorMessages errorMessages;

    @InjectMocks
    private RequestDeleteAccountUseCase requestDeleteAccountUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows() {
        // Arrange
        Long id = 1L;

        // i don't care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("any msg")).when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> requestDeleteAccountUseCase.handle(id))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("any msg");
    }

    @Test
    void handle_userNotFoundThenNotFoundException() {
        // Arrange
        Long id = 1L;

        when(userRepository.findByIdCustom(id)).thenReturn(Optional.empty());
        when(errorMessages.getUserNotFoundMessage()).thenReturn("fail msg");

        // Act & Assert
        assertThatThrownBy(() -> requestDeleteAccountUseCase.handle(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("fail msg");
    }

    @Test
    void handle_success() {
        // Arrange
        UserModel u  = new UserModel.Builder().setId(1L).setEmail("Hello World").build();

        when(userRepository.findByIdCustom(1L)).thenReturn(Optional.of(u));
        // Act
        requestDeleteAccountUseCase.handle(1L);

        // Assert
        verify(emailService).sendRequestDeleteAccountEmail(1L, "Hello World");
    }
}