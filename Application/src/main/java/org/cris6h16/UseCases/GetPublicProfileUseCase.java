package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.GetPublicProfilePort;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

public class GetPublicProfileUseCase implements GetPublicProfilePort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final ErrorMessages errorMessages;

    public GetPublicProfileUseCase(UserValidator userValidator, UserRepository userRepository, ErrorMessages errorMessages) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.errorMessages = errorMessages;
    }


    @Override
    public GetPublicProfileOutput handle(Long id) {
        userValidator.validateId(id);
        return toPublicProfileOutput(findByIdElseThrow(id));
    }

    private UserModel findByIdElseThrow(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(errorMessages.getUserNotFoundMessage()));
    }

    private GetPublicProfileOutput toPublicProfileOutput(UserModel userModel) {
        return new GetPublicProfileOutput(userModel);
    }
}
