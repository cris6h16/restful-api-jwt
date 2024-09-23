package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.RefreshAccessTokenPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;

import java.util.Set;

public class RefreshAccessTokenUseCase implements RefreshAccessTokenPort {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ErrorMessages errorMessages;
    private final UserValidator userValidator;

    public RefreshAccessTokenUseCase(JwtUtils jwtUtils, UserRepository userRepository, ErrorMessages errorMessages, UserValidator userValidator) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.errorMessages = errorMessages;
        this.userValidator = userValidator;
    }

    @Override
    public String handle(Long id){
        userValidator.validateId(id);

        userExists(id);
        Set<ERoles> roles = userRepository.getRolesByIdCustom(id);
        return jwtUtils.genAccessToken(id, roles);
    }

    private void userExists(Long id) {
        if (!userRepository.existsByIdCustom(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }
}
