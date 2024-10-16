package org.cris6h16.Config.SpringBoot.Security.Filters;


import jakarta.servlet.http.Cookie;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.cris6h16.Adapters.In.Rest.Facades.UserControllerFacadeTest;
import org.cris6h16.Config.SpringBoot.Main;
import org.cris6h16.Config.SpringBoot.Properties.CorsProperties;
import org.cris6h16.Config.SpringBoot.Properties.EmailServiceProperties;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "org.cris6h16.Config.SpringBoot.Security",
        "org.cris6h16.Adapters.In.Rest",
        "org.cris6h16.Config.SpringBoot.Properties",
}, excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = {
                        EmailServiceProperties.class,
                }
        )
}
)
class CustomConfig {
}


@SpringBootTest(classes = {CustomConfig.class})
@AutoConfigureMockMvc // MockMvc
@ActiveProfiles("test")
public class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ErrorMessages errorMessages;

    @MockBean
    private JwtUtilsImpl jwtUtils;

    @MockBean // avoid real interactions with the port
    private AuthenticationControllerFacade authenticationControllerFacade;

    @MockBean
    private UserAccountControllerFacade userAccountControllerFacade;

    @Value("${controller.authentication.refresh-access-token}")
    private String postAndAuthenticatedPath;


    @Test
    public void whenRequestWithValidJwt_thenAuthenticated() throws Exception {
        // Arrange
        String validJwt = "validJwt";
        HttpHeaders headers = authenticationHeader(validJwt);

        when(jwtUtils.validate(validJwt)).thenReturn(true);
        when(jwtUtils.getId(validJwt)).thenReturn(1L);

        // Act
        mockMvc.perform(post(postAndAuthenticatedPath)
                        .headers(headers))
                .andExpect(status().isOk());

        // Assert
        verify(authenticationControllerFacade, times(1)).refreshAccessToken(); // the method behind postAndAuthenticatedPath
    }

    private HttpHeaders authenticationHeader(String validJwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + validJwt);
        return headers;
    }


    @Test
    public void whenRequestWithInvalidJwt_thenUnauthenticated() throws Exception {
        // Arrange
        String invalidJwt = "invalidJwtToken";
        HttpHeaders headers = authenticationHeader(invalidJwt);

        when(jwtUtils.validate(invalidJwt)).thenReturn(false);

        // Act
        mockMvc.perform(post(postAndAuthenticatedPath)
                        .headers(headers))
                .andExpect(status().isForbidden());
    }
}
