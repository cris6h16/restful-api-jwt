package org.cris6h16.UseCases;

import org.cris6h16.In.Ports.DeleteAccountPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

public class DeleteAccountUseCase implements DeleteAccountPort {

    private final UserValidator userValidator;
    private final TransactionManager transactionManager;
    private final UserRepository userRepository;

    public DeleteAccountUseCase(UserValidator userValidator, TransactionManager transactionManager, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Long id) {
        userValidator.validateId(id);
        transactionManager.readCommitted(() -> userRepository.deactivate(id));
    }
}
