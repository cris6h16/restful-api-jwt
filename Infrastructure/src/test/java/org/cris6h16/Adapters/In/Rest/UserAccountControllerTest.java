package org.cris6h16.Adapters.In.Rest;

import CommonConfigs.ControllerAndAdviceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {ControllerAndAdviceConfig.class})
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters
@ActiveProfiles(value = {"test"})
@Tag("with-spring-context")
public class UserAccountControllerTest {


    @Autowired
    private ErrorMessages errorMessages;

    @Autowired
    private UserAccountControllerFacade userAccountControllerFacade;

    @Autowired
    private AuthenticationControllerFacade authenticationControllerFacade;


    @Value("${controller.user.account.request.delete}")
    String requestDeleteMyAccountPath;

    @Value("${controller.user.account.core}")
    String deleteMyAccountPath;

    @Value("${controller.user.account.update.username}")
    String updateMyUsernamePath;

    @Value("${controller.user.account.update.password}")
    String updateMyPasswordPath;

    @Value("${controller.user.account.request.update-email}")
    String requestUpdateMyEmailPath;

    @Value("${controller.user.account.update.email}")
    String updateMyEmailPath;

    @Value("${controller.user.pagination.all}")
    String getAllUsersPath;


    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        when(errorMessages.getUnexpectedErrorMessage()).thenReturn("Unexpected error");
    }

    @Test
    void requestDeleteMyAccount_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(userAccountControllerFacade.requestDeleteMyAccount()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(requestDeleteMyAccountPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).requestDeleteMyAccount();
    }

    @Test
    void deleteMyAccount_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(userAccountControllerFacade.deleteMyAccount()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete(deleteMyAccountPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).deleteMyAccount();
    }

    @Test
    void updateMyUsername_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String newUsername = "newUsername";
        when(userAccountControllerFacade.updateMyUsername(anyString())).thenReturn(ResponseEntity.ok().build());


        mockMvc.perform(patch(updateMyUsernamePath)
                        .contentType(TEXT_PLAIN)
                        .content(newUsername))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).updateMyUsername(anyString());
    }

    @Test
    void updateMyUsername_shouldRejectNonTextPlain() throws Exception {
        String newUsername = "newUsername";
        mockMvc.perform(patch(updateMyUsernamePath)
                        .contentType(APPLICATION_JSON)
                        .content(newUsername))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateMyPassword_shouldCallFacadeAndReturnStatusOk() throws Exception {
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("oldPassword", "newPassword");
        String dtoJson = new ObjectMapper().writeValueAsString(dto);
        when(userAccountControllerFacade.updateMyPassword(any(UpdateMyPasswordDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(updateMyPasswordPath)
                        .contentType(APPLICATION_JSON)
                        .content(dtoJson)

                )
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).updateMyPassword(any(UpdateMyPasswordDTO.class));
    }

    @Test
    void updateMyPassword_shouldRejectNonJsonContentType() throws Exception {
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("oldPassword", "newPassword");

        mockMvc.perform(patch(updateMyPasswordPath)
                        .contentType(TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void requestUpdateMyEmail_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(userAccountControllerFacade.requestUpdateMyEmail()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(requestUpdateMyEmailPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).requestUpdateMyEmail();
    }

    @Test
    void updateMyEmail_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String newEmail = "newemail@example.com";
        when(userAccountControllerFacade.updateMyEmail(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(updateMyEmailPath)
                        .contentType(TEXT_PLAIN)
                        .content(newEmail))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).updateMyEmail(anyString());
    }

    @Test
    void updateMyEmail_shouldRejectNonTextPlain() throws Exception {
        String newEmail = "newemail@example.com";

        mockMvc.perform(patch(updateMyEmailPath)
                        .contentType(APPLICATION_JSON)
                        .content(newEmail))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMyAccount_shouldCallFacadeAndReturnProfile() throws Exception {
        PublicProfileDTO profile = new PublicProfileDTO();
        when(userAccountControllerFacade.getMyAccount()).thenReturn(ResponseEntity.ok(profile));

        mockMvc.perform(get(deleteMyAccountPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).getMyAccount();
    }

    @Test
    void getAllUsers_shouldCallFacadeAndReturnPageOfProfiles() throws Exception {
        Page<PublicProfileDTO> page = new PageImpl<>(List.of(new PublicProfileDTO()));
        when(userAccountControllerFacade.getAllUsers(any(Pageable.class))).thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(get(getAllUsersPath)
                        .param("page", "0")
                        .param("size", "50")
                        .param("sort", "id,DESC"))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).getAllUsers(any(Pageable.class));
    }
}
