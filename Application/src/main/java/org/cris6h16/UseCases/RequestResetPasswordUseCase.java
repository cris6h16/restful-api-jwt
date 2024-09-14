package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Services.CacheService;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class RequestResetPasswordUseCase implements RequestResetPasswordPort {

    private final EmailService emailService;
    private final TransactionManager transactionManager;
    private final UserValidator userValidator;
    private final CacheService cacheService;

    public RequestResetPasswordUseCase(EmailService emailService, TransactionManager transactionManager, UserValidator userValidator, CacheService cacheService) {
        this.emailService = emailService;
        this.transactionManager = transactionManager;
        this.userValidator = userValidator;
        this.cacheService = cacheService;
    }


    @Override
    public void requestResetPassword(String email) {
        userValidator.validateEmail(email);

        AtomicReference<UserModel> reference = new AtomicReference<>();
        transactionManager.readCommitted(() -> {
            reference.set(cacheService.getByEmail(email) // low frequent operation, no need to be cached... but it is used later for login, get details, etc
                    .orElseThrow(() -> new NotFoundException("User not found"))
            );
        });

        UserModel user = reference.get();
        // non-blocking
        emailService.sendAsychResetPasswordEmail(user);
    }
}
