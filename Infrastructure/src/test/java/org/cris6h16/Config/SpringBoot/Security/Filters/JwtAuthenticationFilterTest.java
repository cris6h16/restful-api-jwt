package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtilsImpl jwtUtilsImpl;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    String accessTokenCookieName = "accessTokenCookieName";
    String refreshTokenCookieName = "refreshTokenCookieName";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    }

    @Test
    void doFilterInternal_doesntExistAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                mock(HttpServletResponse.class),
                filterChain
        );

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
        verify(jwtUtilsImpl, never()).validate(any());
    }


    @Test
    void doFilterInternal_tokenInvalid() throws ServletException, IOException {
        // Arrange
        String invalidToken = "expired,etc";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtilsImpl.validate(invalidToken)).thenReturn(false);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                mock(HttpServletResponse.class),
                filterChain
        );

        // Assert
        verify(jwtUtilsImpl, times(1)).validate(invalidToken);
        verify(filterChain, times(1)).doFilter(any(), any());
        verify(jwtUtilsImpl, never()).getId(any());
    }


    @Test
    void doFilterInternal_tokenValid() throws ServletException, IOException {
        // Arrange
        SecurityContext securityContext = currentMockedSecurityContext();
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN);
        Long id = 99L;

        String validToken = "validToken";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);


        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtilsImpl.validate(validToken)).thenReturn(true);
        when(jwtUtilsImpl.getId(validToken)).thenReturn(id);
        when(jwtUtilsImpl.getRoles(validToken)).thenReturn(roles);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                mock(HttpServletResponse.class),
                filterChain
        );

        // Assert
        verify(securityContext, times(1)).setAuthentication(argThat(authentication -> { // UsernamePasswordAuthenticationToken
            assertTrue(contains(authentication.getPrincipal(), id, roles));
            assertNull(authentication.getCredentials());
            assertEquals(roles, toSetERoles(authentication.getAuthorities()));
            return true;
        }));
        verify(jwtUtilsImpl, times(1)).validate(validToken);
        verify(jwtUtilsImpl, times(1)).getId(validToken);
        verify(jwtUtilsImpl, times(1)).getRoles(validToken);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    private Set<ERoles> toSetERoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(grantedAuthority -> ERoles.valueOf(grantedAuthority.getAuthority()))
                .collect(Collectors.toSet());
    }

    private boolean contains(Object principal, Long expectedId, Set<ERoles> expectedRoles) {
        if (!(principal instanceof UserDetailsWithId user)) return false;
        boolean idMatches = user.getId().equals(expectedId);
        boolean rolesMatches = user.getAuthorities().stream()
                .map(grantedAuthority -> ERoles.valueOf(grantedAuthority.getAuthority()))
                .allMatch(expectedRoles::contains);
        return idMatches && rolesMatches;
    }


    private SecurityContext currentMockedSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        return securityContext;
    }

    @Test
    void getTokenFromRequest_AuthorizationHeaderDoesntExist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        verify(request, times(1)).getHeader("Authorization");
        assertNull(result);
    }


    @Test
    void getTokenFromRequest_success() {
        // Arrange
        String header = "Bearer token123456789";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn(header);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        verify(request, times(1)).getHeader("Authorization");
        assertEquals("token123456789", result);
    }
}
