package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;

import java.util.concurrent.atomic.AtomicReference;

public class RequestResetPasswordUseCase implements RequestResetPasswordPort {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TransactionManager transactionManager;

    public RequestResetPasswordUseCase(UserRepository userRepository, EmailService emailService, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.transactionManager = transactionManager;
    }


    @Override
    public void requestResetPassword(String email) {
        validateEmail(email);

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


    private void validateEmail(String email) {// todo: move to a validator
        if (email == null || email.trim().isEmpty() || !email.matches("^\\S+@\\S+\\.\\S+$")) { //--> ^ = start of the string, \S = any non-whitespace character, + = one or more, @ = @, \S = any non-whitespace character, + = one or more, \. = ., \S = any non-whitespace character, + = one or more, $ = end of the string
            throw new InvalidAttributeException("Email is invalid");
        }
    }
}
