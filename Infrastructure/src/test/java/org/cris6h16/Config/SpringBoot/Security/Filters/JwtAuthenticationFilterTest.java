package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
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

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    String accessTokenCookieName = "accessTokenCookieName";
    String refreshTokenCookieName = "refreshTokenCookieName";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockProperties();
    }

    private void mockProperties() {

        when(jwtProperties.getToken()).thenReturn(mock(JwtProperties.Token.class));
        when(jwtProperties.getToken().getAccess()).thenReturn(mock(JwtProperties.Token.Access.class));
        when(jwtProperties.getToken().getRefresh()).thenReturn(mock(JwtProperties.Token.Refresh.class));
        when(jwtProperties.getToken().getAccess().getCookie()).thenReturn(mock(JwtProperties.Token.Access.Cookie.class));
        when(jwtProperties.getToken().getRefresh().getCookie()).thenReturn(mock(JwtProperties.Token.Refresh.Cookie.class));

        when(jwtProperties.getToken().getAccess().getCookie().getName()).thenReturn(accessTokenCookieName);
        when(jwtProperties.getToken().getRefresh().getCookie().getName()).thenReturn(refreshTokenCookieName);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
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
    void doFilterInternal_tokenInvalid() throws ServletException, IOException {
        // Arrange
        String invalidToken = "expired,etc";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(createCookiesContainingCustom(accessTokenCookieName, invalidToken));
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
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    private Cookie[] createCookiesContainingCustom(String cookieName, String value) {
        Cookie other1 = new Cookie("other1", "value of the cookie1");
        Cookie other2 = new Cookie("other2", "value of the cookie2");
        Cookie custom = new Cookie(cookieName, value);
        Cookie other3 = new Cookie("other3", "value of the cookie3");
        Cookie other4 = new Cookie("other4", "value of the cookie4");

        return new Cookie[]{other1, other2, custom, other3, other4};
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


        when(request.getCookies()).thenReturn(createCookiesContainingCustom(refreshTokenCookieName, validToken));
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


//      /**
//     * Extracts the token from the cookies in the request
//     *
//     * @param request the request containing the cookies
//     * @return if the access token is found, it is returned, otherwise the refresh token is returned ( null if not found any of them )
//     */
//protected String getAccessOrRefreshTokenFromCookies(HttpServletRequest request) {
//    Cookie[] cookies = request.getCookies();
//    if (cookies == null) {
//        log.debug("No cookies found in request");
//        return null;
//    }
//
//    String accessToken = getAccessTokenFromCookie(cookies);
//    String refreshToken = getRefreshTokenFromCookie(cookies);
//
//    return accessToken != null ? accessToken : refreshToken;
//}
//
//    protected String getRefreshTokenFromCookie(Cookie[] cookies) {
//        String refreshTokenCookieName = jwtProperties.getToken().getRefresh().getCookie().getName();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(refreshTokenCookieName)) {
//                log.debug("found refresh token cookie");
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
//
//    protected String getAccessTokenFromCookie(Cookie[] cookies) {
//        String accessTokenCookieName = jwtProperties.getToken().getAccess().getCookie().getName();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(accessTokenCookieName)) {
//                log.debug("found access token cookie");
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
//     */

    @Test
    void getAccessOrRefreshTokenFromCookies_CorrectInteractions() {
        // Arrange
        String accessToken = "accessToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = createCookiesContainingCustom(accessTokenCookieName, accessToken);
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        verify(request, times(1)).getCookies();
        verify(jwtProperties.getToken().getAccess().getCookie(), times(1)).getName();
        verify(jwtProperties.getToken().getRefresh().getCookie(), times(1)).getName();
    }


    // todo: in used technologies in docs add what are the functionality of each one
    @Test
    void getAccessOrRefreshTokenFromCookies_cookiesNull() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(null);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        assertNull(result);
    }

    @Test
    void getAccessOrRefreshTokenFromCookies_bothTokensFound_returnAccessTkReturned() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        Cookie[] withAccess = createCookiesContainingCustom(accessTokenCookieName, accessToken);
        Cookie[] withRefresh = createCookiesContainingCustom(refreshTokenCookieName, refreshToken);

        when(request.getCookies()).thenReturn(combine(withAccess, withRefresh));

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        assertEquals(accessToken, result);
    }


    @Test
    void getAccessOrRefreshTokenFromCookies_onlyAccessTokenFound_returnAccessTkReturned() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String accessToken = "accessToken";
        Cookie[] withAccess = createCookiesContainingCustom(accessTokenCookieName, accessToken);

        when(request.getCookies()).thenReturn(withAccess);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        assertEquals(accessToken, result);
    }

    @Test
    void getAccessOrRefreshTokenFromCookies_onlyRefreshTokenFound_returnRefreshTkReturned() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String refreshToken = "refreshToken";
        Cookie[] withRefresh = createCookiesContainingCustom(refreshTokenCookieName, refreshToken);

        when(request.getCookies()).thenReturn(withRefresh);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        assertEquals(refreshToken, result);
    }

    @Test
    void getAccessOrRefreshTokenFromCookies_noTokenFound_returnNull() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] noToken = new Cookie[0];

        when(request.getCookies()).thenReturn(noToken);

        // Act
        String result = jwtAuthenticationFilter.getTokenFromRequest(request);

        // Assert
        assertNull(result);
    }

    private Cookie[] combine(Cookie[] withAccess, Cookie[] withRefresh) {
        Cookie[] result = new Cookie[withAccess.length + withRefresh.length];
        int i = 0;
        for (Cookie cookie : withAccess) {
            result[i++] = cookie;
        }
        for (Cookie cookie : withRefresh) {
            result[i++] = cookie;
        }
        return result;
    }
}
