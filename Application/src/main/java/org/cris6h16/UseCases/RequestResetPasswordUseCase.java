package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

public class RequestResetPasswordUseCase implements RequestResetPasswordPort {

    private final EmailService emailService;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final ErrorMessages errorMessages;

    public RequestResetPasswordUseCase(EmailService emailService, UserValidator userValidator, UserRepository userRepository, ErrorMessages errorMessages) {
        this.emailService = emailService;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.errorMessages = errorMessages;
    }


    @Override
    public void handle(String email) {
        userValidator.validateEmail(email);

        UserModel user = findByEmailElseThrow(email);
        emailService.sendResetPasswordEmail(user.getId(), user.getEmail());
    }

    private UserModel findByEmailElseThrow(String email) {
        return userRepository
                .findByEmailCustom(email)
                .orElseThrow(() -> new NotFoundException(errorMessages.getUserNotFoundMessage()));
    }
}
