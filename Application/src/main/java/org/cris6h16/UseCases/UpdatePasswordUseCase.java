package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.Exceptions.Impls.PasswordNotMatchException;
import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Ports.UpdatePasswordPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;

public class UpdatePasswordUseCase implements UpdatePasswordPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final MyPasswordEncoder myPasswordEncoder;
    private final ErrorMessages errorMessages;

    public UpdatePasswordUseCase(
            UserValidator userValidator,
            UserRepository userRepository,
            MyPasswordEncoder myPasswordEncoder, ErrorMessages errorMessages) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.myPasswordEncoder = myPasswordEncoder;
        this.errorMessages = errorMessages;
    }

    @Override
    public void handle(Long id, String currentPassword, String newPassword) {
        currentPassword = currentPassword.trim();
        newPassword = newPassword.trim();

        validateInputs(id, currentPassword, newPassword);

        userExists(id);
        validateCurrentPassword(id, currentPassword);
        updatePassword(id, newPassword);
    }

    private void validateInputs(Long id, String currentPassword, String newPassword) {
        userValidator.validateId(id);
        userValidator.validatePassword(currentPassword);
        userValidator.validatePassword(newPassword);
    }

    private void userExists(Long id) {
        if (!userRepository.existsByIdCustom(id)) {
            throw new NotFoundException(errorMessages.getUserNotFoundMessage());
        }
    }

    private void validateCurrentPassword(Long id, String currentPassword) {
        String storedPassword = userRepository.findPasswordByIdCustom(id)
                .orElseThrow(() -> new UnexpectedException("User must have a non-null password"));

        if (!myPasswordEncoder.matches(currentPassword, storedPassword)) {
            throw new PasswordNotMatchException(errorMessages.getCurrentPasswordNotMacthMessage());
        }
    }

    private void updatePassword(Long id, String newPassword) {
        String encodedNewPassword = myPasswordEncoder.encode(newPassword);
        userRepository.updatePasswordByIdCustom(id, encodedNewPassword);
    }
}
