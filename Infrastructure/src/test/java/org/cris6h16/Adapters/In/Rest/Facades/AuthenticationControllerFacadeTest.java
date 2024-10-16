package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginResponseDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.RefreshAccessTokenResponseDTO;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// todo: test Common class
public class AuthenticationControllerFacadeTest {

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
    private ControllerProperties controllerProperties;

    @InjectMocks
    private AuthenticationControllerFacade authenticationControllerFacade;

    private final Long userId = 999L;

    String locationPath = "/location-path";
    String refreshTokenCookiePath = "/refresh-path";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockControllerProperties();
        mockPrincipal(userId);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    }

    private void mockControllerProperties() {
        when(controllerProperties.getUser()).thenReturn(mock(ControllerProperties.User.class));
        when(controllerProperties.getAuthentication()).thenReturn(mock(ControllerProperties.Authentication.class));
        when(controllerProperties.getAuthentication().getCore()).thenReturn(locationPath);
    }


    @Test
    void signup_dtoNullThenIllegalArgumentException() {
        assertThatThrownBy(() -> authenticationControllerFacade.signup(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dto cannot be null");
    }

    @Test
    void signup_dtoMappedCorrectly() {
        CreateAccountDTO dto = createAccountDto();
        when(controllerProperties.getUser().getCore()).thenReturn("any");

        ResponseEntity<Void> res = authenticationControllerFacade.signup(dto);

        verify(createAccountPort).handle(argThat(cmd -> {
            assertEquals(cmd.getEmail(), dto.getEmail());
            assertEquals(cmd.getUsername(), dto.getUsername());
            assertEquals(cmd.getPassword(), dto.getPassword());
            assertThat(cmd.getRoles()).containsAll(Set.of(ERoles.ROLE_USER));
            return true;
        }));
    }

    private CreateAccountDTO createAccountDto() {
        return createAccountDtoWithNullAttribute("vdfvdfvdfv");
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "password", "email"})
    void signup_withNullAttributes_createsAccount(String attribute) {
        CreateAccountDTO dto = createAccountDtoWithNullAttribute(attribute);
        when(controllerProperties.getUser().getCore()).thenReturn(locationPath);

        ResponseEntity<Void> res = authenticationControllerFacade.signup(dto);

        verify(createAccountPort).handle(any());
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getHeaders().get("Location")).containsExactly(locationPath);
    }

    private CreateAccountDTO createAccountDtoWithNullAttribute(String attributeToBeNull) {
        return new CreateAccountDTO(
                "username".equals(attributeToBeNull) ? null : "username",
                "password".equals(attributeToBeNull) ? null : "password",
                "email".equals(attributeToBeNull) ? null : "email"
        );
    }


    @Test
    void verifyEmail_successfulThenNoContent() {
        // Arrange

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.verifyMyEmail();

        // Assert
        verify(verifyEmailPort).handle(userId);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void login_withNullDto_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> authenticationControllerFacade.login(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dto cannot be null");
    }

    @Test
    void login_success() {
        // Arrange
        String generatedAccessToken = "access@#rt3ety34ui4ufi4f4hfiu4";
        String generatedRefreshToken = "refr3sh@#rt3ety34ui4ufi4f4hfiu4";
        LoginDTO loginDTO = new LoginDTO("email1234", "pass^abc");
        LoginOutput output = new LoginOutput(generatedAccessToken, generatedRefreshToken);

        when(loginPort.handle(any(), any())).thenReturn(output);

        // Act
        ResponseEntity<LoginResponseDTO> res = authenticationControllerFacade.login(loginDTO);

        // Assert
        verify(loginPort).handle(loginDTO.getEmail(), loginDTO.getPassword());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, contentTypeHeader(res));
        assertEquals(res.getBody(), outputToDto(output));
    }

    private LoginResponseDTO outputToDto(LoginOutput output) {
        return new LoginResponseDTO(
                output.accessToken(),
                output.refreshToken()
        );
    }


    @Test
    void requestPasswordReset_emailNullThenSuccess() { //  concern of use case
        // Arrange
        String email = null;

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.requestPasswordReset(email);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.getBody()).isNull();
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
    void resetPassword_success() {
        // Arrange

        // Act
        ResponseEntity<Void> res = authenticationControllerFacade.resetPassword(null);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void refreshAccessToken_success() {
        // Arrange
        String refreshToken = "refreshTokenw123234r43t4rgtgt";

        when(refreshAccessTokenPort.handle(userId)).thenReturn(refreshToken);

        // Act
        ResponseEntity<RefreshAccessTokenResponseDTO> res = authenticationControllerFacade.refreshAccessToken();

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, contentTypeHeader(res));
        assertEquals(new RefreshAccessTokenResponseDTO(refreshToken), res.getBody());
    }

    private MediaType contentTypeHeader(ResponseEntity<?> res) {
        return res.getHeaders().getContentType();
    }

    private void mockPrincipal(long id) {
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        Authentication a = mock(Authentication.class);
        UserDetailsWithId principal = mock(UserDetailsWithId.class);
        when(principal.getId()).thenReturn(id);
        when(a.getPrincipal()).thenReturn(principal);

        when(context.getAuthentication()).thenReturn(a);
    }
}
