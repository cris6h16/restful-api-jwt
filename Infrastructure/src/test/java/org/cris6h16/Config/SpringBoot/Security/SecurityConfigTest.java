package org.cris6h16.Config.SpringBoot.Security;

import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.cris6h16.Config.SpringBoot.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @MockBean
    private UserAccountControllerFacade userAccountControllerFacade;
    @MockBean
    private AuthenticationControllerFacade authenticationControllerFacade;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicEndpoints() throws Exception {
        when(authenticationControllerFacade.login(any())).thenReturn(ResponseEntity.ok().build());
        when(authenticationControllerFacade.signup(any())).thenReturn(ResponseEntity.ok().build());

        assertEndpointIsAccessible(securityConfig.loginPath, HttpMethod.POST);
        assertEndpointIsAccessible(securityConfig.signupPath, HttpMethod.POST);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAccessingToAdminEndpointAsUser_forbidden() throws Exception {
        assertEndpointIsForbidden(securityConfig.allUsersPagePath, HttpMethod.GET);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAccessingToUserEndpointAsAdmin_forbidden() throws Exception {
        assertEndpointIsForbidden(securityConfig.userAccountPath, HttpMethod.GET); // get account ( also the core path )
    }

    @Test
    @WithAnonymousUser
    void testSecuredEndpoints() throws Exception {
        // mock the facades are not necessary, request never reaches to facades
        assertEndpointIsForbidden(securityConfig.userAccountPath, HttpMethod.GET); // get account ( also the core path )
        assertEndpointIsForbidden(securityConfig.userAccountPath + "/delete", HttpMethod.DELETE); // delete account ( sub path of userAccountPath, inside the secured pattern )
        assertEndpointIsForbidden(securityConfig.allUsersPagePath, HttpMethod.GET);
    }

    @WithMockUser(roles = "USER")
    @Test
    void testUserAccess() throws Exception {
        when(userAccountControllerFacade.getMyAccount()).thenReturn(ResponseEntity.ok().build());
        assertEndpointIsAccessible(securityConfig.userAccountPath, HttpMethod.GET);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testAdminAccess() throws Exception {
        when(userAccountControllerFacade.getAllUsers(any())).thenReturn(ResponseEntity.ok().build());
        assertEndpointIsAccessible(securityConfig.allUsersPagePath, HttpMethod.GET);
    }

    private void assertEndpointIsAccessible(String path, HttpMethod method) throws Exception {
        if (method == HttpMethod.POST) {
            mockMvc.perform(post(path)
                            .contentType("application/json")
                            .content("{}"))
                    .andExpect(status().isOk());
            return;
        }
        if (method == HttpMethod.GET) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk());
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void assertEndpointIsForbidden(String path, HttpMethod method) throws Exception {
        if (method == HttpMethod.GET) {
            mockMvc.perform(get(path))
                    .andExpect(status().isForbidden());
            return;
        }
        if (method == HttpMethod.DELETE) {
            mockMvc.perform(delete(path))
                    .andExpect(status().isForbidden());
            return;
        }
        throw new UnsupportedOperationException();
    }


}