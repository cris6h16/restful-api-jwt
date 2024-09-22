package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.UpdateEmailPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class UpdateEmailUseCase implements UpdateEmailPort {
    private final UserValidator userValidator;
    private final TransactionManager transactionManager;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UpdateEmailUseCase(UserValidator userValidator, TransactionManager transactionManager, UserRepository userRepository, EmailService emailService) {
        this.userValidator = userValidator;
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public void handle(Long id, String email) {
        userValidator.validateId(id);
        userValidator.validateEmail(email);

        AtomicReference<UserModel> ref = new AtomicReference<>();
        transactionManager.readCommitted(() -> {
            UserModel user = findByIdElseThrow(id);
            user.setEmail(email);
            userRepository.saveCustom(user);

            ref.set(user);
        });

        // non-blocking
        emailService.sendAsychVerificationEmail(ref.get());
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
