package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestDeleteAccountPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

import java.util.Set;

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
        String email = getEmailByIdOrThrow(id);
        Set<ERoles> roles = getRolesById(id);

        emailService.sendRequestDeleteAccountEmail(id, roles, email);
    }

    private Set<ERoles> getRolesById(Long id) {
        return userRepository.getRolesById(id);
    }

    private String getEmailByIdOrThrow(Long id) {
        return userRepository
                .findEmailById(id)
                .orElseThrow(() -> new NotFoundException(errorMessages.getUserNotFoundMessage()));
    }

}
