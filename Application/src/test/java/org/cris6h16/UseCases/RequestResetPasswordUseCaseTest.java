package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Models.ERoles;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class RequestResetPasswordUseCaseTest {
    @Mock
    private EmailService emailService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ErrorMessages errorMessages;
    @InjectMocks
    private RequestResetPasswordUseCase requestResetPasswordUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_invalidEmailThenValidatorThrows() {
        // Arrange
        String email = "invalidEmail";

        // i dont care about the exception, it's a concern of the validator
        doThrow(new OutOfMemoryError("hello cris6h16"))
                .when(userValidator).validateEmail(email);

        // Act & Assert
        assertThatThrownBy(() -> requestResetPasswordUseCase.handle(email))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello cris6h16");
    }

    @Test
    void handle_userNotFoundThenNotFoundException() {
        // Arrange
        String email = "email";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        when(errorMessages.getUserNotFoundMessage())
                .thenReturn("Optional.empty()");

        // Act & Assert
        assertThatThrownBy(() -> requestResetPasswordUseCase.handle(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Optional.empty()");
    }

    @Test
    void handle_success() {
        // Arrange
        String email = "email";
        var aux = new UserModel();
        Long id = 1L;
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);

        when(userRepository.findIdByEmail(email)).thenReturn(Optional.of(id));
        when(userRepository.getRolesByEmail(email)).thenReturn(roles);

        // Act
        requestResetPasswordUseCase.handle(email);

        // Assert
        verify(emailService).sendResetPasswordEmail(id, roles, email);
    }
}