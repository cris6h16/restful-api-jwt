package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.AlreadyExistsException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.UpdateUsernamePort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

public class UpdateUsernameUseCase implements UpdateUsernamePort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final ErrorMessages errorMessages;

    public UpdateUsernameUseCase(UserValidator userValidator, UserRepository userRepository, ErrorMessages errorMessages) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.errorMessages = errorMessages;
    }


    @Override
    public void handle(Long id, String newUsername) {
        newUsername = newUsername.trim();

        userValidator.validateId(id);
        userValidator.validateUsername(newUsername);

        userExists(id);
        usernameNotInUse(newUsername);

        userRepository.updateUsernameById(id, newUsername);
    }

    private void usernameNotInUse(String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            throw new AlreadyExistsException(errorMessages.getUsernameAlreadyExistsMessage());
        }
    }

    private void userExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }
}
