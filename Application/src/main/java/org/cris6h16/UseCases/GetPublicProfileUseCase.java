package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.GetPublicProfilePort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class GetPublicProfileUseCase implements GetPublicProfilePort {
    private final UserValidator userValidator;
    private final TransactionManager transactionManager;
    private final UserRepository userRepository;

    public GetPublicProfileUseCase(UserValidator userValidator, TransactionManager transactionManager, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
    }


    @Override
    // todo: use presenters
    public UserModel handle(Long id) {
        userValidator.validateId(id);

        AtomicReference<UserModel> ref = new AtomicReference<>();
        transactionManager.readCommitted(() -> ref.set(findByIdElseThrow(id)));
        return ref.get();
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
