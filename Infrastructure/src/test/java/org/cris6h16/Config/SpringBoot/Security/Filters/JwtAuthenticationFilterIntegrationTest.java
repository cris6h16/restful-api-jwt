package org.cris6h16.Config.SpringBoot.Security.Filters;


import jakarta.servlet.http.Cookie;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Config.SpringBoot.Main;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc // MockMvc
@ActiveProfiles("test")
public class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtilsImpl jwtUtils;


    @MockBean // avoid real interactions with the port
    private AuthenticationControllerFacade authenticationControllerFacade;

    @Value("${jwt.token.access.cookie.name}")
    private String accessTokenCookieName;

    @Value("${controller.authentication.refresh-access-token}")
    private String postAndAuthenticatedPath;


    @Test
    public void whenRequestWithValidJwt_thenAuthenticated() throws Exception {
        // Arrange
        String validJwt = "validJwt";
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, validJwt);

        when(jwtUtils.validate(validJwt)).thenReturn(true);
        when(jwtUtils.getId(validJwt)).thenReturn(1L);

        // Act
        mockMvc.perform(post(postAndAuthenticatedPath)
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk());

        // Assert
        verify(authenticationControllerFacade, times(1)).refreshAccessToken(); // the method behind postAndAuthenticatedPath
    }


    @Test
    public void whenRequestWithInvalidJwt_thenUnauthenticated() throws Exception {
        // Arrange
        String invalidJwt = "invalidJwtToken";
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, invalidJwt);

        when(jwtUtils.validate(invalidJwt)).thenReturn(false);

        // Act
        mockMvc.perform(post(postAndAuthenticatedPath)
                        .cookie(accessTokenCookie))
                .andExpect(status().isForbidden());
    }
}
