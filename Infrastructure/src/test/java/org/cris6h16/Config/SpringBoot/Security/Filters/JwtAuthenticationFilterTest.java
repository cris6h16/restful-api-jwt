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
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, token);
        Cookie other1 = new Cookie("other1", "value of the cookie1");
        Cookie other2 = new Cookie("other2", "value of the cookie2");
        Cookie other3 = new Cookie("other3", "value of the cookie3");

        Cookie[] cookies = new Cookie[]{other1, other2, accessTokenCookie, other3};

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

    @Test
    void doFilterInternal_tokenInvalid() throws ServletException, IOException {
        // Arrange
        String invalidToken = "expired,etc";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, invalidToken);

        when(request.getCookies())
                .thenReturn(new Cookie[]{accessTokenCookie});
        when(jwtUtilsImpl.validate(invalidToken))
                .thenReturn(false);

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
        SecurityContextHolder.getContext().setAuthentication(null);

        String validToken = "validToken";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("EROLE_USER"));
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, validToken);

        UserDetailsWithId userDetailsWithId = mock(UserDetailsWithId.class);
        when(userDetailsWithId.getAuthorities()).thenReturn(authorities);

        when(request.getCookies()).thenReturn(new Cookie[]{accessTokenCookie});
        when(jwtUtilsImpl.validate(validToken)).thenReturn(true);
        when(jwtUtilsImpl.getId(validToken)).thenReturn(99L);
        when(userDetailsService.loadUserById(99L)).thenReturn(userDetailsWithId);

        //Act
        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        // Assert
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userDetailsWithId, auth.getPrincipal());
        assertEquals(authorities, auth.getAuthorities());

        verify(jwtUtilsImpl, times(1)).validate(validToken);
        verify(jwtUtilsImpl, times(1)).getId(validToken);
        verify(userDetailsService, times(1)).loadUserById(99L);
        verify(filterChain, times(1)).doFilter(request, response);
    }


}