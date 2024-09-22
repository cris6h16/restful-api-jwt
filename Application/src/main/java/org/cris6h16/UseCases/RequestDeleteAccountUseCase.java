package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RequestDeleteAccountPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class RequestDeleteAccountUseCase implements RequestDeleteAccountPort {

    private final UserValidator userValidator;
    private final TransactionManager transactionManager;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public RequestDeleteAccountUseCase(UserValidator userValidator, TransactionManager transactionManager, UserRepository userRepository, EmailService emailService) {
        this.userValidator = userValidator;
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void handle(Long id) {
        userValidator.validateId(id);

        AtomicReference<UserModel> ref = new AtomicReference<>();
        transactionManager.readCommitted(() -> ref.set(findByIdElseThrow(id)));
        UserModel user = ref.get();

        // non-blocking
        emailService.sendAsychRequestDeleteAccountEmail(user);
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

    }
}
