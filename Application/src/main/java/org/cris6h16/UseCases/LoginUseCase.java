package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.EmailNotVerifiedException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.LoginPort;
import org.cris6h16.In.Results.LoginOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;

import java.util.Arrays;
import java.util.Map;

public class LoginUseCase implements LoginPort {


    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final UserValidator userValidator;
    private final long REFRESH_TOKEN_EXP_TIME_SECS;
    private final long ACCESS_TOKEN_EXP_TIME_SECS;

    public LoginUseCase(UserRepository userRepository, MyPasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService, UserValidator userValidator, long refreshTokenExpTimeSecs, long accessTokenExpTimeSecs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.userValidator = userValidator;
        REFRESH_TOKEN_EXP_TIME_SECS = refreshTokenExpTimeSecs;
        ACCESS_TOKEN_EXP_TIME_SECS = accessTokenExpTimeSecs;
    }

    @Override
    public LoginOutput handle(String email, String password) {
        userValidator.validateEmail(email);
        userValidator.validatePassword(password);

        UserModel um = findUserByEmailElseNull(email);
        verifyUser(um, password);

        return toResultLogin(um);
    }

    private void verifyUser(UserModel userModel, String password) {
        if (userModel == null || !userModel.getActive() || !passwordEncoder.matches(password, userModel.getPassword())) {
            throw new NotFoundException("Invalid email or password");
        }

        if (!userModel.getEmailVerified()) {
            emailService.sendVerificationEmail(userModel.getUsername(), userModel.getEmail());
            throw new EmailNotVerifiedException("Email is not verified, please go to your email and verify it");
        }
    }

    private UserModel findUserByEmailElseNull(String email) {
        return userRepository.findByEmailCustom(email).orElse(null);
    }

    private LoginOutput toResultLogin(UserModel userModel) {
        Map<String, String> accessTokenClaims = Map.of("roles", Arrays.toString(userModel.getRoles().toArray()));

        String refreshToken = jwtUtils.genToken(userModel.getId(), null, REFRESH_TOKEN_EXP_TIME_SECS);
        String accessToken = jwtUtils.genToken(userModel.getId(), accessTokenClaims, ACCESS_TOKEN_EXP_TIME_SECS);

        return new LoginOutput(accessToken, refreshToken);
    }

}
//todo: follow the clean architecture principles like make a presenter class
