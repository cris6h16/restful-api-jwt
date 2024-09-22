package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.UserValidator;

public class VerifyEmailUseCase implements VerifyEmailPort {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    //todo: add loggers

    public VerifyEmailUseCase(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Override
    public void handle(Long id) {
        userValidator.validateId(id);

        if (!userRepository.existsByIdCustom(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.updateEmailVerifiedByIdCustom(id, true);
    }


}
