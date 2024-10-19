package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.UpdateEmailPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

import java.util.Set;

public class UpdateEmailUseCase implements UpdateEmailPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ErrorMessages errorMessages;

    public UpdateEmailUseCase(UserValidator userValidator, UserRepository userRepository, EmailService emailService, ErrorMessages errorMessages) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.errorMessages = errorMessages;
    }


    @Override
    public void handle(Long id, String newEmail) {
        newEmail = newEmail.trim();
        userValidator.validateId(id);
        userValidator.validateEmail(newEmail);

        userExists(id);
        userRepository.updateEmailById(id, newEmail);
        userRepository.updateEmailVerifiedById(id, false);
        Set<ERoles> roles = userRepository.getRolesById(id);

        emailService.sendVerificationEmail(id, roles, newEmail);
    }

    private void userExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }
}
