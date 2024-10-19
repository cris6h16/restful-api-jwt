package org.cris6h16.Adapters.In.Rest;

import CommonConfigs.ControllerAndAdviceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ControllerAndAdviceConfig.class)
@AutoConfigureMockMvc(addFilters = false)// bypass security filters
@ActiveProfiles(value = {"test"}) // todo: set in all tests
public class AuthenticationControllerTest {

    @Value("${controller.authentication.signup}")
    String signupPath;

    @Value("${controller.authentication.login}")
    String loginPath;

    @Value("${controller.authentication.verify-email}")
    String verifyMyEmailPath;

    @Value("${controller.authentication.request-reset-password}")
    String requestPasswordResetPath;

    @Value("${controller.authentication.reset-password}")
    String resetPasswordPath;

    @Value("${controller.authentication.refresh-access-token}")
    String refreshAccessTokenPath;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationControllerFacade facade;

    @Test
    void login_shouldCallFacadeAndReturnStatusOk() throws Exception {
        LoginDTO dto = new LoginDTO();
        when(facade.login(any(LoginDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(loginPath)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).login(any(LoginDTO.class));
    }

    @Test
    void login_shouldAcceptJsonContentType() throws Exception {
        LoginDTO dto = new LoginDTO();
        when(facade.login(any(LoginDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(loginPath)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).login(any(LoginDTO.class));
    }

    @Test
    void login_shouldRejectNonJsonContentType() throws Exception {
        LoginDTO dto = new LoginDTO();

        mockMvc.perform(post(loginPath)
                        .contentType(TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void signUp_shouldCallFacadeAndReturnStatusOk() throws Exception {
        // Arrange
        CreateAccountDTO dto = new CreateAccountDTO();
        when(facade.signup(any(CreateAccountDTO.class))).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(post(signupPath)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).signup(any(CreateAccountDTO.class));
    }

    @Test
    void signUp_shouldAcceptJsonContentType() throws Exception {
        CreateAccountDTO dto = new CreateAccountDTO();
        when(facade.signup(any(CreateAccountDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(signupPath)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(facade).signup(any(CreateAccountDTO.class));
    }

    @Test
    void signUp_shouldRejectNonJsonContentType() throws Exception {
        CreateAccountDTO dto = new CreateAccountDTO();

        mockMvc.perform(post(signupPath)
                        .contentType(TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void verifyMyEmail_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(facade.verifyMyEmail()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(put(verifyMyEmailPath))
                .andExpect(status().isOk());

        verify(facade).verifyMyEmail();
    }


    @Test
    void requestPasswordReset_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String email = "test@example.com";
        when(facade.requestPasswordReset(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(requestPasswordResetPath)
                        .contentType(TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isOk());

        verify(facade).requestPasswordReset(anyString());
    }

    @Test
    void requestPasswordReset_shouldAcceptTextPlain() throws Exception {
        String email = "test@example.com";
        when(facade.requestPasswordReset(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(requestPasswordResetPath)
                        .contentType(TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isOk());

        verify(facade).requestPasswordReset(anyString());
    }

    @Test
    void requestPasswordReset_shouldRejectNonTextPlain() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post(requestPasswordResetPath)
                        .contentType(APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void resetPassword_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String newPassword = "newPassword123";
        when(facade.resetPassword(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(resetPasswordPath)
                        .contentType(TEXT_PLAIN)
                        .content(newPassword))
                .andExpect(status().isOk());

        verify(facade).resetPassword(anyString());
    }

    @Test
    void resetPassword_shouldAcceptTextPlain() throws Exception {
        String newPassword = "newPassword123";
        when(facade.resetPassword(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(resetPasswordPath)
                        .contentType(TEXT_PLAIN)
                        .content(newPassword))
                .andExpect(status().isOk());

        verify(facade).resetPassword(anyString());
    }

    @Test
    void resetPassword_shouldRejectNonTextPlain() throws Exception {
        String newPassword = "newPassword123";

        mockMvc.perform(patch(resetPasswordPath)
                        .contentType(APPLICATION_JSON)
                        .content(newPassword))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void refreshAccessToken_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(facade.refreshAccessToken()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(refreshAccessTokenPath))
                .andExpect(status().isOk());

        verify(facade).refreshAccessToken();
    }

}