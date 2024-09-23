package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

public class VerifyEmailUseCase implements VerifyEmailPort {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final ErrorMessages errorMessages;
    //todo: add loggers

    public VerifyEmailUseCase(UserRepository userRepository, UserValidator userValidator, ErrorMessages errorMessages) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.errorMessages = errorMessages;
    }

    @Override
    public void handle(Long id) {
        userValidator.validateId(id);

        userExists(id);
        userRepository.updateEmailVerifiedByIdCustom(id, true);
    }

    private void userExists(Long id) {
        if (!userRepository.existsByIdCustom(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }


}
