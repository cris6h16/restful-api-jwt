package org.cris6h16.Config.SpringBoot.Security.UserDetails;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsWithIdTest {

    @Test
    void testConstructor() {
        // Arrange
        Long expectedId = 123L;
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Act
        UserDetailsWithId userDetails = new UserDetailsWithId(expectedId, authorities);

        // Assert
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertEquals(expectedId, userDetails.getId());
        assertEquals(authorities, userDetails.getAuthorities());
    }

    @Test
    void testGetId() {
        // Arrange
        Long id = 456L;
        UserDetailsWithId userDetails = new UserDetailsWithId(
                id,
                new ArrayList<>()
        );

        // Act
        Long actualId = userDetails.getId();

        // Assert
        assertEquals(id, actualId);
    }
}