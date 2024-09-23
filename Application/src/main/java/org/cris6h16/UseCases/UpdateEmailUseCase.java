package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.UpdateEmailPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.UserValidator;

public class UpdateEmailUseCase implements UpdateEmailPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UpdateEmailUseCase(UserValidator userValidator, UserRepository userRepository, EmailService emailService) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public void handle(Long id, String email) {
        userValidator.validateId(id);
        userValidator.validateEmail(email);

        UserModel u = findByIdElseThrow(id);
        u.setEmail(email);
        userRepository.saveCustom(u);

        // non-blocking
        emailService.sendVerificationEmail(u.getId(), u.getEmail());
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
