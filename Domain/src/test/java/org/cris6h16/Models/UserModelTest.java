package org.cris6h16.Models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserModelTest {

    @Test
    void testFullConstructor() {
        // Arrange
        Long expectedId = 1L;
        String expectedUsername = "cris6h16";
        String expectedPassword = "12345678";
        String expectedEmail = "cristianmherrera21@gmail.com";
        Set<ERoles> expectedRoles = new HashSet<>();
        expectedRoles.add(ERoles.ROLE_USER);
        Boolean expectedActive = true;
        Boolean expectedEmailVerified = false;
        LocalDateTime expectedLastModified = LocalDateTime.now();

        // Act
        UserModel userModel = new UserModel(expectedId, expectedUsername, expectedPassword, expectedEmail,
                expectedRoles, expectedActive, expectedEmailVerified, expectedLastModified);

        // Assert
        assertEquals(expectedId, userModel.getId());
        assertEquals(expectedUsername, userModel.getUsername());
        assertEquals(expectedPassword, userModel.getPassword());
        assertEquals(expectedEmail, userModel.getEmail());
        assertEquals(expectedRoles, userModel.getRoles());
        assertEquals(expectedActive, userModel.getActive());
        assertEquals(expectedEmailVerified, userModel.getEmailVerified());
        assertEquals(expectedLastModified, userModel.getLastModified());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        UserModel userModel = new UserModel();

        // Assert
        assertNull(userModel.getId());
        assertNull(userModel.getUsername());
        assertNull(userModel.getPassword());
        assertNull(userModel.getEmail());
        assertNull(userModel.getRoles());
        assertNull(userModel.getActive());
        assertNull(userModel.getEmailVerified());
        assertNull(userModel.getLastModified());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        UserModel userModel = new UserModel();
        Long expectedId = 2L;
        String expectedUsername = "cris6h16";
        String expectedPassword = "12345678";
        String expectedEmail = "cristianmherrera21@gmail.com";
        Set<ERoles> expectedRoles = new HashSet<>();
        expectedRoles.add(ERoles.ROLE_ADMIN);
        Boolean expectedActive = false;
        Boolean expectedEmailVerified = true;
        LocalDateTime expectedLastModified = LocalDateTime.now();

        // Act
        userModel.setId(expectedId);
        userModel.setUsername(expectedUsername);
        userModel.setPassword(expectedPassword);
        userModel.setEmail(expectedEmail);
        userModel.setRoles(expectedRoles);
        userModel.setActive(expectedActive);
        userModel.setEmailVerified(expectedEmailVerified);
        userModel.setLastModified(expectedLastModified);

        // Assert
        assertEquals(expectedId, userModel.getId());
        assertEquals(expectedUsername, userModel.getUsername());
        assertEquals(expectedPassword, userModel.getPassword());
        assertEquals(expectedEmail, userModel.getEmail());
        assertEquals(expectedRoles, userModel.getRoles());
        assertEquals(expectedActive, userModel.getActive());
        assertEquals(expectedEmailVerified, userModel.getEmailVerified());
        assertEquals(expectedLastModified, userModel.getLastModified());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long expectedId = 3L;
        String expectedUsername = "builderUser";
        String expectedPassword = "builderPassword";
        String expectedEmail = "builder@builder.com";
        Set<ERoles> expectedRoles = new HashSet<>();
        expectedRoles.add(ERoles.ROLE_USER);
        Boolean expectedActive = true;
        Boolean expectedEmailVerified = false;
        LocalDateTime expectedLastModified = LocalDateTime.now();

        // Act
        UserModel userModel = new UserModel.Builder()
                .setId(expectedId)
                .setUsername(expectedUsername)
                .setPassword(expectedPassword)
                .setEmail(expectedEmail)
                .setRoles(expectedRoles)
                .setActive(expectedActive)
                .setEmailVerified(expectedEmailVerified)
                .setLastModified(expectedLastModified)
                .build();

        // Assert
        assertEquals(expectedId, userModel.getId());
        assertEquals(expectedUsername, userModel.getUsername());
        assertEquals(expectedPassword, userModel.getPassword());
        assertEquals(expectedEmail, userModel.getEmail());
        assertEquals(expectedRoles, userModel.getRoles());
        assertEquals(expectedActive, userModel.getActive());
        assertEquals(expectedEmailVerified, userModel.getEmailVerified());
        assertEquals(expectedLastModified, userModel.getLastModified());
    }
}
