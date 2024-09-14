package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EIsolationLevel;
import org.cris6h16.Services.TransactionManager;

import java.util.logging.Logger;

public class VerifyEmailUseCase implements VerifyEmailPort {
    private final UserRepository userRepository;
    private final TransactionManager transactionManager;
    private static final Logger log = Logger.getLogger(VerifyEmailUseCase.class.getName());

    public VerifyEmailUseCase(UserRepository userRepository, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void verifyEmailById(Long id) {
        validateId(id);
        transactionManager.readCommitted(() -> {
            if (!userRepository.existsByIdCustom(id)) {
                throw new NotFoundException("User not found");
            }
            userRepository.updateEmailVerifiedByIdCustom(id, true);
        });
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new InvalidAttributeException("Id cannot be null");
        }
    }


}
