package org.cris6h16.Adapters.In.Rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Config.SpringBoot.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest
@SpringBootTest(classes = Main.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // avoid using the real database
@AutoConfigureMockMvc(addFilters = false)// bypass security filters
@ActiveProfiles(value = {"test"}) // todo: set in all tests
class AuthenticationControllerTest {

    @Value("${controller.path.core}" + "${controller.path.authentication.core}")
    String mainPath;

    @Value("${controller.path.authentication.signup}")
    String signupPath;

    @Value("${controller.path.authentication.login}")
    String loginPath;

    @Value("${controller.path.authentication.verify-email}")
    String verifyMyEmailPath;

    @Value("${controller.path.authentication.request-reset-password}")
    String requestPasswordResetPath;

    @Value("${controller.path.authentication.reset-password}")
    String resetPasswordPath;

    @Value("${controller.path.authentication.refresh-access-token}")
    String refreshAccessTokenPath;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationControllerFacade facade;

    @Test
    void login_shouldCallFacadeAndReturnStatusOk() throws Exception {
        LoginDTO dto = new LoginDTO();
        when(facade.login(any(LoginDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).login(any(LoginDTO.class));
    }

    @Test
    void login_shouldAcceptJsonContentType() throws Exception {
        LoginDTO dto = new LoginDTO();
        when(facade.login(any(LoginDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).login(any(LoginDTO.class));
    }

    @Test
    void login_shouldRejectNonJsonContentType() throws Exception {
        LoginDTO dto = new LoginDTO();

        mockMvc.perform(post(mainPath + loginPath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void signUp_shouldCallFacadeAndReturnStatusOk() throws Exception {
        // Arrange
        CreateAccountDTO dto = new CreateAccountDTO();
        when(facade.signup(any(CreateAccountDTO.class))).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(post(mainPath + signupPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).signup(any(CreateAccountDTO.class));
    }

    @Test
    void signUp_shouldAcceptJsonContentType() throws Exception {
        CreateAccountDTO dto = new CreateAccountDTO();
        when(facade.signup(any(CreateAccountDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + signupPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).signup(any(CreateAccountDTO.class));
    }

    @Test
    void signUp_shouldRejectNonJsonContentType() throws Exception {
        CreateAccountDTO dto = new CreateAccountDTO();

        mockMvc.perform(post(mainPath + signupPath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void verifyMyEmail_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(facade.verifyMyEmail()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(put(mainPath + verifyMyEmailPath))
                .andExpect(status().isOk());

        verify(facade).verifyMyEmail();
    }


    @Test
    void requestPasswordReset_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String email = "test@example.com";
        when(facade.requestPasswordReset(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + requestPasswordResetPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isOk());

        verify(facade).requestPasswordReset(anyString());
    }

    @Test
    void requestPasswordReset_shouldAcceptJsonContentType() throws Exception {
        String email = "test@example.com";
        when(facade.requestPasswordReset(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + requestPasswordResetPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isOk());

        verify(facade).requestPasswordReset(anyString());
    }

    @Test
    void requestPasswordReset_shouldRejectNonJsonContentType() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post(mainPath + requestPasswordResetPath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void resetPassword_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String newPassword = "newPassword123";
        when(facade.resetPassword(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(mainPath + resetPasswordPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPassword))
                .andExpect(status().isOk());

        verify(facade).resetPassword(anyString());
    }

    @Test
    void resetPassword_shouldAcceptJsonContentType() throws Exception {
        String newPassword = "newPassword123";
        when(facade.resetPassword(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(mainPath + resetPasswordPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPassword))
                .andExpect(status().isOk());

        verify(facade).resetPassword(anyString());
    }

    @Test
    void resetPassword_shouldRejectNonJsonContentType() throws Exception {
        String newPassword = "newPassword123";

        mockMvc.perform(patch(mainPath + resetPasswordPath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(newPassword))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void refreshAccessToken_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(facade.refreshAccessToken()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + refreshAccessTokenPath))
                .andExpect(status().isOk());

        verify(facade).refreshAccessToken();
    }

}