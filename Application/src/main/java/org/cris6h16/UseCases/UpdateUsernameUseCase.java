package org.cris6h16.UseCases;

import org.cris6h16.In.Ports.UpdateUsernamePort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.UserValidator;

public class UpdateUsernameUseCase implements UpdateUsernamePort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public UpdateUsernameUseCase(UserValidator userValidator, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }


    @Override
    public void handle(Long id, String username) {
        userValidator.validateId(id);
        userValidator.validateUsername(username);

        userRepository.updateUsernameByIdCustom(id, username);
    }
}
