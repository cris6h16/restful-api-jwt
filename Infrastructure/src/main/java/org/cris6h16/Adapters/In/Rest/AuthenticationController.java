package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginResponseDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {
    private final AuthenticationControllerFacade facade;

    public AuthenticationController(AuthenticationControllerFacade facade) {
        this.facade = facade;
    }

    @PostMapping(
            value = "${controller.authentication.signup}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> signUp(@RequestBody CreateAccountDTO dto) {
        return facade.signup(dto);
    }


    @PostMapping(
            value = "${controller.authentication.login}",
            consumes = "application/json"
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
            consumes = "application/json"
    )
    public ResponseEntity<Void> requestPasswordReset(@RequestBody String email) {
        return facade.requestPasswordReset(email);
    }

    @PatchMapping(
            value = "${controller.authentication.reset-password}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> resetPassword(@RequestBody String newPassword) {
        return facade.resetPassword(newPassword);
    }

    @PostMapping(
            value = "${controller.authentication.refresh-access-token}"
    )
    public ResponseEntity<Void> refreshAccessToken() {
        return facade.refreshAccessToken();
    }


}
