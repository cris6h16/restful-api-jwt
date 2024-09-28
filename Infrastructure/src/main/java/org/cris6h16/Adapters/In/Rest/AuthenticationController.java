package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "${controller.path.core}" +
                "${controller.path.authentication.core}"
)
public class AuthenticationController {
    private final AuthenticationControllerFacade facade;

    public AuthenticationController(AuthenticationControllerFacade facade) {
        this.facade = facade;
    }

    @PostMapping(
            value = "${controller.path.authentication.signup}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> signUp(@RequestBody CreateAccountDTO dto) {
        return facade.signup(dto);
    }


    @PostMapping(
            value = "${controller.path.authentication.login}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> login(@RequestBody LoginDTO dto) { //todo: if is logged in, say something
        return facade.login(dto);
    }

    @PutMapping(
            value = "${controller.path.authentication.verify-email}"
    )
    public ResponseEntity<Void> verifyMyEmail() { //todo: test this
        return facade.verifyMyEmail();
    }

    @PostMapping(
            value = "${controller.path.authentication.request-reset-password}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> requestPasswordReset(@RequestBody String email) {
        return facade.requestPasswordReset(email);
    }

    @PatchMapping(
            value = "${controller.path.authentication.reset-password}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> resetPassword(@RequestBody String newPassword) {
        return facade.resetPassword(newPassword);
    }

    @PostMapping(
            value = "${controller.path.authentication.refresh-access-token}",
            produces = "application/json"
    )
    public ResponseEntity<Void> refreshAccessToken() {
        return facade.refreshAccessToken();
    }


}
