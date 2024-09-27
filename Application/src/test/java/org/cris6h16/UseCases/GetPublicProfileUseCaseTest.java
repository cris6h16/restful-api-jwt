package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class GetPublicProfileUseCaseTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ErrorMessages errorMessages;

    @InjectMocks
    private GetPublicProfileUseCase getPublicProfileUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_idNull() {
        // Arrange
        Long id = null;

        // i dont care about the exception, that's a concern of validator
        doThrow(new OutOfMemoryError("hello cris6h16"))
                .when(userValidator).validateId(id);

        // Act & Assert
        assertThatThrownBy(() -> getPublicProfileUseCase.handle(null))
                .isInstanceOf(OutOfMemoryError.class)
                .hasMessage("hello cris6h16");
    }

    @Test
    void handle_userNorFoundThenNotFoundException() {
        // Arrange
        Long id = 10L;

        when(errorMessages.getUserNotFoundMessage())
                .thenReturn("not found 123");
        when(userRepository.existsById(id))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> getPublicProfileUseCase.handle(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("not found 123");
    }

    @Test
    void handle_success() {
        // Arrange
        Long id = 10L;
        UserModel user = new UserModel(
                1L,
                "cris6h16",
                "encodedPassword",
                "cristianmherrera21@gmail.com",
                Set.of(ERoles.ROLE_USER),
                Boolean.TRUE,
                Boolean.TRUE,
                Instant.ofEpochMilli(System.currentTimeMillis())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()  // Removes timezone info
        );

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // Act
        GetPublicProfileOutput output = getPublicProfileUseCase.handle(id);

        // Assert
        assertThat(output)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("username", user.getUsername())
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("roles", user.getRoles())
                .hasFieldOrPropertyWithValue("active", user.getActive())
                .hasFieldOrPropertyWithValue("emailVerified", user.getEmailVerified())
                .hasFieldOrPropertyWithValue("lastModified", user.getLastModified());
    }
}