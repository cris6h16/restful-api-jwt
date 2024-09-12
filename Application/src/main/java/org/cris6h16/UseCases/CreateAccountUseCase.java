package org.cris6h16.UseCases;

import org.cris6h16.Constants.EmailContent;
import org.cris6h16.Exceptions.Impls.AlreadyExistException;
import org.cris6h16.Exceptions.Impls.ImplementationException;
import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EIsolationLevel;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Services.MyPasswordEncoder;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CreateAccountUseCase implements CreateAccountPort {

    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;
    private final TransactionManager transactionManager;
    private static final Logger log = Logger.getLogger(CreateAccountUseCase.class.getName());

    public CreateAccountUseCase(UserRepository userRepository, MyPasswordEncoder passwordEncoder, EmailService emailService, JwtUtils jwtUtils, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtils = jwtUtils;
        this.transactionManager = transactionManager;
    }

    public Long createAccount(CreateAccountCommand command) {
        validateNotNull(command);
        validateUsername(command.getUsername());
        validatePassword(command.getPassword());
        validateEmail(command.getEmail());
        validateRoles(command.getRoles());

        String username = command.getUsername().trim();
        String password = command.getPassword().trim();
        String email = command.getEmail().trim();
        Set<ERoles> roles = command.getRoles();


        UserModel userModel = new UserModel.Builder()
                .setUsername(username)
                .setPassword(passwordEncoder.encode(password))
                .setEmail(email)
                .setRoles(roles)
                .setActive(true)
                .setEmailVerified(false)
                .setLastModified(System.currentTimeMillis())
                .build();

        transactionManager.executeInTransaction(EIsolationLevel.READ_COMMITTED, () -> {
            if (userRepository.existsByUsername(userModel.getUsername())) {
                throw new AlreadyExistException("Username already exists");
            }
            if (userRepository.existsByEmail(userModel.getEmail())) {
                throw new AlreadyExistException("Email already exists");
            }

            userRepository.save(userModel);
        });

       emailService.sendAsychVerificationEmail(userModel); // non-blocking

        return userModel.getId();
    }

    private void validateNotNull(CreateAccountCommand command) {
        if (command == null) { // Never should be null
            throw new ImplementationException("Command cannot be null");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidAttributeException("Username cannot be null or blank");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty() || password.length() < 8) {
            throw new InvalidAttributeException("Password must be at least 8 characters long");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches("^\\S+@\\S+\\.\\S+$")) { //--> ^ = start of the string, \S = any non-whitespace character, + = one or more, @ = @, \S = any non-whitespace character, + = one or more, \. = ., \S = any non-whitespace character, + = one or more, $ = end of the string
            throw new InvalidAttributeException("Email is invalid");
        }
    }

    private void validateRoles(Set<ERoles> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new ImplementationException("Roles cannot be null or empty");
        }
    }


}
