package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestUpdateEmailPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

import java.util.Set;

public class RequestUpdateEmailUseCase implements RequestUpdateEmailPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ErrorMessages errorMessages;

    public RequestUpdateEmailUseCase(UserValidator userValidator, UserRepository userRepository, EmailService emailService, ErrorMessages errorMessages) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.errorMessages = errorMessages;
    }


    @Override
    public void handle(Long id) {
        userValidator.validateId(id);

        String email = findEmailByIdElseThrow(id);
        Set<ERoles> roles = findRolesById(id);

        emailService.sendRequestUpdateEmail(id, roles, email);
    }

    private Set<ERoles> findRolesById(Long id) {
        return userRepository.getRolesById(id);
    }

    private String findEmailByIdElseThrow(Long id) {
        return userRepository.findEmailById(id).orElseThrow(
                () -> new NotFoundException(errorMessages.getUserNotFoundMessage()));
    }


}
