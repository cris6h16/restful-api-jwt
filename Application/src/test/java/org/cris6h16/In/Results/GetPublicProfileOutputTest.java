package org.cris6h16.In.Results;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GetPublicProfileOutputTest {

    private UserModel userModel;
    private GetPublicProfileOutput profileOutput;

    @BeforeEach
    void setUp() {
        Set<ERoles> roles = new HashSet<>();
        roles.add(ERoles.ROLE_USER);
        roles.add(ERoles.ROLE_ADMIN);

        userModel = new UserModel.Builder()
                .setId(1L)
                .setUsername("cris6h16")
                .setEmail("cristianmherrera21@gmail.com")
                .setActive(true)
                .setEmailVerified(true)
                .setLastModified(LocalDateTime.now())
                .setRoles(roles)
                .build();
    }

    @Test
    void testDefaultConstructor() {
        profileOutput = new GetPublicProfileOutput();

        assertNull(profileOutput.getId());
        assertNull(profileOutput.getUsername());
        assertNull(profileOutput.getEmail());
        assertNull(profileOutput.getRoles());
        assertNull(profileOutput.getActive());
        assertNull(profileOutput.getEmailVerified());
        assertNull(profileOutput.getLastModified());
    }

    @Test
    void testConstructorWithUserModel() {
        profileOutput = new GetPublicProfileOutput(userModel);

        assertEquals(userModel.getId(), profileOutput.getId());
        assertEquals(userModel.getUsername(), profileOutput.getUsername());
        assertEquals(userModel.getEmail(), profileOutput.getEmail());
        assertEquals(userModel.getRoles(), profileOutput.getRoles());
        assertEquals(userModel.getActive(), profileOutput.getActive());
        assertEquals(userModel.getEmailVerified(), profileOutput.getEmailVerified());
        assertEquals(userModel.getLastModified(), profileOutput.getLastModified());
    }

    @Test
    void testGetId() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getId(), profileOutput.getId());
    }

    @Test
    void testGetUsername() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getUsername(), profileOutput.getUsername());
    }

    @Test
    void testGetEmail() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getEmail(), profileOutput.getEmail());
    }

    @Test
    void testGetRoles() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getRoles(), profileOutput.getRoles());
    }

    @Test
    void testGetActive() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getActive(), profileOutput.getActive());
    }

    @Test
    void testGetEmailVerified() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getEmailVerified(), profileOutput.getEmailVerified());
    }

    @Test
    void testGetLastModified() {
        profileOutput = new GetPublicProfileOutput(userModel);
        assertEquals(userModel.getLastModified(), profileOutput.getLastModified());
    }
}
