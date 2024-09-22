package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestUpdateEmailPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.UserValidator;

public class RequestUpdateEmailUseCase implements RequestUpdateEmailPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RequestUpdateEmailUseCase(UserValidator userValidator, UserRepository userRepository, EmailService emailService) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public void handle(Long id) {
        userValidator.validateId(id);

        UserModel user = findByIdElseThrow(id);

        // non-blocking
        emailService.sendAsychRequestUpdateEmail(user.getUsername(), user.getEmail());
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
