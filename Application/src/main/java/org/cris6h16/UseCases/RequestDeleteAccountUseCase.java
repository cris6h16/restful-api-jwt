package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestDeleteAccountPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class RequestDeleteAccountUseCase implements RequestDeleteAccountPort {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ErrorMessages errorMessages;


    public RequestDeleteAccountUseCase(UserValidator userValidator, UserRepository userRepository, EmailService emailService, ErrorMessages errorMessages) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.errorMessages = errorMessages;
    }

    @Override
    public void handle(Long id) {
        userValidator.validateId(id);
        UserModel user = findByIdElseThrow(id);

        emailService.sendRequestDeleteAccountEmail(user.getId(), user.getEmail());
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException(errorMessages.getUserNotFoundMessage()));

    }
}
