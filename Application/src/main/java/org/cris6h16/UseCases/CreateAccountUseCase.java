package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.AlreadyExistException;
import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.UserValidator;

import java.util.Set; // todo:add logger


public class CreateAccountUseCase implements CreateAccountPort {

    private final ErrorMessages errorMessages;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public CreateAccountUseCase(UserRepository userRepository, MyPasswordEncoder passwordEncoder, EmailService emailService, JwtUtils jwtUtils, ErrorMessages constants, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.errorMessages = constants;
        this.userValidator = userValidator;
    }

    public Long handle(CreateAccountCommand command) {
        command = validateAndCleanCommand(command);

        String username = command.getUsername();
        String password = command.getPassword();
        String email = command.getEmail();
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

        checkDBForDuplicates(userModel);
        userModel = userRepository.saveCustom(userModel);

        emailService.sendVerificationEmail(userModel.getId(), userModel.getEmail());

        return userModel.getId();
    }

    private CreateAccountCommand validateAndCleanCommand(CreateAccountCommand cmd) {
        if (cmd == null) {
            throw new UnexpectedException("Command cannot be null");
        }

        userValidator.validateUsername(cmd.getUsername());
        userValidator.validatePassword(cmd.getPassword());
        userValidator.validateEmail(cmd.getEmail());
        userValidator.validateRoles(cmd.getRoles());

        return new CreateAccountCommand(
                cmd.getUsername().trim(),
                cmd.getPassword().trim(),
                cmd.getEmail().trim(),
                cmd.getRoles()
        );
    }

    private void checkDBForDuplicates(UserModel userModel) {
        if (userRepository.existsByUsernameCustom(userModel.getUsername())) {
            throw new AlreadyExistException(errorMessages.getUsernameAlreadyExistsMessage());
        }
        if (userRepository.existsByEmailCustom(userModel.getEmail())) {
            throw new AlreadyExistException(errorMessages.getEmailAlreadyExistsMessage());
        }
    }

}
