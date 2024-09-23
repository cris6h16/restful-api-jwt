package org.cris6h16.Utils;
import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class UserValidatorTest {

    @Mock
    private ErrorMessages errorMessages;

    @InjectMocks
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Username validation tests
    @Test
    void validateUsername_shouldThrowException_whenUsernameIsNull() {
        when(errorMessages.getUsernameCannotBeBlankMessage()).thenReturn("Username cannot be blank");

        assertThatThrownBy(() -> userValidator.validateUsername(null))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Username cannot be blank");
    }

    @Test
    void validateUsername_shouldThrowException_whenUsernameIsEmpty() {
        when(errorMessages.getUsernameCannotBeBlankMessage()).thenReturn("Username cannot be blank");

        assertThatThrownBy(() -> userValidator.validateUsername("   "))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Username cannot be blank");
    }

    @Test
    void validateUsername_shouldNotThrowException_whenUsernameIsValid() {
        userValidator.validateUsername("validUsername");
    }

    @Test
    void validatePassword_shouldThrowException_whenPasswordIsNull() {
        when(errorMessages.getPasswordTooShortMessage()).thenReturn("Password is too short");

        assertThatThrownBy(() -> userValidator.validatePassword(null))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Password is too short");
    }

    @Test
    void validatePassword_shouldThrowException_whenPasswordIsEmpty() {
        when(errorMessages.getPasswordTooShortMessage()).thenReturn("Password is too short");

        assertThatThrownBy(() -> userValidator.validatePassword("   "))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Password is too short");
    }

    @Test
    void validatePassword_shouldThrowException_whenPasswordIsTooShort() {
        when(errorMessages.getPasswordTooShortMessage()).thenReturn("Password is too short");

        assertThatThrownBy(() -> userValidator.validatePassword("short"))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Password is too short");
    }

    @Test
    void validatePassword_shouldNotThrowException_whenPasswordIsValid() {
        userValidator.validatePassword("validPassword");
    }

    @Test
    void validateEmail_shouldThrowException_whenEmailIsNull() {
        when(errorMessages.getEmailInvalidMessage()).thenReturn("Invalid email");

        assertThatThrownBy(() -> userValidator.validateEmail(null))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Invalid email");
    }

    @Test
    void validateEmail_shouldThrowException_whenEmailIsEmpty() {
        when(errorMessages.getEmailInvalidMessage()).thenReturn("Invalid email");

        assertThatThrownBy(() -> userValidator.validateEmail("   "))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Invalid email");
    }

    @Test
    void validateEmail_shouldThrowException_whenEmailIsInvalid() {
        when(errorMessages.getEmailInvalidMessage()).thenReturn("Invalid email");

        assertThatThrownBy(() -> userValidator.validateEmail("invalid-email"))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Invalid email");
    }

    @Test
    void validateEmail_shouldNotThrowException_whenEmailIsValid() {
        userValidator.validateEmail("test@example.com");
    }

    @Test
    void validateRoles_shouldThrowException_whenRolesAreNull() {
        when(errorMessages.getRolesCannotBeEmptyMessage()).thenReturn("Roles cannot be empty");

        assertThatThrownBy(() -> userValidator.validateRoles(null))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Roles cannot be empty");
    }

    @Test
    void validateRoles_shouldThrowException_whenRolesAreEmpty() {
        when(errorMessages.getRolesCannotBeEmptyMessage()).thenReturn("Roles cannot be empty");

        assertThatThrownBy(() -> userValidator.validateRoles(Set.of()))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Roles cannot be empty");
    }

    @Test
    void validateRoles_shouldNotThrowException_whenRolesAreValid() {
        userValidator.validateRoles(Set.of(ERoles.ROLE_USER));
    }

    // ID validation tests
    @Test
    void validateId_shouldThrowException_whenIdIsNull() {
        when(errorMessages.getIdCannotBeNullMessage()).thenReturn("ID cannot be null");

        assertThatThrownBy(() -> userValidator.validateId(null))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("ID cannot be null");
    }

    @Test
    void validateId_shouldNotThrowException_whenIdIsValid() {
        userValidator.validateId(1L);
    }
}
