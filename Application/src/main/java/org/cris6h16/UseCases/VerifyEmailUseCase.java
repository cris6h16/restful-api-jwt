package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.CacheService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.logging.Logger;

public class VerifyEmailUseCase implements VerifyEmailPort {
    private final UserRepository userRepository;
    private final TransactionManager transactionManager;
    private final UserValidator userValidator;
    private final CacheService cacheService;
    private static final Logger log = Logger.getLogger(VerifyEmailUseCase.class.getName());

    public VerifyEmailUseCase(UserRepository userRepository, TransactionManager transactionManager, UserValidator userValidator, CacheService cacheService) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
        this.userValidator = userValidator;
        this.cacheService = cacheService;
    }

    @Override
    public void verifyEmailById(Long id) {
        userValidator.validateId(id);

        transactionManager.readCommitted(() -> {
            if (!userRepository.existsByIdCustom(id)) { // todo: for docs-> small & low frequent & impact operation, no need to be cached
                throw new NotFoundException("User not found");
            }
            cacheService.updateEmailVerifiedIfExists(id, true);
            userRepository.updateEmailVerifiedByIdCustom(id, true);
        });
    }




}
