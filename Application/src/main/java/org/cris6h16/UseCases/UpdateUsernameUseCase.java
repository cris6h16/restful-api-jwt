package org.cris6h16.UseCases;

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
    public void handle(Long id, String username) {
        username = username.trim();

        userValidator.validateId(id);
        userValidator.validateUsername(username);

        userExists(id);
        userRepository.updateUsernameByIdCustom(id, username);
    }

    private void userExists(Long id) {
        if (!userRepository.existsByIdCustom(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }
}
