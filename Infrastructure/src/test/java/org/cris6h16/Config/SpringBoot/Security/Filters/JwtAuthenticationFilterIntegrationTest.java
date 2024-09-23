package org.cris6h16.Config.SpringBoot.Security.Filters;


import jakarta.servlet.http.Cookie;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.CustomUserDetailsService;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo: create a yml for prod and tests
@SpringBootTest
@AutoConfigureMockMvc // MockMvc
public class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtilsImpl jwtUtils;

    @MockBean
    private VerifyEmailPort verifyEmailPort;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Value("${jwt.expiration.token.access.cookie.name}")
    private String accessTokenCookieName;

    @Test
    void whenRequestWithValidJwt_thenAuthenticated() throws Exception {
        // Arrange
        UserDetailsWithId userDetails = mock(UserDetailsWithId.class);
        String validJwt = "validJwt";
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, validJwt);

        when(userDetails.getAuthorities()).thenReturn(userAuthority());
        when(userDetails.getId()).thenReturn(1L);

        when(jwtUtils.validate(validJwt)).thenReturn(true);
        when(jwtUtils.getId(validJwt)).thenReturn(1L);

        // - beans
        when(userDetailsService.loadUserById(1L)).thenReturn(userDetails);


        // Act
        mockMvc.perform(put("/api/v1/auth/verify-email")
                        .cookie(accessTokenCookie))
                .andExpect(status().isNoContent());

        // Assert
        verify(verifyEmailPort, times(1)).handle(1L);
    }

    private Collection<GrantedAuthority> userAuthority() {
        return Set.of(new SimpleGrantedAuthority(ERoles.ROLE_USER.toString()));
    }

    @Test
    void whenRequestWithInvalidJwt_thenUnauthenticated() throws Exception {
        // Arrange
        String invalidJwt = "invalidJwtToken";
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, invalidJwt);
        when(jwtUtils.validate(invalidJwt)).thenReturn(false);

        // Act
        mockMvc.perform(put("/api/v1/auth/verify-email")
                        .cookie(accessTokenCookie))
                .andExpect(status().isForbidden());
    }
}
