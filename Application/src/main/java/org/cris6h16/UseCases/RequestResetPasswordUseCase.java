package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class RequestResetPasswordUseCase implements RequestResetPasswordPort {

    private final EmailService emailService;
    private final TransactionManager transactionManager;
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public RequestResetPasswordUseCase(EmailService emailService, TransactionManager transactionManager, UserValidator userValidator, UserRepository userRepository) {
        this.emailService = emailService;
        this.transactionManager = transactionManager;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }


    @Override
    public void requestResetPassword(String email) {
        userValidator.validateEmail(email);

        AtomicReference<UserModel> reference = new AtomicReference<>();
        transactionManager.readCommitted(() -> {
            reference.set(userRepository.findByEmailCustom(email)
                    .orElseThrow(() -> new NotFoundException("User not found"))
            );
        });

        UserModel user = reference.get();
        // non-blocking
        emailService.sendAsychResetPasswordEmail(user);
    }
}
