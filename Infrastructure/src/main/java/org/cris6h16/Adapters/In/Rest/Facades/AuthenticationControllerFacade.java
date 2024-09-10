package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Exceptions.Impls.AlreadyExistException;
import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Exceptions.Impls.Rest.MyResponseStatusException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@Component
public class AuthenticationControllerFacade {

    private final CreateAccountPort createAccountPort;
    private static final Logger log = Logger.getLogger(AuthenticationControllerFacade.class.getName());

    public AuthenticationControllerFacade(CreateAccountPort createAccountPort) {
        this.createAccountPort = createAccountPort;
    }

    public ResponseEntity<Void> signUp(CreateAccountCommand cmd) {
        AtomicReference<Long> id = new AtomicReference<>(); // thread-safe: eg update without explicit synchronization, required for lambdas
        Runnable createAccount = () -> id.set(createAccountPort.createAccount(cmd));
        handleExceptions(createAccount);

        URI location = URI.create("/v1/users/me");  //.../me because our app is JWT based
        return ResponseEntity.created(location).build();
    }


    private void handleExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (AlreadyExistException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.CONFLICT);
        } catch (InvalidAttributeException e) {
            throw new MyResponseStatusException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // & ImplementationException
            log.severe(e.getMessage());
            throw new MyResponseStatusException("An unexpected error happened, try later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
