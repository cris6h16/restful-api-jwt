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

class RequestUpdateEmailUseCaseTest {
    @Mock
    private  UserValidator userValidator;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  EmailService emailService;
    @Mock
    private  ErrorMessages errorMessages;

    @InjectMocks
    private RequestUpdateEmailUseCase requestUpdateEmailUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows(){
        // Arrange
        Long id = 1L;

        // i dont care about the exception, that's a concern of the validator
        doThrow(new OutOfMemoryError("hello cris6h16"))
                .when(userValidator).validateId(id);

        // Arrange
        assertThatThrownBy(() -> requestUpdateEmailUseCase.handle(id))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello cris6h16");
    }

    @Test
    void handle_userNotFoundThenNotFoundException(){
        // Arrange
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());
        when(errorMessages.getUserNotFoundMessage()).thenReturn("any msg");

        // Act and Assert
        assertThatThrownBy(() -> requestUpdateEmailUseCase.handle(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("any msg");
    }

    @Test
    void handle_success(){
        // Arrange
        String email = "cristianmherrera21@gmail.com";
        Long id = 112L;

        when(userRepository.findEmailById(id)).thenReturn(Optional.of(email));

        // Act
        requestUpdateEmailUseCase.handle(id);

        // Assert
        verify(emailService).sendRequestUpdateEmail(id, email);
    }
}