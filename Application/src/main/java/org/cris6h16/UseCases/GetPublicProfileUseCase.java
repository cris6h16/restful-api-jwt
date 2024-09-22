package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.GetPublicProfilePort;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.UserValidator;

import java.util.concurrent.atomic.AtomicReference;

public class GetPublicProfileUseCase implements GetPublicProfilePort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public GetPublicProfileUseCase(UserValidator userValidator, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }


    @Override
    // todo: use presenters
    public UserModel handle(Long id) {
        userValidator.validateId(id);
        return findByIdElseThrow(id);
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findByIdCustom(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
