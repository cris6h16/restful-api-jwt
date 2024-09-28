package org.cris6h16.Utils;

import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource(strings = {"blank", "empty", "null", "length1", "length2", "length21", "length22"})
    void validateUsername_shouldThrowException_whenUsernameIsInvalid(String now) {
        String username = switch (now) {
            case "blank" -> "    ";
            case "empty" -> "    ";
            case "null" -> null;
            case "lenght1" -> "a".repeat(1);
            case "lenght2" -> "a".repeat(2);
            case "lenght21" -> "a".repeat(21);
            case "lenght22" -> "a".repeat(22);
            default -> throw new IllegalStateException();
        };

        when(errorMessages.getUsernameLengthFailMessage()).thenReturn("Username length fail etc etc ");

        assertThatThrownBy(() -> userValidator.validateUsername(null))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessage("Username length fail etc etc ");
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20})
    void validateUsername_shouldNotThrowException_whenUsernameIsValid(int n) {
        userValidator.validateUsername("a".repeat(n));
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
