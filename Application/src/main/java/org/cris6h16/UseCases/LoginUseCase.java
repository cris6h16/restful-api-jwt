package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.EmailNotVerifiedException;
import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.LoginPort;
import org.cris6h16.In.Results.ResultLogin;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EIsolationLevel;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.JwtUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoginUseCase implements LoginPort {
    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TransactionManager transactionManager;
    private final EmailService emailService;
    private final long REFRESH_TOKEN_EXP_TIME_SECS;
    private final long ACCESS_TOKEN_EXP_TIME_SECS;

    public LoginUseCase(UserRepository userRepository, MyPasswordEncoder passwordEncoder, JwtUtils jwtUtils, TransactionManager transactionManager, EmailService emailService, long refreshTokenExpTimeSecs, long accessTokenExpTimeSecs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.transactionManager = transactionManager;
        this.emailService = emailService;
        REFRESH_TOKEN_EXP_TIME_SECS = refreshTokenExpTimeSecs;
        ACCESS_TOKEN_EXP_TIME_SECS = accessTokenExpTimeSecs;
    }

    @Override
    public ResultLogin login(String email, String password) {
        validateEmail(email); // todo: make a validator class, the validations are boilerplate
        validatePassword(password);

        String encodedPassword = passwordEncoder.encode(password);
        AtomicReference<UserModel> user = new AtomicReference<>(); // necessary for lambdas

        transactionManager.readCommitted(() -> {
            user.set(
                    userRepository.findByEmailCustom(email).orElse(null)
            );
        });
        UserModel userModel = user.get();

        if (userModel == null || !passwordEncoder.matches(password, userModel.getPassword())) {
            throw new NotFoundException("Invalid email or password");
        }

        if (!userModel.getEmailVerified()) {
            emailService.sendAsychVerificationEmail(userModel);
            throw new EmailNotVerifiedException("Email is not verified, please go to your email and verify it");
        }

        return toResultLogin(userModel);
    }

    private ResultLogin toResultLogin(UserModel userModel) {
        Map<String, String> accessTokenClaims = Map.of("roles", Arrays.toString(userModel.getRoles().toArray()));

        String refreshToken = jwtUtils.genToken(userModel.getId(), null, REFRESH_TOKEN_EXP_TIME_SECS);
        String accessToken = jwtUtils.genToken(userModel.getId(), accessTokenClaims, ACCESS_TOKEN_EXP_TIME_SECS);

        return new ResultLogin(accessToken, refreshToken);
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidAttributeException("Password cannot be blank");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches("^\\S+@\\S+\\.\\S+$")) { //--> ^ = start of the string, \S = any non-whitespace character, + = one or more, @ = @, \S = any non-whitespace character, + = one or more, \. = ., \S = any non-whitespace character, + = one or more, $ = end of the string
            throw new InvalidAttributeException("Email is invalid");
        }
    }
}
//todo: follow the clean architecture principles like make a presenter class
