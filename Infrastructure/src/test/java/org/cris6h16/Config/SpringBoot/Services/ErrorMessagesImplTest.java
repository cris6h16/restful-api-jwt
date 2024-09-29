package org.cris6h16.Config.SpringBoot.Services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = ErrorMessagesImpl.class)
@ActiveProfiles("test")
class ErrorMessagesImplTest {

    @Autowired
    private ErrorMessagesImpl errorMessagesImpl;

    @Test
    void testGetUsernameAlreadyExistsMessage() {
        // Arrange, Act & Assert
        assertEquals("Username already exists", errorMessagesImpl.getUsernameAlreadyExistsMessage());
    }

    @Test
    void testGetEmailAlreadyExistsMessage() {
        assertEquals("Email already exists", errorMessagesImpl.getEmailAlreadyExistsMessage());
    }

    @Test
    void testGetPasswordTooShortMessage() {
        assertEquals("Password must be at least 8 characters long", errorMessagesImpl.getPasswordTooShortMessage());
    }

    @Test
    void testGetEmailInvalidMessage() {
        assertEquals("Email is invalid", errorMessagesImpl.getEmailInvalidMessage());
    }

    @Test
    void testGetIdCannotBeNullMessage() {
        assertEquals("ID cannot be null", errorMessagesImpl.getIdCannotBeNullMessage());
    }

    @Test
    void testGetRolesCannotBeEmptyMessage() {
        assertEquals("Roles cannot be empty", errorMessagesImpl.getRolesCannotBeEmptyMessage());
    }

    @Test
    void testGetUserNotFoundMessage() {
        assertEquals("User not found", errorMessagesImpl.getUserNotFoundMessage());
    }

    @Test
    void testGetEmailNotVerifiedMessage() {
        assertEquals("Email not verified, please check your email", errorMessagesImpl.getEmailNotVerifiedMessage());
    }

    @Test
    void testGetCurrentPasswordNotMatchMessage() {
        assertEquals("Your current password not matches", errorMessagesImpl.getCurrentPasswordNotMacthMessage());
    }

    @Test
    void testGetInvalidCredentialsMessage() {
        assertEquals("Invalid credentials", errorMessagesImpl.getInvalidCredentialsMessage());
    }

    @Test
    void testGetUnexpectedErrorMessage() {
        assertEquals("Unexpected error", errorMessagesImpl.getUnexpectedErrorMessage());
    }

    @Test
    void testGetUsernameLengthFailMessage() {
        assertEquals("Username length must be between 3 & 20 ( included )", errorMessagesImpl.getUsernameLengthFailMessage());
    }
}
