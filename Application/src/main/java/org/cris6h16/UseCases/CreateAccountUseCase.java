package org.cris6h16.UseCases;

import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.Repositories.UserRepository;

public class CreateAccountUseCase {

    private final UserRepository userRepository;

    protected CreateAccountUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected void createAccount(CreateAccountCommand command) {


        userRepository.save();
    }

}
