package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.EmailNotVerifiedException;
import org.cris6h16.Exceptions.Impls.InvalidCredentialsException;
import org.cris6h16.In.Ports.LoginPort;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;

public class LoginUseCase implements LoginPort {


    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final ErrorMessages errorMessages;
    private final UserValidator userValidator;

    public LoginUseCase(UserRepository userRepository, MyPasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService, ErrorMessages errorMessages, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.errorMessages = errorMessages;
        this.userValidator = userValidator;
    }

    @Override
    public LoginOutput handle(String email, String password) {
        userValidator.validateEmail(email);
        userValidator.validatePassword(password);

        UserModel um = findUserByEmailElseNull(email);
        verifyUser(um, password);

        return toOutput(um);
    }

    private void verifyUser(UserModel userModel, String password) {
        if (userModel == null || !userModel.getActive() || !passwordEncoder.matches(password, userModel.getPassword())) {
            throw new InvalidCredentialsException(errorMessages.getInvalidCredentialsMessage());
        }

        if (!userModel.getEmailVerified()) {
            emailService.sendVerificationEmail(userModel.getId(), userModel.getEmail());
            throw new EmailNotVerifiedException(errorMessages.getEmailNotVerifiedMessage());
        }
    }

    private UserModel findUserByEmailElseNull(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    private LoginOutput toOutput(UserModel userModel) {
        String refreshToken = jwtUtils.genRefreshToken(userModel.getId());
        String accessToken = jwtUtils.genAccessToken(userModel.getId(), userModel.getRoles());

        return new LoginOutput(accessToken, refreshToken);
    }

}
