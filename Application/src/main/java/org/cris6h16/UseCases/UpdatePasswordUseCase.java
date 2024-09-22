package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Exceptions.Impls.NotFoundException;
import org.cris6h16.In.Ports.UpdatePasswordPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.Utils.UserValidator;

public class UpdatePasswordUseCase implements UpdatePasswordPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final MyPasswordEncoder myPasswordEncoder;

    public UpdatePasswordUseCase(
            UserValidator userValidator,
            UserRepository userRepository,
            MyPasswordEncoder myPasswordEncoder) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.myPasswordEncoder = myPasswordEncoder;
    }

    @Override
    public void handle(Long id, String currentPassword, String newPassword) {
        validateInputs(id, currentPassword, newPassword);

        validateUserExists(id);
        validateCurrentPassword(id, currentPassword);
        updatePassword(id, newPassword);
    }

    private void validateInputs(Long id, String currentPassword, String newPassword) {
        userValidator.validateId(id);
        userValidator.validatePassword(currentPassword);
        userValidator.validatePassword(newPassword);
    }

    private void validateUserExists(Long id) {
        if (!userRepository.existsByIdCustom(id)) {
            throw new NotFoundException("User not found");
        }
    }

    private void validateCurrentPassword(Long id, String currentPassword) {
        String storedPassword = userRepository.findPasswordByIdCustom(id)
                .orElseThrow(() -> new IllegalStateException("User must have a non-null password"));

        if (!myPasswordEncoder.matches(currentPassword, storedPassword)) {
            throw new InvalidAttributeException("Current password is incorrect");
        }
    }

    private void updatePassword(Long id, String newPassword) {
        String encodedNewPassword = myPasswordEncoder.encode(newPassword);
        userRepository.updatePasswordByIdCustom(id, encodedNewPassword);
    }
}
