package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationControllerFacade facade;

    public AuthenticationController(AuthenticationControllerFacade facade) {
        this.facade = facade;
    }

    @PostMapping(
            value = "/signup",
            consumes = "application/json"
    )
    public ResponseEntity<Void> signUp(@RequestBody CreateAccountDTO dto) {
        return facade.signUp(dto);
    }


}
