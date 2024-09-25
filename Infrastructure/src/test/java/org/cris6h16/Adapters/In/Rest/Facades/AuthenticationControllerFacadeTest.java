package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Exceptions.Impls.*;
import org.cris6h16.Exceptions.Impls.Rest.MyResponseStatusException;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerFacadeTest {
    @Mock
    private CreateAccountPort createAccountPort;
    @Mock
    private VerifyEmailPort verifyEmailPort;
    @Mock
    private LoginPort loginPort;
    @Mock
    private RequestResetPasswordPort requestResetPasswordPort;
    @Mock
    private ResetPasswordPort resetPasswordPort;
    @Mock
    private RefreshAccessTokenPort refreshAccessTokenPort;
    @Mock
    private JwtUtilsImpl jwtUtilsImpl;
    @Mock
    private ErrorMessages errorMessages;

    private AuthenticationControllerFacade authenticationControllerFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authenticationControllerFacade = new AuthenticationControllerFacade(
                createAccountPort,
                verifyEmailPort,
                loginPort,
                requestResetPasswordPort,
                resetPasswordPort,
                refreshAccessTokenPort,
                errorMessages,
                jwtUtilsImpl,
                "refreshTokenCookieName",
                "refreshTokenCookiePath",
                "accessTokenCookieName",
                "accessTokenCookiePath"
        );
    }

    @Test
    void signup_dtoNullThenIllegalArgumentException() {
        assertThatThrownBy(() -> authenticationControllerFacade.signup(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dto cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"password", "username", "email"})
    void signup_anyAttributeNullThenSuccess(String now) { // concern of use case
        // Arrange
        String username = now.equals("username") ? null : "username";
        String password = now.equals("password") ? null : "password";
        String email = now.equals("email") ? null : "email";
        CreateAccountDTO dto = new CreateAccountDTO(username, password, email);

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.signup(dto);

        // Assert
        verify(createAccountPort).handle(any());
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void verifyMyEmail_principalIsNotAnInstanceOfUserDetailsWithIdThenIllegalStateException() {
        // Arrange
        Authentication a = mock(Authentication.class);
        when(a.getPrincipal()).thenReturn(mock(User.class));
        SecurityContextHolder.getContext().setAuthentication(a);

        // Act & Assert
        assertThatThrownBy(() -> authenticationControllerFacade.verifyMyEmail())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Principal is not an instance of UserDetailsWithId");
    }



    @Test
    void verifyEmail_successfulThenNoContent() {
        // Arrange
        Authentication a = mock(Authentication.class);
        UserDetailsWithId user = mock(UserDetailsWithId.class);
        when(user.getId()).thenReturn(999L);
        when(a.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(a);

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.verifyMyEmail();

        // Assert
        verify(verifyEmailPort).handle(999L);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void login_dtoNullThenIllegalArgumentException() {
        // Arrange
        LoginDTO dto = null;

        // Act & Assert
        assertThatThrownBy(() -> authenticationControllerFacade.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dto cannot be null");
    }

    @Test
    void login_success() {
        // Arrange
        when(loginPort.handle(any(), any())).thenReturn(
                new LoginOutput("MockedAccessToken", "MockedRefreshToken"));

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.login(new LoginDTO("email", "pass"));

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> setCookies = res.getHeaders().get("Set-Cookie");
        assertThat(setCookies.get(0)).isEqualTo(expectedAccessTokenCookie());
        assertThat(setCookies.get(1)).isEqualTo(expectedRefreshTokenCookie());
        verify(loginPort).handle("email", "pass");
        verify(jwtUtilsImpl).getAccessTokenExpTimeSecs();
        verify(jwtUtilsImpl).getRefreshTokenExpTimeSecs();

    }

    private String expectedRefreshTokenCookie() {
        StringBuilder sb = new StringBuilder();
        sb.append(authenticationControllerFacade.refreshTokenCookieName)
                .append("=")
                .append("MockedRefreshToken")
                .append("; Path=")
                .append(authenticationControllerFacade.refreshTokenCookiePath)
                .append("; Max-Age=")
                .append("0; Expires=")
                .append("Thu, 01 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=Strict");
        return sb.toString();
    }

    private String expectedAccessTokenCookie() {
        StringBuilder sb = new StringBuilder();
        sb.append(authenticationControllerFacade.accessTokenCookieName)
                .append("=")
                .append("MockedAccessToken")
                .append("; Path=")
                .append(authenticationControllerFacade.accessTokenCookiePath)
                .append("; Max-Age=")
                .append("0; Expires=")
                .append("Thu, 01 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=Strict");
        return sb.toString();
    }


    @Test
    void requestPasswordReset_emailNullThenIllegalArgumentException() {
        assertThatThrownBy(() -> authenticationControllerFacade.requestPasswordReset(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email cannot be null");
    }

    @Test
    void requestPasswordReset_thrownNotFoundExceptionThenAccepted() { // when someone requests reset its password, it shouldn't know if that email exists in the system
        // Arrange
        String email = "email";
        doThrow(new NotFoundException("user not found with that email etc etc"))
                .when(requestResetPasswordPort).handle(email);

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.requestPasswordReset(email);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.getBody()).isNull();
    }


    @Test
    void requestPasswordReset_successful() {
        // Arrange
        String email = "email";

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.requestPasswordReset(email);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(requestResetPasswordPort).handle(email);
    }


    @Test
    void resetPassword_success(){
        // Arrange
        Authentication a = mock(Authentication.class);
        UserDetailsWithId user = mock(UserDetailsWithId.class);
        when(user.getId()).thenReturn(999L);
        when(a.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(a);

        // Act
        ResponseEntity<Void> res =  authenticationControllerFacade.resetPassword(null);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void refreshAccessToken_success(){
        // Arrange
        Authentication a = mock(Authentication.class);
        UserDetailsWithId user = mock(UserDetailsWithId.class);
        when(user.getId()).thenReturn(999L);
        when(a.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(a);

        when(refreshAccessTokenPort.handle(999L))
                .thenReturn("MockedAccessToken");

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.refreshAccessToken();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getHeaders().get("Set-Cookie").get(0))
                .isEqualTo(expectedAccessTokenCookie());
    }
}

// todo: test each if are or not inside a transaction