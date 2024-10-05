package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.Config.SpringBoot.Properties.ErrorMessagesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorMessagesImplTest {

    private ErrorMessagesImpl errorMessagesImpl;
    private ErrorMessagesProperties errorMessagesProperties;

    @BeforeEach
    void setUp() {
        errorMessagesProperties = createErrorMessagesProperties();
        errorMessagesImpl = new ErrorMessagesImpl(errorMessagesProperties);
    }

    @Test
    void testGetUsernameAlreadyExistsMessage() {
        String msg = errorMessagesProperties.getUser().getUnique().getUsername();
        assertEquals(msg, errorMessagesImpl.getUsernameAlreadyExistsMessage());
    }

    @Test
    void testGetEmailAlreadyExistsMessage() {
        String msg = errorMessagesProperties.getUser().getUnique().getEmail();
        assertEquals(msg, errorMessagesImpl.getEmailAlreadyExistsMessage());
    }

    @Test
    void testGetPasswordTooShortMessage() {
        String msg = errorMessagesProperties.getUser().getInvalid().getPassword().getLength().getTooShort();
        assertEquals(msg, errorMessagesImpl.getPasswordTooShortMessage());
    }

    @Test
    void testGetEmailInvalidMessage() {
        String msg = errorMessagesProperties.getUser().getInvalid().getEmail();
        assertEquals(msg, errorMessagesImpl.getEmailInvalidMessage());
    }

    @Test
    void testGetIdCannotBeNullMessage() {
        String msg = errorMessagesProperties.getUser().getInvalid().getId().getNullVal();
        assertEquals(msg, errorMessagesImpl.getIdCannotBeNullMessage());
    }

    @Test
    void testGetRolesCannotBeEmptyMessage() {
        String msg = errorMessagesProperties.getUser().getInvalid().getRoles().getEmpty();
        assertEquals(msg, errorMessagesImpl.getRolesCannotBeEmptyMessage());
    }

    @Test
    void testGetUserNotFoundMessage() {
        String msg = errorMessagesProperties.getUser().getNotFound();
        assertEquals(msg, errorMessagesImpl.getUserNotFoundMessage());
    }

    @Test
    void testGetEmailNotVerifiedMessage() {
        String msg = errorMessagesProperties.getUser().getEmailNotVerified();
        assertEquals(msg, errorMessagesImpl.getEmailNotVerifiedMessage());
    }

    @Test
    void testGetCurrentPasswordNotMatchMessage() {
        String msg = errorMessagesProperties.getUser().getUpdatingPassword().getPasswordNotMatch();
        assertEquals(msg, errorMessagesImpl.getCurrentPasswordNotMacthMessage());
    }

    @Test
    void testGetInvalidCredentialsMessage() {
        String msg = errorMessagesProperties.getUser().getLogin().getInvalidCredentials();
        assertEquals(msg, errorMessagesImpl.getInvalidCredentialsMessage());
    }

    @Test
    void testGetUnexpectedErrorMessage() {
        String msg = errorMessagesProperties.getSystem().getUnexpected();
        assertEquals(msg, errorMessagesImpl.getUnexpectedErrorMessage());
    }

    @Test
    void testGetUsernameLengthFailMessage() {
        String msg = errorMessagesProperties.getUser().getInvalid().getUsername().getLength();
        assertEquals(msg, errorMessagesImpl.getUsernameLengthFailMessage());
    }



    private ErrorMessagesProperties createErrorMessagesProperties() {
        ErrorMessagesProperties errorMessagesProperties = new ErrorMessagesProperties();

        //  System messages
        errorMessagesProperties.setSystem(new ErrorMessagesProperties.System());
        errorMessagesProperties.getSystem().setUnexpected("An unexpected error occurred.");

        //  User messages
        errorMessagesProperties.setUser(new ErrorMessagesProperties.User());
        errorMessagesProperties.getUser().setNotFound("User not found.");
        errorMessagesProperties.getUser().setEmailNotVerified("Email not verified.");

        //  Password messages
        ErrorMessagesProperties.User.UpdatingPassword updatingPassword = new ErrorMessagesProperties.User.UpdatingPassword();
        updatingPassword.setPasswordNotMatch("Passwords do not match.");
        errorMessagesProperties.getUser().setUpdatingPassword(updatingPassword);

        //  messages
        ErrorMessagesProperties.User.Login login = new ErrorMessagesProperties.User.Login();
        login.setInvalidCredentials("Invalid credentials provided.");
        errorMessagesProperties.getUser().setLogin(login);

        // Unique messages
        ErrorMessagesProperties.User.Unique unique = new ErrorMessagesProperties.User.Unique();
        unique.setUsername("Username already taken.");
        unique.setEmail("Email already taken.");
        errorMessagesProperties.getUser().setUnique(unique);

        // Invalid messages
        ErrorMessagesProperties.User.Invalid invalid = new ErrorMessagesProperties.User.Invalid();
        invalid.setEmail("Invalid email format.");

        // Id messages
        ErrorMessagesProperties.User.Invalid.Id id = new ErrorMessagesProperties.User.Invalid.Id();
        id.setNullVal("ID cannot be null.");
        invalid.setId(id);

        // Username length messages
        ErrorMessagesProperties.User.Invalid.Username username = new ErrorMessagesProperties.User.Invalid.Username();
        username.setLength("Username must be between 3 and 20 characters.");
        invalid.setUsername(username);

        // Password length messages
        ErrorMessagesProperties.User.Invalid.Password password = new ErrorMessagesProperties.User.Invalid.Password();
        ErrorMessagesProperties.User.Invalid.Password.Length length = new ErrorMessagesProperties.User.Invalid.Password.Length();
        length.setTooShort("Password must be at least 8 characters.");
        password.setLength(length);
        invalid.setPassword(password);

        // Roles messages
        ErrorMessagesProperties.User.Invalid.Roles roles = new ErrorMessagesProperties.User.Invalid.Roles();
        roles.setEmpty("Roles cannot be empty.");
        invalid.setRoles(roles);

        errorMessagesProperties.getUser().setInvalid(invalid);

        return errorMessagesProperties;
    }
}
