package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginResponseDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.RefreshAccessTokenResponseDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class AuthenticationController {
    private final AuthenticationControllerFacade facade;

    public AuthenticationController(AuthenticationControllerFacade facade) {
        this.facade = facade;
    }

    @PostMapping(
            value = "${controller.authentication.signup}",
            consumes = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> signUp(@RequestBody CreateAccountDTO dto) {
        return facade.signup(dto);
    }


    @PostMapping(
            value = "${controller.authentication.login}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) { //todo: if is logged in, say something
        return facade.login(dto);
    }

    @PutMapping(
            value = "${controller.authentication.verify-email}"
    )
    public ResponseEntity<Void> verifyMyEmail() { //todo: test this
        return facade.verifyMyEmail();
    }

    @PostMapping(
            value = "${controller.authentication.request-reset-password}",
            consumes = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> requestPasswordReset(@RequestBody String email) {
        return facade.requestPasswordReset(email);
    }

    @PatchMapping(
            value = "${controller.authentication.reset-password}",
            consumes = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> resetPassword(@RequestBody String newPassword) {
        return facade.resetPassword(newPassword);
    }

    @PostMapping(
            value = "${controller.authentication.refresh-access-token}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RefreshAccessTokenResponseDTO> refreshAccessToken() {
        return facade.refreshAccessToken();
    }


}
