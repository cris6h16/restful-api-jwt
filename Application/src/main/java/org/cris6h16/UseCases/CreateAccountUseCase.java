package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.AlreadyExistException;
import org.cris6h16.Exceptions.Impls.ImplementationException;
import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.In.Commands.CreateAccountCommand;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.MyPasswordEncoder;

import java.util.Set;

public class CreateAccountUseCase implements CreateAccountPort {

    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;

    public CreateAccountUseCase(UserRepository userRepository, MyPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long createAccount(CreateAccountCommand command) {
        verifyNotNull(command);
        verifyUsername(command.getUsername());
        verifyPassword(command.getPassword());
        verifyEmail(command.getEmail());
        verifyRoles(command.getRoles());

        String username = command.getUsername().trim();
        String password = command.getPassword().trim();
        String email = command.getEmail().trim();
        Set<ERoles> roles = command.getRoles();


        UserModel userModel = new UserModel.Builder()
                .setUsername(username)
                .setPassword(passwordEncoder.encode(password))
                .setEmail(email)
                .setRoles(roles)
                .setActive(false)
                .setEmailVerified(false)
                .setLastModified(System.currentTimeMillis())
                .build();

        if (userRepository.existsByUsername(userModel.getUsername())) {
            throw new AlreadyExistException("Username already exists");
        }
        if (userRepository.existsByEmail(userModel.getEmail())) {
            throw new AlreadyExistException("Email already exists");
        }

        userRepository.save(userModel);

        return userModel.getId();
    }

    private void verifyNotNull(CreateAccountCommand command) {
        if (command == null) { // Never should be null
            throw new ImplementationException("Command cannot be null");
        }
    }

    private void verifyUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidAttributeException("Username cannot be null or blank");
        }
    }

    private void verifyPassword(String password) {
        if (password == null || password.trim().isEmpty() || password.length() < 8) {
            throw new InvalidAttributeException("Password must be at least 8 characters long");
        }
    }

    private void verifyEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches("^\\S+@\\S+\\.\\S+$")) { //--> ^ = start of the string, \S = any non-whitespace character, + = one or more, @ = @, \S = any non-whitespace character, + = one or more, \. = ., \S = any non-whitespace character, + = one or more, $ = end of the string
            throw new InvalidAttributeException("Email is invalid");
        }
    }

    private void verifyRoles(Set<ERoles> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new ImplementationException("Roles cannot be null or empty");
        }
    }


}
