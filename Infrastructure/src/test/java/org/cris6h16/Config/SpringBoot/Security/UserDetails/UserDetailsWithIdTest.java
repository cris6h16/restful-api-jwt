package org.cris6h16.Config.SpringBoot.Security.UserDetails;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsWithIdTest {

    @Test
    void testConstructorAndGetId() {
        // Arrange
        String username = "cris6h16";
        String password = "12345678";
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        Long expectedId = 123L;
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Act
        UserDetailsWithId userDetails = new UserDetailsWithId(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities, expectedId);

        // Assert
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertEquals(expectedId, userDetails.getId());
    }

    @Test
    void testGetId() {
        // Arrange
        Long id = 456L;
        UserDetailsWithId userDetails = new UserDetailsWithId("username", "password", true, true, true, true,
                new ArrayList<>(), id);

        // Act
        Long actualId = userDetails.getId();

        // Assert
        assertEquals(id, actualId);
    }
}