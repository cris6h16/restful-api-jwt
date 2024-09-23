package org.cris6h16.Adapters.In.Rest.Facades;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Exceptions.Impls.*;
import org.cris6h16.Exceptions.Impls.Rest.MyResponseStatusException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class AuthenticationControllerFacade {

    private final CreateAccountPort createAccountPort;
    private final VerifyEmailPort verifyEmailPort;
    private final LoginPort loginPort;
    private final RequestResetPasswordPort requestResetPasswordPort;
    private final ResetPasswordPort resetPasswordPort;
    private final RefreshAccessTokenPort refreshAccessTokenPort;
    private final ErrorMessages errorMessages;

    private final JwtUtilsImpl jwtUtilsImpl;

    protected final String refreshTokenCookieName;
    protected final String refreshTokenCookiePath;
    protected final String accessTokenCookieName;
    protected final String accessTokenCookiePath;

    public AuthenticationControllerFacade(CreateAccountPort createAccountPort,
                                          VerifyEmailPort verifyEmailPort,
                                          LoginPort loginPort,
                                          RequestResetPasswordPort requestResetPasswordPort,
                                          ResetPasswordPort resetPasswordPort,
                                          RefreshAccessTokenPort refreshAccessTokenPort,
                                          ErrorMessages errorMessages,
                                          JwtUtilsImpl jwtUtilsImpl,
                                          String refreshTokenCookieName,
                                          String refreshTokenCookiePath,
                                          String accessTokenCookieName,
                                          String accessTokenCookiePath) {
        this.createAccountPort = createAccountPort;
        this.verifyEmailPort = verifyEmailPort;
        this.loginPort = loginPort;
        this.requestResetPasswordPort = requestResetPasswordPort;
        this.resetPasswordPort = resetPasswordPort;
        this.refreshAccessTokenPort = refreshAccessTokenPort;
        this.errorMessages = errorMessages;
        this.jwtUtilsImpl = jwtUtilsImpl;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.refreshTokenCookiePath = refreshTokenCookiePath;
        this.accessTokenCookieName = accessTokenCookieName;
        this.accessTokenCookiePath = accessTokenCookiePath;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> signup(CreateAccountDTO dto) {
        if (dto == null) throw new IllegalArgumentException("dto cannot be null");

        Set<ERoles> defaultRoles = new HashSet<>(Set.of(ERoles.ROLE_USER));
        CreateAccountCommand cmd = new CreateAccountCommand(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                defaultRoles
        );
        Long id = _signup(cmd);

        URI location = URI.create("/v1/users/me");  //.../me because my app is JWT based ( principal with id )
        return ResponseEntity.created(location).build();
    }

    private Long _signup(CreateAccountCommand cmd) {
        try {
            return createAccountPort.handle(cmd);

        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AlreadyExistException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Unhandled exception in: {}", e.toString());
            throw new MyResponseStatusException(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> verifyMyEmail() {
        Long id = getPrincipalId();
        _verifyEmail(id);
        return ResponseEntity.noContent().build();
    }

    private void _verifyEmail(Long id) {
        try {
            verifyEmailPort.handle(id);

        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Unhandled exception in: {}", e.toString());
            throw new MyResponseStatusException(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // read-only operation (no transactional)
    public ResponseEntity<Void> login(LoginDTO dto) {
        if (dto == null) throw new IllegalArgumentException("dto cannot be null");

        LoginOutput loginOutput = _login(dto.getEmail(), dto.getPassword());
        String accessToken = loginOutput.accessToken();
        String refreshToken = loginOutput.refreshToken();

        ResponseCookie cookieAccessToken = createAccessTokenCookie(accessToken);

        ResponseCookie cookieRefreshToken = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .path(refreshTokenCookiePath)
                .maxAge(jwtUtilsImpl.getRefreshTokenExpTimeSecs())
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookieAccessToken.toString())
                .header("Set-Cookie", cookieRefreshToken.toString())
                .build();
    }

    private ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from(accessTokenCookieName, accessToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)  // todo: add in docs info about HTTPS and a reverse proxy
                .path(accessTokenCookiePath)
                .maxAge(jwtUtilsImpl.getAccessTokenExpTimeSecs())
                .build();
    }

    private LoginOutput _login(String email, String password) {
        try {
            return loginPort.handle(email, password);

        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidCredentialsException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EmailNotVerifiedException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            log.error("Unhandled exception in: {}", e.toString());
            throw new MyResponseStatusException(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // read-only operation (no transactional)
    public ResponseEntity<Void> requestPasswordReset(String email) {
        if (email == null) throw new IllegalArgumentException("email cannot be null");

        _requestPasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    private void _requestPasswordReset(String email) {
        try {
            requestResetPasswordPort.handle(email);

        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) { // I shouldn't say if the email exists or not in the request reset password
        } catch (Exception e) {
            log.error("Unhandled exception in: {}", e.toString());
            throw new MyResponseStatusException(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> resetPassword(String newPassword) {
        Long id = getPrincipalId();
        _resetPassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    private void _resetPassword(Long id, String newPassword) {
        try {
            resetPasswordPort.handle(id, newPassword);

        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Unhandled exception in: {}", e.toString());
            throw new MyResponseStatusException(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // no transactional because it's a read-only operation
    public ResponseEntity<Void> refreshAccessToken() {
        String accessToken = refreshAccessTokenPort.handle(getPrincipalId());

        return ResponseEntity.ok()
                .header("Set-Cookie", createAccessTokenCookie(accessToken).toString())
                .build();
    }

    private Long getPrincipalId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ((UserDetailsWithId) principal).getId();

        } catch (ClassCastException e) {
            throw new IllegalStateException("Principal is not an instance of UserDetailsWithId");
        }
    }
}
