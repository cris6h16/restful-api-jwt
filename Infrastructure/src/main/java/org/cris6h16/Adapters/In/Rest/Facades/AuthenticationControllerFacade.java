package org.cris6h16.Adapters.In.Rest.Facades;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    private final JwtProperties jwtProperties;
    private final ControllerProperties controllerProperties;

    public AuthenticationControllerFacade(CreateAccountPort createAccountPort,
                                          VerifyEmailPort verifyEmailPort,
                                          LoginPort loginPort,
                                          RequestResetPasswordPort requestResetPasswordPort,
                                          ResetPasswordPort resetPasswordPort,
                                          RefreshAccessTokenPort refreshAccessTokenPort, JwtProperties jwtProperties, ControllerProperties controllerProperties) {
        this.createAccountPort = createAccountPort;
        this.verifyEmailPort = verifyEmailPort;
        this.loginPort = loginPort;
        this.requestResetPasswordPort = requestResetPasswordPort;
        this.resetPasswordPort = resetPasswordPort;
        this.refreshAccessTokenPort = refreshAccessTokenPort;
        this.jwtProperties = jwtProperties;
        this.controllerProperties = controllerProperties;
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
        Long id = createAccountPort.handle(cmd);

        URI location = URI.create(controllerProperties.getUser().getCore()); //... ( the get account info's path ), to avoid complexities my app has Principal.id, this let me know whos made the request ( pass ID isnt necessary )
        return ResponseEntity.created(location).build();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> verifyMyEmail() {
        verifyEmailPort.handle(Common.getPrincipalId());
        return ResponseEntity.noContent().build();
    }


    // read-only operation (no transactional)
    public ResponseEntity<Void> login(LoginDTO dto) {
        if (dto == null) throw new IllegalArgumentException("dto cannot be null");

        LoginOutput output = loginPort.handle(dto.getEmail(), dto.getPassword());
        String accessToken = output.accessToken();
        String refreshToken = output.refreshToken();

        ResponseCookie cookieAccessToken = createAccessTokenCookie(accessToken);
        ResponseCookie cookieRefreshToken = createRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookieAccessToken.toString())
                .header("Set-Cookie", cookieRefreshToken.toString())
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        String name = jwtProperties.getToken().getRefresh().getCookie().getName();
        String path = jwtProperties.getToken().getRefresh().getCookie().getPath();
        long expiration = jwtProperties.getToken().getRefresh().getExpiration().getSecs();

        return ResponseCookie.from(name, refreshToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .path(path)
                .maxAge(expiration)
                .build();

    }

    private ResponseCookie createAccessTokenCookie(String accessToken) {
        String name = jwtProperties.getToken().getAccess().getCookie().getName();
        String path = jwtProperties.getToken().getAccess().getCookie().getPath();
        long expiration = jwtProperties.getToken().getAccess().getExpiration().getSecs();

        return ResponseCookie.from(name, accessToken)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)  // todo: add in docs info about HTTPS and a reverse proxy
                .path(path)
                .maxAge(expiration)
                .build();
    }


    // read-only operation (no transactional)
    public ResponseEntity<Void> requestPasswordReset(String email) {
        _requestPasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    private void _requestPasswordReset(String email) {
        try {
            requestResetPasswordPort.handle(email);

        } catch (
                NotFoundException e) { // I shouldn't say if the email exists or not in the request reset password ( else I'd be processed by the advice )
            // do nothing
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> resetPassword(String newPassword) {
        Long id = Common.getPrincipalId();
        resetPasswordPort.handle(id, newPassword);
        return ResponseEntity.noContent().build();
    }


    // no transactional because it's a read-only operation
    public ResponseEntity<Void> refreshAccessToken() {
        String accessToken = refreshAccessTokenPort.handle(Common.getPrincipalId());

        return ResponseEntity.ok()
                .header("Set-Cookie", createAccessTokenCookie(accessToken).toString())
                .build();
    }


}
