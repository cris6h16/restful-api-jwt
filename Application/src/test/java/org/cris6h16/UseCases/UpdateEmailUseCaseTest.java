package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.AlreadyExistsException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UpdateEmailUseCaseTest {
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private ErrorMessages errorMessages;
    @InjectMocks
    private UpdateEmailUseCase updateEmailUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidIdThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String email = "email";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("Any msg"))
                .when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> updateEmailUseCase.handle(id, email))
               .isInstanceOf(OutOfMemoryError.class)
               .hasMessage("Any msg");
    }
    @Test
    void handle_invalidEmailThenValidatorThrows(){
        // Arrange
        Long id = 1L;
        String email = "email";

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("Any msg"))
                .when(userValidator).validateEmail(email);

        // Act & Assert
        assertThatThrownBy(() -> updateEmailUseCase.handle(id, email))
               .isInstanceOf(OutOfMemoryError.class)
               .hasMessage("Any msg");
    }

    @Test
    void handle_userDoesNotExistThenThrowNotFoundException(){
        // Arrange
        Long id = 1L;
        String email = "email";

        when(userRepository.existsById(id)).thenReturn(false);
        when(errorMessages.getUserNotFoundMessage()).thenReturn("Any msg");

        // Act & Assert
        assertThatThrownBy(() -> updateEmailUseCase.handle(id, email))
               .isInstanceOf(NotFoundException.class)
               .hasMessage("Any msg");
    }

    @Test
    void handle_emailAlreadyInUseThenThrowAlreadyExistsException(){
        // Arrange
        Long id = 1L;
        String email = "email";

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(errorMessages.getEmailAlreadyExistsMessage()).thenReturn("Any msg qwerty");

        // Act & Assert
        assertThatThrownBy(() -> updateEmailUseCase.handle(id, email))
               .isInstanceOf(AlreadyExistsException.class)
               .hasMessage("Any msg qwerty");
    }

    @Test
    void handle_successUntrimmed(){
        // Arrange
        Long id = 1L;
        String email = "      email   ";
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.getRolesById(id)).thenReturn(roles);

        // Act
        updateEmailUseCase.handle(id, email);

        // Assert
        verify(userRepository).updateEmailById(id, email.trim());
        verify(userRepository).updateEmailVerifiedById(id, false);
        verify(emailService).sendVerificationEmail(id, roles,  email.trim());
    }
}