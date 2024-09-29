package org.cris6h16.Config.SpringBoot.Security;

import static org.junit.jupiter.api.Assertions.*;

import org.cris6h16.Config.SpringBoot.Security.Filters.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityConfigTest {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private SecurityConfig securityConfig;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(securityConfig)
                .addFilters(jwtAuthenticationFilter)  // Add the JWT filter
                .build();
    }

    @Test
    void testSecurityFilterChain() throws Exception {
        // permit all
        mockMvc.perform(get(securityConfig.loginPath))
                .andExpect(status().isOk());

        mockMvc.perform(get(securityConfig.signupPath))
                .andExpect(status().isOk());

        // secured
        mockMvc.perform(get(securityConfig.userAccountPath))
                .andExpect(status().isForbidden());

        mockMvc.perform(get(securityConfig.allUsersPagePath))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testUserAccess() throws Exception {
        mockMvc.perform(get(securityConfig.userAccountPath))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testAdminAccess() throws Exception {
        mockMvc.perform(get(securityConfig.allUsersPagePath))
                .andExpect(status().isOk());
    }

}
