package org.cris6h16.UseCases;

import org.cris6h16.In.Ports.ResetPasswordPort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.UserValidator;

import java.util.Optional;

public class ResetPasswordUseCase implements ResetPasswordPort {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final MyPasswordEncoder passwordEncoder;

    public ResetPasswordUseCase(UserRepository userRepository, UserValidator userValidator, MyPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void resetPasswordById(Long id, String password) {
        userValidator.validateId(id);

        Optional<UserModel> op = userRepository.findByIdCustom(id);
        if (op.isPresent()) {
            UserModel u = op.get();
            u.setPassword(passwordEncoder.encode(password));
            userRepository.saveCustom(u);
        }
    }
}
