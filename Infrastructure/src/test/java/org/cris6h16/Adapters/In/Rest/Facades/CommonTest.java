package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommonTest {

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetPrincipalIdSuccess() {
        // Arrange
        UserDetailsWithId userDetails = mock(UserDetailsWithId.class);
        when(userDetails.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        Long principalId = Common.getPrincipalId();

        // Assert
        assertEquals(1L, principalId);
    }

    @Test
    void testGetPrincipalIdClassCastException() {
        // Arrange
        Object notUserDetails = new Object();
        when(authentication.getPrincipal()).thenReturn(notUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, Common::getPrincipalId);
        assertEquals("Principal is not an instance of UserDetailsWithId", exception.getMessage());
    }
}
