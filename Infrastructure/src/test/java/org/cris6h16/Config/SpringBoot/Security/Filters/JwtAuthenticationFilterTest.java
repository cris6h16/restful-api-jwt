package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.CustomUserDetailsService;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// todo: refactor this
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtilsImpl jwtUtilsImpl;
    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    String accessTokenCookieName = "accessTokenCookieName";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtProperties.getToken()).thenReturn(mock(JwtProperties.Token.class));
        when(jwtProperties.getToken().getAccess()).thenReturn(mock(JwtProperties.Token.Access.class));
        when(jwtProperties.getToken().getAccess().getCookie()).thenReturn(mock(JwtProperties.Token.Access.Cookie.class));

        when(jwtProperties.getToken().getAccess().getCookie().getName())
                .thenReturn(accessTokenCookieName);
    }

    @Test
    void doFilterInternal_cookiesNull() throws ServletException, IOException {
        // Arrange
        FilterChain filterChain = mock(FilterChain.class);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                mock(HttpServletRequest.class),
                mock(HttpServletResponse.class),
                filterChain
        );

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
        verify(jwtUtilsImpl, never()).validate(any());
    }

    @Test
    void doFilterInternal_tokenExtractedCorrectly() throws ServletException, IOException {
        // Arrange
        String token = "imthetoken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = createCookiesWithToken(token);

        when(request.getCookies()).thenReturn(cookies);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                mock(HttpServletResponse.class),
                mock(FilterChain.class)
        );

        // Assert
        verify(jwtUtilsImpl, times(1)).validate(token);
    }

    private Cookie[] createCookiesWithToken(String token) {
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, token);
        Cookie other1 = new Cookie("other1", "value of the cookie1");
        Cookie other2 = new Cookie("other2", "value of the cookie2");
        Cookie other3 = new Cookie("other3", "value of the cookie3");

        return new Cookie[]{accessTokenCookie, other1, other2, other3};
    }

    @Test
    void doFilterInternal_tokenInvalid() throws ServletException, IOException {
        // Arrange
        String invalidToken = "expired,etc";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(createCookiesWithToken(invalidToken));
        when(jwtUtilsImpl.validate(invalidToken)).thenReturn(false);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                mock(HttpServletResponse.class),
                filterChain
        );

        // Assert
        verify(jwtUtilsImpl, times(1)).validate(any());
        verify(jwtUtilsImpl, never()).getId(any());
        verify(userDetailsService, never()).loadUserById(any());
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_tokenValid() throws ServletException, IOException {
        // Arrange
        SecurityContext securityContext = currentMockedSecurityContext();
        UserDetailsWithId userDetailsWithId = userDWIWithAuthorities();

        String validToken = "validToken";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);


        when(request.getCookies()).thenReturn(createCookiesWithToken(validToken));
        when(jwtUtilsImpl.validate(validToken)).thenReturn(true);
        when(jwtUtilsImpl.getId(validToken)).thenReturn(99L);
        when(userDetailsService.loadUserById(99L)).thenReturn(userDetailsWithId);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                mock(HttpServletResponse.class),
                filterChain
        );

        // Assert
        verify(securityContext, times(1)).setAuthentication(argThat(authentication -> { // UsernamePasswordAuthenticationToken
            assertEquals(userDetailsWithId, authentication.getPrincipal());
            assertNull(authentication.getCredentials());
            assertEquals(userDetailsWithId.getAuthorities(), authentication.getAuthorities());
            return true;
        }));
        verify(jwtUtilsImpl, times(1)).validate(validToken);
        verify(jwtUtilsImpl, times(1)).getId(validToken);
        verify(userDetailsService, times(1)).loadUserById(99L);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    private UserDetailsWithId userDWIWithAuthorities() {
        UserDetailsWithId userDetailsWithId = mock(UserDetailsWithId.class);
        when(userDetailsWithId.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return userDetailsWithId;
    }

    private SecurityContext currentMockedSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        return securityContext;
    }


}