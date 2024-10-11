package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.DeleteAccountPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.UserValidator;

public class DeleteAccountUseCase implements DeleteAccountPort {

    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public DeleteAccountUseCase(UserValidator userValidator, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Long id) {
        userValidator.validateId(id);
        userExists(id);
        userRepository.deactivate(id);
    }

    private void userExists(Long id) {
        if (!userRepository.existsById(id)){
            throw new NotFoundException("User was not found");
        }
    }
}
