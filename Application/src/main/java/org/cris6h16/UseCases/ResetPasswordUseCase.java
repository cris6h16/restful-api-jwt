package org.cris6h16.UseCases;

import org.cris6h16.In.Ports.ResetPasswordPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

public class ResetPasswordUseCase implements ResetPasswordPort {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final TransactionManager transactionManager;
    private final MyPasswordEncoder passwordEncoder;

    public ResetPasswordUseCase(UserRepository userRepository, UserValidator userValidator, TransactionManager transactionManager, MyPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.transactionManager = transactionManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void resetPasswordById(Long id, String password) {
        userValidator.validateId(id);

        transactionManager.readCommitted(() -> {
            userRepository.findByIdCustom(id).ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.saveCustom(user);
            });
        });
    }
}
