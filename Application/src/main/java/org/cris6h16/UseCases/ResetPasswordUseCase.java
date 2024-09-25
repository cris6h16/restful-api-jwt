package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.ResetPasswordPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

public class ResetPasswordUseCase implements ResetPasswordPort {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final MyPasswordEncoder passwordEncoder;
    private final ErrorMessages errorMessages;

    public ResetPasswordUseCase(UserRepository userRepository, UserValidator userValidator, MyPasswordEncoder passwordEncoder, ErrorMessages errorMessages) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.errorMessages = errorMessages;
    }

    @Override
    public void handle(Long id, String password) {
        userValidator.validateId(id);

        userExists(id);
        userRepository.updatePasswordById(id, passwordEncoder.encode(password.trim()));
    }

    private void userExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }
}
