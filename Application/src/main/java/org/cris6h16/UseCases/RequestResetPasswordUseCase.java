package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

import java.util.Set;

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

        Long id = findIdByEmailElseThrow(email);
        Set<ERoles> roles = getRolesByEmail(email);

        emailService.sendResetPasswordEmail(id, roles, email);
    }

    private Long findIdByEmailElseThrow(String email) {
        return userRepository.findIdByEmail(email)
                .orElseThrow(() -> new NotFoundException(errorMessages.getUserNotFoundMessage()));
    }

    private Set<ERoles> getRolesByEmail(String email) {
        return userRepository.getRolesByEmail(email);
    }

}
