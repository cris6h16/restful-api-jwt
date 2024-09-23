package org.cris6h16.Adapters.In.Rest.Facades;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Exceptions.Impls.*;
import org.cris6h16.Exceptions.Impls.Rest.MyResponseStatusException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.JwtUtils;
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
    private final JwtUtils jwtUtils;

    @Value("${jwt.token.refresh.cookie.name}")
    private  String  refreshTokenCookieName;
    @Value("${jwt.token.refresh.cookie.path}")
    private  String  refreshTokenCookiePath;

    @Value("${jwt.token.access.cookie.name}")
    private  String  accessTokenCookieName;
    @Value("${jwt.token.access.cookie.path}")
    private  String  accessTokenCookiePath;

    public AuthenticationControllerFacade(CreateAccountPort createAccountPort,
                                          VerifyEmailPort verifyEmailPort,
                                          LoginPort loginPort,
                                          RequestResetPasswordPort requestResetPasswordPort,
                                          ResetPasswordPort resetPasswordPort,
                                          RefreshAccessTokenPort refreshAccessTokenPort, JwtUtils jwtUtils) {
        this.createAccountPort = createAccountPort;
        this.verifyEmailPort = verifyEmailPort;
        this.loginPort = loginPort;
        this.requestResetPasswordPort = requestResetPasswordPort;
        this.resetPasswordPort = resetPasswordPort;
        this.refreshAccessTokenPort = refreshAccessTokenPort;
        this.jwtUtils = jwtUtils;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> signUp(CreateAccountDTO dto) {
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
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
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
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // read-only operation (no transactional)
    public ResponseEntity<Void> login(LoginDTO dto) {
        LoginOutput loginOutput = _login(dto.getEmail(), dto.getPassword());
        String accessToken = loginOutput.accessToken();
        String refreshToken = loginOutput.refreshToken();

        if (accessToken == null || refreshToken == null) {
            log.error("Login failed, accessToken or refreshToken are null, accessToken: {}, refreshToken: {}", accessToken, refreshToken);
            throw new MyResponseStatusException("An unexpected error occurred, try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseCookie cookieAccessToken = createAccessTokenCookie(accessToken);

        ResponseCookie cookieRefreshToken = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .path(refreshTokenCookiePath)
                .maxAge(jwtUtils.getRefreshTokenExpTimeSecs())
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
                .maxAge(jwtUtils.getAccessTokenExpTimeSecs())
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
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // read-only operation (no transactional)
    public ResponseEntity<Void> requestPasswordReset(String email) {
        _requestPasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    private void _requestPasswordReset(String email) {
        try {
            requestResetPasswordPort.handle(email);

        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } /*catch (NotFoundException e) { // I shouldn't say if the email exists or not in the request reset password
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.NOT_FOUND);
        }*/ catch (Exception e) {
            log.error("Unhandled exception in: {}", e.toString());
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
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
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
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

        } catch (Exception e) {
            if (e instanceof ClassCastException) {
                log.error("Principal is not an instance of UserDetailsWithId: {}", e.toString());
            } else {
                log.debug("Exception trying to get user id: {}", e.toString());
            }
            throw new MyResponseStatusException("Failed to get user id, possibly you're not authenticated", HttpStatus.UNAUTHORIZED);
        }
    }
}
