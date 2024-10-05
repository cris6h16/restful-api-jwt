package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RefreshAccessTokenUseCaseTest {
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ErrorMessages errorMessages;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_idInvalid(){ // tested null, negatives, ect is a concern of validator
        doThrow(new IllegalArgumentException("hello ur id is invalidd")).when(userValidator).validateId(any());

        assertThatThrownBy(()-> refreshAccessTokenUseCase.handle(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("hello ur id is invalidd");
    }

    @Test
    void handle_userNotExists(){
        when(userRepository.existsById(anyLong())).thenReturn(false);
        when(errorMessages.getUserNotFoundMessage()).thenReturn("user not found plz try aga]in");

        assertThatThrownBy(()-> refreshAccessTokenUseCase.handle(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("user not found plz try aga]in");

        verify(userRepository).existsById(1L);
    }

    @Test
    void handle_successAndTokenGenerated(){
        String token = "im a token";
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.getRolesById(id)).thenReturn(Set.of());
        when(jwtUtils.genAccessToken(id, Set.of())).thenReturn(token);

        String actualToken = refreshAccessTokenUseCase.handle(1L);

        assertEquals(token, actualToken);
        verify(userRepository).getRolesById(id);
        verify(jwtUtils).genAccessToken(id, Set.of());
    }
}