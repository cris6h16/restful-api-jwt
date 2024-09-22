package org.cris6h16.Adapters.In.Rest.Facades;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Exceptions.Impls.AlreadyExistException;
import org.cris6h16.Exceptions.Impls.EmailNotVerifiedException;
import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Exceptions.Impls.Rest.MyResponseStatusException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.In.Ports.LoginPort;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.ERoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class AuthenticationControllerFacade {

    private final CreateAccountPort createAccountPort;
    private final VerifyEmailPort verifyEmailPort;
    private final LoginPort loginPort;
    private final RequestResetPasswordPort requestResetPasswordPort;
    private final long REFRESH_TOKEN_EXP_TIME_SECS;
    private final long ACCESS_TOKEN_EXP_TIME_SECS;

    public AuthenticationControllerFacade(CreateAccountPort createAccountPort,
                                          VerifyEmailPort verifyEmailPort,
                                          LoginPort loginPort, RequestResetPasswordPort requestResetPasswordPort,
                                          @Value("${jwt.expiration.token.refresh.secs}") long refreshTokenExpTimeSecs,
                                          @Value("${jwt.expiration.token.access.secs}") long accessTokenExpTimeSecs) {
        this.createAccountPort = createAccountPort;
        this.verifyEmailPort = verifyEmailPort;
        this.loginPort = loginPort;
        this.requestResetPasswordPort = requestResetPasswordPort;
        REFRESH_TOKEN_EXP_TIME_SECS = refreshTokenExpTimeSecs;
        ACCESS_TOKEN_EXP_TIME_SECS = accessTokenExpTimeSecs;
    }

    public ResponseEntity<Void> signUp(CreateAccountDTO dto) {
        Set<ERoles> defaultRoles = new HashSet<>(Set.of(ERoles.ROLE_USER));
        CreateAccountCommand cmd = new CreateAccountCommand(dto.getUsername(), dto.getPassword(), dto.getEmail(), defaultRoles);
        AtomicReference<Long> id = new AtomicReference<>(); // thread-safe: eg update without explicit synchronization, required for lambdas
        Runnable createAccount = () -> id.set(createAccountPort.handle(cmd));
        handleExceptions(createAccount);

        URI location = URI.create("/v1/users/me");  //.../me because my app is JWT based
        return ResponseEntity.created(location).build();
    }


    public ResponseEntity<Void> verifyMyEmail() {
        Long id = getPrincipalId();
        handleExceptions(() -> verifyEmailPort.handle(id));
        return ResponseEntity.noContent().build();
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

    private void handleExceptions(Runnable runnable) {
        try {
            runnable.run();

        } catch (AlreadyExistException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.CONFLICT);
        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EmailNotVerifiedException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); // semantic error
        } catch (Exception e) { // & ImplementationException
            log.error("Unexpected error: {}", e.toString());
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> login(LoginDTO dto) {
        String accessToken;
        String refreshToken;
        AtomicReference<LoginOutput> resultLogin = new AtomicReference<>();

        handleExceptions(() -> resultLogin.set(
                loginPort.handle(
                        dto.getEmail(),
                        dto.getPassword()
                )
        ));

        accessToken = resultLogin.get().accessToken();
        refreshToken = resultLogin.get().refreshToken();

        if (accessToken == null || refreshToken == null) {
            log.error("Login failed, accessToken or refreshToken are null, accessToken: {}, refreshToken: {}", accessToken, refreshToken);
            throw new MyResponseStatusException("An unexpected error occurred, try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseCookie cookieAccessToken = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)  // todo: add in docs info about HTTPS and a reverse proxy
                .path("/")
                .maxAge(ACCESS_TOKEN_EXP_TIME_SECS)
                .build();

        ResponseCookie cookieRefreshToken = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .path("/...") //todo: add the centrilized path
                .maxAge(REFRESH_TOKEN_EXP_TIME_SECS)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookieAccessToken.toString())
                .header("Set-Cookie", cookieRefreshToken.toString())
                .build();
    }

    public ResponseEntity<Void> requestPasswordReset(String email) {
        handleExceptions(() -> requestResetPasswordPort.handle(email));
        return ResponseEntity.accepted().build();
    }
}
