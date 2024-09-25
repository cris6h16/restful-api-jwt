package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserControllerFacadeTest {

    @Mock
    private RequestDeleteAccountPort requestDeleteAccountPort;
    @Mock
    private DeleteAccountPort deleteAccountPort;
    @Mock
    private UpdateUsernamePort updateUsernamePort;
    @Mock
    private UpdatePasswordPort updatePasswordPort;
    @Mock
    private UpdateEmailPort updateEmailPort;
    @Mock
    private RequestUpdateEmailPort requestUpdateEmailPort;
    @Mock
    private GetPublicProfilePort getPublicProfilePort;
    @InjectMocks
    private UserControllerFacade userControllerFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void requestDeleteMyAccount_success() {
        // Arrange
        var principalMocked = mockPrincipalId(17L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.requestDeleteMyAccount();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(requestDeleteAccountPort).handle(17L);
    }


    @Test
    void deleteMyAccount_success() {
        // Arrange
        var principalMocked = mockPrincipalId(756L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.deleteMyAccount();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(deleteAccountPort).handle(756L);
    }

    @Test
    void updateMyUsername_success() {
        // Arrange
        String newUsername = "cris6h16";
        var principalMocked = mockPrincipalId(756L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.updateMyUsername(newUsername);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(updateUsernamePort).handle(756L, newUsername);
    }

    @Test
    void updateMyPassword_success() {
        // Arrange
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("oldPassword", "newPassword");
        var principalMocked = mockPrincipalId(666L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.updateMyPassword(dto);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(updatePasswordPort).handle(666L, dto.currentPassword(), dto.newPassword());
    }
    @Test
    void requestUpdateMyEmail_success() {
        // Arrange
        String newEmail = "cristianmherrera21@gmail.com";
        var principalMocked = mockPrincipalId(123L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.requestUpdateMyEmail();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(requestUpdateEmailPort).handle(123L);
    }

    @Test
    void updateMyEmail_success() {
        // Arrange
        String newEmail = "cristianmherrera21@gmail.com";
        var principalMocked = mockPrincipalId(600L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.updateMyEmail(newEmail);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(updateEmailPort).handle(600L, newEmail);
    }

    @Test
    void getMyAccount_success() {
        // Arrange
        UserModel user = new UserModel.Builder()
                .setId(97123L)
                .setUsername("cris6h16")
                .setEmail("cristianmherrera21@gmail.com")
                .setPassword("12345678")
                .setRoles(Set.of(ERoles.ROLE_USER))
                .setActive(true)
                .setLastModified(System.currentTimeMillis())
                .setEmailVerified(true)
                .build();
        GetPublicProfileOutput output = new GetPublicProfileOutput(user);
        PublicProfileDTO dto = new PublicProfileDTO(output);
        var principalMocked = mockPrincipalId(97123L);

        when(getPublicProfilePort.handle(97123L)).thenReturn(output);

        // Act
        ResponseEntity<PublicProfileDTO> res = userControllerFacade.getMyAccount();

        // Assert
        verify(principalMocked).getId();
        verify(getPublicProfilePort).handle(97123L);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(dto);
    }

    private UserDetailsWithId mockPrincipalId(Long id) {
        Authentication a = mock(Authentication.class);
        UserDetailsWithId user = mock(UserDetailsWithId.class);
        when(user.getId()).thenReturn(id);
        when(a.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(a);
        return user;
    }

    @Test
    void getAllUsers_pageableNullThenIllegalArgumentException(){
        // Arrange
        Pageable pageable = null;

        // Act & Assert
        assertThatThrownBy(() -> userControllerFacade.getAllUsers(pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pageable cannot be null");
    }
}